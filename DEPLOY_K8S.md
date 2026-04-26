## 目标

把本项目（Spring Boot + MySQL）部署到 Kubernetes，并生成可执行构建产物（JAR/WAR）。

## 生成可执行文件（Windows / PowerShell）

- **打 JAR（默认）**：

```powershell
.\scripts\build-artifact.ps1
```

- **打 WAR（可选）**：

```powershell
.\scripts\build-artifact.ps1 -War
```

产物在 `target\` 目录。

## 部署到 Kubernetes（需要 kubectl + docker）

1) 确保你的 K8s 节点能拉取本地构建的镜像（例如 Docker Desktop 自带 K8s，或你已配置镜像仓库）。

2) 一键部署（会创建 namespace、导入 `db/springboot38hdw40x.sql` 到 MySQL、构建镜像、apply 清单）：

```powershell
.\scripts\deploy-k8s.ps1
```

3) 查看状态：

```powershell
kubectl -n warehouse get pods,svc,ingress
kubectl -n warehouse logs -f deploy/warehouse-app
```

## 访问

- **后端（NodePort）**：`warehouse-app` Service 类型为 NodePort（可在 `k8s/app.yaml` 固定 `nodePort`）
- **前端（NodePort）**：`warehouse-frontend` Service 类型为 NodePort（可在 `k8s/frontend.yaml` 固定 `nodePort`）
- **后端 context-path**：`/springboot38hdw40x`
- **MySQL**：本部署为“无持久化”，重建 Pod 会丢数据（仅适合测试/演示）

