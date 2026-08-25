param(
    [string]$OfflineBundle = "",
    [string]$ImagePlatform = ""
)

$ErrorActionPreference = "Stop"
$projectDir = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path

if ([string]::IsNullOrWhiteSpace($OfflineBundle)) {
    $OfflineBundle = Join-Path $projectDir "offline-bundle"
}

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw "Docker is required to prepare the offline image bundle."
}

if (-not [string]::IsNullOrWhiteSpace($ImagePlatform)) {
    $env:DOCKER_DEFAULT_PLATFORM = $ImagePlatform
}

New-Item -ItemType Directory -Force -Path $OfflineBundle | Out-Null

Write-Host "Resolving Maven plugins and dependencies..."
Push-Location $projectDir
try {
    & (Join-Path $projectDir "mvnw.cmd") -B -U dependency:go-offline
    if ($LASTEXITCODE -ne 0) { throw "Maven dependency resolution failed." }
}
finally {
    Pop-Location
}

$images = @(
    "temporalio/auto-setup:1.28.1",
    "postgres:16",
    "temporalio/ui:2.34.0",
    "grafana/loki:3.7.0",
    "grafana/grafana:12.1.0"
)

Write-Host "Pulling infrastructure container images..."
foreach ($image in $images) {
    $pullArgs = @("pull")
    if (-not [string]::IsNullOrWhiteSpace($ImagePlatform)) {
        $pullArgs += @("--platform", $ImagePlatform)
    }
    $pullArgs += $image
    & docker @pullArgs
    if ($LASTEXITCODE -ne 0) { throw "Could not pull $image." }
}

$imagesArchive = Join-Path $OfflineBundle "images.tar"
Write-Host "Writing image archive: $imagesArchive"
& docker image save --output $imagesArchive @images
if ($LASTEXITCODE -ne 0) { throw "Could not write the image archive." }

$m2Dir = if ($env:M2_REPO_HOME) { $env:M2_REPO_HOME } else { Join-Path $env:USERPROFILE ".m2" }
$mavenArchive = Join-Path $OfflineBundle "maven-cache.tar.gz"
Write-Host "Writing Maven cache archive: $mavenArchive"
& tar.exe -C $m2Dir -czf $mavenArchive repository wrapper
if ($LASTEXITCODE -ne 0) { throw "Could not write the Maven cache archive." }

$manifest = @(
    "Created: $((Get-Date).ToUniversalTime().ToString('yyyy-MM-ddTHH:mm:ssZ'))",
    "Image platform: $(if ($ImagePlatform) { $ImagePlatform } else { 'host default' })",
    "Java: $((& java -version 2>&1 | Select-Object -First 1))",
    "Maven: $((& (Join-Path $projectDir 'mvnw.cmd') -version 2>$null | Select-Object -First 1))"
)
$manifest | Set-Content -Encoding utf8 (Join-Path $OfflineBundle "MANIFEST.txt")

Write-Host "Offline bundle created at $OfflineBundle"
