# Sign YT Router on Windows (PowerShell). No compiling needed - just sign the unsigned APK.
# Requirements: JDK (keytool) and Android SDK Build-Tools (apksigner). Android Studio includes both.
# Run:  powershell -ExecutionPolicy Bypass -File .\sign.ps1 -Apk .\YT-Router-v1.0-unsigned.apk
param(
    [string]$Apk      = ".\YT-Router-v1.0-unsigned.apk",
    [string]$Keystore = ".\ytrouter-release.jks",
    [string]$Alias    = "ytrouter",
    [string]$Out      = ".\YT-Router.apk"
)
$ErrorActionPreference = "Stop"

if (-not (Test-Path $Apk)) { throw "APK not found: $Apk" }

# --- locate apksigner (Android SDK Build-Tools) ---
$sdk = $env:ANDROID_HOME
if (-not $sdk) { $sdk = $env:ANDROID_SDK_ROOT }
if (-not $sdk) { $sdk = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
$apksigner = Get-ChildItem (Join-Path $sdk "build-tools") -Recurse -Filter "apksigner.bat" -ErrorAction SilentlyContinue |
    Sort-Object FullName -Descending | Select-Object -First 1
if (-not $apksigner) {
    throw "apksigner.bat not found. Install 'Android SDK Build-Tools' (Android Studio > SDK Manager) or set ANDROID_HOME."
}

# --- locate keytool (JDK) ---
$keytool = (Get-Command keytool -ErrorAction SilentlyContinue).Source
if (-not $keytool) {
    $candidates = @(
        "$env:ProgramFiles\Android\Android Studio\jbr\bin\keytool.exe",
        "$env:JAVA_HOME\bin\keytool.exe"
    )
    foreach ($c in $candidates) { if (Test-Path $c) { $keytool = $c; break } }
}

# --- create the key once (asks for a password and details; back it up, never commit it) ---
if (-not (Test-Path $Keystore)) {
    if (-not $keytool) { throw "keytool not found. Install a JDK or Android Studio." }
    Write-Host "Creating your release key: $Keystore"
    & $keytool -genkeypair -v -keystore $Keystore -alias $Alias -keyalg RSA -keysize 2048 -validity 10000
    if ($LASTEXITCODE -ne 0) { throw "keytool failed" }
}

# --- sign (apksigner asks for the keystore password; it is not stored or put on the command line) ---
& $apksigner.FullName sign --ks $Keystore --ks-key-alias $Alias --out $Out $Apk
if ($LASTEXITCODE -ne 0) { throw "signing failed" }
& $apksigner.FullName verify --verbose $Out
Write-Host ""
Write-Host "Signed: $Out"
Get-FileHash $Out -Algorithm SHA256 | Format-List Hash, Path
