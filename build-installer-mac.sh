#!/bin/bash
# Build native Mac installer (.dmg) for SignalShow
# Requires: Java 17+ with jpackage

set -e  # Exit on error

echo "🔨 Building SignalShow Mac Installer..."
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

# Clean and build
echo "📦 Building JAR with Maven..."
mvn clean package -q

if [ ! -f "target/signalshow-1.2.0.jar" ]; then
    echo "❌ Error: JAR file not found after build"
    exit 1
fi

echo "✓ JAR built successfully"
echo ""

# Create distribution directory
mkdir -p target/dist

# Check if icon exists
ICON_PATH="assets/icons/SignalShow.icns"
if [ ! -f "$ICON_PATH" ]; then
    echo "⚠️  Warning: Icon not found at $ICON_PATH"
    echo "   Run ./create-mac-icon.sh to create it"
    ICON_ARG=""
else
    echo "✓ Using icon: $ICON_PATH"
    ICON_ARG="--icon $ICON_PATH"
fi

# Build DMG installer
echo "🍎 Creating Mac DMG installer..."
jpackage \
  --type dmg \
  --name SignalShow \
  --app-version 1.2.0 \
  --input target \
  --main-jar signalshow-1.2.0.jar \
  --main-class SignalShow \
  --dest target/dist \
  --vendor "SignalShow" \
  --copyright "Copyright © 2005-2025 SignalShow" \
  --description "Educational signal and image processing application" \
  --mac-package-identifier org.signalshow \
  --mac-package-name SignalShow \
  --java-options -Xmx2g \
  --java-options "-Dapple.awt.application.name=SignalShow" \
  $ICON_ARG

echo ""
echo "✅ Build complete!"
echo ""
echo "Installer location:"
ls -lh target/dist/*.dmg
echo ""
echo "To install:"
echo "  1. Open target/dist/SignalShow-1.2.0.dmg"
echo "  2. Drag SignalShow to Applications folder"
echo ""
