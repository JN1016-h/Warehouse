# 安全/质量工具：是否要对接到本仓库？如何 Docker 部署？

本仓库技术栈：**Spring Boot (Java) + Vue2 + MySQL + K8s**。  
参考文档里不少工具属于 **Django/React/Python**，不能原样照搬。

---

## 1. 结论先看

| 类别 | 要不要改业务代码？ | 怎么用？ |
|------|-------------------|----------|
| Gitleaks / Semgrep / Trivy / Checkov / ZAP / Newman / k6 / JMeter | **基本不用改业务代码** | 已在 `.github/workflows/cicd.yml` 里跑 |
| SonarQube | **不用改代码**；CI 上传分析结果 | **服务器 Docker/K8s 常驻**，CI 调 `SONAR_HOST_URL` |
| Maven / JaCoCo | `pom.xml` 已加 JaCoCo | CI `mvn test jacoco:report` |
| Bandit / pip-audit / flake8 / black / drf-spectacular | **不对接**（Python/Django） | 用 Sonar + Semgrep(Java) + 依赖扫描代替 |
| Jest / Playwright（React） | **不对接**（本前端是 Vue） | 可选以后加 Vue CLI / Cypress；当前用 Newman 接口冒烟 |
| ESLint / npm audit | **可选**（前端目录有 package.json） | 可加 CI job；非必须 |

**对接 = 写进 GitHub Actions（或调远程 Sonar）**，不是把 Bandit 嵌进 Java 工程。

---

## 2. 清单对照（文档工具 → 本仓库）

### SAST

| 文档工具 | 本仓库状态 | 说明 |
|----------|------------|------|
| **Gitleaks** | ✅ 已对接 CI | `curl` 下二进制 → `gitleaks detect` |
| **Bandit** | ❌ 不对接 | Bandit 只扫 **Python**。Java 用 **Semgrep p/java + Sonar** |
| **Semgrep** | ✅ 已对接 CI | `pip install semgrep`；扫 Java + JS/Vue |
| **Sonar** | ✅ 已对接（需开开关） | Variable `SONAR_ENABLED=true` + Secrets；分析结果推到服务器 Sonar |
| **pip-audit** | ❌ 不对接 | 无 `requirements.txt`。Java 可用 **OWASP Dependency-Check / Trivy fs**（Trivy 已扫） |

### 镜像与 IaC

| 工具 | 状态 | 说明 |
|------|------|------|
| **Trivy** | ✅ CI | 二进制安装；扫仓库 + `warehouse-app` / frontend 镜像 |
| **Checkov** | ✅ CI | `pip install checkov`；扫 `k8s/`、Dockerfile |

### DAST

| 工具 | 状态 | 说明 |
|------|------|------|
| **OWASP ZAP** | ✅ CI | `docker run ghcr.io/zaproxy/zaproxy` + `zap-baseline.py`（对本地起的 Java 服务） |

### 测试与质量

| 工具 | 状态 | 说明 |
|------|------|------|
| Jest / RTL / Playwright | ❌ | React 专用；本项目 Vue |
| **后端测试 Java** | ✅ | `mvn test` + **JaCoCo** |
| **Newman** | ✅ | `tests/postman/*.json` |
| ESLint / Prettier | ⚪ 可选 | 前端可后续加 |
| flake8 / black | ❌ | Python |
| npm audit | ⚪ 可选 | 可对 `admin` 前端跑 |
| pip-audit | ❌ | Python |

### 性能

| 工具 | 状态 |
|------|------|
| **k6** | ✅ `tests/k6/smoke.js` |
| **JMeter** | ✅ `tests/jmeter/warehouse-load.jmx` |

### 可观测 / API 文档

| 工具 | 状态 |
|------|------|
| drf-spectacular | ❌ Django。Java 可用 SpringDoc/Swagger（当前未强制） |

---

## 3. 两类部署方式（重要）

```text
┌─────────────────────────────────────────────────────────┐
│ A. CI 临时工具（GitHub Runner 每次安装/Docker 跑完即弃）   │
│    Gitleaks Semgrep Trivy Checkov ZAP Newman k6 JMeter   │
│    → 已在 cicd.yml，一般不需要你在服务器 docker 常驻      │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ B. 服务器常驻（Docker Compose）                           │
│    MySQL + Java App(:8080) + SonarQube(:9000)            │
│    + 报表 Nginx(:9080) + Harbor(:80，独立安装)             │
│    → deploy/server-tools/docker-compose.yml              │
└─────────────────────────────────────────────────────────┘
```

生产 Java 也可走 **K8s**（`k8s/app.yaml`）；扫描类工具仍以 GitHub Actions 为主。

---

## 4. Docker 部署（常驻组件）

### 4.1 Sonar + 报表站

```bash
# 集群节点或专用机
cd /opt/deploy/warehouse-management-project/deploy/server-tools
cp .env.example .env
sudo sysctl -w vm.max_map_count=262144
chmod +x bootstrap.sh && ./bootstrap.sh
```

| 容器 | 端口 | 用途 |
|------|------|------|
| `warehouse-sonarqube` | 9000 | 代码质量 / 覆盖率 UI |
| `warehouse-sonar-db` | 内部 | Sonar DB |
| `warehouse-ci-reports` | 9080 | CI HTML（Trivy/JaCoCo/ZAP…） |

GitHub：

```text
Secrets:  SONAR_HOST_URL=http://<IP>:9000
          SONAR_TOKEN=<Sonar里生成>
Variables: SONAR_ENABLED=true
           REPORTS_SYNC_ENABLED=true
           REPORTS_REMOTE_DIR=/opt/ci-reports
```

### 4.2 本地用 Docker 跑「单次扫描」（可选，不必上服务器）

```bash
# Trivy 扫镜像
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy:0.70.0 image warehouse-app:latest

# Checkov 扫 k8s
docker run --rm -v "$PWD:/src" bridgecrew/checkov -d /src/k8s

# ZAP baseline（目标需可达）
docker run --rm --network host -v "$PWD/reports:/zap/wrk" \
  ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t http://127.0.0.1:8080/springboot38hdw40x/config/list?page=1&limit=1 -r zap.html
```

CI 里已经自动做这些，服务器一般**只常驻 Sonar + 报表 Nginx**。

### 4.3 Java 业务服务 Docker / K8s

```bash
# 仅本地
docker compose --env-file .env.docker up -d --build

# 生产 Java → K8s
./k8s/apply-java.sh
# 或 GitHub Actions: Deploy Aliyun
```

---

## 5. 你需要做的最少步骤

1. **推代码**，跑 GitHub **Cicd** → 已含 Gitleaks/Semgrep/Trivy/Checkov/ZAP/Newman/k6/JMeter。  
2. **服务器** `deploy/server-tools/bootstrap.sh` 起 Sonar + 报表。  
3. 配 `SONAR_*` / `REPORTS_*` → 再跑 Cicd，浏览器看 Sonar 与 `:9080/reports/latest/`。  
4. **不要**装 Bandit/pip-audit/drf-spectacular 进本 Java 项目。

---

## 6. 文档入口

| 文档 | 内容 |
|------|------|
| `deploy/CICD-GITHUB.md` | GitHub Secrets / 流水线 |
| `deploy/server-tools/README.md` | Sonar Docker 部署 |
| `deploy/K8S-JAVA.md` | Java K8s 部署 |
| `.github/workflows/cicd.yml` | 工具实际调用处 |
