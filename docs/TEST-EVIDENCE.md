# 五类测试证据清单（可核查）

本仓库通过 GitHub Actions workflow **Cicd** 自动执行并留存证据；发布目录：

- http://121.40.253.17:9080/reports/latest/evidence-index.html
- 历史 run：`/opt/ci-reports/<run_number>/`

## 1. 单元测试 Unit Testing

| 证据 | 位置 |
|------|------|
| 用例源码 | `src/test/java/**` |
| 执行 | CI job `unit`：`mvn test jacoco:report` |
| 结果 HTML | `unit-summary.html` |
| 覆盖率 | `jacoco/index.html` |
| 日志 | Actions 日志 + Artifact `unit-reports` |

## 2. 集成测试 Integration Testing

| 证据 | 位置 |
|------|------|
| 用例 | `tests/postman/warehouse.postman_collection.json`（跨模块 API + pm.test） |
| 执行 | CI job `integration`：MySQL + App + Newman |
| 报告 | `newman.html` |
| Artifact | `integration-reports` |

## 3. 端到端测试 E2E Testing

| 证据 | 位置 |
|------|------|
| 脚本 | `tests/e2e/user-journey.py`（前端首页 → 分类/商品/入库/订货流程） |
| 执行 | CI job `e2e` |
| 报告 | `e2e-summary.html` / `e2e-results.json` / `e2e-run.log` |
| Artifact | `e2e-reports` |

## 4. 压力 / 负载测试 Stress & Load Testing

| 证据 | 位置 |
|------|------|
| k6 脚本 | `tests/k6/smoke.js`、`tests/k6/stress.js`（阶梯加压） |
| JMeter | `tests/jmeter/warehouse-load.jmx` |
| 执行 | CI job `performance` |
| 曲线/指标 | `k6-summary.html`、`jmeter-dashboard/index.html` |
| 瓶颈分析 | `perf-analysis.html` |
| Artifact | `performance-reports` |

## 5. 安全测试 Security Testing

| 证据 | 位置 |
|------|------|
| SAST | Gitleaks + Semgrep → `sast-summary.html` |
| DAST | OWASP ZAP → `zap-report.html` |
| 容器/IaC | Trivy + Checkov → `app-image.html` / `fs.html` / `k8s.html` |
| 质量门禁 | SonarQube → `sonar-summary.html` + http://121.40.253.17:9000 |
| 汇总与复测清单 | `security-evidence.html` |
| 修复提交 | Git commit / PR（标明 CVE 或规则 ID） |
| 复测凭证 | 新一次 CI 的 `/reports/latest/` 与 Sonar Quality Gate |

## 审计入口

打开 **evidence-index.html** 即可一页核查五类证据是否齐全。
