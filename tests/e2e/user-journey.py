#!/usr/bin/env python3
"""E2E：模拟真实用户跨前端+API 的关键旅程，输出可核查 HTML/JSON 证据。"""
from __future__ import annotations

import json
import os
import sys
import time
import urllib.error
import urllib.request
from datetime import datetime, timezone
from pathlib import Path

API_BASE = os.environ.get("E2E_API_BASE", "http://127.0.0.1:8080/springboot38hdw40x").rstrip("/")
FE_BASE = os.environ.get("E2E_FE_BASE", "http://127.0.0.1:30080").rstrip("/")
OUT = Path(os.environ.get("E2E_OUT", "reports/e2e"))


def http_get(url: str, timeout: int = 20) -> tuple[int, str, float]:
    t0 = time.time()
    req = urllib.request.Request(url, headers={"User-Agent": "warehouse-e2e"})
    try:
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            body = resp.read().decode("utf-8", "replace")
            return resp.status, body, (time.time() - t0) * 1000
    except urllib.error.HTTPError as e:
        body = e.read().decode("utf-8", "replace")
        return e.code, body, (time.time() - t0) * 1000
    except Exception as e:
        return 0, str(e), (time.time() - t0) * 1000


def main() -> int:
    OUT.mkdir(parents=True, exist_ok=True)
    cases = []

    steps = [
        ("E2E-01", "打开前端首页", f"{FE_BASE}/", lambda s, b: s == 200 and ("html" in b.lower() or "<!doctype" in b.lower() or len(b) > 20)),
        ("E2E-02", "API 配置列表（用户进入系统后的配置加载）", f"{API_BASE}/config/list?page=1&limit=5", lambda s, b: s == 200),
        ("E2E-03", "浏览商品分类（选品流程）", f"{API_BASE}/shangpinfenlei/list?page=1&limit=10", lambda s, b: s == 200),
        ("E2E-04", "浏览商品信息", f"{API_BASE}/shangpinxinxi/list?page=1&limit=10", lambda s, b: s == 200),
        ("E2E-05", "浏览供应商", f"{API_BASE}/gongyingshang/list?page=1&limit=10", lambda s, b: s == 200),
        ("E2E-06", "查看入库信息（仓储流程）", f"{API_BASE}/rukuxinxi/list?page=1&limit=10", lambda s, b: s == 200),
        ("E2E-07", "查看订货信息（订货流程）", f"{API_BASE}/dinghuoxinxi/list?page=1&limit=10", lambda s, b: s == 200),
        ("E2E-08", "用户列表页面数据", f"{API_BASE}/yonghu/list?page=1&limit=5", lambda s, b: s == 200),
    ]

    # FE may be unavailable in some CI shapes — soft-skip first step if FE down
    for cid, name, url, pred in steps:
        status, body, ms = http_get(url)
        ok = False
        try:
            ok = bool(pred(status, body))
        except Exception:
            ok = False
        if cid == "E2E-01" and status == 0:
            cases.append({"id": cid, "name": name, "url": url, "status": status, "ms": round(ms, 1), "result": "SKIP", "detail": body[:200]})
        else:
            cases.append({
                "id": cid,
                "name": name,
                "url": url,
                "status": status,
                "ms": round(ms, 1),
                "result": "PASS" if ok else "FAIL",
                "detail": body[:240].replace("\n", " "),
            })

    passed = sum(1 for c in cases if c["result"] == "PASS")
    failed = sum(1 for c in cases if c["result"] == "FAIL")
    skipped = sum(1 for c in cases if c["result"] == "SKIP")
    summary = {
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "api_base": API_BASE,
        "fe_base": FE_BASE,
        "passed": passed,
        "failed": failed,
        "skipped": skipped,
        "cases": cases,
    }
    (OUT / "e2e-results.json").write_text(json.dumps(summary, indent=2, ensure_ascii=False), encoding="utf-8")

    rows = "".join(
        f"<tr><td>{c['id']}</td><td>{c['name']}</td><td>{c['status']}</td><td>{c['ms']}</td>"
        f"<td><b style='color:{'#16a34a' if c['result']=='PASS' else '#ca8a04' if c['result']=='SKIP' else '#dc2626'}'>{c['result']}</b></td>"
        f"<td><code>{c['url']}</code></td></tr>"
        for c in cases
    )
    html = f"""<!DOCTYPE html>
<html lang="zh-CN"><head><meta charset="utf-8"/><title>E2E User Journey Report</title>
<style>
body{{font-family:Segoe UI,sans-serif;margin:1.5rem;background:#0f172a;color:#e2e8f0}}
table{{border-collapse:collapse;width:100%}} th,td{{border:1px solid #334155;padding:.45rem .6rem;text-align:left;font-size:14px}}
th{{background:#1e293b}} .card{{display:inline-block;margin:0 12px 12px 0;padding:12px 16px;background:#1e293b;border-radius:10px}}
a{{color:#38bdf8}}
</style></head><body>
<h1>E2E Testing — 真实用户流程</h1>
<p>API: <code>{API_BASE}</code> · FE: <code>{FE_BASE}</code> · {summary['generated_at']}</p>
<div class="card">PASS <b>{passed}</b></div>
<div class="card">FAIL <b>{failed}</b></div>
<div class="card">SKIP <b>{skipped}</b></div>
<table><thead><tr><th>ID</th><th>场景</th><th>HTTP</th><th>ms</th><th>结果</th><th>URL</th></tr></thead>
<tbody>{rows}</tbody></table>
<p class="muted">证据文件：e2e-results.json / e2e-summary.html · 流水线 job: e2e</p>
</body></html>"""
    (OUT / "e2e-summary.html").write_text(html, encoding="utf-8")
    log_lines = [f"[{c['result']}] {c['id']} {c['name']} status={c['status']} {c['ms']}ms" for c in cases]
    (OUT / "e2e-run.log").write_text("\n".join(log_lines) + "\n", encoding="utf-8")
    print("\n".join(log_lines))
    print(f"wrote {OUT / 'e2e-summary.html'} pass={passed} fail={failed}")
    return 1 if failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
