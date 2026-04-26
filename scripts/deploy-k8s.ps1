param(
  [string]$Namespace = "warehouse",
  [string]$ImageName = "warehouse-app:latest"
)

$ErrorActionPreference = "Stop"

Push-Location (Split-Path -Parent $PSScriptRoot)

Write-Host "Creating namespace $Namespace ..."
kubectl apply -f .\k8s\namespace.yaml | Out-Host

Write-Host "Creating/Updating mysql initdb ConfigMap from db\springboot38hdw40x.sql ..."
kubectl -n $Namespace create configmap mysql-initdb `
  --from-file=01-init.sql=.\db\springboot38hdw40x.sql `
  --dry-run=client -o yaml | kubectl apply -f - | Out-Host

Write-Host "Applying MySQL manifests ..."
kubectl apply -f .\k8s\mysql.yaml | Out-Host

Write-Host "Building container image $ImageName ..."
docker build -t $ImageName . | Out-Host

Write-Host "Applying app manifests ..."
kubectl apply -f .\k8s\app.yaml | Out-Host

Write-Host "Optional: apply ingress (requires an Ingress controller) ..."
kubectl apply -f .\k8s\ingress.yaml | Out-Host

Write-Host "Waiting for rollouts ..."
kubectl -n $Namespace rollout status deploy/mysql --timeout=180s | Out-Host
kubectl -n $Namespace rollout status deploy/warehouse-app --timeout=180s | Out-Host

Write-Host ""
Write-Host "Done. Useful commands:"
Write-Host "  kubectl -n $Namespace get pods,svc,ingress"
Write-Host "  kubectl -n $Namespace logs -f deploy/warehouse-app"

Pop-Location

