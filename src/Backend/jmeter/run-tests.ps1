# =============================================================================
#  Запуск нагрузочных тестов Kanban API через Apache JMeter (non-GUI режим)
# =============================================================================
#  Использование:
#     .\run-tests.ps1                # запустить все сценарии
#     .\run-tests.ps1 -Scenario load # запустить один сценарий (smoke|load|multithread|stress)
#
#  Перед запуском убедитесь, что бэкенд поднят на http://localhost:8080
# =============================================================================

param(
    [string]$Scenario = "all",
    [string]$JMeterHome = "C:\Program Files\apache-jmeter-5.6.3",
    [string]$JavaHome   = "C:\Program Files\Java\jdk-25",
    [string]$Host       = "localhost",
    [int]$Port          = 8080
)

$ErrorActionPreference = "Stop"
$here = Split-Path -Parent $MyInvocation.MyCommand.Path
$plan = Join-Path $here "kanban-test-plan.jmx"
$resultsDir = Join-Path $here "results"
$jmeterBat = Join-Path $JMeterHome "bin\jmeter.bat"

$env:JAVA_HOME = $JavaHome

# Сценарий = имя; потоки; разгон(с); итераций цикла
$scenarios = @(
    @{ name = "smoke";       threads = 1;   rampup = 1;  loops = 5  },
    @{ name = "load";        threads = 20;  rampup = 20; loops = 10 },
    @{ name = "multithread"; threads = 50;  rampup = 1;  loops = 5  },
    @{ name = "stress";      threads = 100; rampup = 10; loops = 10 }
)

if (-not (Test-Path $resultsDir)) { New-Item -ItemType Directory -Path $resultsDir | Out-Null }

foreach ($s in $scenarios) {
    if ($Scenario -ne "all" -and $Scenario -ne $s.name) { continue }

    $name = $s.name
    $runId = $name.Substring(0,2) + (Get-Date -UFormat %s).Substring(5)  # уникальный id запуска
    $jtl    = Join-Path $resultsDir "$name.jtl"
    $report = Join-Path $resultsDir "$name-report"

    if (Test-Path $jtl)    { Remove-Item $jtl -Force }
    if (Test-Path $report) { Remove-Item $report -Recurse -Force }

    Write-Host "=== Сценарий '$name': threads=$($s.threads) rampup=$($s.rampup) loops=$($s.loops) runId=$runId ===" -ForegroundColor Cyan

    & $jmeterBat -n -t $plan -l $jtl -e -o $report `
        -Jthreads=$($s.threads) -Jrampup=$($s.rampup) -Jloops=$($s.loops) `
        -JrunId=$runId -Jhost=$Host -Jport=$Port

    Write-Host "HTML-отчёт: $report\index.html`n" -ForegroundColor Green
}
