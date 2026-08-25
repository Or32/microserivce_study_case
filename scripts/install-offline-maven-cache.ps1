param(
    [string]$OfflineBundle = ""
)

$ErrorActionPreference = "Stop"
$projectDir = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path

if ([string]::IsNullOrWhiteSpace($OfflineBundle)) {
    $OfflineBundle = Join-Path $projectDir "offline-bundle"
}

$cacheArchive = Join-Path $OfflineBundle "maven-cache.tar.gz"
if (-not (Test-Path -LiteralPath $cacheArchive -PathType Leaf)) {
    throw "Maven cache archive not found: $cacheArchive"
}

$m2Dir = if ($env:M2_REPO_HOME) { $env:M2_REPO_HOME } else { Join-Path $env:USERPROFILE ".m2" }
New-Item -ItemType Directory -Force -Path $m2Dir | Out-Null

& tar.exe -C $m2Dir -xzf $cacheArchive
if ($LASTEXITCODE -ne 0) { throw "Could not install the Maven cache." }

Write-Host "Installed Maven cache into $m2Dir"
