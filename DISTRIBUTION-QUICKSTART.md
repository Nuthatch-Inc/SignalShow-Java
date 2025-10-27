# Quick Start: Distribution

## For End Users

### Download Options

**macOS:**
- Download `SignalShow-1.0.0.dmg`
- Open DMG, drag to Applications
- No Java installation needed

**Windows:**
- Download `SignalShow-1.0.0.exe`
- Run installer, follow wizard
- No Java installation needed

**Any Platform (requires Java 17+):**
- Download `signalshow-1.0.0-SNAPSHOT.jar`
- Run: `java -jar signalshow-1.0.0-SNAPSHOT.jar`

---

## For Developers/Distributors

### Quick Build Commands

```bash
# Build JAR only (cross-platform)
mvn clean package
# → target/signalshow-1.0.0-SNAPSHOT.jar

# Build Mac DMG installer
./build-installer-mac.sh
# → target/dist/SignalShow-1.0.0.dmg

# Build Windows installer (run on Windows)
./build-installer-windows.bat
# → target/dist/SignalShow-1.0.0.exe
```

### Automated Builds (GitHub Actions)

```bash
# Tag a version to trigger automated build
git tag v1.0.0
git push origin v1.0.0

# Check GitHub Actions tab for build progress
# Installers will be attached to the GitHub Release
```

---

## Distribution Workflow

1. **Develop** → Make changes to code
2. **Test** → `mvn clean package && mvn exec:java`
3. **Tag** → `git tag v1.0.0 && git push origin v1.0.0`
4. **Wait** → GitHub Actions builds Mac + Windows installers
5. **Review** → Check draft release on GitHub
6. **Publish** → Approve release, installers go public
7. **Distribute** → Users download from GitHub Releases

---

## File Locations

After building:

```
target/
├── signalshow-1.0.0-SNAPSHOT.jar    # Cross-platform JAR
└── dist/
    ├── SignalShow-1.0.0.dmg         # macOS installer
    └── SignalShow-1.0.0.exe         # Windows installer
```

---

## Need Help?

- Full guide: See `DISTRIBUTION.md`
- Build issues: Check Java 17+ is installed, jpackage available
- Windows: Need WiX Toolset for .msi installers
- Code signing: See DISTRIBUTION.md for details
