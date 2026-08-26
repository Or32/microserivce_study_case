param(
    [string]$OutputDirectory = "offline-images"
)

$ErrorActionPreference = "Stop"
New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null

$images = @(
    "temporalio/auto-setup:1.28.1",
    "postgres:16",
    "temporalio/ui:2.34.0",
    "grafana/loki:3.7.0",
    "grafana/grafana:12.1.0"
)

foreach ($image in $images) {
    $filename = (($image -replace "/", "_") -replace ":", "_") + ".tar"
    $filePath = Join-Path $OutputDirectory $filename
    & docker pull $image
    if ($LASTEXITCODE -ne 0) { throw "Could not pull $image." }
    & docker image save --output $filePath $image
    if ($LASTEXITCODE -ne 0) { throw "Could not save $image." }
    Write-Host "Saved $image to $filePath"
}
