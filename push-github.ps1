#Requires -Version 5.1
<#
  Run in PowerShell from repo root (double-click or: .\push-github.ps1)
  - Ensures SSH to GitHub uses port 443 (ssh.github.com)
  - Pushes local master to github remote (overwrites remote if needed via --force-with-lease)

  Prerequisite: SSH key added at https://github.com/settings/keys
#>
$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $repoRoot

$sshDir = Join-Path $env:USERPROFILE '.ssh'
$configPath = Join-Path $sshDir 'config'
$githubBlock = @"

Host github.com
  HostName ssh.github.com
  Port 443
  User git
"@

if (-not (Test-Path $sshDir)) {
    New-Item -ItemType Directory -Path $sshDir -Force | Out-Null
}

$needsBlock = $true
if (Test-Path $configPath) {
    $raw = Get-Content $configPath -Raw -ErrorAction SilentlyContinue
    if ($raw -match '(?m)^Host\s+github\.com\s*$') { $needsBlock = $false }
}
if ($needsBlock) {
    Add-Content -LiteralPath $configPath -Value $githubBlock -Encoding utf8
    Write-Host "Added GitHub (SSH over 443) to $configPath"
}

git remote set-url github git@github.com:JN1016-h/Warehouse.git

Write-Host "`n=== ssh -T (expect: Hi JN1016-h ... ) ===" -ForegroundColor Cyan
ssh -T git@github.com 2>&1 | Write-Host

Write-Host "`n=== git fetch github ===" -ForegroundColor Cyan
git fetch github

Write-Host "`n=== git push (force-with-lease: replaces remote master only if no one else pushed) ===" -ForegroundColor Cyan
git push -u github master --force-with-lease

Write-Host "`nDone." -ForegroundColor Green
