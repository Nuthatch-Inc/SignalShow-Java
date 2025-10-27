#!/bin/bash
# Build installers for all platforms using Maven profiles
# Note: Still requires building on respective platforms (Mac for .dmg, Windows for .exe)

set -e

echo "🔨 SignalShow Multi-Platform Build"
echo "===================================="
echo ""

# Detect current platform
if [[ "$OSTYPE" == "darwin"* ]]; then
    PLATFORM="macOS"
    echo "🍎 Detected platform: macOS"
elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
    PLATFORM="Windows"
    echo "🪟 Detected platform: Windows"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    PLATFORM="Linux"
    echo "🐧 Detected platform: Linux"
else
    echo "❌ Unsupported platform: $OSTYPE"
    exit 1
fi

echo ""
echo "📦 Building JAR (cross-platform)..."
mvn clean package

echo ""
echo "🎁 Creating native installer for $PLATFORM..."

case $PLATFORM in
    "macOS")
        ./build-installer-mac.sh
        ;;
    "Windows")
        ./build-installer-windows.sh
        ;;
    "Linux")
        echo "⚠️  Linux installer build not yet implemented"
        echo "   Use JAR distribution or add jpackage DEB/RPM config"
        ;;
esac

echo ""
echo "✅ Build complete for $PLATFORM!"
