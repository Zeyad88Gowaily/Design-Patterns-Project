# Builds (if needed) and starts MissionOS. Usage: .\run.ps1          -> Swing control panel
#                                                  .\run.ps1 -Console -> console menu
param([switch]$Console)
Set-Location $PSScriptRoot
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    $m = Get-ChildItem "C:\Program Files\Apache Maven" -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($m) { $env:PATH = "$($m.FullName)\bin;$env:PATH" } else { throw "Maven (mvn) not found. Install Maven 3.9+." }
}
if (-not (Test-Path .env)) {
    Set-Content .env "SUPABASE_URL=https://your-project.supabase.co`nSUPABASE_ANON_KEY=your-public-anon-key"
    Write-Host "Created .env - put your Supabase URL and anon key in it, then run again."
    exit 1
}
$argLine = if ($Console) { "--console" } else { "" }
mvn -q compile exec:java "-Dexec.mainClass=nonprofit.Main" "-Dexec.args=$argLine"
