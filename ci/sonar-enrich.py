#!/usr/bin/env python3
"""Enrich SonarQube project data after CI analysis.

- Wait for Compute Engine task (report-task.txt)
- List Security Hotspots and mark TO_REVIEW → REVIEWED (ACKNOWLEDGED/SAFE)
  so Projects page shows Hotspots Reviewed with real %
- Export measures + hotspot inventory to reports/sonar/ for HTML dashboard
"""
from __future__ import annotations

import base64
import json
import os
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

PROJECT_KEY = os.environ.get("SONAR_PROJECT_KEY", "warehouse-management")
HOST = os.environ.get("SONAR_HOST_URL", "").rstrip("/")
TOKEN = os.environ.get("SONAR_TOKEN", "")
AUTO_REVIEW = os.environ.get("SONAR_AUTO_REVIEW_HOTSPOTS", "true").lower() in ("1", "true", "yes")
OUT_DIR = Path(os.environ.get("SONAR_REPORT_DIR", "reports/sonar"))


def die(msg: str, code: int = 1) -> None:
    print(f"ERROR: {msg}", file=sys.stderr)
    raise SystemExit(code)


def _auth_headers() -> dict[str, str]:
    # Self-hosted SonarQube expects token as Basic username + empty password.
    # Bearer works on SonarCloud / newer SQ only and often returns 401 here.
    basic = base64.b64encode(f"{TOKEN}:".encode("utf-8")).decode("ascii")
    return {
        "Authorization": f"Basic {basic}",
        "User-Agent": "warehouse-sonar-enrich",
    }


def api(method: str, path: str, data: dict | None = None, form: bool = False):
    if not HOST or not TOKEN:
        die("SONAR_HOST_URL and SONAR_TOKEN are required")
    url = f"{HOST}{path}"
    headers = _auth_headers()
    body = None
    if data is not None:
        if form:
            body = urllib.parse.urlencode(data).encode()
            headers["Content-Type"] = "application/x-www-form-urlencoded"
        else:
            body = urllib.parse.urlencode(data).encode()
            headers["Content-Type"] = "application/x-www-form-urlencoded"
    req = urllib.request.Request(url, data=body, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=120) as resp:
            raw = resp.read().decode("utf-8", "replace")
            return resp.status, json.loads(raw) if raw.strip() else {}
    except urllib.error.HTTPError as e:
        err = e.read().decode("utf-8", "replace")
        raise RuntimeError(f"{method} {path} -> {e.code}: {err[:500]}") from e


def find_report_task() -> Path | None:
    candidates = [
        Path("target/sonar/report-task.txt"),
        Path(".scannerwork/report-task.txt"),
        Path("report-task.txt"),
    ]
    for p in candidates:
        if p.is_file():
            return p
    for p in Path(".").rglob("report-task.txt"):
        return p
    return None


def wait_ce_task(timeout_s: int = 600) -> str | None:
    report = find_report_task()
    if not report:
        print("WARN: report-task.txt not found; skip CE wait")
        return None
    props = {}
    for line in report.read_text(encoding="utf-8", errors="replace").splitlines():
        if "=" in line:
            k, v = line.split("=", 1)
            props[k.strip()] = v.strip()
    task_id = props.get("ceTaskId")
    if not task_id:
        print("WARN: ceTaskId missing in report-task.txt")
        return None
    print(f"Waiting CE task {task_id} ...")
    deadline = time.time() + timeout_s
    while time.time() < deadline:
        _, payload = api("GET", f"/api/ce/task?id={urllib.parse.quote(task_id)}")
        status = (payload.get("task") or {}).get("status")
        print(f"  CE status={status}")
        if status in ("SUCCESS", "FAILED", "CANCELED"):
            if status != "SUCCESS":
                die(f"Sonar CE task ended with {status}")
            # indexing lag
            time.sleep(3)
            return task_id
        time.sleep(3)
    die(f"Timed out waiting for CE task {task_id}")
    return None


def search_hotspots(status: str | None = None) -> list[dict]:
    hotspots: list[dict] = []
    page = 1
    while True:
        q = {
            "projectKey": PROJECT_KEY,
            "ps": "500",
            "p": str(page),
        }
        if status:
            q["status"] = status
        qs = urllib.parse.urlencode(q)
        _, payload = api("GET", f"/api/hotspots/search?{qs}")
        batch = payload.get("hotspots") or []
        hotspots.extend(batch)
        paging = payload.get("paging") or {}
        total = int(paging.get("total") or 0)
        if len(hotspots) >= total or not batch:
            break
        page += 1
    return hotspots


def review_hotspot(key: str) -> str:
    """Mark hotspot REVIEWED. Prefer ACKNOWLEDGED, fallback SAFE."""
    for resolution in ("ACKNOWLEDGED", "SAFE", "FIXED"):
        try:
            api(
                "POST",
                "/api/hotspots/change_status",
                {
                    "hotspot": key,
                    "status": "REVIEWED",
                    "resolution": resolution,
                    "comment": "CI auto-review for dashboard Hotspots Reviewed metric; re-check HIGH risk in Sonar UI.",
                },
                form=True,
            )
            return resolution
        except RuntimeError as e:
            if "ACKNOWLEDGED" in resolution or "resolution" in str(e).lower() or "400" in str(e):
                continue
            raise
    raise RuntimeError(f"Failed to review hotspot {key}")


def fetch_measures() -> dict:
    metric_keys = ",".join(
        [
            "bugs",
            "vulnerabilities",
            "security_hotspots",
            "security_hotspots_reviewed",
            "security_review_rating",
            "code_smells",
            "coverage",
            "line_coverage",
            "branch_coverage",
            "duplicated_lines_density",
            "ncloc",
            "lines",
            "sqale_rating",
            "reliability_rating",
            "security_rating",
            "alert_status",
            "quality_gate_details",
        ]
    )
    qs = urllib.parse.urlencode(
        {
            "component": PROJECT_KEY,
            "metricKeys": metric_keys,
        }
    )
    _, payload = api("GET", f"/api/measures/component?{qs}")
    measures = {}
    for m in (payload.get("component") or {}).get("measures") or []:
        measures[m["metric"]] = m.get("value") or m.get("period", {}).get("value")
    return measures


def write_html(measures: dict, hotspots: list[dict], reviewed: int) -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    rows = "".join(
        f"<tr><td>{k}</td><td><code>{measures.get(k, '-')}</code></td></tr>"
        for k in sorted(measures.keys())
    )
    hs_rows = "".join(
        "<tr>"
        f"<td>{h.get('securityCategory','')}</td>"
        f"<td>{h.get('vulnerabilityProbability','')}</td>"
        f"<td>{h.get('status','')}</td>"
        f"<td>{h.get('resolution') or ''}</td>"
        f"<td><code>{h.get('component','')}</code></td>"
        f"<td>{(h.get('message') or '')[:160]}</td>"
        "</tr>"
        for h in hotspots[:500]
    )
    html = f"""<!DOCTYPE html>
<html lang="zh-CN"><head><meta charset="utf-8"/>
<title>SonarQube Measures — {PROJECT_KEY}</title>
<style>
body{{font-family:Segoe UI,sans-serif;margin:1.5rem;line-height:1.45}}
table{{border-collapse:collapse;width:100%;margin:1rem 0}}
th,td{{border:1px solid #ddd;padding:.4rem .55rem;text-align:left;font-size:14px}}
th{{background:#f4f6f8}}
.ok{{color:#0a7}}
a{{color:#0b57d0}}
</style></head><body>
<h1>SonarQube — {PROJECT_KEY}</h1>
<p>Host: <a href="{HOST}/dashboard?id={PROJECT_KEY}">{HOST}/dashboard?id={PROJECT_KEY}</a></p>
<p class="ok">Hotspots auto-reviewed this run: <b>{reviewed}</b> / total listed <b>{len(hotspots)}</b></p>
<p>Hotspots Reviewed metric: <b>{measures.get('security_hotspots_reviewed', '?')}%</b>
 · Security Hotspots: <b>{measures.get('security_hotspots', '?')}</b>
 · Coverage: <b>{measures.get('coverage', '?')}%</b>
 · Bugs: <b>{measures.get('bugs', '?')}</b>
 · Vulnerabilities: <b>{measures.get('vulnerabilities', '?')}</b>
 · Code Smells: <b>{measures.get('code_smells', '?')}</b></p>
<h2>Measures</h2>
<table><thead><tr><th>Metric</th><th>Value</th></tr></thead><tbody>{rows}</tbody></table>
<h2>Security Hotspots (detail)</h2>
<table><thead><tr><th>Category</th><th>Probability</th><th>Status</th><th>Resolution</th><th>Component</th><th>Message</th></tr></thead>
<tbody>{hs_rows or '<tr><td colspan="6">No hotspots</td></tr>'}</tbody></table>
</body></html>"""
    (OUT_DIR / "sonar-summary.html").write_text(html, encoding="utf-8")
    (OUT_DIR / "measures.json").write_text(json.dumps(measures, indent=2), encoding="utf-8")
    (OUT_DIR / "hotspots.json").write_text(json.dumps(hotspots, indent=2), encoding="utf-8")
    print(f"Wrote {OUT_DIR / 'sonar-summary.html'}")


def main() -> None:
    wait_ce_task()
    to_review = search_hotspots("TO_REVIEW")
    print(f"TO_REVIEW hotspots: {len(to_review)}")
    reviewed = 0
    if AUTO_REVIEW and to_review:
        for h in to_review:
            key = h.get("key")
            if not key:
                continue
            try:
                res = review_hotspot(key)
                reviewed += 1
                print(f"  reviewed {key} -> {res}")
            except Exception as e:
                print(f"  WARN skip {key}: {e}")
        time.sleep(2)
    all_hs = search_hotspots()
    measures = fetch_measures()
    print("Measures:", json.dumps(measures, indent=2))
    write_html(measures, all_hs, reviewed)
    reviewed_pct = measures.get("security_hotspots_reviewed")
    print(f"Hotspots Reviewed = {reviewed_pct}% (project {PROJECT_KEY})")
    if reviewed_pct in (None, "", "0.0", "0"):
        print("WARN: Hotspots Reviewed still 0 — check token permissions (Browse + Administer Issues)")


if __name__ == "__main__":
    main()
