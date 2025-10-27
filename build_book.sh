#!/bin/bash

# Script to generate SignalShow Modernization Plan
# 
# This script:
# 1. Concatenates all markdown chapters into a single file
# 2. Adds HTML anchors for internal navigation
# 3. Converts .md links to clickable anchor links
# 4. Generates a single PDF with working hyperlinks
#
# Usage: ./build_book.sh [output_directory]
#
# Example: ./build_book.sh ~/Desktop

set -e  # Exit on error

# Configuration
OUTPUT_DIR="${1:-./book_output}"
FINAL_BOOK="SignalShow_Modernization_Plan.pdf"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
TEMP_DIR="$SCRIPT_DIR/.book_temp"
COMBINED_MD="$TEMP_DIR/complete_book.md"

# Color output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}  SignalShow Documentation Book Builder${NC}"
echo -e "${BLUE}================================================${NC}"
echo ""

# Check dependencies
echo -e "${YELLOW}Checking dependencies...${NC}"

if ! command -v md-to-pdf &> /dev/null; then
    echo "Error: md-to-pdf is not installed"
    echo "Install with: npm install -g md-to-pdf"
    exit 1
fi

echo -e "${GREEN}✓ All dependencies available${NC}"
echo ""

# Create temp directory
rm -rf "$TEMP_DIR"
mkdir -p "$TEMP_DIR"
mkdir -p "$OUTPUT_DIR"

# Define chapter order with anchor IDs
# Format: "filename|Chapter Title|anchor-id"
declare -a CHAPTERS=(
    # Front Matter
    "BOOK_INTRODUCTION|Introduction|introduction"
    "BOOK_TOC|Table of Contents|table-of-contents"
    
    # Part I: Project Overview & Vision
    "README|Chapter 1: Project Overview and Introduction|chapter-1-overview"
    "RESEARCH_OVERVIEW|Chapter 2: Research Overview and Modernization Strategy|chapter-2-research"
    
    # Part II: Literature Review
    "SIMILAR_PROJECTS_COMPARISON|Chapter 3: Competitive Analysis and Market Positioning|chapter-3-competitive"
    
    # Part III: Strategic Goals & User Scenarios
    "SIGNALSHOW_STRATEGIC_RECOMMENDATIONS|Chapter 4: Strategic Recommendations and Positioning|chapter-4-strategy"
    "PERSONAS|Chapter 5: User Personas and Use Cases|chapter-5-personas"
    "ROADMAP_REVISIONS|Chapter 6: Roadmap and Implementation Priorities|chapter-6-roadmap"
    
    # Part IV: Proposed Technical Implementation
    "ARCHITECTURE|Chapter 7: System Architecture (Proposed)|chapter-7-architecture"
    "TECH_STACK|Chapter 8: Technology Stack (Proposed)|chapter-8-tech-stack"
    "FILE_BASED_ARCHITECTURE|Chapter 9: File-Based Architecture (Proposed)|chapter-9-file-architecture"
    "FEATURE_MAPPING|Chapter 10: Feature Overview and Implementation Mapping|chapter-10-features"
    "FEATURE_ADDITIONS|Chapter 11: Feature Implementation Roadmap|chapter-11-feature-roadmap"
    "JAVASCRIPT_DSP_RESEARCH|Chapter 12: JavaScript DSP Library Research|chapter-12-javascript"
    "RUST_DSP_RESEARCH|Chapter 13: Rust DSP Library Research|chapter-13-rust"
    "ANIMATION_AND_3D_STRATEGY|Chapter 14: Animation and 3D Visualization Strategy|chapter-14-animation"
    "NUTHATCH_PLATFORM_PORT_ANALYSIS|Chapter 15: Nuthatch Platform Integration Analysis|chapter-15-nuthatch"
    
    # Part V: Desktop Backend & Implementation Reference
    "DESKTOP_BACKEND_REFERENCE|Chapter 16: Desktop Backend and Implementation Reference|chapter-16-desktop-backend"
    
    # Part VI: Appendices
    "BUGS|Chapter 17: Known Issues and Bug Tracking|chapter-17-bugs"
    "MAVEN_BUILD_SYSTEM|Chapter 18: Maven Build System and Distribution|chapter-18-maven"
)

echo -e "${YELLOW}Building combined markdown file with hyperlinks...${NC}"
echo ""

# Start the combined markdown file
> "$COMBINED_MD"

CHAPTER_COUNT=0

for chapter_info in "${CHAPTERS[@]}"; do
    IFS='|' read -r filename chapter_title anchor_id <<< "$chapter_info"
    
    MD_FILE="$SCRIPT_DIR/${filename}.md"
    
    if [ ! -f "$MD_FILE" ]; then
        echo -e "${YELLOW}⚠ Warning: $MD_FILE not found, skipping${NC}"
        continue
    fi
    
    CHAPTER_COUNT=$((CHAPTER_COUNT + 1))
    echo -e "${BLUE}[$CHAPTER_COUNT] Adding: ${chapter_title}${NC}"
    
    # Add page break before chapter (except first one)
    if [ $CHAPTER_COUNT -gt 1 ]; then
        echo '<div style="page-break-after: always;"></div>' >> "$COMBINED_MD"
        echo "" >> "$COMBINED_MD"
        echo "---" >> "$COMBINED_MD"
        echo "" >> "$COMBINED_MD"
    fi
    
    # Add HTML anchor for this chapter
    echo "<a id=\"${anchor_id}\"></a>" >> "$COMBINED_MD"
    echo "" >> "$COMBINED_MD"
    
    # Append the chapter content
    cat "$MD_FILE" >> "$COMBINED_MD"
    echo "" >> "$COMBINED_MD"
done

echo ""
echo -e "${GREEN}✓ Combined ${CHAPTER_COUNT} chapters${NC}"
echo ""

# Convert .md links to plain text chapter references
echo -e "${YELLOW}Converting .md links to chapter references...${NC}"

# Function to convert links
convert_links_to_anchors() {
    local file="$1"
    
    # Define link conversions: filename|anchor-id|display-name
    local links=(
        "BOOK_INTRODUCTION|introduction|Introduction"
        "BOOK_TOC|table-of-contents|Table of Contents"
        "QUICK_START|chapter-1-quick-start|Chapter 1: Quick Start Guide"
        "README|chapter-2-readme|Chapter 2: README and Project Overview"
        "PERSONAS|chapter-3-personas|Chapter 3: User Personas"
        "FEATURE_MAPPING|chapter-4-features|Chapter 4: Feature Overview"
        "ARCHITECTURE|chapter-5-architecture|Chapter 5: System Architecture"
        "TECH_STACK|chapter-6-tech-stack|Chapter 6: Technology Stack"
        "FILE_BASED_ARCHITECTURE|chapter-7-file-architecture|Chapter 7: File-Based Architecture"
        "FEATURE_ADDITIONS|chapter-8-roadmap|Chapter 8: Feature Implementation Roadmap"
        "SIMILAR_PROJECTS_COMPARISON|chapter-9-competitive|Chapter 9: Competitive Analysis"
        "JULIA_SERVER_LIFECYCLE|chapter-10-lifecycle|Chapter 10: Julia Server Lifecycle"
        "JULIA_INSTALLATION_GUIDE|chapter-11-installation|Chapter 11: Julia Installation Guide"
        "JULIA_AUTOSTART_IMPLEMENTATION|chapter-12-autostart|Chapter 12: Julia Auto-Start"
        "ANIMATION_AND_3D_STRATEGY|chapter-13-animation|Chapter 13: Animation and 3D Strategy"
        "JAVASCRIPT_DSP_RESEARCH|chapter-14-javascript|Chapter 14: JavaScript DSP Research"
        "RESEARCH_OVERVIEW|chapter-15-research|Chapter 15: Research Overview"
        "SIGNALSHOW_STRATEGIC_RECOMMENDATIONS|chapter-16-strategy|Chapter 16: Strategic Recommendations"
        "ROADMAP_REVISIONS|chapter-17-revisions|Chapter 17: Roadmap Revisions"
        "NUTHATCH_PLATFORM_PORT_ANALYSIS|chapter-18-nuthatch|Chapter 18: Nuthatch Platform Integration"
        "RUST_DSP_RESEARCH|chapter-19-rust|Chapter 19: Rust DSP Research"
        "MAVEN_BUILD_SYSTEM|chapter-20-maven|Chapter 20: Maven Build System and Distribution"
        "BUGS|chapter-21-bugs|Chapter 21: Known Issues"
    )
    
    for link_info in "${links[@]}"; do
        IFS='|' read -r filename anchor_id display_name <<< "$link_info"
        
        # Pattern 1: [`filename.md`](./filename.md) or [`filename.md`](filename.md)
        # Convert to plain text reference
        sed -i '' "s|\[\`${filename}\.md\`\](\./${filename}\.md)|${display_name}|g" "$file"
        sed -i '' "s|\[\`${filename}\.md\`\](${filename}\.md)|${display_name}|g" "$file"
        
        # Pattern 2: [text](./filename.md) or [text](filename.md)
        # Convert to plain text with chapter reference
        sed -i '' "s|\[\([^]]*\)\](\./${filename}\.md)|\1 (see ${display_name})|g" "$file"
        sed -i '' "s|\[\([^]]*\)\](${filename}\.md)|\1 (see ${display_name})|g" "$file"
        
        # Pattern 3: Links with anchors - convert to chapter reference
        sed -i '' "s|\[\`${filename}\.md#[^]]*\`\](\./${filename}\.md#[^)]*)|${display_name}|g" "$file"
        sed -i '' "s|\[\([^]]*\)\](\./${filename}\.md#[^)]*)|\1 (see ${display_name})|g" "$file"
        sed -i '' "s|\[\([^]]*\)\](${filename}\.md#[^)]*)|\1 (see ${display_name})|g" "$file"
        
        # Pattern 4: Plain text mentions (see FILENAME.md) - convert to chapter reference
        sed -i '' "s|(see ${filename}\.md)|(see ${display_name})|g" "$file"
        sed -i '' "s|(See ${filename}\.md)|(See ${display_name})|g" "$file"
    done
}

convert_links_to_anchors "$COMBINED_MD"

echo -e "${GREEN}✓ Link conversion complete${NC}"
echo ""

echo -e "${GREEN}✓ Link conversion complete${NC}"
echo ""

# Generate the final PDF using md-to-pdf
echo -e "${YELLOW}Generating PDF from combined markdown...${NC}"

FINAL_PATH="$OUTPUT_DIR/$FINAL_BOOK"

cd "$TEMP_DIR"

# Use md-to-pdf for fast, reliable PDF generation
echo ""
md-to-pdf "$COMBINED_MD" \
    --pdf-options '{
        "format": "Letter",
        "margin": {
            "top": "1in",
            "right": "1in",
            "bottom": "1in",
            "left": "1in"
        },
        "printBackground": true,
        "displayHeaderFooter": true,
        "headerTemplate": "<div style=\"font-size: 10px; text-align: center; width: 100%;\">SignalShow Modernization Plan</div>",
        "footerTemplate": "<div style=\"font-size: 10px; text-align: center; width: 100%; margin: 0 auto;\"><span class=\"pageNumber\"></span> / <span class=\"totalPages\"></span></div>"
    }'

echo ""
GENERATED_PDF="${COMBINED_MD%.md}.pdf"

if [ -f "$GENERATED_PDF" ]; then
    mv "$GENERATED_PDF" "$FINAL_PATH"
    FILE_SIZE=$(ls -lh "$FINAL_PATH" | awk '{print $5}')
    echo ""
    echo -e "${GREEN}✓ Book created successfully!${NC}"
    echo ""
    echo -e "${BLUE}================================================${NC}"
    echo -e "${GREEN}  Final Book: $FINAL_PATH${NC}"
    echo -e "${GREEN}  File Size: $FILE_SIZE${NC}"
    echo -e "${GREEN}  Total Chapters: ${CHAPTER_COUNT}${NC}"
    echo -e "${BLUE}================================================${NC}"
    echo ""
else
    echo -e "${YELLOW}✗ Error: Failed to create final book${NC}"
    exit 1
fi

# Clean up temp directory
rm -rf "$TEMP_DIR"
echo -e "${GREEN}✓ Cleaned up temporary files${NC}"

echo ""
echo -e "${GREEN}Book generation complete!${NC}"
echo ""
echo -e "${BLUE}All chapter cross-references converted to plain text.${NC}"

