# 服务器：Java 中间件 + Sonar / 报表

## 分工

| 位置 | 组件 |
|------|------|
| **本机 Docker（本目录）** | MySQL、Java App、SonarQube、CI 报表 Nginx |
| **已单独部署** | Harbor（`:80`） |
| **GitHub Actions `cicd.yml`** | Gitleaks、Semgrep(Java/JS)、Maven、Newman、Trivy、Checkov、OWASP ZAP、k6、JMeter → 推 Sonar / 同步报表 |

## 启动

```bash
cd deploy/server-tools
cp .env.example .env   # 按需改密码/端口
chmod +x bootstrap.sh scan-once.sh
./bootstrap.sh
```

| 服务 | 地址 |
|------|------|
| Java API | `http://<IP>:8080/springboot38hdw40x/` |
| MySQL | `<IP>:3306` |
| SonarQube | `http://<IP>:9000`（`admin`/`admin`，立刻改密） |
| CI HTML 报表 | `http://<IP>:9080/reports/` |
| Harbor | `http://<IP>/`（勿再把前端绑到 80） |

可选本机扫一次（同 CI 工具镜像）：`./scan-once.sh`

## GitHub 对接 Sonar / 报表

Secrets：`SONAR_HOST_URL=http://<IP>:9000`、`SONAR_TOKEN`、SSH 部署密钥  
Variables：`SONAR_ENABLED=true`、`REPORTS_SYNC_ENABLED=true`、`REPORTS_REMOTE_DIR=/opt/ci-reports`
