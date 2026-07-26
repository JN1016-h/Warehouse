# 本机 Docker 部署

## 组成

| 服务 | 容器名 | 端口 |
|------|--------|------|
| MySQL 8 | warehouse-mysql | 3306 |
| SpringBoot | warehouse-app | 8080 |
| Nginx 前端 | warehouse-frontend | 80 |

首次启动会自动导入 `db/springboot38hdw40x.sql` 与 `db/migration_ai_assistant.sql`。

## 启动

```powershell
# 仓库根目录
docker compose --env-file .env.docker up -d --build
```

或：

```powershell
powershell -ExecutionPolicy Bypass -File .\deploy\docker-up.ps1
```

## 访问

- 管理端：http://localhost/
- 后端：http://localhost:8080/springboot38hdw40x/
- AI 配置见 `.env.docker`（`AI_BASE_URL` / `AI_API_KEY` / `AI_MODEL`）

## 常用命令

```powershell
docker compose ps
docker compose logs -f app
docker compose down          # 停容器，保留数据卷
docker compose down -v       # 停容器并清空 MySQL 数据（下次会重新初始化）
```

## 说明

- 若国内拉镜像慢，可在 `.env.docker` 将 `REGISTRY_MIRROR` 改为例如 `m.daocloud.io/docker.io/library`
- 首次 build 后端会跑 Maven，前端会跑 npm，耗时较长属正常
- 修改 AI Key 后执行：`docker compose up -d app` 即可热更新环境变量（无需重建镜像）
