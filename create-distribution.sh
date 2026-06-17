#!/bin/bash

# Distribution Package Creator
# Creates ready-to-share distribution packages for NetworkPacketAnalyzer

set -e

VERSION="1.0-SNAPSHOT"
DIST_DIR="dist"
APP_NAME="NetworkPacketAnalyzer"

echo "=========================================="
echo "Building Distribution Packages"
echo "=========================================="

# Step 1: Build the project
echo ""
echo "[1/4] Building project..."
mvn clean package -DskipTests > /dev/null 2>&1
echo "✓ Build complete"

# Step 2: Create distribution directory structure
echo ""
echo "[2/4] Creating package structure..."
PACKAGE_DIR="$DIST_DIR/$APP_NAME-$VERSION"
rm -rf "$DIST_DIR"
mkdir -p "$PACKAGE_DIR"

# Copy files
cp target/NetworkPacketAnalyzer.jar "$PACKAGE_DIR/"
cp run.sh "$PACKAGE_DIR/"
cp run.bat "$PACKAGE_DIR/"
cp DISTRIBUTION.md "$PACKAGE_DIR/"
cp README-DOWNLOAD.md "$PACKAGE_DIR/"
cp JAVA_21_UPGRADE.md "$PACKAGE_DIR/"

# Make shell script executable
chmod +x "$PACKAGE_DIR/run.sh"

echo "✓ Files packaged"

# Step 3: Create archives
echo ""
echo "[3/4] Creating archives..."

cd "$DIST_DIR"

# Create tar.gz for Unix-like systems
tar -czf "$APP_NAME-$VERSION.tar.gz" "$APP_NAME-$VERSION/"
echo "  ✓ Created: $APP_NAME-$VERSION.tar.gz"

# Create zip for Windows and universal download
zip -q -r "$APP_NAME-$VERSION.zip" "$APP_NAME-$VERSION/"
echo "  ✓ Created: $APP_NAME-$VERSION.zip"

cd ..

# Step 4: Generate summary
echo ""
echo "[4/4] Generating summary..."

cat > "$DIST_DIR/DISTRIBUTION_SUMMARY.txt" << EOF
NetworkPacketAnalyzer Distribution Packages
Generated: $(date)

AVAILABLE PACKAGES:
==================

1. $APP_NAME-$VERSION.tar.gz (Linux/macOS/Unix)
   - Recommended for Linux and macOS users
   - Extract: tar -xzf $APP_NAME-$VERSION.tar.gz
   - Run: cd $APP_NAME-$VERSION && ./run.sh

2. $APP_NAME-$VERSION.zip (Windows/Universal)
   - Recommended for Windows users
   - Extract using Windows Explorer or 7-Zip
   - Run: Double-click run.bat or use Command Prompt

PACKAGE CONTENTS:
=================
- NetworkPacketAnalyzer.jar         (2.9 MB executable JAR)
- run.sh                             (Linux/macOS launcher)
- run.bat                            (Windows launcher)
- DISTRIBUTION.md                   (Complete user guide)
- README-DOWNLOAD.md                (Quick start guide)
- JAVA_21_UPGRADE.md                (Java 21 details)

SYSTEM REQUIREMENTS:
====================
- Java 21 LTS or later
- LibPcap development libraries
- Windows/macOS/Linux

QUICK START:
===========
Linux/macOS:
  1. tar -xzf $APP_NAME-$VERSION.tar.gz
  2. cd $APP_NAME-$VERSION
  3. sudo apt install libpcap-dev  (if needed)
  4. sudo ./run.sh

Windows:
  1. Extract $APP_NAME-$VERSION.zip
  2. Double-click run.bat
  3. Or run from Command Prompt: run.bat

DOWNLOAD SIZE:
==============
- tar.gz: ~3 MB (compressed)
- zip:    ~3 MB (compressed)
- Extracted: ~5 MB

For detailed instructions, see DISTRIBUTION.md in the package.
EOF

echo "✓ Summary created"

# Display results
echo ""
echo "=========================================="
echo "Distribution packages ready!"
echo "=========================================="
echo ""
echo "📦 Output Directory: $DIST_DIR/"
echo ""
ls -lh "$DIST_DIR"/$APP_NAME-$VERSION.* | awk '{print "  " $9 " (" $5 ")"}'
echo ""
echo "📄 Summary: $DIST_DIR/DISTRIBUTION_SUMMARY.txt"
echo ""
echo "Next steps:"
echo "  1. Upload packages to a server or cloud storage"
echo "  2. Share download links with users"
echo "  3. Direct users to read DISTRIBUTION.md in the package"
echo ""
