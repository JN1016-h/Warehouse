#!/usr/bin/env bash
# Bootstrap: MySQL + Java App + SonarQube + CI reports nginx
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

if ! command -v docker >/dev/null; then
  echo "docker is required"
  exit 1
fi

if [ -f .env ]; then
  set -a
  # shellcheck disable=SC1091
  . ./.env
  set +a
fi

if [ "$(id -u)" -eq 0 ]; then
  sysctl -w vm.max_map_count=262144 || true
  echo 'vm.max_map_count=262144' > /etc/sysctl.d/99-sonarqube.conf || true
else
  echo "If Sonar fails: sudo sysctl -w vm.max_map_count=262144"
fi

export MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-123123}"
export MYSQL_PORT="${MYSQL_PORT:-3306}"
export APP_HTTP_PORT="${APP_HTTP_PORT:-8080}"
export SONAR_DB_PASSWORD="${SONAR_DB_PASSWORD:-sonarpass}"
export SONAR_HTTP_PORT="${SONAR_HTTP_PORT:-9000}"
export REPORTS_HTTP_PORT="${REPORTS_HTTP_PORT:-9080}"
export CI_REPORTS_HOST_DIR="${CI_REPORTS_HOST_DIR:-/opt/ci-reports}"
export REGISTRY_MIRROR="${REGISTRY_MIRROR:-m.daocloud.io/docker.io/library}"

mkdir -p "${CI_REPORTS_HOST_DIR}/latest"
if [ ! -f "${CI_REPORTS_HOST_DIR}/index.html" ]; then
  cat > "${CI_REPORTS_HOST_DIR}/index.html" <<'HTML'
<html><body>
<h1>CI Reports</h1>
<p>Waiting for GitHub Actions sync (Gitleaks / Semgrep / Trivy / ZAP / JaCoCo / Newman / k6 / JMeter)…</p>
<a href="./latest/">latest</a>
</body></html>
HTML
fi

# Pre-pull base images via China mirrors when Docker Hub is slow
pull_tag() {
  local dest="$1"
  shift
  if docker image inspect "$dest" >/dev/null 2>&1; then
    return 0
  fi
  local src
  for src in "$@"; do
    echo "Trying $src -> $dest"
    if docker pull "$src"; then
      docker tag "$src" "$dest"
      return 0
    fi
  done
  echo "WARN: could not pull $dest"
  return 1
}

echo "== pre-pull images"
pull_tag mysql:8.0 \
  docker.m.daocloud.io/library/mysql:8.0 \
  m.daocloud.io/docker.io/library/mysql:8.0 \
  mysql:8.0 || true
pull_tag postgres:15 \
  docker.m.daocloud.io/library/postgres:15 \
  m.daocloud.io/docker.io/library/postgres:15 \
  postgres:15 || true
pull_tag nginx:1.25-alpine \
  docker.m.daocloud.io/library/nginx:1.25-alpine \
  m.daocloud.io/docker.io/library/nginx:1.25-alpine \
  nginx:1.25-alpine || true
pull_tag sonarqube:lts-community \
  docker.m.daocloud.io/library/sonarqube:lts-community \
  m.daocloud.io/docker.io/library/sonarqube:lts-community \
  sonarqube:lts-community || true

echo "== docker compose up (mysql app sonar reports)"
docker compose up -d --build mysql app sonar-db sonarqube reports-nginx

echo "== wait MySQL / App / Sonar"
for i in $(seq 1 60); do
  if docker exec warehouse-mysql mysqladmin ping -h 127.0.0.1 -uroot -p"${MYSQL_ROOT_PASSWORD}" --silent 2>/dev/null; then
    echo "mysql: ok"
    break
  fi
  sleep 3
done

for i in $(seq 1 60); do
  if curl -fsS -o /dev/null "http://127.0.0.1:${APP_HTTP_PORT}/springboot38hdw40x/config/list?page=1&limit=1" 2>/dev/null \
     || curl -fsS -o /dev/null "http://127.0.0.1:${APP_HTTP_PORT}/" 2>/dev/null; then
    echo "app: reachable on :${APP_HTTP_PORT}"
    break
  fi
  sleep 5
done

for i in $(seq 1 90); do
  if curl -fsS "http://127.0.0.1:${SONAR_HTTP_PORT}/api/system/status" 2>/dev/null | grep -qiE 'UP|DB_MIGRATION|STARTING'; then
    STATUS="$(curl -fsS "http://127.0.0.1:${SONAR_HTTP_PORT}/api/system/status" | tr -d '\n')"
    echo "sonar: ${STATUS}"
    echo "${STATUS}" | grep -qi '"status":"UP"' && break
  fi
  sleep 5
done

cat <<EOF

============================================================
服务器常驻已启动

  Java API   : http://<SERVER_IP>:${APP_HTTP_PORT}/springboot38hdw40x/
  MySQL      : <SERVER_IP>:${MYSQL_PORT}  root / ${MYSQL_ROOT_PASSWORD}
  SonarQube  : http://<SERVER_IP>:${SONAR_HTTP_PORT}   (admin/admin → 立刻改密)
  CI Reports : http://<SERVER_IP>:${REPORTS_HTTP_PORT}/reports/
  Harbor     : http://<SERVER_IP>/   (已占用 80，勿再绑前端到 80)

GitHub Actions (Cicd) 仍负责临时工具：
  SAST: Gitleaks, Semgrep(Java/JS)
  Unit: Maven(Java 8)+JaCoCo
  Integration: Compose MySQL+App, Newman
  Container/IaC: Trivy, Checkov
  DAST: OWASP ZAP
  Perf: k6, JMeter
  → 开 SONAR_ENABLED / REPORTS_SYNC_ENABLED 后推送到本机 Sonar 与 :9080

单次本机扫描示例见: ./scan-once.sh
============================================================
EOF
