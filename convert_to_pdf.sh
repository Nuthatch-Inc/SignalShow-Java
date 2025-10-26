#!/bin/bash

# Script to convert markdown to PDF using md-to-pdf (headless Chrome)
# This method preserves emojis natively without any replacements
#
# Usage: ./convert_to_pdf.sh <input.md> [output.pdf]
#
# Example: ./convert_to_pdf.sh RESEARCH_OVERVIEW.md ~/Desktop/Research_Overview.pdf

set -e  # Exit on error

# Check arguments
if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
    echo "Usage: $0 <input.md> [output.pdf]"
    echo "Example: $0 RESEARCH_OVERVIEW.md ~/Desktop/Research_Overview.pdf"
    exit 1
fi

INPUT_FILE="$1"
OUTPUT_FILE="${2:-${INPUT_FILE%.md}.pdf}"

# Check if input file exists
if [ ! -f "$INPUT_FILE" ]; then
    echo "Error: Input file '$INPUT_FILE' not found"
    exit 1
fi

# Check if md-to-pdf is installed
if ! command -v md-to-pdf &> /dev/null; then
    echo "Error: md-to-pdf is not installed"
    echo "Install with: npm install -g md-to-pdf"
    exit 1
fi

echo "Generating PDF with md-to-pdf (Chromium-based, preserves emojis)..."

# Generate PDF using md-to-pdf with nice formatting
md-to-pdf "$INPUT_FILE" \
  --pdf-options '{
    "format": "Letter",
    "margin": {
      "top": "1in",
      "right": "1in",
      "bottom": "1in",
      "left": "1in"
    },
    "printBackground": true
  }'

# Get the generated PDF filename (same as input but with .pdf extension)
GENERATED_PDF="${INPUT_FILE%.md}.pdf"

# Move to desired location if different
if [ "$OUTPUT_FILE" != "$GENERATED_PDF" ]; then
    mv "$GENERATED_PDF" "$OUTPUT_FILE"
fi

# Check if PDF was created successfully
if [ -f "$OUTPUT_FILE" ]; then
    FILE_SIZE=$(ls -lh "$OUTPUT_FILE" | awk '{print $5}')
    echo "✓ PDF created successfully: $OUTPUT_FILE ($FILE_SIZE)"
    echo "✓ Emojis rendered natively (no replacements needed)"
else
    echo "✗ Error: PDF generation failed"
    exit 1
fi
