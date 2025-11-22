# Maven Build System and Distribution

## Overview

SignalShow has been successfully migrated from legacy shell-script-based compilation (circa 2005-2009) to a modern Maven build system with native installer distribution. The migrated codebase is hosted on GitHub at:

**Repository**: `https://github.com/Nuthatch-Inc/SignalShow-Java`  
**Branch**: `feature/maven-migration`

## Build System Architecture

### Maven Configuration

**Group ID**: `org.signalshow`  
**Artifact ID**: `signalshow`  
**Version**: `1.0.0-SNAPSHOT`  
**Java Target**: Java 25 LTS

The `pom.xml` configures:

- **Maven Shade Plugin**: Creates executable JAR with bundled dependencies
- **Maven Exec Plugin**: Runs application via `mvn exec:java`
- **JPackage Plugin**: Generates native installers for macOS and Windows

### Directory Structure

```
SignalShow-Java/
├── pom.xml                      # Maven build configuration
├── src/
│   ├── main/
│   │   ├── java/                # Source code (395 files)
│   │   │   ├── SignalShow.java  # Main class
│   │   │   └── signals/         # Application packages
│   │   └── resources/           # Application resources (339 files)
│   │       ├── images/
│   │       ├── guiIcons/
│   │       ├── demoIcons/
│   │       ├── plotIcons/
│   │       ├── functiondoc/
│   │       └── operationdoc/
│   └── test/java/               # Test sources (future)
├── legacy-build/                # JAI dependencies (not available in Maven Central)
    ├── jai_core.jar            # JAI dependency (installed to local Maven repo)
    └── jai_codec.jar           # JAI dependency (installed to local Maven repo)
├── assets/icons/               # Application icons
│   ├── SignalShowIcon.png      # Source icon (48x48)
│   ├── SignalShow.icns         # macOS icon
│   └── SignalShow.ico          # Windows icon
├── compile.sh                  # Helper: compile only
├── run-maven.sh                # Helper: compile and run
├── package.sh                  # Helper: build JAR
├── run.sh                      # Helper: run from JAR
├── create-mac-icon.sh          # Generate .icns from PNG
├── create-windows-icon.sh      # Generate .ico from PNG
├── build-installer-mac.sh      # Build macOS .dmg
├── build-installer-windows.sh  # Build Windows .exe/.msi
└── .github/workflows/
    └── build-installers.yml    # Automated builds on version tags
```

## Dependencies

### Java Advanced Imaging (JAI)

SignalShow depends on JAI libraries (`jai_core.jar`, `jai_codec.jar`) for image processing operations. Since these are not available in Maven Central, they must be:

1. Stored in `legacy-build/` directory (required for builds)
2. Installed to local Maven repository during initial setup
3. Referenced in `pom.xml` with coordinates:
   - `javax.media.jai:jai-core:1.1.3`
   - `javax.media.jai:jai-codec:1.1.3`

Installation command:

```bash
mvn install:install-file -Dfile=legacy-build/jai_core.jar \
  -DgroupId=javax.media.jai -DartifactId=jai-core \
  -Dversion=1.1.3 -Dpackaging=jar
```

## Build Commands

### Development

**Compile**: `mvn clean compile` or `./compile.sh`  
**Run**: `mvn exec:java` or `./run-maven.sh`  
**Package**: `mvn clean package` or `./package.sh`  
**Run JAR**: `java -jar target/signalshow-1.0.0-SNAPSHOT.jar` or `./run.sh`

### Distribution

**macOS Installer**: `./build-installer-mac.sh` → `target/dist/SignalShow-1.0.0.dmg`  
**Windows Installer**: `./build-installer-windows.sh` → `target/dist/SignalShow-1.0.0.exe`

Both installers:

- Bundle Java Runtime Environment (JRE 17+)
- Include application icon
- Create desktop shortcuts
- Add to system application menu
- Allow directory chooser (Windows)

## Automated Distribution

GitHub Actions workflow (`.github/workflows/build-installers.yml`) automatically builds both macOS and Windows installers when a version tag is pushed:

```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

The workflow:

1. Sets up Java 25 and Maven on both `macos-latest` and `windows-latest` runners
2. Installs JAI dependencies to local Maven repository
3. Generates platform-specific icons (`.icns` for macOS, `.ico` for Windows)
4. Builds native installers using `jpackage`
5. Creates GitHub Release with installers attached as downloadable artifacts

Artifacts are retained for 90 days and attached to the corresponding GitHub Release.

## Icon System

SignalShow uses a custom icon derived from the original `guiIcons/SignalShowIcon.png` (48×48 PNG):

- **macOS**: `SignalShow.icns` (979KB, contains sizes: 16×16 to 512×512 @1x and @2x)
- **Windows**: `SignalShow.ico` (70KB, contains sizes: 16×16, 32×32, 48×48, 256×256)

Icons are generated using:

- **macOS**: `sips` and `iconutil` (native tools)
- **Windows**: ImageMagick 7 (`magick convert` command)

Build scripts automatically include icons in generated installers.

## Migration Artifacts

The Maven migration preserved all functionality while modernizing the build system:

**Source Files Migrated**: 395 Java files  
**Resource Files Migrated**: 339 files (images, icons, documentation)  
**Compilation Status**: BUILD SUCCESS (all files compile cleanly)  
**Runtime Status**: Application launches and functions correctly  
**Distribution Status**: Native installers build successfully on macOS and Windows

JAI dependencies are preserved in `legacy-build/` directory as they are not available in public Maven repositories.

## Key Benefits

1. **Dependency Management**: Maven handles JAI libraries and future dependencies
2. **IDE Integration**: Full support in IntelliJ IDEA, Eclipse, VS Code
3. **Automated Builds**: GitHub Actions CI/CD pipeline
4. **Professional Distribution**: Native installers with bundled JRE
5. **Version Control**: Clean Git workflow with feature branches
6. **Standardization**: Industry-standard Maven conventions

## Future Enhancements

Potential improvements enabled by Maven migration:

- **Unit Testing**: Add JUnit tests in `src/test/java/`
- **Code Coverage**: Integrate JaCoCo for coverage reporting
- **Dependency Updates**: Migrate to modern image processing libraries (e.g., ImageJ, OpenCV)
- **Multi-Module Structure**: Separate core, GUI, and plugins
- **Continuous Deployment**: Automatic releases to GitHub Releases or Maven Central
