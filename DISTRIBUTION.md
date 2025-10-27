# SignalShow Distribution Guide

## Overview

SignalShow can be distributed as native installers for Mac and Windows, or as a cross-platform JAR file.

## Distribution Options

### 1. Native Installers (Recommended)

Professional installers with bundled Java runtime - users don't need to install Java.

#### macOS (.dmg)

- Drag-to-install disk image
- Bundles Java 25 runtime
- ~100-150 MB download
- Works on macOS 10.15+

#### Windows (.exe or .msi)

- Standard Windows installer
- Bundles Java 25 runtime
- ~100-150 MB download
- Works on Windows 10+

### 2. Cross-Platform JAR

Universal Java application - requires users to have Java installed.

- Single file works on Mac, Windows, Linux
- ~5-10 MB download
- Requires Java 17+ installed
- Run with: `java -jar signalshow-1.0.0-SNAPSHOT.jar`

---

## Building Installers

### Prerequisites

**For Mac builds:**

- macOS computer
- Java 17 or later (with jpackage)
- Xcode Command Line Tools

**For Windows builds:**

- Windows computer
- Java 17 or later (with jpackage)
- WiX Toolset 3.11+ (for .msi) - https://wixtoolset.org/

### Local Builds

**Build Mac installer:**

```bash
./build-installer-mac.sh
# Creates: target/dist/SignalShow-1.0.0.dmg
```

**Build Windows installer:**

```bash
./build-installer-windows.sh
# Creates: target/dist/SignalShow-1.0.0.exe or .msi
```

**Build current platform:**

```bash
./build-all-platforms.sh
```

### Automated GitHub Actions Builds

The repository includes a GitHub Actions workflow that automatically builds installers for both platforms.

**Trigger automated build:**

1. **On version tag:**

   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

   This creates a draft release with installers attached.

2. **Manual trigger:**
   - Go to GitHub Actions tab
   - Select "Build Native Installers" workflow
   - Click "Run workflow"

**Artifacts:**

- Builds run on macOS and Windows runners
- Installers saved as artifacts (90 days)
- On version tags: Creates GitHub release with installers

---

## JAR-Only Distribution

If you prefer simple JAR distribution:

**Build:**

```bash
mvn clean package
# Creates: target/signalshow-1.0.0-SNAPSHOT.jar
```

**Distribute:**

1. Upload JAR to your website/server
2. Provide installation instructions:
   ```
   Requirements: Java 17 or later
   Download: signalshow-1.0.0-SNAPSHOT.jar
   Run: java -jar signalshow-1.0.0-SNAPSHOT.jar
   ```

---

## Code Signing (Optional but Recommended)

### macOS

Sign the DMG to avoid Gatekeeper warnings:

```bash
# Get Developer ID certificate from Apple Developer account
# Then sign:
codesign --force --sign "Developer ID Application: Your Name" \
  target/dist/SignalShow-1.0.0.dmg

# Notarize (requires Apple Developer account):
xcrun notarytool submit target/dist/SignalShow-1.0.0.dmg \
  --apple-id your@email.com \
  --team-id TEAMID \
  --password app-specific-password
```

### Windows

Sign the EXE to avoid SmartScreen warnings:

```bash
# Get code signing certificate
# Then sign with signtool:
signtool sign /f certificate.pfx /p password \
  /t http://timestamp.digicert.com \
  target/dist/SignalShow-1.0.0.exe
```

---

## Distribution Checklist

- [ ] Build native installers for Mac and Windows
- [ ] Test installers on clean systems
- [ ] Code sign installers (optional)
- [ ] Create release notes
- [ ] Upload to distribution server or GitHub Releases
- [ ] Update website with download links
- [ ] Create installation guide for users

---

## File Sizes

Approximate download sizes:

| Format          | Size    | Java Included |
| --------------- | ------- | ------------- |
| JAR only        | ~8 MB   | No            |
| macOS DMG       | ~120 MB | Yes           |
| Windows EXE/MSI | ~130 MB | Yes           |

---

## Support

For distribution questions, see:

- Maven jpackage plugin: https://github.com/petr-panteleyev/jpackage-maven-plugin
- Java jpackage docs: https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html
