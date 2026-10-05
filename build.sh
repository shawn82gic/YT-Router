#!/bin/bash
# Build YT Router. Linux / WSL / Ubuntu:
#   sudo apt install aapt apksigner zipalign dalvik-exchange libandroid-23-java default-jdk-headless
#
# Sign with your own key by setting environment variables:
#   KEYSTORE=/path/to/ytrouter-release.jks KS_PASS='your-password' ./build.sh
# Optional: KEY_ALIAS (default: ytrouter), KEY_PASS (default: same as KS_PASS)
set -e
AJ=/usr/lib/android-sdk/platforms/android-23/android.jar
rm -rf out && mkdir -p out/classes
javac --release 8 -cp "$AJ" -d out/classes $(find src -name '*.java')
java -cp /usr/share/java/com.android.dx.jar com.android.dx.command.Main --dex --output=out/classes.dex out/classes
aapt package -f -M AndroidManifest.xml -S res -I "$AJ" -F out/unsigned.apk
(cd out && aapt add unsigned.apk classes.dex >/dev/null && zipalign -f 4 unsigned.apk YT-Router-unsigned.apk)
echo "Built: out/YT-Router-unsigned.apk (aligned, NOT signed - cannot be installed yet)"

if [ -z "$KEYSTORE" ] || [ -z "$KS_PASS" ]; then
  echo "Skipping signing: set KEYSTORE and KS_PASS (run ./gen-key.sh to create a key)."
  exit 0
fi
export KEY_PASS="${KEY_PASS:-$KS_PASS}"
apksigner sign --ks "$KEYSTORE" --ks-key-alias "${KEY_ALIAS:-ytrouter}" \
  --ks-pass env:KS_PASS --key-pass env:KEY_PASS \
  --out out/YT-Router.apk out/YT-Router-unsigned.apk
apksigner verify out/YT-Router.apk
echo "Signed: out/YT-Router.apk"
sha256sum out/YT-Router.apk
