#!/usr/bin/env bash
# Optional one-shot scans on the server (same tools as GitHub Cicd).
# Requires: docker, and warehouse-app image or reachable APP_URL.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
REPO="$(cd "$ROOT/../.." && pwd)"
APP_URL="${APP_URL:-http://127.0.0.1:8080/springboot38hdw40x/config/list?page=1&limit=1}"
OUT="${OUT:-/opt/ci-reports/latest/manual}"
mkdir -p "$OUT"

echo "== Trivy image"
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy:0.70.0 image --format table warehouse-app:server | tee "$OUT/trivy-app.txt" || true

echo "== Checkov k8s/Dockerfile"
docker run --rm -v "$REPO:/src" bridgecrew/checkov:3.2.484 \
  -d /src/k8s -d /src/Dockerfile -o cli | tee "$OUT/checkov.txt" || true

echo "== OWASP ZAP baseline"
docker run --rm --network host -v "$OUT:/zap/wrk" \
  ghcr.io/zaproxy/zaproxy:stable \
  zap-baseline.py -t "$APP_URL" -r zap.html -I || true

echo "== k6 smoke (needs k6 image + script)"
if [ -f "$REPO/tests/k6/smoke.js" ]; then
  docker run --rm --network host -v "$REPO/tests/k6:/scripts" \
    grafana/k6:0.54.0 run /scripts/smoke.js | tee "$OUT/k6.txt" || true
fi

echo "Done. Reports under $OUT"
