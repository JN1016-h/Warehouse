# Java 服务 Kubernetes 部署

本仓库后端是 **Spring Boot**（`Dockerfile` → `warehouse-app` 镜像），通过 `k8s/app.yaml` 部署到集群。

## 资源

| 资源 | 名称 | 说明 |
|------|------|------|
| Deployment | `warehouse-app` | Java 进程，端口 8080 |
| Service | `warehouse-app` | **NodePort 30081** |
| ConfigMap | `warehouse-app-config` | JDBC / context-path |
| Secret | `warehouse-app-secret` | DB 密码 |
| 依赖 | `mysql`（`k8s/mysql.yaml`） | 同命名空间 `warehouse` |

访问：

```text
http://<节点IP>:30081/springboot38hdw40x/
```

Ingress（可选）：路径 `/springboot38hdw40x` → `warehouse-app:8080`（见 `k8s/ingress.yaml`）。

## 一次性准备（集群节点）

```bash
# 节点需能 docker build，且 kubectl 可用
sudo sysctl -w vm.max_map_count=262144   # 若同机还跑 Sonar 再需要

# 克隆代码到部署机
sudo mkdir -p /opt/deploy
sudo git clone <REPO_URL> /opt/deploy/warehouse-management-project
cd /opt/deploy/warehouse-management-project
```

## 手动部署 Java

```bash
cd /opt/deploy/warehouse-management-project
chmod +x k8s/apply-java.sh

# 构建并发布（默认镜像 warehouse-app:latest）
APP_IMAGE=warehouse-app:manual.1 ./k8s/apply-java.sh

# 仅改镜像、不重新 build
SKIP_BUILD=1 APP_IMAGE=warehouse-app:build.12 ./k8s/apply-java.sh
```

等价命令：

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/mysql.yaml
docker build -t warehouse-app:build.1 -f Dockerfile .
kubectl apply -f k8s/app.yaml
kubectl set image deployment/warehouse-app app=warehouse-app:build.1 -n warehouse
kubectl rollout status deployment/warehouse-app -n warehouse
curl -fsS "http://127.0.0.1:30081/springboot38hdw40x/config/list?page=1&limit=1"
```

## GitHub CD（自动）

Workflow：`.github/workflows/deploy-aliyun.yml`

```text
gate → build(docker build warehouse-app) → import(ctr)
  → deploy-infrastructure(mysql)
  → deploy-services(kubectl apply app.yaml + set image)   # Java
  → deploy-gateway(ingress)
  → verify
```

需配置 Secrets / Variables（与 `deploy/CICD-GITHUB.md` 相同）：

- `ALIYUN_HOST` / `ALIYUN_USER` / `ALIYUN_SSH_KEY`
- `ALIYUN_DEPLOY_PATH`、`K8S_DIR`、`K8S_NAMESPACE=warehouse`

CI（`Cicd`）在 `master`/`main` 成功后会触发 CD；也可手动 **Deploy Aliyun → Run workflow**。

## 探针与健康

`app.yaml` 对下列路径做 startup / readiness / liveness：

```text
GET /springboot38hdw40x/config/list?page=1&limit=1
```

该接口带 `@IgnoreAuth`，无需 Token。

## 配置覆盖

容器环境变量覆盖 `application.yml` 中的数据源（Spring Boot 松散绑定）：

- `SPRING_DATASOURCE_URL` → 默认指向集群内 Service `mysql:3306`
- `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD`

改密码：编辑 `k8s/app.yaml` 中 Secret 后 `kubectl apply`，再滚动重启：

```bash
kubectl rollout restart deployment/warehouse-app -n warehouse
```

## 排障

```bash
kubectl get pods -n warehouse -l app=warehouse-app
kubectl describe pod -n warehouse -l app=warehouse-app
kubectl logs -n warehouse -l app=warehouse-app --tail=200
kubectl get svc warehouse-app -n warehouse
```

常见问题：

1. **ImagePullBackOff**：单节点集群请用 `imagePullPolicy: IfNotPresent`，并在**本机** `docker build` + `ctr images import`（脚本已处理）。  
2. **Crash / 连不上库**：先确认 `mysql` Pod Ready，`SPRING_DATASOURCE_URL` 主机名为 `mysql`。  
3. **探针失败**：等启动完成；查看 logs 是否仍在迁移/连库。
