# Code Signing Guide for SignalShow

This document explains how to properly sign SignalShow binaries for macOS and Windows distribution to avoid security warnings for end users.

## Overview

Currently, the SignalShow builds are **unsigned**, which means users will see security warnings when trying to install or run the application. Code signing provides:

- **User Trust**: Verifies the software comes from you and hasn't been tampered with
- **No Security Warnings**: Eliminates scary dialogs that discourage installation
- **Professional Distribution**: Required for enterprise and professional deployments
- **App Store Distribution**: Required if you ever want to distribute via app stores

---

## 🍎 macOS Code Signing

### Prerequisites

1. **Apple Developer Account** ($99/year)

   - Sign up at: https://developer.apple.com/programs/
   - Required for obtaining certificates and notarization

2. **Developer ID Application Certificate**

   - Obtained from Apple Developer portal → Certificates, IDs & Profiles
   - Used to sign applications distributed outside the Mac App Store
   - Download and install on your Mac

3. **Notarization** (required for macOS 10.15+)
   - Apple's malware scanning and approval process
   - Required for apps to run without warnings on modern macOS
   - Automated through command-line tools

### Step 1: Obtain Developer ID Certificate

1. Log in to https://developer.apple.com/account
2. Navigate to **Certificates, Identifiers & Profiles**
3. Click **+** to create a new certificate
4. Select **Developer ID Application**
5. Follow the prompts to create a Certificate Signing Request (CSR)
6. Download the certificate and double-click to install in Keychain

### Step 2: Export Certificate for CI/CD

To use the certificate in GitHub Actions, you need to export it:

```bash
# On your Mac, open Keychain Access
# Find your "Developer ID Application" certificate
# Right-click → Export "Developer ID Application: Your Name"
# Save as DeveloperID.p12
# Set a strong password when prompted

# Base64 encode the certificate for GitHub Secrets
base64 -i DeveloperID.p12 | pbcopy
# This copies the base64 string to your clipboard
```

### Step 3: Create App-Specific Password

For notarization, you need an app-specific password:

1. Go to https://appleid.apple.com
2. Sign in with your Apple ID
3. Navigate to **Security** → **App-Specific Passwords**
4. Click **+** to generate a new password
5. Label it "SignalShow Notarization"
6. Save the generated password (you'll need it for GitHub Secrets)

### Step 4: Find Your Team ID

```bash
# Your Team ID is visible in your Developer Account
# Or run this command on your Mac:
security find-certificate -c "Developer ID Application" -p | \
  openssl x509 -noout -text | grep "OU="
```

### Step 5: Add GitHub Secrets

Go to your repository: `https://github.com/Nuthatch-Inc/SignalShow-Java/settings/secrets/actions`

Add these secrets:

| Secret Name                  | Value                      | Description                       |
| ---------------------------- | -------------------------- | --------------------------------- |
| `APPLE_CERTIFICATE_BASE64`   | (paste base64 from Step 2) | Your Developer ID certificate     |
| `APPLE_CERTIFICATE_PASSWORD` | (password from Step 2)     | Password for the .p12 file        |
| `APPLE_ID`                   | your@email.com             | Your Apple ID email               |
| `APPLE_TEAM_ID`              | ABC123XYZ                  | Your 10-character Team ID         |
| `APPLE_APP_PASSWORD`         | xxxx-xxxx-xxxx-xxxx        | App-specific password from Step 3 |

### Step 6: Update GitHub Actions Workflow

Add these steps to your macOS build job in `.github/workflows/build-installers.yml`:

```yaml
- name: Import Code Signing Certificate
  env:
    CERTIFICATE_BASE64: ${{ secrets.APPLE_CERTIFICATE_BASE64 }}
    CERTIFICATE_PASSWORD: ${{ secrets.APPLE_CERTIFICATE_PASSWORD }}
  run: |
    # Decode certificate
    echo "$CERTIFICATE_BASE64" | base64 --decode > certificate.p12

    # Create temporary keychain
    security create-keychain -p temp_password build.keychain
    security default-keychain -s build.keychain
    security unlock-keychain -p temp_password build.keychain

    # Import certificate
    security import certificate.p12 -k build.keychain \
      -P "$CERTIFICATE_PASSWORD" -T /usr/bin/codesign

    # Allow codesign to access the certificate
    security set-key-partition-list -S apple-tool:,apple: \
      -s -k temp_password build.keychain

- name: Build and Sign macOS DMG
  env:
    APPLE_TEAM_ID: ${{ secrets.APPLE_TEAM_ID }}
  run: |
    # Get the certificate identity
    CERT_IDENTITY=$(security find-identity -v -p codesigning build.keychain | \
      grep "Developer ID Application" | head -1 | \
      sed 's/.*"\(.*\)"/\1/')

    echo "Using certificate: $CERT_IDENTITY"

    # Build with jpackage (includes signing)
    jpackage \
      --type dmg \
      --name SignalShow \
      --app-version 1.0.0 \
      --input target \
      --main-jar signalshow-1.0.0-SNAPSHOT.jar \
      --main-class SignalShow \
      --dest target/dist \
      --vendor "SignalShow" \
      --copyright "Copyright © 2005-2025 SignalShow" \
      --description "Educational signal and image processing application" \
      --mac-package-identifier org.signalshow \
      --mac-package-name SignalShow \
      --mac-sign \
      --mac-signing-key-user-name "$CERT_IDENTITY" \
      --java-options -Xmx2g \
      --icon assets/icons/SignalShow.icns

- name: Notarize macOS App
  env:
    APPLE_ID: ${{ secrets.APPLE_ID }}
    APPLE_APP_PASSWORD: ${{ secrets.APPLE_APP_PASSWORD }}
    APPLE_TEAM_ID: ${{ secrets.APPLE_TEAM_ID }}
  run: |
    # Find the DMG file
    DMG_FILE=$(ls target/dist/*.dmg)
    echo "Notarizing: $DMG_FILE"

    # Submit for notarization
    xcrun notarytool submit "$DMG_FILE" \
      --apple-id "$APPLE_ID" \
      --password "$APPLE_APP_PASSWORD" \
      --team-id "$APPLE_TEAM_ID" \
      --wait

    # Staple the notarization ticket to the DMG
    xcrun stapler staple "$DMG_FILE"

    # Verify the staple
    xcrun stapler validate "$DMG_FILE"

- name: Cleanup Keychain
  if: always()
  run: |
    security delete-keychain build.keychain || true
    rm -f certificate.p12
```

### Testing Locally

Before pushing to GitHub, test signing locally:

```bash
# Build the DMG
./build-installer-mac.sh

# Sign the DMG (if not already signed by jpackage)
codesign --force --sign "Developer ID Application: Your Name (TEAM_ID)" \
  target/dist/SignalShow-1.0.0.dmg

# Verify the signature
codesign --verify --verbose target/dist/SignalShow-1.0.0.dmg

# Check the signature details
codesign -dvv target/dist/SignalShow-1.0.0.dmg

# Notarize (this will take a few minutes)
xcrun notarytool submit target/dist/SignalShow-1.0.0.dmg \
  --apple-id "your@email.com" \
  --password "xxxx-xxxx-xxxx-xxxx" \
  --team-id "ABC123XYZ" \
  --wait

# Staple the ticket
xcrun stapler staple target/dist/SignalShow-1.0.0.dmg
```

---

## 🪟 Windows Code Signing

### Prerequisites

1. **Code Signing Certificate**

   - Purchase from a Certificate Authority (CA)
   - Recommended providers:
     - **Sectigo** (formerly Comodo): ~$200/year
     - **DigiCert**: ~$400/year
     - **SSL.com**: ~$200/year
   - You'll need to verify your identity (individual) or company

2. **Certificate Format**

   - `.pfx` or `.p12` file containing private key
   - Password to protect the certificate

3. **Windows SDK** (for signtool)
   - Included with Visual Studio
   - Or install Windows 10/11 SDK separately

### Step 1: Purchase and Obtain Certificate

1. Choose a Certificate Authority from the list above
2. Select "Code Signing Certificate" (not SSL/TLS)
3. Complete identity verification:
   - **Individual**: Government-issued ID, phone verification
   - **Organization**: Business registration documents, phone verification
4. Generate CSR (Certificate Signing Request) when prompted
5. Download the certificate in `.pfx` format
6. Set a strong password for the certificate

### Step 2: Export Certificate for CI/CD

```powershell
# On Windows, base64 encode your certificate
[Convert]::ToBase64String([IO.File]::ReadAllBytes("C:\path\to\certificate.pfx")) | `
  Set-Clipboard
# This copies the base64 string to your clipboard
```

Or on macOS/Linux:

```bash
base64 -i certificate.pfx | pbcopy  # macOS
base64 -w 0 certificate.pfx | xclip -selection clipboard  # Linux
```

### Step 3: Add GitHub Secrets

Add these secrets to your GitHub repository:

| Secret Name                    | Value                       | Description                   |
| ------------------------------ | --------------------------- | ----------------------------- |
| `WINDOWS_CERTIFICATE_BASE64`   | (paste base64 from Step 2)  | Your code signing certificate |
| `WINDOWS_CERTIFICATE_PASSWORD` | (your certificate password) | Password for the .pfx file    |

### Step 4: Update GitHub Actions Workflow

Add these steps to your Windows build job in `.github/workflows/build-installers.yml`:

```yaml
- name: Import Code Signing Certificate
  env:
    CERTIFICATE_BASE64: ${{ secrets.WINDOWS_CERTIFICATE_BASE64 }}
    CERTIFICATE_PASSWORD: ${{ secrets.WINDOWS_CERTIFICATE_PASSWORD }}
  shell: pwsh
  run: |
    # Decode certificate to file
    $bytes = [Convert]::FromBase64String($env:CERTIFICATE_BASE64)
    [IO.File]::WriteAllBytes("$env:TEMP\certificate.pfx", $bytes)

    # Import to certificate store
    $password = ConvertTo-SecureString -String $env:CERTIFICATE_PASSWORD `
      -AsPlainText -Force
    Import-PfxCertificate -FilePath "$env:TEMP\certificate.pfx" `
      -CertStoreLocation Cert:\CurrentUser\My `
      -Password $password

- name: Build Windows Installer
  shell: bash
  run: |
    chmod +x build-installer-windows.sh
    ./build-installer-windows.sh

- name: Sign Windows Installer
  env:
    CERTIFICATE_PASSWORD: ${{ secrets.WINDOWS_CERTIFICATE_PASSWORD }}
  shell: pwsh
  run: |
    # Find signtool (part of Windows SDK)
    $signtool = Get-ChildItem -Path "C:\Program Files (x86)\Windows Kits" `
      -Recurse -Filter "signtool.exe" | Select-Object -First 1 -ExpandProperty FullName

    if (-not $signtool) {
      Write-Error "signtool.exe not found. Install Windows SDK."
      exit 1
    }

    Write-Output "Using signtool: $signtool"

    # Find the installer files
    $installers = Get-ChildItem -Path "target\dist" -Filter "SignalShow*"

    foreach ($installer in $installers) {
      Write-Output "Signing: $($installer.FullName)"
      
      # Sign the installer
      & $signtool sign /f "$env:TEMP\certificate.pfx" `
        /p "$env:CERTIFICATE_PASSWORD" `
        /t http://timestamp.digicert.com `
        /fd SHA256 `
        /d "SignalShow" `
        /du "https://github.com/Nuthatch-Inc/SignalShow-Java" `
        $installer.FullName
      
      if ($LASTEXITCODE -ne 0) {
        Write-Error "Signing failed for $($installer.Name)"
        exit 1
      }
      
      # Verify the signature
      & $signtool verify /pa $installer.FullName
    }

- name: Cleanup Certificate
  if: always()
  shell: pwsh
  run: |
    Remove-Item -Path "$env:TEMP\certificate.pfx" -ErrorAction SilentlyContinue
```

### Testing Locally (Windows)

```powershell
# Build the installer
.\build-installer-windows.sh

# Sign the MSI/EXE
signtool sign /f certificate.pfx `
  /p "YourPassword" `
  /t http://timestamp.digicert.com `
  /fd SHA256 `
  /d "SignalShow" `
  target\dist\SignalShow-1.0.0.msi

# Verify the signature
signtool verify /pa target\dist\SignalShow-1.0.0.msi

# Check signature details
Get-AuthenticodeSignature target\dist\SignalShow-1.0.0.msi | Format-List
```

---

## 💰 Cost Summary

| Item                                 | Provider | Annual Cost       | Notes                                     |
| ------------------------------------ | -------- | ----------------- | ----------------------------------------- |
| **Apple Developer Account**          | Apple    | $99               | Required for macOS signing & notarization |
| **Windows Code Signing Certificate** | Sectigo  | ~$200             | Individual certificate                    |
| **Windows Code Signing Certificate** | DigiCert | ~$400             | More trusted, faster issuance             |
| **Windows EV Certificate**           | Various  | ~$300-500         | Instant SmartScreen reputation            |
| **Total (Standard)**                 | -        | **$300-500/year** | Basic signing for both platforms          |
| **Total (EV Windows)**               | -        | **$400-600/year** | Recommended for professional release      |

### Certificate Comparison

| Type                      | Cost       | SmartScreen Reputation | Validation Time |
| ------------------------- | ---------- | ---------------------- | --------------- |
| **Standard Code Signing** | ~$200/year | Must build over time   | 1-3 days        |
| **EV Code Signing**       | ~$400/year | Immediate reputation   | 3-7 days        |

**Recommendation**: Start with standard certificates. Upgrade to EV for Windows if you get many SmartScreen warnings.

---

## 🚀 Quick Start Without Signing (Testing/Beta)

If you want to release **now** without purchasing certificates:

### macOS Workaround

Users must:

1. Right-click the app → **Open** (first time only)
2. Click **Open** in the security dialog
3. App will run normally after first launch

Or disable Gatekeeper temporarily (not recommended):

```bash
sudo spctl --master-disable  # Disable Gatekeeper
# Install app
sudo spctl --master-enable   # Re-enable Gatekeeper
```

### Windows Workaround

Users will see Windows SmartScreen:

1. Click **More info**
2. Click **Run anyway**
3. App will install/run normally

### When Unsigned is Acceptable

- Internal testing and beta releases
- Open source projects with technical users
- Academic/research distribution
- Development builds

### When Signing is Required

- Public commercial release
- Enterprise deployment
- App store distribution
- Professional/production software

---

## 🔒 Security Best Practices

### Protect Your Certificates

1. **Never commit certificates to Git**

   - Add `*.p12`, `*.pfx`, `*.pem` to `.gitignore`
   - Use GitHub Secrets for CI/CD

2. **Use strong passwords**

   - Minimum 16 characters
   - Store in a password manager

3. **Rotate certificates before expiration**

   - Most certificates are valid for 1-3 years
   - Set calendar reminders 30 days before expiration

4. **Backup certificates securely**
   - Store in encrypted storage
   - Keep offline backup

### Verify Signatures After Building

```bash
# macOS
codesign --verify --verbose YourApp.dmg
spctl --assess --type install YourApp.dmg

# Windows
signtool verify /pa YourApp.exe
Get-AuthenticodeSignature YourApp.exe
```

---

## 📚 Additional Resources

### macOS Code Signing

- [Apple Developer Documentation](https://developer.apple.com/documentation/security/notarizing_macos_software_before_distribution)
- [Notarization Guide](https://developer.apple.com/documentation/security/notarizing_macos_software_before_distribution/customizing_the_notarization_workflow)
- [Code Signing Guide](https://developer.apple.com/library/archive/documentation/Security/Conceptual/CodeSigningGuide/Introduction/Introduction.html)

### Windows Code Signing

- [Microsoft Code Signing Best Practices](https://docs.microsoft.com/en-us/windows-hardware/drivers/install/code-signing-best-practices)
- [SignTool Documentation](https://docs.microsoft.com/en-us/windows/win32/seccrypto/signtool)
- [Certificate Authorities List](https://docs.microsoft.com/en-us/security/trusted-root/participants-list)

### Certificate Providers

- [Sectigo Code Signing](https://sectigo.com/ssl-certificates-tls/code-signing)
- [DigiCert Code Signing](https://www.digicert.com/signing/code-signing-certificates)
- [SSL.com Code Signing](https://www.ssl.com/certificates/code-signing/)

---

## 🛠️ Troubleshooting

### macOS Issues

**"No signing identity found"**

```bash
# List available identities
security find-identity -v -p codesigning

# If none found, reimport certificate
security import certificate.p12 -k ~/Library/Keychains/login.keychain-db
```

**Notarization fails with "Invalid"**

- Ensure hardened runtime is enabled
- Check that all binaries in the app bundle are signed
- Verify bundle identifier matches your provisioning profile

**"Could not find the altool"**

```bash
# Install Xcode command line tools
xcode-select --install

# Or use notarytool (recommended for macOS 13+)
xcrun notarytool submit ...
```

### Windows Issues

**"No certificates were found"**

```powershell
# List installed certificates
Get-ChildItem -Path Cert:\CurrentUser\My -CodeSigningCert

# Reimport certificate
Import-PfxCertificate -FilePath certificate.pfx -CertStoreLocation Cert:\CurrentUser\My
```

**SmartScreen blocks signed app**

- This is normal for new certificates
- Reputation builds over time with downloads
- Consider EV certificate for immediate reputation

**Timestamp server timeout**

- Try alternative timestamp servers:
  - `http://timestamp.sectigo.com`
  - `http://timestamp.comodoca.com`
  - `http://timestamp.digicert.com`

---

## ✅ Checklist for First-Time Setup

- [ ] Purchase Apple Developer account ($99/year)
- [ ] Create Developer ID Application certificate
- [ ] Export certificate as .p12 with password
- [ ] Generate app-specific password for notarization
- [ ] Find Apple Team ID
- [ ] Purchase Windows code signing certificate ($200-500/year)
- [ ] Complete identity verification (3-7 days)
- [ ] Download certificate as .pfx with password
- [ ] Base64 encode both certificates
- [ ] Add all secrets to GitHub repository
- [ ] Update GitHub Actions workflow with signing steps
- [ ] Test signing locally before pushing
- [ ] Create new release tag to trigger signed build
- [ ] Verify signatures on downloaded installers
- [ ] Document certificate expiration dates

---

## 📅 Maintenance Schedule

| Task                                        | Frequency | When                      |
| ------------------------------------------- | --------- | ------------------------- |
| Renew Apple Developer membership            | Annual    | 30 days before expiration |
| Renew Windows certificate                   | 1-3 years | 30 days before expiration |
| Update GitHub secrets with new certificates | As needed | After renewal             |
| Test signing process                        | Monthly   | Before major releases     |
| Verify certificate validity                 | Quarterly | Beginning of quarter      |

---

## 🎓 Summary

Code signing is an investment in professional software distribution:

1. **macOS**: Requires Apple Developer ($99/year) + notarization setup
2. **Windows**: Requires code signing certificate ($200-500/year)
3. **Total annual cost**: ~$300-600
4. **Setup time**: 1-2 weeks (including identity verification)
5. **Maintenance**: Minimal, mostly annual renewals

**Worth it?** Absolutely, if you're distributing to non-technical users or want professional credibility.

**Not yet ready?** Unsigned builds work fine for beta testing, development, and technical audiences who know how to bypass security warnings.
