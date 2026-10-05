# YT Router

**English** | [中文](#中文)

[![Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Development-FF5E5B?logo=ko-fi&logoColor=white)](https://ko-fi.com/shawn82)

A tiny Android app that routes links to the apps you choose.
Set it as your default browser, then pick which app opens YouTube links and which browser opens everything else.

- Package: `yt.router.android` · Android 6.0+ (API 23)
- No permissions, no internet access, no ads, no tracking
- Dark UI, English / 中文 by system language

## Features

- YouTube links (`youtube.com`, `m.youtube.com`, `youtu.be`) → the YouTube app you choose (third-party clients such as Morphe, NewPipe, or the official app)
- Everything else, including local `.html` files → the browser you choose (default: Chrome first)
- Auto-detects installed YouTube apps and browsers; falls back safely if one is removed
- Detects whether the official YouTube app is still grabbing links, with a shortcut to fix it

## Why Use YT Router?

**Problem:** Many third-party YouTube clients (Morphe, NewPipe, ReVanced, etc.) cannot become the default app for YouTube links on Android. Even if you prefer these clients, clicking a YouTube link in messages, emails, or browsers often opens the official YouTube app or shows a system chooser every time.

**Solution:** YT Router acts as a bridge. Set it as your default browser, and it will:
- Route YouTube links to your preferred third-party client
- Route all other links to your preferred browser
- Give you full control over which app opens what

### Common Use Cases

- **Use Morphe/NewPipe/ReVanced by default** instead of official YouTube
- **Force all YouTube links to open in a specific app** instead of asking every time
- **Manage multiple browsers** without relying on Android's limited default settings
- **Privacy-focused users** who want to avoid Google's official YouTube app

## Setup

1. Install the APK from [Releases](../../releases) and allow "install unknown apps".
2. Open YT Router and tap **Set as default**, then choose **YT Router** as the Browser app.
3. Pick your YouTube app and browser inside YT Router.
4. **If the official YouTube app is installed** (Android 12+): open its *Open by default* settings and turn off **Open supported links**. The app stays installed and enabled. Without this, Android hands YouTube links straight to the official app and YT Router never sees them. YT Router shows a card with a shortcut when this is needed.
5. **Google app search results** open in the Google app's built-in browser and skip your default browser. In the Google app: profile picture → Settings → General → turn off *Open web pages in the app*.

Some ROMs hide the link settings. As an alternative, via ADB:

```
adb shell pm set-app-links-allowed --user 0 --package com.google.android.youtube false
```

## Build

### Linux / WSL / Ubuntu

Plain `javac` + Android build tools, no Gradle:

```bash
sudo apt install aapt apksigner zipalign dalvik-exchange libandroid-23-java default-jdk-headless
./gen-key.sh                       # once; back up the .jks and never commit it
KEYSTORE=./ytrouter-release.jks KS_PASS='your_password' ./build.sh
```

Output: `out/YT-Router.apk`

### Windows

Requires Android SDK (automatically detected from Android Studio):

```powershell
# Build and sign in one command
.\build.ps1 -KeystorePassword "YourPassword"

# Or build unsigned APK only (omit password)
.\build.ps1
```

The script will:
- Auto-detect your Android SDK location
- Compile Java → DEX → APK
- Sign with `ytrouter-release.jks` (if password provided)
- Output to `out\YT-Router.apk` (~238KB)

**First-time setup**: Create your signing key once:

```cmd
keytool -genkeypair -v -keystore ytrouter-release.jks -alias ytrouter -keyalg RSA -keysize 2048 -validity 10000
```

Choose a strong password and keep `ytrouter-release.jks` safe — you'll need it to sign all future updates.

**Legacy PowerShell signing script**: If you only need to sign an existing APK:

```powershell
powershell -ExecutionPolicy Bypass -File .\sign.ps1 -Apk .\YT-Router-unsigned.apk
```

It creates your key on first run (asks for a password), signs the APK, verifies it, and prints the SHA-256.

Verify a download: `sha256sum YT-Router.apk` and compare with the checksum in the release notes.

## Notes

- Always sign updates with the same key, otherwise users must uninstall before updating.
- Not affiliated with Google or YouTube. Third-party YouTube clients are separate projects with their own terms.

## Support Development

If you find YT Router useful, consider supporting its development:

[![Ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/shawn82)

## License

MIT License - see [LICENSE](LICENSE) file for details.

---

## 中文

一个很小的 Android 应用，把链接转发到你指定的 app。
把它设为默认浏览器，然后选择用哪个 app 打开 YouTube 链接、用哪个浏览器打开其他链接。

- 包名：`yt.router.android` · 需要 Android 6.0 及以上
- 不申请任何权限、不联网、无广告、无追踪
- 暗黑界面，随系统语言显示中文或英文

### 功能

- YouTube 链接（`youtube.com`、`m.youtube.com`、`youtu.be`）→ 你选的 YouTube app（Morphe、NewPipe 等第三方，或官方版）
- 其他链接和本地 `.html` 文件 → 你选的浏览器（默认优先 Chrome）
- 自动检测已安装的 YouTube app 和浏览器，选中的被卸载时自动回退
- 检测官方 YouTube 是否仍在抢链接，并提供设置快捷入口

### 为什么需要 YT Router？

**问题：** 许多第三方 YouTube 客户端（Morphe、NewPipe、ReVanced 等）无法成为 Android 上 YouTube 链接的默认应用。即使你更喜欢这些客户端，点击消息、邮件或浏览器中的 YouTube 链接时，往往会打开官方 YouTube 应用或每次都弹出系统选择器。

**解决方案：** YT Router 充当桥梁。将它设为默认浏览器后，它会：
- 将 YouTube 链接路由到你偏好的第三方客户端
- 将其他所有链接路由到你偏好的浏览器
- 让你完全控制哪个应用打开什么内容

### 常见使用场景

- **默认使用 Morphe/NewPipe/ReVanced** 而非官方 YouTube
- **强制所有 YouTube 链接在特定应用中打开** 而不是每次都询问
- **管理多个浏览器** 而不依赖 Android 有限的默认设置
- **注重隐私的用户** 想要避免使用 Google 的官方 YouTube 应用

### 使用

1. 从 [Releases](../../releases) 下载 APK 安装，允许"安装未知来源应用"。
2. 打开 YT Router，点 **设为默认**，在系统里把浏览器应用选为 **YT Router**。
3. 在 YT Router 里选好 YouTube app 和浏览器。
4. **如果装了官方 YouTube**（Android 12+）：进入它的"默认打开"设置，关闭**打开支持的链接**。不需要停用或卸载。否则系统会直接把 YouTube 链接交给官方 app，YT Router 收不到。需要时界面会出现提示卡和快捷入口。
5. **Google app 搜索结果**使用 Google app 自带的内置浏览器，不会走默认浏览器。在 Google app：头像 → 设置 → 常规 → 关闭"在应用内打开网页"。

部分 ROM 会隐藏链接设置，也可以用 ADB：

```
adb shell pm set-app-links-allowed --user 0 --package com.google.android.youtube false
```

### 自行编译

#### Linux / WSL / Ubuntu

只需 `javac` 和 Android 构建工具，不用 Gradle：

```bash
sudo apt install aapt apksigner zipalign dalvik-exchange libandroid-23-java default-jdk-headless
./gen-key.sh                       # 只做一次；备份 .jks，不要提交到 git
KEYSTORE=./ytrouter-release.jks KS_PASS='你的密码' ./build.sh
```

输出：`out/YT-Router.apk`

#### Windows

需要 Android SDK（自动从 Android Studio 检测）：

```powershell
# 一键编译并签名
.\build.ps1 -KeystorePassword "你的密码"

# 或仅编译（不签名）
.\build.ps1
```

脚本会自动：
- 检测 Android SDK 位置
- 编译 Java → DEX → APK
- 签名（如果提供了密码）
- 输出到 `out\YT-Router.apk`（约 238KB）

**首次使用**：创建签名密钥（只做一次）：

```cmd
keytool -genkeypair -v -keystore ytrouter-release.jks -alias ytrouter -keyalg RSA -keysize 2048 -validity 10000
```

设置一个强密码，妥善保管 `ytrouter-release.jks` —— 以后所有更新都需要用同一个密钥签名。

**旧版 PowerShell 签名脚本**：如果只需要签名现有 APK：

```powershell
powershell -ExecutionPolicy Bypass -File .\sign.ps1 -Apk .\YT-Router-unsigned.apk
```

第一次运行会创建你的密钥（需要设置密码），然后签名、校验并输出 SHA-256。

校验下载文件：`sha256sum YT-Router.apk`，与 Release 说明里的校验值对比。


### 说明

- 每次更新必须用同一个密钥签名，否则用户要先卸载才能更新。
- 与 Google、YouTube 无关联。第三方 YouTube 客户端是各自独立的项目。

### 许可证

发布前请自行添加许可证文件（例如 MIT）。
