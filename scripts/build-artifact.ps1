param(
  [switch]$War
)

$ErrorActionPreference = "Stop"

Push-Location (Split-Path -Parent $PSScriptRoot)

if ($War) {
  Write-Host "Building WAR using pom-war.xml ..."
  .\mvnw.cmd -Dmaven.test.skip=true clean package -f pom-war.xml | Out-Host
  Write-Host "WAR output is under target\\ (finalName: springboot38hdw40x.war)"
} else {
  Write-Host "Building JAR using pom.xml ..."
  .\mvnw.cmd -Dmaven.test.skip=true clean package | Out-Host
  Write-Host "JAR output is under target\\"
}

Pop-Location

