# 服务器 121.40.253.17 — Docker + K8s（k3s）已装

## 当前状态（已安装）

| 组件 | 版本/说明 |
|------|-----------|
| OS | Ubuntu 24.04.4 LTS，8C / 29G / 148G |
| Docker | 29.6.2 + Compose v5.3.1 |
| Kubernetes | **k3s** v1.36.2+k3s1（单节点 control-plane） |
| 运行时 | containerd（k3s）；镜像构建用 Docker |
| kubectl | `/usr/local/bin/kubectl`（k3s 自带） |
| Harbor | **v2.12.2**（docker compose，目录 `/opt/harbor`，数据 `/data/harbor`） |
| Java 中间件 | MySQL `:3306` + App `:8080`（`deploy/server-tools`） |
| Sonar / 报表 | SonarQube `:9000` + Nginx `:9080`（同目录） |

### DevSecOps 与中间件

| 类别 | 工具 | 跑在哪 |
|------|------|--------|
| SAST | Gitleaks、Semgrep(Java/JS) | GitHub Actions |
| Unit | Maven（Java 8）+ JaCoCo | GitHub Actions |
| Integration | Docker MySQL+App、Newman | CI；服务器也常驻 MySQL+App |
| Container/IaC | Trivy、Checkov | GitHub Actions（可选 `scan-once.sh`） |
| DAST | OWASP ZAP | GitHub Actions |
| Perf | k6、JMeter | GitHub Actions |
| Sonar | SonarQube | **服务器** `:9000` |
| 报表站 | Nginx | **服务器** `:9080` |

代码目录：`/opt/deploy/warehouse-management-project`  
启动：`cd .../deploy/server-tools && ./bootstrap.sh`

### Harbor（私有镜像仓库）

- UI / API：`http://121.40.253.17/`（占用宿主机 **80**）
- 账号：`admin` / 初始密码 `Harbor12345`（登录后请立刻修改）
- 项目：`warehouse`（CD 推送目标）
- 部署链路：**代码上传 → docker build → push Harbor → k3s pull**（见 `deploy/CICD-K8S.md`）

```bash
docker login 121.40.253.17
# CD / apply-stack 会推送：
# 121.40.253.17/warehouse/warehouse-app:build.<N>
# 121.40.253.17/warehouse/warehouse-frontend:build.<N>
```

导入本地构建镜像到 k3s（旧方式，已不推荐；现用 Harbor pull）：

```bash
docker build -t warehouse-app:build.1 -f Dockerfile .
k8s-import-image warehouse-app:build.1
# 等价：docker save warehouse-app:build.1 | k3s ctr images import -
```

## 部署 Java 服务

```bash
# 把代码拉到服务器后
cd /opt/deploy/warehouse-management-project   # 按实际路径
chmod +x k8s/apply-java.sh
# 注意：apply-java.sh 里若用 ctr，请改成 k3s ctr 或先执行 k8s-import-image
APP_IMAGE=warehouse-app:build.1 ./k8s/apply-java.sh
```

访问（安全组需放行）：

- 前端 K8s：`http://121.40.253.17:30080/`
- 后端 K8s：`http://121.40.253.17:30081/springboot38hdw40x/`
- Docker 调试后端（非 K8s）：`http://121.40.253.17:8080/springboot38hdw40x/`

## GitHub CD → k3s（前后端）

详见 **`deploy/CICD-K8S.md`**。

流水线：`Cicd` 成功 → `Deploy Aliyun`（SCP 同步 → build → `k3s ctr` 导入 → mysql/app/frontend）。

Secrets：`ALIYUN_HOST` / `ALIYUN_USER` / `ALIYUN_SSH_KEY`（私钥在本机 `deploy/.secrets/ALIYUN_SSH_KEY`，勿提交）。  
Variables：`ALIYUN_DEPLOY_PATH=/opt/deploy/warehouse-management-project`，`K8S_NAMESPACE=warehouse`。

**注意：** 当前工作区若还不是 git 仓库，需先 `git init` 并推到 GitHub，Actions 才能跑。

## 安全提醒

1. **Root 密码已在聊天中暴露，请立刻修改**：`passwd`  
2. 建议改用 **SSH 密钥**，关闭密码登录  
3. 阿里云安全组放行：`22`、`80`、`443`、`6443`、`30080`、`30081`（以及 Sonar `9000` / 报表 `9080` 如需要）

## GitHub CD 变量建议

- `ALIYUN_HOST=121.40.253.17`
- `ALIYUN_USER=root`
- `ALIYUN_SSH_KEY` = 私钥（不要再用聊天里的密码）
- `ALIYUN_DEPLOY_PATH=/opt/deploy/warehouse-management-project`
- `K8S_NAMESPACE=warehouse`
