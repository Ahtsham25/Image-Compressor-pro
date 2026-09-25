# Google Play Store Release & Keystore Secrets Guide

This guide explains how to generate your release keystore, add the secrets to GitHub, and automatically build signed `.aab` (Android App Bundle) and `.apk` files for Google Play Store upload.

---

## 1. Generate Release Keystore (Run once on your computer)

Open your terminal or command prompt and run:

```bash
keytool -genkey -v -keystore release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias imagecompressor
```

You will be asked to choose:
1. Keystore Password (e.g. `MyStrongPass123!`)
2. Key Password
3. Name / Organization

---

## 2. Convert Keystore to Base64 (For GitHub Secrets)

### On Linux / macOS / Git Bash:
```bash
base64 -i release.jks | tr -d '\n' > keystore_base64.txt
```

### On Windows PowerShell:
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.jks")) | Set-Content keystore_base64.txt
```

---

## 3. Add Secrets to GitHub Repository

1. Open your repository on GitHub: `https://github.com/<your-username>/<repo-name>`
2. Go to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret** and add these 4 secrets:

| Secret Name | Value |
|---|---|
| `KEYSTORE_BASE64` | Paste entire content of `keystore_base64.txt` |
| `KEYSTORE_PASSWORD` | The password you entered for the keystore |
| `KEY_ALIAS` | `imagecompressor` |
| `KEY_PASSWORD` | The key password you entered |

---

## 4. Automatic Build & Play Store Download

Whenever you push to `main` (or click **Actions** → **Build & Package Android APK & AAB** → **Run workflow**):

- GitHub Actions builds your signed **PlayStore-Release-AAB** (`.aab` bundle).
- Download the `.aab` file from the **Artifacts** section.
- Go to [Google Play Console](https://play.google.com/console) → **Release** → **Production** (or Internal Testing) → **Upload AAB**.

---

## 5. Contact & Support

For queries or developer assistance:
- **WhatsApp:** +92 308 7179003 ([Chat on WhatsApp](https://wa.me/923087179003))
- **Email:** princeshami365@gmail.com
