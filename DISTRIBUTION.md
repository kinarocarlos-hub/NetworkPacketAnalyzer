# NetworkPacketAnalyzer - Distribution & Installation Guide

## Overview
NetworkPacketAnalyzer is now packaged as a standalone, downloadable application. It can be built into a self-contained JAR file with all dependencies bundled, ready to distribute and run.

## Quick Start

### Linux/macOS Users
```bash
# 1. Build the application
mvn clean package

# 2. Run the application
chmod +x run.sh
./run.sh
```

### Windows Users
```cmd
REM 1. Build the application
mvn clean package

REM 2. Run the application
run.bat
```

## What Gets Built

After running `mvn clean package`, the following files are created:

- **NetworkPacketAnalyzer.jar** (in `target/` directory)
  - Executable standalone JAR file
  - Contains all dependencies (fat JAR)
  - Size: ~10-15 MB (with all libraries)
  - Runnable with: `java -jar NetworkPacketAnalyzer.jar`

## Distribution Package Structure

For distribution, package your application like this:

```
NetworkPacketAnalyzer-1.0-SNAPSHOT/
├── NetworkPacketAnalyzer.jar    (executable JAR)
├── run.sh                         (Linux/macOS launcher)
├── run.bat                        (Windows launcher)
├── README.md                      (user guide)
├── INSTALL.md                     (installation instructions)
└── lib/
    └── (optional: external libraries if needed)
```

## Building for Distribution

### Step 1: Build the Project
```bash
cd /path/to/NetworkPacketAnalyzer
mvn clean package
```

### Step 2: Create Distribution Package
```bash
# Create distribution directory
mkdir -p dist/NetworkPacketAnalyzer-1.0-SNAPSHOT

# Copy JAR and scripts
cp target/NetworkPacketAnalyzer.jar dist/NetworkPacketAnalyzer-1.0-SNAPSHOT/
cp run.sh dist/NetworkPacketAnalyzer-1.0-SNAPSHOT/
cp run.bat dist/NetworkPacketAnalyzer-1.0-SNAPSHOT/
cp README.md dist/NetworkPacketAnalyzer-1.0-SNAPSHOT/

# Make script executable
chmod +x dist/NetworkPacketAnalyzer-1.0-SNAPSHOT/run.sh

# Create archives for distribution
cd dist
tar -czf NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz NetworkPacketAnalyzer-1.0-SNAPSHOT/
zip -r NetworkPacketAnalyzer-1.0-SNAPSHOT.zip NetworkPacketAnalyzer-1.0-SNAPSHOT/
```

## Installation Instructions for End Users

### Prerequisites
- **Java 23 or later** (required)
- **LibPcap** (for packet capture - platform-specific)

### Installation Steps

#### Linux
```bash
# 1. Install Java 23 (if not already installed)
sudo apt update
sudo apt install openjdk-23-jdk-headless

# 2. Install LibPcap development libraries
sudo apt install libpcap-dev

# 3. Extract the distribution package
tar -xzf NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz
cd NetworkPacketAnalyzer-1.0-SNAPSHOT

# 4. Run the application
sudo ./run.sh    # (requires sudo for packet capture)
```

#### macOS
```bash
# 1. Install Java 23 (using Homebrew)
brew install openjdk@23

# 2. Install LibPcap (usually pre-installed, or use Homebrew)
brew install libpcap

# 3. Extract the distribution package
tar -xzf NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz
cd NetworkPacketAnalyzer-1.0-SNAPSHOT

# 4. Run the application
sudo ./run.sh    # (requires sudo for packet capture)
```

#### Windows
```cmd
REM 1. Install Java 23 from https://jdk.java.net/23/

REM 2. Extract the distribution package (use Windows Explorer or 7-Zip)
REM    Extract NetworkPacketAnalyzer-1.0-SNAPSHOT.zip

REM 3. Run the application
REM    Double-click run.bat
REM    Or open Command Prompt and run:
cd NetworkPacketAnalyzer-1.0-SNAPSHOT
run.bat
```

## Running the Application

### Using Launcher Scripts

**Linux/macOS:**
```bash
./run.sh
```

**Windows:**
```cmd
run.bat
```

### Direct JAR Execution

```bash
# Linux/macOS
java -jar NetworkPacketAnalyzer.jar

# Windows
java -jar NetworkPacketAnalyzer.jar
```

## Troubleshooting

### Issue: "Java not found"
**Solution**: Install Java 23
- Linux: `sudo apt install openjdk-21-jdk-headless`
- macOS: `brew install openjdk@21`
- Windows: Download from https://jdk.java.net/21/

### Issue: "Permission denied" on Linux/macOS
**Solution**: Make the script executable
```bash
chmod +x run.sh
```

### Issue: "Cannot capture packets" or "No interface found"
**Causes**:
1. Not running with sufficient privileges
2. LibPcap not installed
3. Network interface doesn't exist

**Solutions**:
```bash
# Run with sudo
sudo ./run.sh

# Install LibPcap
sudo apt install libpcap-dev       # Linux
brew install libpcap               # macOS
```

### Issue: "JAR not found"
**Solution**: Build the project first
```bash
cd /path/to/NetworkPacketAnalyzer
mvn clean package
```

## Advanced Usage

### Running with Custom Arguments
```bash
./run.sh --arg1 value1 --arg2 value2
```

### Running with Custom JVM Options
```bash
java -Xmx512m -jar NetworkPacketAnalyzer.jar
```

### Memory Configuration
For systems with limited memory:
```bash
java -Xms256m -Xmx512m -jar NetworkPacketAnalyzer.jar
```

## File Manifest

### Generated Files After Build

```
target/
├── NetworkPacketAnalyzer.jar           (executable - main deliverable)
├── NetworkPacketAnalyzer-sources.jar   (source code)
├── classes/                            (compiled classes)
├── maven-status/                       (build metadata)
└── site/                              (documentation)
```

## Distribution Channels

### Option 1: Direct Download
- Host the JAR on your website
- Provide installation instructions
- Users download and run with provided scripts

### Option 2: Archive Distribution
- Create `.tar.gz` for Linux/macOS
- Create `.zip` for Windows
- Include all scripts and documentation
- Users extract and run

### Option 3: Package Manager Integration
- Create Debian package (.deb)
- Create RPM package (.rpm)
- Create Homebrew formula
- Users install via `apt`, `yum`, or `brew`

## Automated Distribution Script

Save as `create-dist.sh`:
```bash
#!/bin/bash
VERSION="1.0-SNAPSHOT"
DIST_DIR="dist"

# Clean and build
mvn clean package

# Create distribution directory
mkdir -p "$DIST_DIR/NetworkPacketAnalyzer-$VERSION"

# Copy files
cp target/NetworkPacketAnalyzer.jar "$DIST_DIR/NetworkPacketAnalyzer-$VERSION/"
cp run.sh "$DIST_DIR/NetworkPacketAnalyzer-$VERSION/"
cp run.bat "$DIST_DIR/NetworkPacketAnalyzer-$VERSION/"
cp README.md "$DIST_DIR/NetworkPacketAnalyzer-$VERSION/"

# Make script executable
chmod +x "$DIST_DIR/NetworkPacketAnalyzer-$VERSION/run.sh"

# Create archives
cd "$DIST_DIR"
tar -czf NetworkPacketAnalyzer-$VERSION.tar.gz NetworkPacketAnalyzer-$VERSION/
zip -r NetworkPacketAnalyzer-$VERSION.zip NetworkPacketAnalyzer-$VERSION/

echo "Distribution packages created:"
echo "  - NetworkPacketAnalyzer-$VERSION.tar.gz"
echo "  - NetworkPacketAnalyzer-$VERSION.zip"
```

Usage:
```bash
chmod +x create-dist.sh
./create-dist.sh
```

## Summary

✅ **What You Get**:
- Standalone executable JAR (no build required for users)
- Cross-platform launcher scripts
- Complete dependency bundling
- Minimal setup for end users

✅ **Distribution Ready**:
- Single file deployment (JAR)
- Easy to archive and download
- Works on Windows, macOS, and Linux
- Professional package structure

## Next Steps

1. Build the application: `mvn clean package`
2. Test the JAR: `java -jar target/NetworkPacketAnalyzer.jar`
3. Test the scripts: `./run.sh` (Linux/macOS) or `run.bat` (Windows)
4. Create distribution packages
5. Distribute to users

For questions or issues, refer to the troubleshooting section above.
