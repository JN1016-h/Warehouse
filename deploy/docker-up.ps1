# 本机 Docker 一键启动（MySQL + 后端 + 前端）
$ErrorActionPreference = "Stop"
$root = Split-Path $PSScriptRoot -Parent
Set-Location $root
if (-not (Test-Path ".\docker-compose.yml")) {
    throw "未找到 docker-compose.yml，当前目录: $root"
}

Write-Host "==> docker compose up -d --build"
docker compose --env-file .env.docker up -d --build
if ($LASTEXITCODE -ne 0) {
    throw "docker compose 启动失败"
}

Write-Host ""
Write-Host "部署完成，访问地址："
Write-Host "  前端管理端:  http://localhost/"
Write-Host "  后端直连:    http://localhost:8080/springboot38hdw40x/"
Write-Host "  MySQL:       localhost:3306  root/123123  db=springboot38hdw40x"
Write-Host ""
Write-Host "查看日志: docker compose logs -f app"
Write-Host "停止:     docker compose down"
Write-Host "清数据:   docker compose down -v"
