#!/bin/bash
# Convert SignalShow PNG icon to Windows .ico format
# Requires ImageMagick (install with: brew install imagemagick)

set -e

ICON_PNG="assets/icons/SignalShowIcon.png"
ICON_ICO="assets/icons/SignalShow.ico"

echo "🎨 Converting PNG to Windows .ico format..."
echo ""

# Check if source PNG exists
if [ ! -f "$ICON_PNG" ]; then
    echo "❌ Error: $ICON_PNG not found"
    exit 1
fi

# Check if ImageMagick is installed
if ! command -v magick &> /dev/null && ! command -v convert &> /dev/null; then
    echo "❌ Error: ImageMagick not found"
    echo "   Install with: choco install imagemagick (Windows) or brew install imagemagick (Mac)"
    exit 1
fi

echo "📐 Generating Windows .ico with multiple sizes..."

# Use 'magick convert' on Windows, 'convert' on Unix
if command -v magick &> /dev/null; then
    CONVERT_CMD="magick convert"
else
    CONVERT_CMD="convert"
fi

# Create .ico with multiple resolutions (16, 32, 48, 256)
$CONVERT_CMD "$ICON_PNG" \
    \( -clone 0 -resize 16x16 \) \
    \( -clone 0 -resize 32x32 \) \
    \( -clone 0 -resize 48x48 \) \
    \( -clone 0 -resize 256x256 \) \
    -delete 0 -colors 256 "$ICON_ICO"

echo "✓ Created $ICON_ICO"
echo ""

# Show file info
ls -lh "$ICON_ICO"
file "$ICON_ICO"

echo ""
echo "✅ Icon conversion complete!"
echo "   Windows icon: $ICON_ICO"
