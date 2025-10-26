#!/bin/bash

# LEGACY: Script to convert markdown to PDF with emoji replacements using Pandoc/LaTeX
# This version replaces emojis with text equivalents for smaller file size
# 
# For native emoji rendering, use convert_to_pdf.sh instead
#
# Usage: ./convert_to_pdf_pandoc_legacy.sh <input.md> <output.pdf>
#
# Example: ./convert_to_pdf_pandoc_legacy.sh RESEARCH_OVERVIEW.md ~/Desktop/Research_Overview.pdf

set -e  # Exit on error

# Check arguments
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <input.md> <output.pdf>"
    echo "Example: $0 RESEARCH_OVERVIEW.md ~/Desktop/Research_Overview.pdf"
    exit 1
fi

INPUT_FILE="$1"
OUTPUT_FILE="$2"

# Check if input file exists
if [ ! -f "$INPUT_FILE" ]; then
    echo "Error: Input file '$INPUT_FILE' not found"
    exit 1
fi

# Check if pandoc is installed
if ! command -v pandoc &> /dev/null; then
    echo "Error: pandoc is not installed"
    echo "Install with: brew install pandoc"
    exit 1
fi

# Check if xelatex is available (part of BasicTeX)
if ! command -v xelatex &> /dev/null; then
    echo "Error: xelatex is not installed"
    echo "Install with: brew install --cask basictex"
    echo "Then update PATH: eval \"\$(/usr/libexec/path_helper)\""
    exit 1
fi

# Create temporary file for emoji replacement
TEMP_FILE="${INPUT_FILE%.md}_temp.md"
cp "$INPUT_FILE" "$TEMP_FILE"

echo "Replacing emojis with text equivalents..."

# Replace emojis with text symbols
sed -i '' \
  -e 's/✅/[DONE]/g' \
  -e 's/❌/[NO]/g' \
  -e 's/⚠️/[WARNING]/g' \
  -e 's/⚠/[WARNING]/g' \
  -e 's/📋/[TODO]/g' \
  -e 's/⏳/[IN PROGRESS]/g' \
  -e 's/🔄/[UPDATED]/g' \
  -e 's/🎯/[TARGET]/g' \
  -e 's/🚀/[LAUNCH]/g' \
  -e 's/🔍/[SEARCH]/g' \
  -e 's/🎨/[DESIGN]/g' \
  -e 's/✨/[NEW]/g' \
  -e 's/🔮/[FUTURE]/g' \
  -e 's/⭐/[STAR]/g' \
  -e 's/🤔/[QUESTION]/g' \
  -e 's/🚧/[WIP]/g' \
  -e 's/💡/[IDEA]/g' \
  -e 's/🎓/[GUIDED]/g' \
  -e 's/🔬/[EXPERT]/g' \
  -e 's/🔊/[SONIFY]/g' \
  -e 's/📊/[CHART]/g' \
  -e 's/📈/[GRAPH]/g' \
  -e 's/🎬/[ACTION]/g' \
  -e 's/🎥/[VIDEO]/g' \
  -e 's/📱/[MOBILE]/g' \
  -e 's/💻/[DESKTOP]/g' \
  -e 's/🌐/[WEB]/g' \
  -e 's/🔗/[LINK]/g' \
  -e 's/📦/[PACKAGE]/g' \
  -e 's/🛠️/[TOOLS]/g' \
  -e 's/🛠/[TOOLS]/g' \
  -e 's/⚡/[FAST]/g' \
  -e 's/🔥/[HOT]/g' \
  -e 's/👍/[GOOD]/g' \
  -e 's/👎/[BAD]/g' \
  -e 's/🎉/[SUCCESS]/g' \
  -e 's/💪/[STRONG]/g' \
  -e 's/🚨/[ALERT]/g' \
  -e 's/📝/[NOTE]/g' \
  -e 's/📚/[DOCS]/g' \
  -e 's/🔑/[KEY]/g' \
  -e 's/🎁/[GIFT]/g' \
  -e 's/🌟/[FEATURE]/g' \
  -e 's/🏆/[ACHIEVEMENT]/g' \
  -e 's/🎮/[INTERACTIVE]/g' \
  "$TEMP_FILE"

echo "Generating PDF with xelatex..."

# Generate PDF using pandoc with xelatex engine
pandoc "$TEMP_FILE" -o "$OUTPUT_FILE" --pdf-engine=xelatex 2>&1 | \
  grep -v "Missing character" || true  # Suppress font warnings

# Clean up temporary file
rm "$TEMP_FILE"

# Check if PDF was created successfully
if [ -f "$OUTPUT_FILE" ]; then
    FILE_SIZE=$(ls -lh "$OUTPUT_FILE" | awk '{print $5}')
    echo "✓ PDF created successfully: $OUTPUT_FILE ($FILE_SIZE)"
else
    echo "✗ Error: PDF generation failed"
    exit 1
fi
