#!/usr/bin/env bash
# Upload/build on host → push Harbor → k8s pulls from Harbor
set -euo pipefail

NS="${K8S_NAMESPACE:-warehouse}"
KD="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "${KD}/.." && pwd)"
HARBOR_HOST="${HARBOR_HOST:-121.40.253.17}"
HARBOR_PROJECT="${HARBOR_PROJECT:-warehouse}"
HARBOR_USER="${HARBOR_USER:-admin}"
HARBOR_PASS="${HARBOR_PASS:-Harbor12345}"
TAG="${IMAGE_TAG:-$(date +%Y%m%d%H%M%S)}"
APP_IMAGE="${APP_IMAGE:-${HARBOR_HOST}/${HARBOR_PROJECT}/warehouse-app:${TAG}}"
FE_IMAGE="${FE_IMAGE:-${HARBOR_HOST}/${HARBOR_PROJECT}/warehouse-frontend:${TAG}}"
WAIT="${WAIT_TIMEOUT:-600s}"
SKIP_BUILD="${SKIP_BUILD:-0}"
SKIP_PUSH="${SKIP_PUSH:-0}"

echo "== ns=${NS} harbor=${HARBOR_HOST}/${HARBOR_PROJECT} tag=${TAG}"
echo "== app=${APP_IMAGE}"
echo "== fe=${FE_IMAGE}"
command -v kubectl >/dev/null
command -v docker >/dev/null

ensure_harbor_project() {
  local code
  code="$(curl -fsS -o /dev/null -w '%{http_code}' -u "${HARBOR_USER}:${HARBOR_PASS}" \
    "http://${HARBOR_HOST}/api/v2.0/projects?project_name=${HARBOR_PROJECT}" || echo 000)"
  # list API returns 200 always; try create
  curl -fsS -u "${HARBOR_USER}:${HARBOR_PASS}" -H 'Content-Type: application/json' \
    -X POST "http://${HARBOR_HOST}/api/v2.0/projects" \
    -d "{\"project_name\":\"${HARBOR_PROJECT}\",\"public\":true,\"metadata\":{\"public\":\"true\"}}" \
    >/dev/null 2>&1 || true
  echo "== harbor project ${HARBOR_PROJECT} ready (create may 409 if exists)"
}

ensure_k3s_harbor_registry() {
  mkdir -p /etc/rancher/k3s
  local tmp=/tmp/registries.yaml.harbor
  cat >"${tmp}" <<EOF
mirrors:
  docker.io:
    endpoint:
      - "https://docker.m.daocloud.io"
      - "https://docker.1ms.run"
  "${HARBOR_HOST}":
    endpoint:
      - "http://${HARBOR_HOST}"
configs:
  "${HARBOR_HOST}":
    auth:
      username: ${HARBOR_USER}
      password: ${HARBOR_PASS}
    tls:
      insecure_skip_verify: true
EOF
  if ! cmp -s "${tmp}" /etc/rancher/k3s/registries.yaml 2>/dev/null; then
    cp "${tmp}" /etc/rancher/k3s/registries.yaml
    systemctl restart k3s
    sleep 12
    echo "== k3s restarted with Harbor registry"
  else
    echo "== k3s Harbor registry already configured"
  fi
}

if [ "${SKIP_BUILD}" != "1" ]; then
  echo "== docker build app"
  docker build -t "${APP_IMAGE}" -f "${ROOT}/Dockerfile" "${ROOT}"
  echo "== docker build frontend"
  docker build -t "${FE_IMAGE}" -f "${ROOT}/deploy/frontend-ci.Dockerfile" "${ROOT}"
fi

if [ "${SKIP_PUSH}" != "1" ]; then
  ensure_harbor_project
  echo "${HARBOR_PASS}" | docker login "${HARBOR_HOST}" -u "${HARBOR_USER}" --password-stdin
  echo "== push ${APP_IMAGE}"
  docker push "${APP_IMAGE}"
  echo "== push ${FE_IMAGE}"
  docker push "${FE_IMAGE}"
fi

# k3s must pull from Harbor (not ctr import)
ensure_k3s_harbor_registry

kubectl apply -f "${KD}/namespace.yaml"

echo "== harbor pull secret"
kubectl -n "${NS}" create secret docker-registry harbor-cred \
  --docker-server="${HARBOR_HOST}" \
  --docker-username="${HARBOR_USER}" \
  --docker-password="${HARBOR_PASS}" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "== mysql init ConfigMap"
kubectl -n "${NS}" create configmap mysql-initdb \
  --from-file=01_schema.sql="${ROOT}/db/springboot38hdw40x.sql" \
  --from-file=02_ai.sql="${ROOT}/db/migration_ai_assistant.sql" \
  --from-file=03_user_role_demo.sql="${ROOT}/db/migration_user_role_and_demo_fix.sql" \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl apply -f "${KD}/mysql.yaml"
kubectl rollout status "deployment/mysql" -n "${NS}" --timeout="${WAIT}" || true

# Patch manifests defaults then set image to Harbor refs
kubectl apply -f "${KD}/app.yaml"
kubectl apply -f "${KD}/frontend.yaml"
kubectl -n "${NS}" patch deployment warehouse-app --type strategic -p \
  "{\"spec\":{\"template\":{\"spec\":{\"imagePullSecrets\":[{\"name\":\"harbor-cred\"}]}}}}"
kubectl -n "${NS}" patch deployment warehouse-frontend --type strategic -p \
  "{\"spec\":{\"template\":{\"spec\":{\"imagePullSecrets\":[{\"name\":\"harbor-cred\"}]}}}}"

kubectl set image "deployment/warehouse-app" "app=${APP_IMAGE}" -n "${NS}"
kubectl set image "deployment/warehouse-frontend" "frontend=${FE_IMAGE}" -n "${NS}"
kubectl -n "${NS}" patch deployment warehouse-app -p \
  '{"spec":{"template":{"spec":{"containers":[{"name":"app","imagePullPolicy":"Always"}]}}}}'
kubectl -n "${NS}" patch deployment warehouse-frontend -p \
  '{"spec":{"template":{"spec":{"containers":[{"name":"frontend","imagePullPolicy":"Always"}]}}}}'

kubectl rollout status "deployment/warehouse-app" -n "${NS}" --timeout="${WAIT}"
kubectl rollout status "deployment/warehouse-frontend" -n "${NS}" --timeout="${WAIT}"

if [ -f "${KD}/ingress.yaml" ]; then
  kubectl apply -f "${KD}/ingress.yaml" || true
fi

echo "== pods/svc"
kubectl get pods,svc -n "${NS}" -o wide
kubectl get deploy -n "${NS}" -o wide

NODE_IP="$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}' 2>/dev/null || echo 127.0.0.1)"
echo "== API   http://${NODE_IP}:30081/springboot38hdw40x/"
echo "== FE    http://${NODE_IP}:30080/"
curl -fsS --connect-timeout 5 --max-time 20 \
  "http://${NODE_IP}:30081/springboot38hdw40x/config/list?page=1&limit=1" | head -c 200 || true
echo
echo "== Harbor → k8s deploy done"
