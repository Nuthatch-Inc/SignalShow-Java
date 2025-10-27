#!/bin/bash
# Build native Windows installer (.exe) for SignalShow
# Must be run on Windows with Java 17+ and WiX Toolset installed
# https://wixtoolset.org/

set -e  # Exit on error

echo "🔨 Building SignalShow Windows Installer..."
echo ""

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Error: Java 17 or later required (found Java $JAVA_VERSION)"
    echo "   jpackage is available in Java 17+"
    exit 1
fi

echo "✓ Java $JAVA_VERSION detected"
echo ""

# Check for WiX Toolset (required for MSI/EXE on Windows)
if ! command -v candle.exe &> /dev/null; then
    echo "⚠️  Warning: WiX Toolset not found in PATH"
    echo "   Install from: https://wixtoolset.org/"
    echo "   Required for .msi installers (falling back to .exe)"
    TYPE="exe"
else
    echo "✓ WiX Toolset detected"
    TYPE="msi"
fi

echo ""

# Clean and build
echo "📦 Building JAR with Maven..."
mvn clean package -q

if [ ! -f "target/signalshow-1.0.0-SNAPSHOT.jar" ]; then
    echo "❌ Error: JAR file not found after build"
    exit 1
fi

echo "✓ JAR built successfully"
echo ""

# Create distribution directory
mkdir -p target/dist

# Build Windows installer
echo "🪟 Creating Windows installer ($TYPE)..."
jpackage \
  --type "$TYPE" \
  --name SignalShow \
  --app-version 1.0.0 \
  --input target \
  --main-jar signalshow-1.0.0-SNAPSHOT.jar \
  --main-class SignalShow \
  --dest target/dist \
  --vendor "SignalShow" \
  --copyright "Copyright © 2005-2025 SignalShow" \
  --description "Educational signal and image processing application" \
  --win-dir-chooser \
  --win-menu \
  --win-shortcut \
  --win-menu-group SignalShow \
  --java-options -Xmx2g

echo ""
echo "✅ Build complete!"
echo ""
echo "Installer location:"
ls -lh target/dist/SignalShow* 2>/dev/null || echo "Check target/dist/ directory"
echo ""
echo "To distribute:"
echo "  Upload the installer to your download server"
echo "  Users run the installer to install SignalShow"
echo ""
