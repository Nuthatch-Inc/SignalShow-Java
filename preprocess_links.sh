#!/bin/bash

# Script to preprocess markdown files and convert .md links to chapter references
# This creates temporary versions of the markdown files with updated links
#
# Usage: ./preprocess_links.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
TEMP_DIR="$SCRIPT_DIR/.book_temp"

# Chapter mapping: filename -> chapter number/name
declare -A CHAPTER_MAP=(
    ["BOOK_INTRODUCTION"]="the Introduction"
    ["BOOK_TOC"]="the Table of Contents"
    ["QUICK_START"]="Chapter 1 (Quick Start Guide)"
    ["README"]="Chapter 2 (README and Project Overview)"
    ["PERSONAS"]="Chapter 3 (User Personas)"
    ["FEATURE_MAPPING"]="Chapter 4 (Feature Overview)"
    ["ARCHITECTURE"]="Chapter 5 (System Architecture)"
    ["TECH_STACK"]="Chapter 6 (Technology Stack)"
    ["FILE_BASED_ARCHITECTURE"]="Chapter 7 (File-Based Architecture)"
    ["FEATURE_ADDITIONS"]="Chapter 8 (Feature Implementation Roadmap)"
    ["SIMILAR_PROJECTS_COMPARISON"]="Chapter 9 (Competitive Analysis)"
    ["JULIA_SERVER_LIFECYCLE"]="Chapter 10 (Julia Server Lifecycle)"
    ["JULIA_INSTALLATION_GUIDE"]="Chapter 11 (Julia Installation Guide)"
    ["JULIA_AUTOSTART_IMPLEMENTATION"]="Chapter 12 (Julia Auto-Start Implementation)"
    ["ANIMATION_AND_3D_STRATEGY"]="Chapter 13 (Animation and 3D Strategy)"
    ["JAVASCRIPT_DSP_RESEARCH"]="Chapter 14 (JavaScript DSP Research)"
    ["RESEARCH_OVERVIEW"]="Chapter 15 (Research Overview)"
    ["SIGNALSHOW_STRATEGIC_RECOMMENDATIONS"]="Chapter 16 (Strategic Recommendations)"
    ["ROADMAP_REVISIONS"]="Chapter 17 (Roadmap Revisions)"
    ["NUTHATCH_PLATFORM_PORT_ANALYSIS"]="Chapter 18 (Nuthatch Platform Integration)"
    ["RUST_DSP_RESEARCH"]="Chapter 19 (Rust DSP Research)"
    ["MAVEN_MIGRATION_PLAN"]="Chapter 20 (Maven Migration Plan)"
    ["BUGS"]="Chapter 21 (Known Issues)"
)

echo "Creating temporary directory for preprocessed files..."
rm -rf "$TEMP_DIR"
mkdir -p "$TEMP_DIR"

echo "Preprocessing markdown files to convert links..."

for md_file in "$SCRIPT_DIR"/*.md; do
    if [ -f "$md_file" ]; then
        filename=$(basename "$md_file")
        temp_file="$TEMP_DIR/$filename"
        
        # Copy original file
        cp "$md_file" "$temp_file"
        
        # Convert each .md link to a chapter reference
        for key in "${!CHAPTER_MAP[@]}"; do
            chapter_name="${CHAPTER_MAP[$key]}"
            
            # Match various link formats:
            # [text](./FILE.md)
            # [text](FILE.md)
            # [text](./FILE.md#section)
            # [text](FILE.md#section)
            
            # Pattern 1: ./FILENAME.md or FILENAME.md (without anchor)
            sed -i '' "s|\[\([^]]*\)\](\./${key}\.md)|\1 (see ${chapter_name})|g" "$temp_file"
            sed -i '' "s|\[\([^]]*\)\](${key}\.md)|\1 (see ${chapter_name})|g" "$temp_file"
            
            # Pattern 2: ./FILENAME.md#anchor or FILENAME.md#anchor (with anchor)
            sed -i '' "s|\[\([^]]*\)\](\./${key}\.md#[^)]*)|\1 (see ${chapter_name})|g" "$temp_file"
            sed -i '' "s|\[\([^]]*\)\](${key}\.md#[^)]*)|\1 (see ${chapter_name})|g" "$temp_file"
        done
        
        echo "  Processed: $filename"
    fi
done

echo ""
echo "✓ Preprocessing complete!"
echo "  Temp files in: $TEMP_DIR"
echo ""
echo "You can now run build_book.sh with these preprocessed files."
