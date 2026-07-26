#!/usr/bin/env python3
"""Convert CI JSON/text outputs into screenshot-style HTML dashboards."""
from __future__ import annotations

import csv
import html
import json
import sys
from collections import Counter
from datetime import datetime, timezone
from pathlib import Path


CSS = """
:root { --bg:#0f172a; --card:#1e293b; --text:#e2e8f0; --muted:#94a3b8;
  --ok:#22c55e; --warn:#eab308; --bad:#ef4444; --info:#38bdf8; --border:#334155; }
* { box-sizing:border-box; }
body { margin:0; font-family:Segoe UI,system-ui,sans-serif; background:var(--bg); color:var(--text); }
header { padding:24px 32px; border-bottom:1px solid var(--border);
  background:linear-gradient(120deg,#0f172a,#1e3a5f); }
header h1 { margin:0 0 6px; font-size:1.6rem; }
header p { margin:0; color:var(--muted); }
main { padding:24px 32px 48px; max-width:1200px; margin:0 auto; }
.grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(180px,1fr)); gap:14px; margin:18px 0 28px; }
.card { background:var(--card); border:1px solid var(--border); border-radius:12px; padding:16px; }
.card .n { font-size:1.8rem; font-weight:700; }
.card .l { color:var(--muted); font-size:.85rem; margin-top:4px; }
.ok .n { color:var(--ok); } .warn .n { color:var(--warn); } .bad .n { color:var(--bad); } .info .n { color:var(--info); }
table { width:100%; border-collapse:collapse; font-size:.9rem; }
th,td { text-align:left; padding:10px 12px; border-bottom:1px solid var(--border); vertical-align:top; }
th { color:var(--muted); font-weight:600; }
tr:hover td { background:rgba(148,163,184,.08); }
.sev { display:inline-block; padding:2px 8px; border-radius:999px; font-size:.75rem; font-weight:600; }
.sev-CRITICAL,.sev-HIGH,.sev-error,.sev-ERROR { background:rgba(239,68,68,.2); color:#fca5a5; }
.sev-MEDIUM,.sev-warning,.sev-WARNING { background:rgba(234,179,8,.2); color:#fde047; }
.sev-LOW,.sev-info,.sev-INFO,.sev-UNKNOWN { background:rgba(56,189,248,.15); color:#7dd3fc; }
.links a { color:var(--info); margin-right:14px; }
pre { white-space:pre-wrap; word-break:break-word; background:#0b1220; padding:12px; border-radius:8px; }
.muted { color:var(--muted); }
"""


def page(title: str, body: str, subtitle: str = "") -> str:
    now = datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC")
    return f"""<!DOCTYPE html>
<html lang="zh-CN"><head><meta charset="utf-8"/><meta name="viewport" content="width=device-width,initial-scale=1"/>
<title>{html.escape(title)}</title><style>{CSS}</style></head>
<body><header><h1>{html.escape(title)}</h1>
<p>{html.escape(subtitle or "Warehouse CI report")} · generated {now}</p></header>
<main>{body}</main></body></html>"""


def write(path: Path, title: str, body: str, subtitle: str = "") -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(page(title, body, subtitle), encoding="utf-8")
    print(f"wrote {path}")


def load_json(path: Path):
    if not path.exists():
        return None
    try:
        return json.loads(path.read_text(encoding="utf-8", errors="ignore"))
    except json.JSONDecodeError:
        return None


def sast_html(out: Path, root: Path) -> None:
    gitleaks = load_json(root / "gitleaks.json")
    sj = load_json(root / "semgrep-java.json") or {}
    ss = load_json(root / "semgrep-js.json") or {}

    leaks = gitleaks if isinstance(gitleaks, list) else []
    java_findings = (sj.get("results") or []) if isinstance(sj, dict) else []
    js_findings = (ss.get("results") or []) if isinstance(ss, dict) else []

    body = f"""
    <div class="grid">
      <div class="card {'bad' if leaks else 'ok'}"><div class="n">{len(leaks)}</div><div class="l">Gitleaks secrets</div></div>
      <div class="card {'warn' if java_findings else 'ok'}"><div class="n">{len(java_findings)}</div><div class="l">Semgrep Java</div></div>
      <div class="card {'warn' if js_findings else 'ok'}"><div class="n">{len(js_findings)}</div><div class="l">Semgrep JS/Vue</div></div>
      <div class="card info"><div class="n">{len(leaks)+len(java_findings)+len(js_findings)}</div><div class="l">Total findings</div></div>
    </div>
    <div class="card"><h2>Gitleaks</h2>
    <table><tr><th>Rule</th><th>File</th><th>Secret</th></tr>
    {''.join(f"<tr><td>{html.escape(str(x.get('RuleID','')))}</td><td>{html.escape(str(x.get('File','')))}:{x.get('StartLine','')}</td><td class='muted'>{html.escape(str(x.get('Description') or x.get('Match','')[:80]))}</td></tr>" for x in leaks[:200]) or '<tr><td colspan=3 class=muted>No secrets found</td></tr>'}
    </table></div>
    <div class="card" style="margin-top:16px"><h2>Semgrep Java</h2>
    <table><tr><th>Severity</th><th>Check</th><th>Path</th><th>Message</th></tr>
    {''.join(_semgrep_row(r) for r in java_findings[:300]) or '<tr><td colspan=4 class=muted>No findings</td></tr>'}
    </table></div>
    <div class="card" style="margin-top:16px"><h2>Semgrep JavaScript / Vue</h2>
    <table><tr><th>Severity</th><th>Check</th><th>Path</th><th>Message</th></tr>
    {''.join(_semgrep_row(r) for r in js_findings[:300]) or '<tr><td colspan=4 class=muted>No findings</td></tr>'}
    </table></div>
    """
    write(out / "sast-summary.html", "SAST Security Scan Summary", body, "Gitleaks + Semgrep")


def _semgrep_row(r: dict) -> str:
    sev = str((r.get("extra") or {}).get("severity") or r.get("severity") or "INFO").upper()
    check = html.escape(str(r.get("check_id", "")))
    path = html.escape(f"{r.get('path','')}:{((r.get('start') or {}).get('line') or '')}")
    msg = html.escape(str((r.get("extra") or {}).get("message") or r.get("message") or "")[:200])
    return f"<tr><td><span class='sev sev-{html.escape(sev)}'>{html.escape(sev)}</span></td><td>{check}</td><td>{path}</td><td>{msg}</td></tr>"


def trivy_html(out: Path, json_path: Path, title: str) -> None:
    """Screenshot-style Trivy report (purple header + severity counters)."""
    data = load_json(json_path) or {}
    results = data.get("Results") or []
    rows = []
    counts = Counter()
    for block in results:
        target = block.get("Target", "")
        for v in block.get("Vulnerabilities") or []:
            sev = (v.get("Severity") or "UNKNOWN").upper()
            counts[sev] += 1
            rows.append(
                "<tr>"
                f"<td><a href='https://nvd.nist.gov/vuln/detail/{html.escape(v.get('VulnerabilityID',''))}'>"
                f"{html.escape(v.get('VulnerabilityID',''))}</a></td>"
                f"<td><span class='pill pill-{html.escape(sev)}'>{html.escape(sev)}</span></td>"
                f"<td><b>{html.escape(v.get('PkgName',''))}</b></td>"
                f"<td>{html.escape(str(v.get('InstalledVersion') or v.get('PkgPath') or ''))}</td>"
                f"<td>{html.escape((v.get('Title') or v.get('Description') or '')[:160])}</td>"
                "</tr>"
            )
        for m in block.get("Misconfigurations") or []:
            sev = (m.get("Severity") or "UNKNOWN").upper()
            counts[sev] += 1
            rows.append(
                "<tr>"
                f"<td>{html.escape(m.get('ID') or m.get('AVDID',''))}</td>"
                f"<td><span class='pill pill-{html.escape(sev)}'>{html.escape(sev)}</span></td>"
                f"<td><b>config</b></td><td>{html.escape(target)}</td>"
                f"<td>{html.escape((m.get('Title') or '')[:160])}</td></tr>"
            )

    total = sum(counts.values())
    css = """
    body{margin:0;font-family:Inter,Segoe UI,system-ui,sans-serif;background:#f5f7fb;color:#1f2937}
    .hero{background:linear-gradient(120deg,#4c1d95,#7c3aed 45%,#a855f7);color:#fff;padding:28px 32px}
    .hero h1{margin:0;font-size:1.6rem}.hero p{margin:6px 0 0;opacity:.9}
    .wrap{max-width:1100px;margin:-28px auto 40px;padding:0 20px}
    .panel{background:#fff;border-radius:16px;box-shadow:0 10px 30px rgba(15,23,42,.08);padding:18px 20px}
    .stats{display:grid;grid-template-columns:repeat(5,1fr);gap:12px;margin-bottom:18px}
    .stat{text-align:center;padding:14px 8px;border-radius:12px;background:#f8fafc;border:1px solid #e5e7eb}
    .stat .n{font-size:1.8rem;font-weight:800}.stat .l{font-size:.8rem;color:#6b7280;margin-top:4px}
    .c-total .n{color:#4c1d95}.c-CRITICAL .n{color:#dc2626}.c-HIGH .n{color:#ea580c}
    .c-MEDIUM .n{color:#ca8a04}.c-LOW .n{color:#16a34a}
    table{width:100%;border-collapse:collapse;font-size:.92rem}
    th{text-align:left;color:#6b7280;font-size:.78rem;letter-spacing:.04em;text-transform:uppercase;
       padding:10px 8px;border-bottom:1px solid #e5e7eb}
    td{padding:12px 8px;border-bottom:1px solid #f1f5f9;vertical-align:top}
    a{color:#6d28d9;text-decoration:none}
    .pill{display:inline-block;padding:3px 10px;border-radius:999px;font-size:.72rem;font-weight:700;color:#fff}
    .pill-CRITICAL{background:#dc2626}.pill-HIGH{background:#ea580c}
    .pill-MEDIUM{background:#ca8a04}.pill-LOW{background:#16a34a}.pill-UNKNOWN{background:#64748b}
    """
    body = f"""<!DOCTYPE html><html lang="en"><head><meta charset="utf-8"/><title>{html.escape(title)}</title>
    <style>{css}</style></head><body>
    <div class="hero"><h1>🔍 Trivy Security Scan Report</h1><p>{html.escape(str(json_path.name))}</p></div>
    <div class="wrap"><div class="panel">
      <div class="stats">
        <div class="stat c-total"><div class="n">{total}</div><div class="l">Total Vulnerabilities</div></div>
        <div class="stat c-CRITICAL"><div class="n">{counts.get('CRITICAL',0)}</div><div class="l">Critical</div></div>
        <div class="stat c-HIGH"><div class="n">{counts.get('HIGH',0)}</div><div class="l">High</div></div>
        <div class="stat c-MEDIUM"><div class="n">{counts.get('MEDIUM',0)}</div><div class="l">Medium</div></div>
        <div class="stat c-LOW"><div class="n">{counts.get('LOW',0)}</div><div class="l">Low</div></div>
      </div>
      <table><thead><tr><th>Vulnerability ID</th><th>Severity</th><th>Package</th><th>Version</th><th>Description</th></tr></thead>
      <tbody>{''.join(rows[:500]) or '<tr><td colspan=5>No vulnerabilities found</td></tr>'}</tbody></table>
    </div></div></body></html>"""
    out.mkdir(parents=True, exist_ok=True)
    path = out / f"{json_path.stem}.html"
    path.write_text(body, encoding="utf-8")
    print(f"wrote {path}")


def checkov_html(out: Path, json_path: Path, title: str) -> None:
    data = load_json(json_path)
    # Checkov JSON may be a list of reports or a single object
    reports = data if isinstance(data, list) else ([data] if isinstance(data, dict) else [])
    failed = []
    passed = 0
    for rep in reports:
        if not isinstance(rep, dict):
            continue
        results = rep.get("results") or {}
        failed.extend(results.get("failed_checks") or [])
        passed += len(results.get("passed_checks") or [])

    body = f"""
    <div class="grid">
      <div class="card ok"><div class="n">{passed}</div><div class="l">Passed</div></div>
      <div class="card {'bad' if failed else 'ok'}"><div class="n">{len(failed)}</div><div class="l">Failed</div></div>
    </div>
    <div class="card"><table>
      <tr><th>Check</th><th>Resource</th><th>File</th><th>Guideline</th></tr>
      {''.join(
        f"<tr><td>{html.escape(str(c.get('check_id','')))} {html.escape(str(c.get('check_name','')[:80]))}</td>"
        f"<td>{html.escape(str(c.get('resource','')[:80]))}</td>"
        f"<td>{html.escape(str(c.get('file_path','')))}:{c.get('file_line_range',[''])[0] if c.get('file_line_range') else ''}</td>"
        f"<td class='muted'>{html.escape(str(c.get('guideline') or '')[:100])}</td></tr>"
        for c in failed[:400]
      ) or '<tr><td colspan=4 class=muted>No failed checks</td></tr>'}
    </table></div>"""
    write(out / f"{json_path.stem}.html", title, body, str(json_path.name))


def unit_html(out: Path, surefire_dir: Path) -> None:
    xmls: list[Path] = []
    if surefire_dir.exists():
        xmls = sorted(surefire_dir.glob("TEST-*.xml"))
        if not xmls:
            xmls = sorted(surefire_dir.rglob("TEST-*.xml"))
    tests = fails = errors = skipped = 0
    rows = []
    import re
    from xml.etree import ElementTree as ET

    def _attr_int(el: ET.Element, *names: str) -> int:
        for n in names:
            v = el.attrib.get(n)
            if v is not None and str(v).strip() != "":
                try:
                    return int(float(v))
                except ValueError:
                    pass
        return 0

    for xp in xmls:
        t = f = e = s = 0
        parsed = False
        try:
            root = ET.parse(xp).getroot()
            # root may be testsuite, or testsuites wrapping suites
            suites = [root] if root.tag.endswith("testsuite") else []
            suites.extend([c for c in root if c.tag.endswith("testsuite")])
            if not suites and root.tag.endswith("testsuites"):
                suites = [c for c in root if c.tag.endswith("testsuite")]
            for suite in suites or [root]:
                t += _attr_int(suite, "tests")
                f += _attr_int(suite, "failures", "failure")
                e += _attr_int(suite, "errors", "error")
                s += _attr_int(suite, "skipped", "disabled")
                parsed = True
        except Exception:
            parsed = False
        if not parsed:
            text = xp.read_text(encoding="utf-8", errors="ignore")
            # attribute order varies across Surefire versions — read each attr alone
            def one(name: str) -> int:
                m = re.search(rf'\b{name}="(\d+)"', text)
                return int(m.group(1)) if m else 0

            t, f, e, s = one("tests"), one("failures"), one("errors"), one("skipped")
        tests += t
        fails += f
        errors += e
        skipped += s
        status = "FAIL" if f or e else "OK"
        rows.append(
            f"<tr><td>{html.escape(xp.stem.replace('TEST-', ''))}</td>"
            f"<td>{t}</td><td>{f}</td><td>{e}</td><td>{s}</td>"
            f"<td><span class='sev sev-{'ERROR' if status == 'FAIL' else 'INFO'}'>{status}</span></td></tr>"
        )
    # fallback: parse Maven surefire console summary from mvn-test.log
    if not xmls:
        log = out / "mvn-test.log"
        if log.exists():
            text = log.read_text(encoding="utf-8", errors="ignore")
            # last "Tests run: N, Failures: N, Errors: N, Skipped: N"
            matches = re.findall(
                r"Tests run:\s*(\d+),\s*Failures:\s*(\d+),\s*Errors:\s*(\d+),\s*Skipped:\s*(\d+)",
                text,
            )
            if matches:
                t, f, e, s = map(int, matches[-1])
                tests, fails, errors, skipped = t, f, e, s
                rows.append(
                    f"<tr><td>mvn-test.log (summary)</td><td>{t}</td><td>{f}</td><td>{e}</td><td>{s}</td>"
                    f"<td><span class='sev sev-INFO'>LOG</span></td></tr>"
                )
    body = f"""
    <div class="grid">
      <div class="card info"><div class="n">{tests}</div><div class="l">Tests</div></div>
      <div class="card {'bad' if fails else 'ok'}"><div class="n">{fails}</div><div class="l">Failures</div></div>
      <div class="card {'bad' if errors else 'ok'}"><div class="n">{errors}</div><div class="l">Errors</div></div>
      <div class="card warn"><div class="n">{skipped}</div><div class="l">Skipped</div></div>
    </div>
    <div class="card"><table>
      <tr><th>Suite</th><th>Tests</th><th>Failures</th><th>Errors</th><th>Skipped</th><th>Status</th></tr>
      {''.join(rows) or '<tr><td colspan=6 class=muted>No surefire XML found</td></tr>'}
    </table></div>"""
    write(out / "unit-summary.html", "Unit Test Summary", body, "Maven Surefire")


def k6_html(out: Path, raw_path: Path) -> None:
    samples = []
    failed = 0
    if raw_path.exists():
        for line in raw_path.read_text(encoding="utf-8", errors="ignore").splitlines():
            if not line.strip():
                continue
            try:
                obj = json.loads(line)
            except json.JSONDecodeError:
                continue
            if obj.get("type") != "Point":
                continue
            m = obj.get("metric")
            v = obj.get("metric_value")
            if v is None:
                v = obj.get("value")
            if v is None and isinstance(obj.get("data"), dict):
                v = obj["data"].get("value")
            try:
                v = float(v)
            except (TypeError, ValueError):
                continue
            if m == "http_req_duration":
                samples.append(v)
            elif m == "http_req_failed" and v > 0:
                failed += 1

    def pct(q: float) -> float:
        if not samples:
            return 0.0
        s = sorted(samples)
        idx = int(round((len(s) - 1) * q))
        return s[max(0, min(len(s) - 1, idx))]

    avg = (sum(samples) / len(samples)) if samples else 0.0
    body = f"""
    <div class="grid">
      <div class="card info"><div class="n">{len(samples)}</div><div class="l">Samples</div></div>
      <div class="card info"><div class="n">{avg:.1f}</div><div class="l">Avg ms</div></div>
      <div class="card info"><div class="n">{pct(0.5):.1f}</div><div class="l">p50 ms</div></div>
      <div class="card warn"><div class="n">{pct(0.95):.1f}</div><div class="l">p95 ms</div></div>
      <div class="card {'bad' if failed else 'ok'}"><div class="n">{failed}</div><div class="l">Failed points</div></div>
    </div>
    <div class="card"><p class="muted">Source: {html.escape(str(raw_path))}</p>
    <pre>avg={avg:.2f} ms
p50={pct(0.5):.2f} ms
p95={pct(0.95):.2f} ms
max={max(samples) if samples else 0:.2f} ms
http_req_failed_points={failed}</pre></div>"""
    write(out / "k6-summary.html", "K6 Performance Test Summary", body)


def jmeter_html(out: Path, jtl_path: Path) -> None:
    rows = []
    if jtl_path.exists():
        with jtl_path.open("r", encoding="utf-8", errors="ignore", newline="") as f:
            reader = csv.DictReader(f)
            for r in reader:
                try:
                    rows.append(float(r.get("elapsed", "0")))
                except ValueError:
                    pass
    def pct(q: float) -> float:
        if not rows:
            return 0.0
        s = sorted(rows)
        idx = int(round((len(s) - 1) * q))
        return s[max(0, min(len(s) - 1, idx))]
    avg = (sum(rows) / len(rows)) if rows else 0.0
    body = f"""
    <div class="grid">
      <div class="card info"><div class="n">{len(rows)}</div><div class="l">Samples</div></div>
      <div class="card info"><div class="n">{avg:.1f}</div><div class="l">Avg ms</div></div>
      <div class="card info"><div class="n">{pct(0.5):.1f}</div><div class="l">p50 ms</div></div>
      <div class="card warn"><div class="n">{pct(0.95):.1f}</div><div class="l">p95 ms</div></div>
      <div class="card info"><div class="n">{max(rows) if rows else 0:.1f}</div><div class="l">Max ms</div></div>
    </div>
    <div class="card"><p class="muted">Open JMeter HTML dashboard (if generated): <code>jmeter-dashboard/index.html</code></p>
    <pre>samples={len(rows)}
avg={avg:.2f} ms
p50={pct(0.5):.2f} ms
p95={pct(0.95):.2f} ms
max={max(rows) if rows else 0:.2f} ms</pre></div>"""
    write(out / "jmeter-summary.html", "JMeter Performance Test Summary", body)


def index_html(out: Path, links: list[tuple[str, str, str]]) -> None:
    items = "".join(
        f"<tr><td>{html.escape(stage)}</td><td><a href='{html.escape(href)}'>{html.escape(label)}</a></td>"
        f"<td class='muted'>{html.escape(note)}</td></tr>"
        for stage, label, href, note in ((a[0], a[1], a[2], a[3] if len(a) > 3 else "") for a in links)
    )
    # fix unpacking - links are tuples of 4
    items = ""
    for item in links:
        stage, label, href, note = item if len(item) == 4 else (*item, "")
        items += (
            f"<tr><td>{html.escape(stage)}</td>"
            f"<td><a href='{html.escape(href)}'>{html.escape(label)}</a></td>"
            f"<td class='muted'>{html.escape(note)}</td></tr>"
        )
    body = f"""
    <div class="card">
      <p>下载 Artifact <b>ci-html-dashboard</b> 后，用浏览器打开本文件即可查看与参考文档类似的报表页面。</p>
      <table>
        <tr><th>阶段</th><th>报告</th><th>说明</th></tr>
        {items or '<tr><td colspan=3 class=muted>No reports linked</td></tr>'}
      </table>
    </div>"""
    write(out / "index.html", "Warehouse CI Report Dashboard", body, "DevSecOps HTML reports")


def main(argv: list[str]) -> int:
    if len(argv) < 2:
        print("usage: generate-html-reports.py <command> ...")
        return 2
    cmd = argv[1]
    if cmd == "sast":
        sast_html(Path(argv[2]), Path(argv[3]))
    elif cmd == "trivy":
        trivy_html(Path(argv[2]), Path(argv[3]), argv[4] if len(argv) > 4 else "Trivy Report")
    elif cmd == "checkov":
        checkov_html(Path(argv[2]), Path(argv[3]), argv[4] if len(argv) > 4 else "Checkov Report")
    elif cmd == "unit":
        unit_html(Path(argv[2]), Path(argv[3]))
    elif cmd == "k6":
        k6_html(Path(argv[2]), Path(argv[3]))
    elif cmd == "jmeter":
        jmeter_html(Path(argv[2]), Path(argv[3]))
    elif cmd == "index":
        # index <out_dir> then pairs: stage|label|href|note
        out = Path(argv[2])
        links = []
        for raw in argv[3:]:
            parts = raw.split("|", 3)
            while len(parts) < 4:
                parts.append("")
            links.append(tuple(parts))
        index_html(out, links)
    else:
        print("unknown command", cmd)
        return 2
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
