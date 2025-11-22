#!/bin/bash
# Convert SignalShow PNG icon to macOS .icns format
# Requires macOS with iconutil

set -e

ICON_PNG="assets/icons/SignalShowIcon.png"
ICONSET="assets/icons/SignalShow.iconset"
ICON_ICNS="assets/icons/SignalShow.icns"

echo "🎨 Converting PNG to macOS .icns format..."
echo ""

# Check if source PNG exists
if [ ! -f "$ICON_PNG" ]; then
    echo "❌ Error: $ICON_PNG not found"
    exit 1
fi

# Create iconset directory
mkdir -p "$ICONSET"

# Generate all required icon sizes for macOS
# Using sips (built-in macOS tool) to resize
echo "📐 Generating icon sizes..."

# Standard resolutions
sips -z 16 16     "$ICON_PNG" --out "$ICONSET/icon_16x16.png" > /dev/null
sips -z 32 32     "$ICON_PNG" --out "$ICONSET/icon_16x16@2x.png" > /dev/null
sips -z 32 32     "$ICON_PNG" --out "$ICONSET/icon_32x32.png" > /dev/null
sips -z 64 64     "$ICON_PNG" --out "$ICONSET/icon_32x32@2x.png" > /dev/null
sips -z 128 128   "$ICON_PNG" --out "$ICONSET/icon_128x128.png" > /dev/null
sips -z 256 256   "$ICON_PNG" --out "$ICONSET/icon_128x128@2x.png" > /dev/null
sips -z 256 256   "$ICON_PNG" --out "$ICONSET/icon_256x256.png" > /dev/null
sips -z 512 512   "$ICON_PNG" --out "$ICONSET/icon_256x256@2x.png" > /dev/null
sips -z 512 512   "$ICON_PNG" --out "$ICONSET/icon_512x512.png" > /dev/null
sips -z 1024 1024 "$ICON_PNG" --out "$ICONSET/icon_512x512@2x.png" > /dev/null

echo "✓ Generated all icon sizes"
echo ""

# Convert iconset to icns
echo "🔨 Creating .icns file..."
iconutil -c icns "$ICONSET" -o "$ICON_ICNS"

# Clean up iconset directory
rm -rf "$ICONSET"

echo "✓ Created $ICON_ICNS"
echo ""

# Show file info
ls -lh "$ICON_ICNS"
file "$ICON_ICNS"

echo ""
echo "✅ Icon conversion complete!"
echo "   Mac icon: $ICON_ICNS"
