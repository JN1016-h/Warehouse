# GitHub CI/CD → 服务器构建 → Harbor → k3s 拉取

## 流程

```text
1. sync     上传代码到 /opt/deploy/warehouse-management-project
2. build    本机 docker build，打 Harbor 标签
3. push     docker push → http://121.40.253.17/warehouse/...
4. deploy   k3s 配置 insecure Harbor，Deployment 从 Harbor pull
```

镜像示例：

```text
121.40.253.17/warehouse/warehouse-app:build.<N>
121.40.253.17/warehouse/warehouse-frontend:build.<N>
```

## 手动一键

```bash
cd /opt/deploy/warehouse-management-project
export HARBOR_HOST=121.40.253.17 HARBOR_PROJECT=warehouse
export HARBOR_USER=admin HARBOR_PASS='Harbor12345'
export IMAGE_TAG=manual.1
./k8s/apply-stack.sh
```

## GitHub

Workflow：`Deploy Aliyun`（`push-harbor` 替代原 `ctr import`）

| Secrets | Variables |
|---------|-----------|
| `ALIYUN_HOST/USER/SSH_KEY` | `ALIYUN_DEPLOY_PATH`、`K8S_NAMESPACE` |
| `HARBOR_USER` / `HARBOR_PASSWORD`（可选，默认 admin） | `HARBOR_HOST=121.40.253.17`、`HARBOR_PROJECT=warehouse` |

## 访问

- Harbor UI：http://121.40.253.17/ （项目 `warehouse`）
- 前端：http://121.40.253.17:30080/
- API：http://121.40.253.17:30081/springboot38hdw40x/
