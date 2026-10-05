#!/bin/bash
# Create your own release key ONCE. Back it up. Never commit it. Losing it means users must uninstall to update.
set -e
keytool -genkeypair -v -keystore ytrouter-release.jks -alias ytrouter \
  -keyalg RSA -keysize 2048 -validity 10000
echo "Created ytrouter-release.jks - back it up somewhere safe (not in git)."
