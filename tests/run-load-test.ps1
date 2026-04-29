#Requires -Version 5.1
<#
  Windows 上运行 JMeter 非 GUI 压测。需已安装 JMeter: https://jmeter.apache.org/download_jmeter.cgi
  将 apache-jmeter-xxx\bin 加入系统 PATH，或设置 JMETER_HOME 为解压目录。

  warehouse-load.jmx 为「GUI 友好」：线程组等均为纯数字/主机名，无 ${__P}，避免 JMeter
  界面报「Problem updating GUI」。本脚本在运行前按参数生成临时 jmx 再压测。

  压本机时先启动: mvnw.cmd -Dmaven.test.skip=true spring-boot:run
  默认压测目标为 jmx 中的 47.103.133.105:30081；本机用:
    .\run-load-test.ps1 -ServiceHost 127.0.0.1 -Port 8080

  执行:
    cd tests
    .\run-load-test.ps1
    .\run-load-test.ps1 -Threads 30 -DurationSec 120
#>
param(
    [string] $ServiceHost = "47.103.133.105",
    [int] $Port = 30081,
    [int] $Threads = 20,
    [int] $RampSec = 5,
    [int] $DurationSec = 60,
    [string] $Context = "springboot38hdw40x"
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$jmx = Join-Path $root "jmeter\warehouse-load.jmx"
$outDir = Join-Path $root "jmeter\output"
$jtl = Join-Path $outDir "result.jtl"
$report = Join-Path $outDir "html-report"

if (-not (Test-Path $jmx)) {
    throw "未找到: $jmx"
}

$jmeterExe = $null
if ($env:JMETER_HOME) {
    $bat = Join-Path $env:JMETER_HOME "bin\jmeter.bat"
    if (Test-Path $bat) { $jmeterExe = $bat }
}
if (-not $jmeterExe) {
    $cmd = Get-Command jmeter.bat -ErrorAction SilentlyContinue
    if ($cmd) { $jmeterExe = $cmd.Path }
}
if (-not $jmeterExe) {
    Write-Host "未找到 jmeter。请安装 JMeter 并设置 PATH 或 JMETER_HOME。" -ForegroundColor Red
    exit 1
}

New-Item -ItemType Directory -Force -Path $outDir | Out-Null
if (Test-Path $jtl) { Remove-Item $jtl -Force -ErrorAction SilentlyContinue }
if (Test-Path $report) { Remove-Item $report -Recurse -Force -ErrorAction SilentlyContinue }

# 用模板中固定字面量做替换，供非 GUI 使用（与 jmx 中默认 47.103.133.105:30081 / 20/5/60 对应）
$tmpJmx = Join-Path $env:TEMP "warehouse-load-$$.jmx"
$xml = [System.IO.File]::ReadAllText($jmx, [System.Text.Encoding]::UTF8)
$xml = $xml.Replace('<stringProp name="HTTPSampler.domain">47.103.133.105</stringProp>', ('<stringProp name="HTTPSampler.domain">' + $ServiceHost + '</stringProp>'))
$xml = $xml.Replace('<stringProp name="HTTPSampler.port">30081</stringProp>', ('<stringProp name="HTTPSampler.port">' + $Port + '</stringProp>'))
$xml = $xml.Replace('<stringProp name="ThreadGroup.num_threads">20</stringProp>', ('<stringProp name="ThreadGroup.num_threads">' + $Threads + '</stringProp>'))
$xml = $xml.Replace('<stringProp name="ThreadGroup.ramp_time">5</stringProp>', ('<stringProp name="ThreadGroup.ramp_time">' + $RampSec + '</stringProp>'))
$xml = $xml.Replace('<stringProp name="ThreadGroup.duration">60</stringProp>', ('<stringProp name="ThreadGroup.duration">' + $DurationSec + '</stringProp>'))
if ($Context -ne "springboot38hdw40x") {
    $xml = $xml.Replace("/springboot38hdw40x/", ("/" + $Context + "/"))
}
[System.IO.File]::WriteAllText($tmpJmx, $xml, [System.Text.Encoding]::UTF8)

Write-Host "JMeter: $jmeterExe" -ForegroundColor Cyan
Write-Host "目标: http://${ServiceHost}:${Port}/$Context/" -ForegroundColor Cyan
Write-Host "线程: $Threads  持续: ${DurationSec}s  爬坡: ${RampSec}s" -ForegroundColor Cyan
Write-Host "临时 JMX: $tmpJmx" -ForegroundColor DarkGray

$allArgs = @(
    "-n", "-t", $tmpJmx,
    "-l", $jtl,
    "-e", "-o", $report
)

& $jmeterExe @allArgs
$code = $LASTEXITCODE
try { Remove-Item $tmpJmx -Force -ErrorAction SilentlyContinue } catch {}
if ($code -ne 0) { exit $code }

Write-Host "完成。JTL: $jtl" -ForegroundColor Green
Write-Host "HTML: $report\index.html" -ForegroundColor Green
