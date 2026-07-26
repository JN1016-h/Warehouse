#!/usr/bin/env python3
"""Generate auditable evidence pack covering 5 test types for CI dashboard."""
from __future__ import annotations

import html
import json
import sys
from datetime import datetime, timezone
from pathlib import Path


def write(path: Path, title: str, body: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    now = datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC")
    path.write_text(
        f"""<!DOCTYPE html><html lang="zh-CN"><head><meta charset="utf-8"/>
<title>{html.escape(title)}</title>
<style>
body{{font-family:Segoe UI,sans-serif;margin:0;background:#0b1220;color:#e2e8f0}}
header{{padding:1.4rem 1.8rem;background:linear-gradient(120deg,#0f172a,#1e3a5f);border-bottom:1px solid #334155}}
main{{padding:1.5rem 1.8rem 3rem;max-width:1100px;margin:0 auto}}
.card{{background:#1e293b;border:1px solid #334155;border-radius:12px;padding:1rem 1.2rem;margin:1rem 0}}
table{{width:100%;border-collapse:collapse}} th,td{{border-bottom:1px solid #334155;padding:.55rem .7rem;text-align:left;vertical-align:top}}
th{{color:#94a3b8}} a{{color:#38bdf8}} .ok{{color:#22c55e}} .warn{{color:#eab308}} code{{color:#fde68a}}
ul{{line-height:1.55}}
</style></head><body>
<header><h1>{html.escape(title)}</h1><p>Warehouse DevSecOps 五类测试证据包 · {now}</p></header>
<main>{body}</main></body></html>""",
        encoding="utf-8",
    )
    print(f"wrote {path}")


def exists(root: Path, *parts: str) -> bool:
    return (root.joinpath(*parts)).exists()


def link_row(stage: str, label: str, href: str, evidence: str, present: bool) -> str:
    status = '<span class="ok">有证据</span>' if present else '<span class="warn">缺失/待本次流水线</span>'
    return (
        f"<tr><td>{html.escape(stage)}</td><td><a href='{html.escape(href)}'>{html.escape(label)}</a></td>"
        f"<td>{html.escape(evidence)}</td><td>{status}</td></tr>"
    )


def perf_analysis(dashboard: Path) -> None:
    raw = dashboard / "k6-summary.html"
    jtl = dashboard / "jmeter-summary.html"
    body = f"""
    <div class="card">
      <h2>压力 / 负载 — 瓶颈分析与优化建议</h2>
      <ul>
        <li>工具：k6（阶梯加压 stress.js）+ Apache JMeter（warehouse-load.jmx）</li>
        <li>指标：p95/p99 延迟、错误率、吞吐（见下方报告与 JMeter Dashboard 曲线）</li>
        <li>常见瓶颈：DB 连接池、单实例 JVM 堆、同步接口、缺少缓存</li>
        <li>优化方案：连接池调优、只读副本、热点列表缓存、水平扩展 FE/API、异步订货</li>
      </ul>
      <p>报告链接：
        <a href="./k6-summary.html">k6 Summary</a> ·
        <a href="./jmeter-summary.html">JMeter Summary</a> ·
        <a href="./jmeter-dashboard/index.html">JMeter Charts</a>
      </p>
      <p class="warn">说明：k6/JMeter 原始日志与 JSON/JTL 随 Artifact <code>performance-reports</code> 留存，可供复测对比。</p>
    </div>
    <div class="card">
      <h3>本机文件检查</h3>
      <ul>
        <li>k6-summary.html: {'OK' if raw.exists() else 'MISSING'}</li>
        <li>jmeter-summary.html: {'OK' if jtl.exists() else 'MISSING'}</li>
        <li>jmeter-dashboard: {'OK' if (dashboard/'jmeter-dashboard'/'index.html').exists() else 'MISSING'}</li>
      </ul>
    </div>"""
    write(dashboard / "perf-analysis.html", "Stress & Load — Bottleneck Analysis", body)


def security_evidence(dashboard: Path) -> None:
    body = f"""
    <div class="card">
      <h2>安全测试证据（SAST / DAST / 容器 / IaC）</h2>
      <table>
        <tr><th>类型</th><th>工具</th><th>报告</th><th>状态</th></tr>
        {link_row('SAST','Gitleaks+Semgrep Summary','sast-summary.html','密钥泄露/代码缺陷分级', exists(dashboard,'sast-summary.html'))}
        {link_row('DAST','OWASP ZAP','zap-report.html','动态扫描 HTML', exists(dashboard,'zap-report.html'))}
        {link_row('容器','Trivy App Image','app-image.html','镜像 CVE 分级', exists(dashboard,'app-image.html'))}
        {link_row('容器','Trivy Frontend','frontend-image.html','前端镜像 CVE', exists(dashboard,'frontend-image.html'))}
        {link_row('依赖/FS','Trivy FS','fs.html','文件系统漏洞', exists(dashboard,'fs.html'))}
        {link_row('IaC','Checkov K8s','k8s.html','K8s 配置风险', exists(dashboard,'k8s.html'))}
        {link_row('质量门禁','SonarQube','sonar-summary.html','Bugs/Vulns/Hotspots/Coverage', exists(dashboard,'sonar-summary.html'))}
      </table>
    </div>
    <div class="card">
      <h3>漏洞修复 / 复测 / 风险缓解（核查清单）</h3>
      <ol>
        <li>在 Sonar / Trivy / ZAP 报告中记录 Critical/High 清单</li>
        <li>提交修复 PR（commit message 标明 CVE/规则 ID）</li>
        <li>CI 复跑：同一 job 报告对比（前后 Artifact）</li>
        <li>复测通过凭证：最新 <code>/reports/latest/</code> 与 Sonar Quality Gate Passed</li>
        <li>无法短期修复项：登记风险接受（Hotspot ACKNOWLEDGED / 变更单）</li>
      </ol>
      <p>在线 Sonar：<a href="http://121.40.253.17:9000/dashboard?id=warehouse-management">dashboard</a>
         · Hotspots：<a href="http://121.40.253.17:9000/security_hotspots?id=warehouse-management">security_hotspots</a></p>
    </div>"""
    write(dashboard / "security-evidence.html", "Security Testing Evidence", body)


def evidence_index(dashboard: Path) -> None:
    rows = [
        link_row("1. Unit", "单元测试摘要", "unit-summary.html", "Surefire 用例结果 + 执行统计", exists(dashboard, "unit-summary.html")),
        link_row("1. Unit", "JaCoCo 覆盖率", "jacoco/index.html", "语句/分支覆盖率报告", exists(dashboard, "jacoco", "index.html")),
        link_row("2. Integration", "Newman 接口报告", "newman.html", "跨模块 API 集成", exists(dashboard, "newman.html")),
        link_row("3. E2E", "用户旅程 E2E", "e2e-summary.html", "前后端真实流程 PASS/FAIL", exists(dashboard, "e2e-summary.html")),
        link_row("4. Stress/Load", "k6 性能摘要", "k6-summary.html", "延迟/错误率", exists(dashboard, "k6-summary.html")),
        link_row("4. Stress/Load", "JMeter 图表", "jmeter-dashboard/index.html", "吞吐/响应时间曲线", exists(dashboard, "jmeter-dashboard", "index.html")),
        link_row("4. Stress/Load", "瓶颈分析", "perf-analysis.html", "瓶颈与优化方案记录", exists(dashboard, "perf-analysis.html")),
        link_row("5. Security", "安全证据总表", "security-evidence.html", "SAST/DAST/容器/复测清单", True),
        link_row("5. Security", "SAST Summary", "sast-summary.html", "Gitleaks+Semgrep", exists(dashboard, "sast-summary.html")),
        link_row("5. Security", "ZAP DAST", "zap-report.html", "动态扫描", exists(dashboard, "zap-report.html")),
        link_row("5. Security", "Trivy App", "app-image.html", "镜像漏洞分级", exists(dashboard, "app-image.html")),
    ]
    body = f"""
    <div class="card">
      <p>本页为审计用<strong>五类测试完整证据索引</strong>。CI 每次成功后同步到
      <a href="/reports/latest/evidence-index.html">/reports/latest/</a>，并保留 run 号目录便于对比复测。</p>
      <table>
        <tr><th>类别</th><th>证据</th><th>说明</th><th>状态</th></tr>
        {''.join(rows)}
      </table>
    </div>
    <div class="card">
      <h3>流水线自动执行映射</h3>
      <ul>
        <li>Unit → job <code>unit</code>（mvn test + jacoco）</li>
        <li>Integration → job <code>integration</code>（MySQL+App+Newman）</li>
        <li>E2E → job <code>e2e</code>（FE+API user-journey.py）</li>
        <li>Stress/Load → job <code>performance</code>（k6 stress + JMeter）</li>
        <li>Security → jobs <code>sast</code> / <code>container_iac_scan</code> / <code>dast</code> / <code>sonar</code></li>
      </ul>
    </div>"""
    write(dashboard / "evidence-index.html", "五类测试证据包 Evidence Pack", body)
    meta = {
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "types": ["unit", "integration", "e2e", "performance", "security"],
    }
    (dashboard / "evidence-manifest.json").write_text(json.dumps(meta, indent=2), encoding="utf-8")


def main(argv: list[str]) -> int:
    if len(argv) < 2:
        print("usage: generate-evidence-pack.py <dashboard_dir>")
        return 2
    dashboard = Path(argv[1])
    dashboard.mkdir(parents=True, exist_ok=True)
    perf_analysis(dashboard)
    security_evidence(dashboard)
    evidence_index(dashboard)
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
