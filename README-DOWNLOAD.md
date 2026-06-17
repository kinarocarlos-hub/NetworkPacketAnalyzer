# NetworkPacketAnalyzer - Downloadable App

## ✅ Application is Ready for Download and Distribution

Your NetworkPacketAnalyzer has been transformed into a standalone, downloadable application. Users can now download and run it without needing to build from source.

## What's Ready

### 📦 Downloadable Artifacts

**Location**: `/target/NetworkPacketAnalyzer.jar` (2.9 MB)

This is a **fat JAR** (uber JAR) containing:
- ✅ All application classes (Main, PacketAnalyzer, PacketInfo, StatisticsManager)
- ✅ All dependencies bundled (pcap4j-core, pcap4j-packetfactory, slf4j-simple, jna)
- ✅ Executable manifest with Main-Class configured
- ✅ Ready to run immediately after download

### 🚀 Launcher Scripts

**Linux/macOS**: `run.sh`
- Checks Java version (requires 21+)
- Validates JAR file exists
- Handles permissions for packet capture
- Single command: `./run.sh`

**Windows**: `run.bat`
- Checks Java version
- Validates JAR file exists  
- Single command: `run.bat` (double-click)

### 📖 Documentation

- **[DISTRIBUTION.md](DISTRIBUTION.md)** - Complete guide for end users
  - Installation instructions (Linux, macOS, Windows)
  - How to create distribution packages
  - Troubleshooting guide
  - Advanced usage examples

- **[JAVA_21_UPGRADE.md](JAVA_21_UPGRADE.md)** - Java 21 LTS details
  - Configuration details
  - Build instructions
  - Feature availability

## 🎯 How to Share

### Option 1: Direct JAR Download
Users can download just the JAR file and run:
```bash
java -jar NetworkPacketAnalyzer.jar
```

### Option 2: Share with Launcher Script
Package together:
- `NetworkPacketAnalyzer.jar`
- `run.sh` (for Linux/macOS)
- `run.bat` (for Windows)

Users extract and run: `./run.sh` or `run.bat`

### Option 3: Create Distributable Archive

**Linux/macOS Archive (.tar.gz)**:
```bash
cd target
tar -czf NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz \
  NetworkPacketAnalyzer.jar \
  ../run.sh \
  ../DISTRIBUTION.md
```

**Windows Archive (.zip)**:
```cmd
# Using Windows built-in zip or 7-Zip
# Include:
#   - NetworkPacketAnalyzer.jar
#   - run.bat
#   - DISTRIBUTION.md
```

## 📋 File Checklist

```
✅ pom.xml                              (configured with maven-shade-plugin)
✅ target/NetworkPacketAnalyzer.jar     (executable fat JAR - 2.9 MB)
✅ run.sh                               (Linux/macOS launcher - executable)
✅ run.bat                              (Windows launcher)
✅ DISTRIBUTION.md                      (user guide & distribution instructions)
✅ JAVA_21_UPGRADE.md                   (Java 21 configuration details)
```

## 🔧 System Requirements for Users

| Component | Requirement | Notes |
|-----------|-------------|-------|
| **Java** | 21 LTS or later | Required to run JAR |
| **LibPcap** | Development libraries | Required for packet capture |
| **OS** | Windows/macOS/Linux | Cross-platform support |

### Install Prerequisites

**Linux**:
```bash
sudo apt install openjdk-21-jdk libpcap-dev
```

**macOS**:
```bash
brew install openjdk@21 libpcap
```

**Windows**:
1. Download Java 21 from https://jdk.java.net/21/
2. LibPcap usually pre-installed or available

## ✨ Quick Distribution Steps

1. **Build** (already done):
   ```bash
   mvn clean package
   ```

2. **Copy the JAR** to distribution location:
   ```bash
   cp target/NetworkPacketAnalyzer.jar ~/Downloads/
   ```

3. **Share with users**:
   - Direct download link to JAR
   - Or distribute with launcher scripts
   - Provide installation instructions from DISTRIBUTION.md

## 🎁 What Users Get

After downloading:
- ✅ No build tools required (Maven, JDK 21+ just for running)
- ✅ No compilation needed
- ✅ Single file download (JAR)
- ✅ Double-click or command-line execution
- ✅ Works on all major platforms

## 📊 Build Information

```
Build Tool:        Maven 3.9.12
Java Version:      Java 25.0.3 (compiled for 21 target)
Package Type:      Fat JAR (uber JAR)
Bundled Plugin:    maven-shade-plugin
JAR Size:          2.9 MB (with all dependencies)
Main Class:        com.onesmus.Main
Manifest:          Configured with Main-Class entry
```

## 🚨 Important Notes

- **LibPcap Dependency**: LibPcap (native library) must be installed separately on each system
  - It cannot be bundled in Java JAR files
  - Installation instructions provided in DISTRIBUTION.md
  
- **Elevated Permissions**: On Linux/macOS, packet capture requires root/sudo privileges
  - Users may run with: `sudo ./run.sh`
  - Or configure appropriate capabilities

- **Cross-Platform**: Application runs identically on Windows, macOS, and Linux
  - Only system-level requirement is LibPcap

## 🎉 Summary

**Your application is production-ready for distribution!**

The JAR file is completely self-contained and can be:
- Downloaded directly
- Shared via cloud storage
- Packaged with installers
- Distributed in app stores
- Run from any directory

All dependencies are bundled. Users only need Java 21+ and LibPcap installed.

---

For complete setup and distribution instructions, see [DISTRIBUTION.md](DISTRIBUTION.md).
