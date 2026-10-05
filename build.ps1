# YT Router - PowerShell Build Script
# Usage: .\build.ps1 -KeystorePassword "YTRouter2026!@"

param(
    [string]$KeystorePassword = ""
)

$ErrorActionPreference = "Stop"

# ============================================
# Detect Android SDK
# ============================================
$AndroidSDK = "$env:LOCALAPPDATA\Android\Sdk"
if (-not (Test-Path $AndroidSDK)) {
    Write-Error "Android SDK not found at $AndroidSDK"
    exit 1
}

Write-Host "Using Android SDK: $AndroidSDK" -ForegroundColor Green

# Find latest build-tools
$BuildToolsDir = Get-ChildItem "$AndroidSDK\build-tools" | Sort-Object Name -Descending | Select-Object -First 1
if (-not $BuildToolsDir) {
    Write-Error "No build-tools found"
    exit 1
}

Write-Host "Using build-tools: $($BuildToolsDir.Name)" -ForegroundColor Green

$AAPT = "$($BuildToolsDir.FullName)\aapt.exe"
$D8 = "$($BuildToolsDir.FullName)\d8.bat"
$ZIPALIGN = "$($BuildToolsDir.FullName)\zipalign.exe"
$APKSIGNER = "$($BuildToolsDir.FullName)\apksigner.bat"

# Find platform
$Platform = Get-ChildItem "$AndroidSDK\platforms\android-*" | Sort-Object Name -Descending | Select-Object -First 1
if (-not $Platform) {
    Write-Error "No Android platform found"
    exit 1
}

$AndroidJar = "$($Platform.FullName)\android.jar"
Write-Host "Using android.jar from $($Platform.Name)" -ForegroundColor Green

# ============================================
# Clean
# ============================================
Write-Host "`nCleaning..." -ForegroundColor Cyan
if (Test-Path out) { Remove-Item -Recurse -Force out }
if (Test-Path bin) { Remove-Item -Recurse -Force bin }
New-Item -ItemType Directory -Path out | Out-Null
New-Item -ItemType Directory -Path bin | Out-Null

# ============================================
# Compile Java
# ============================================
Write-Host "`nCompiling Java sources..." -ForegroundColor Cyan
& javac -source 8 -target 8 -bootclasspath $AndroidJar -d bin src\yt\router\android\*.java
if ($LASTEXITCODE -ne 0) {
    Write-Error "Java compilation failed"
    exit 1
}

# ============================================
# Convert to DEX
# ============================================
Write-Host "`nConverting to DEX..." -ForegroundColor Cyan
& $D8 --output out bin\yt\router\android\*.class
if ($LASTEXITCODE -ne 0) {
    Write-Error "DEX conversion failed"
    exit 1
}

Move-Item out\classes.dex out\classes.dex.tmp
New-Item -ItemType Directory -Path out\classes | Out-Null
Move-Item out\classes.dex.tmp out\classes\classes.dex

# ============================================
# Package resources
# ============================================
Write-Host "`nPackaging resources..." -ForegroundColor Cyan
& $AAPT package -f -m -J src -M AndroidManifest.xml -S res -I $AndroidJar
if ($LASTEXITCODE -ne 0) {
    Write-Error "Resource packaging failed"
    exit 1
}

# ============================================
# Create APK
# ============================================
Write-Host "`nAdding DEX to APK..." -ForegroundColor Cyan
& $AAPT package -f -M AndroidManifest.xml -S res -I $AndroidJar -F out\unaligned.apk out\classes
if ($LASTEXITCODE -ne 0) {
    Write-Error "APK creation failed"
    exit 1
}

# ============================================
# Align
# ============================================
Write-Host "`nAligning APK..." -ForegroundColor Cyan
& $ZIPALIGN -f 4 out\unaligned.apk out\YT-Router-unsigned.apk
if ($LASTEXITCODE -ne 0) {
    Write-Error "Alignment failed"
    exit 1
}

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "Built: out\YT-Router-unsigned.apk" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# ============================================
# Sign (if password provided)
# ============================================
if ([string]::IsNullOrEmpty($KeystorePassword)) {
    Write-Host "`nTo sign the APK, provide -KeystorePassword parameter:" -ForegroundColor Yellow
    Write-Host '  .\build.ps1 -KeystorePassword "YTRouter2026!@"' -ForegroundColor Yellow
    exit 0
}

if (-not (Test-Path ytrouter-release.jks)) {
    Write-Error "ytrouter-release.jks not found"
    exit 1
}

Write-Host "`nSigning APK..." -ForegroundColor Cyan
& $APKSIGNER sign --ks ytrouter-release.jks --ks-key-alias ytrouter --ks-pass "pass:$KeystorePassword" --key-pass "pass:$KeystorePassword" --out out\YT-Router.apk out\YT-Router-unsigned.apk
if ($LASTEXITCODE -ne 0) {
    Write-Error "APK signing failed"
    exit 1
}

Write-Host "`nVerifying signature..." -ForegroundColor Cyan
& $APKSIGNER verify out\YT-Router.apk
if ($LASTEXITCODE -ne 0) {
    Write-Error "APK verification failed"
    exit 1
}

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "Success! Signed APK: out\YT-Router.apk" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

$hash = (Get-FileHash out\YT-Router.apk -Algorithm SHA256).Hash
Write-Host "SHA256: $hash" -ForegroundColor Cyan
