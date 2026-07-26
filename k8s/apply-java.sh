#!/usr/bin/env bash
# Deploy / upgrade Java Spring Boot service on Kubernetes
set -euo pipefail

NS="${K8S_NAMESPACE:-warehouse}"
KD="${K8S_DIR:-$(cd "$(dirname "$0")" && pwd)}"
IMAGE="${APP_IMAGE:-warehouse-app:latest}"
WAIT="${WAIT_TIMEOUT:-300s}"

echo "== namespace=${NS} dir=${KD} image=${IMAGE}"
command -v kubectl >/dev/null
command -v docker >/dev/null

# 1) Build image on this node (containerd/docker local cluster)
if [ "${SKIP_BUILD:-0}" != "1" ]; then
  ROOT="$(cd "${KD}/.." && pwd)"
  echo "== docker build ${IMAGE}"
  docker build -t "${IMAGE}" -f "${ROOT}/Dockerfile" "${ROOT}"
  if command -v k8s-import-image >/dev/null 2>&1; then
    k8s-import-image "${IMAGE}"
  elif command -v k3s >/dev/null 2>&1; then
    docker save "${IMAGE}" | k3s ctr images import -
  elif command -v ctr >/dev/null 2>&1; then
    docker save "${IMAGE}" | sudo ctr -n k8s.io images import - || \
      docker save "${IMAGE}" | ctr -n k8s.io images import - || true
  fi
fi

# 2) Infra (optional)
kubectl apply -f "${KD}/namespace.yaml"
if [ -f "${KD}/mysql.yaml" ]; then
  kubectl apply -f "${KD}/mysql.yaml"
  kubectl rollout status "deployment/mysql" -n "${NS}" --timeout="${WAIT}" || true
fi

# 3) Java app
kubectl apply -f "${KD}/app.yaml"
kubectl set image "deployment/warehouse-app" "app=${IMAGE}" -n "${NS}"
kubectl rollout status "deployment/warehouse-app" -n "${NS}" --timeout="${WAIT}"

echo "== pods"
kubectl get pods -n "${NS}" -l app=warehouse-app -o wide
echo "== svc"
kubectl get svc warehouse-app -n "${NS}"

NODE_IP="$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}' 2>/dev/null || true)"
if [ -n "${NODE_IP}" ]; then
  URL="http://${NODE_IP}:30081/springboot38hdw40x/config/list?page=1&limit=1"
  echo "== smoke ${URL}"
  curl -fsS --connect-timeout 5 --max-time 20 "${URL}" | head -c 200 || true
  echo
fi

echo "== Java service K8s deploy done"
