# GitHub CI/CD 组件与配置步骤（本仓库）

按 `CICD参考(1).docx` 的 DevSecOps 结构落地到本仓库（Spring Boot + Vue + MySQL + K8s）。

## 流水线总览

```text
CI (.github/workflows/cicd.yml)
  sast → unit → integration → container_iac_scan → dast → performance

CD (.github/workflows/deploy-aliyun.yml)
  gate → build → import → deploy-infrastructure → deploy-services → deploy-gateway → verify
```

触发：

- **CI**：`push` / `pull_request` 到 `master|main`，或手动 `workflow_dispatch`
- **CD**：CI 在 `master|main` 成功后自动触发，或手动 `workflow_dispatch`

---

## 一、用到的组件清单

### 平台

| 组件 | 用途 |
|------|------|
| GitHub Actions | 编排 CI/CD |
| Ubuntu runners | 执行环境 |
| Docker | 构建/运行应用与扫描目标 |
| Kubernetes + kubectl | 生产部署 |
| containerd `ctr`（可选） | 镜像导入 K8s 运行时 |
| appleboy/ssh-action | CD 通过 SSH 操作阿里云主机 |
| 阿里云 ECS / K8s | 生产环境 |

### CI 工具（相对参考文档的映射）

| 文档阶段 | 文档组件 | 本仓库组件 |
|----------|----------|------------|
| SAST | Gitleaks, Bandit, Semgrep, pip-audit | **Gitleaks** + **Semgrep**(Java/JS)；无 Bandit/pip-audit（非 Python） |
| Unit | Jest / pytest | **Maven** `mvn test`（Java 8） |
| Integration | Compose + Newman + Playwright | **Docker MySQL+App** + **Newman**（`tests/postman`） |
| Container/IaC | Trivy + Checkov | **Trivy**（fs+image）+ **Checkov**（`k8s/`、Dockerfile） |
| DAST | OWASP ZAP | **OWASP ZAP** baseline |
| Performance | k6 + JMeter | **k6**（`tests/k6/smoke.js`）+ **JMeter**（`tests/jmeter`） |

### CD 阶段

| 阶段 | 做什么 |
|------|--------|
| build | 主机上 `git pull` + `docker build` app/frontend |
| import | 有 `ctr` 则导入 containerd；否则跳过 |
| deploy-infrastructure | `namespace.yaml` + `mysql.yaml` |
| deploy-services | `app.yaml` / `frontend.yaml` + `kubectl set image` |
| deploy-gateway | `ingress.yaml` |
| verify | `kubectl get pods/svc/ingress/deploy` + Summary |

---

## 二、GitHub 配置步骤

### 1. 推送工作流文件

确保仓库含有：

- `.github/workflows/cicd.yml`
- `.github/workflows/deploy-aliyun.yml`
- `k8s/*.yaml`、`Dockerfile`、`deploy/frontend-ci.Dockerfile`
- `tests/postman`、`tests/k6`、`tests/jmeter`

### 2. 配置 Secrets（Settings → Secrets and variables → Actions）

| Name | 说明 |
|------|------|
| `ALIYUN_HOST` | 部署机公网 IP / 域名 |
| `ALIYUN_USER` | SSH 用户（如 `root`） |
| `ALIYUN_SSH_KEY` | **私钥** PEM 全文（含 `BEGIN … PRIVATE KEY`） |
| `ALIYUN_SSH_KEY_PASSPHRASE` | 私钥口令（无则可不建） |

### 3. 配置 Variables（Settings → Variables → Actions）

| Name | 示例 | 说明 |
|------|------|------|
| `ALIYUN_DEPLOY_PATH` | `/opt/deploy/warehouse-management-project` | 主机上的 git 克隆目录 |
| `K8S_DIR` | `/opt/deploy/warehouse-management-project/k8s` | 清单目录（可省略，默认 `${DEPLOY_PATH}/k8s`） |
| `K8S_NAMESPACE` | `warehouse` | 命名空间 |
| `ALIYUN_SSH_PORT` | `22` | SSH 端口 |
| `ALIYUN_GIT_SSH_IDENTITY_FILE` | `/root/.ssh/github_repo_clone` | 主机用 SSH 拉 GitHub 时的密钥路径 |

### 4. 阿里云主机一次性准备

```bash
# 安装 docker、kubectl，并能访问集群
sudo mkdir -p /opt/deploy
sudo git clone https://github.com/<org>/<repo>.git /opt/deploy/warehouse-management-project
# 私有库：HTTPS+PAT，或 Deploy Key + 设置 ALIYUN_GIT_SSH_IDENTITY_FILE
```

确认：

```bash
docker version
kubectl get ns
ls /opt/deploy/warehouse-management-project/k8s
```

### 5. 分支保护（Settings → Branches）

对 `master` / `main`：

1. Require a pull request before merging（建议）
2. Require status checks to pass：至少勾选 **sast**、**unit**、**integration**、**dast**
3. （可选）勾选 **container_iac_scan**、**performance**

### 6. 首次验证

1. Actions → **Cicd** → Run workflow  
2. 查看各 job Artifacts（`sast-reports`、`integration-reports`、`container-iac-reports`、`dast-zap-reports`、`performance-reports`）  
3. CI 通过后看 **Deploy Aliyun** 是否自动跑；也可手动 Run workflow  
4. 主机上：`kubectl get pods -n warehouse`

### 7. 安全收紧（建议后续）

- 把 `application.yml` 中的明文 API Key 改为仅环境变量 / Secrets  
- Gitleaks 从 `--exit-code 0` 改为 `1`（阻断泄露）  
- Semgrep / Trivy / Checkov 按团队策略逐步改为硬失败  

---

## 三、与参考文档的差异（刻意适配）

| 参考文档 | 本仓库 |
|----------|--------|
| Django / React / PostgreSQL / Redis | Spring Boot / Vue / MySQL |
| Bandit、pip-audit、npm audit、Playwright | 未启用（栈不符或后续可加） |
| `jsrgzyc/*` 镜像名 | `warehouse-app` / `warehouse-frontend` |
| namespace `mywork` | `warehouse` |
| 多微服务 | 单体 app + frontend + mysql |

---

## 五、如何查看「像参考文档截图」的报表

### A. GitHub Artifact
跑完 **Cicd** → Artifacts → **`ci-html-dashboard`** → 打开 `index.html`。

### B. 服务器（推荐，长期可看）
先按 `deploy/server-tools/README.md` 部署 Sonar + 报表 Nginx，并设置：
- Variables：`SONAR_ENABLED=true`、`REPORTS_SYNC_ENABLED=true`
- Secrets：`SONAR_HOST_URL`、`SONAR_TOKEN`

然后访问：
- Sonar：`http://<SERVER>:9000/dashboard?id=warehouse-management`
- 报表：`http://<SERVER>:9080/reports/latest/`（Trivy / JaCoCo / ZAP / k6…）

| 页面 | 对应文档截图 |
|------|----------------|
| `app-image.html` 等 | Trivy Security Scan Report |
| `jacoco/index.html` | Coverage report |
| Sonar Web | 质量/覆盖率总览 |

---

## 七、Java 服务 Kubernetes 部署

详见 **[K8S-JAVA.md](./K8S-JAVA.md)**。

摘要：

```bash
chmod +x k8s/apply-java.sh
APP_IMAGE=warehouse-app:build.1 ./k8s/apply-java.sh
# 访问 http://<节点IP>:30081/springboot38hdw40x/
```

GitHub CD：`Deploy Aliyun` → `deploy-services` 对 `deployment/warehouse-app` 执行 `kubectl set image` + rollout。
