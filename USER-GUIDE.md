# NetworkPacketAnalyzer - User Download Guide

## 🎯 For End Users: How to Download and Run

If you've just downloaded NetworkPacketAnalyzer, follow this guide to get it running on your system.

## Step 1: Check Requirements

Before downloading, ensure you have:

- **Java 21 LTS or later** installed
- **LibPcap development libraries** installed

### Verify Java is Installed

```bash
# macOS and Linux
java -version

# Windows
java -version

# Should show Java 21 or later
```

If you don't have Java, download from: https://jdk.java.net/21/

## Step 2: Download the Application

Download one of these packages:

- **For Linux/macOS**: `NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz` (~3 MB)
- **For Windows**: `NetworkPacketAnalyzer-1.0-SNAPSHOT.zip` (~3 MB)
- **For Any OS**: Direct JAR file: `NetworkPacketAnalyzer.jar` (2.9 MB)

## Step 3: Install System Dependencies

Before running, install LibPcap on your system:

### Linux (Debian/Ubuntu)
```bash
sudo apt update
sudo apt install libpcap-dev
```

### Linux (Red Hat/CentOS/Fedora)
```bash
sudo yum install libpcap-devel
```

### macOS
```bash
brew install libpcap
```

### Windows
LibPcap is usually pre-installed. If needed, download from: https://www.winpcap.org/

## Step 4: Extract and Run

### On Linux/macOS

```bash
# Extract the package
tar -xzf NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz

# Navigate into the directory
cd NetworkPacketAnalyzer-1.0-SNAPSHOT

# Run with the launcher script
sudo ./run.sh
```

Or directly run:
```bash
sudo java -jar NetworkPacketAnalyzer.jar
```

### On Windows

**Option 1: Using the launcher script**
1. Extract `NetworkPacketAnalyzer-1.0-SNAPSHOT.zip`
2. Open Command Prompt or PowerShell
3. Navigate to the extracted folder
4. Run: `run.bat`

**Option 2: Direct JAR execution**
1. Extract the ZIP file
2. Open Command Prompt
3. Run: `java -jar NetworkPacketAnalyzer.jar`

## Step 5: Troubleshooting

### "Java not found"
- **Solution**: Install Java 21 LTS from https://jdk.java.net/21/
- Or use your system package manager:
  - Ubuntu/Debian: `sudo apt install openjdk-21-jdk`
  - macOS: `brew install openjdk@21`

### "Cannot find or open libpcap"
- **Solution**: Install LibPcap development libraries
- See Step 3 above for your operating system

### "Permission denied" (Linux/macOS)
- **Solution 1**: Make script executable: `chmod +x run.sh`
- **Solution 2**: Use sudo for packet capture: `sudo ./run.sh`

### "Cannot capture packets" or "No interface found"
- **Solution 1**: Run with elevated permissions: `sudo ./run.sh`
- **Solution 2**: Use a valid network interface
- **Solution 3**: On Linux, you may need to configure capabilities:
  ```bash
  sudo setcap cap_net_raw,cap_net_admin=eip /usr/bin/java
  ```

### "JAR file not found"
- **Cause**: You extracted the package but the JAR moved
- **Solution**: Ensure `NetworkPacketAnalyzer.jar` is in the same directory as `run.sh` or `run.bat`

## Advanced Usage

### Custom Memory Settings
If the application runs out of memory:

```bash
java -Xms256m -Xmx1024m -jar NetworkPacketAnalyzer.jar
```

- `-Xms256m` = Initial memory allocation (256 MB)
- `-Xmx1024m` = Maximum memory allocation (1024 MB)

### Running from Any Directory

You can run the JAR from anywhere:

```bash
java -jar /path/to/NetworkPacketAnalyzer.jar
```

Or create a system-wide shortcut (Linux/macOS):

```bash
# Create symbolic link
sudo ln -s /path/to/NetworkPacketAnalyzer.jar /usr/local/bin/packet-analyzer

# Then run from anywhere
packet-analyzer
```

## What's Inside

The downloaded package contains:

```
NetworkPacketAnalyzer-1.0-SNAPSHOT/
├── NetworkPacketAnalyzer.jar       (2.9 MB - The application)
├── run.sh                           (Launcher for Linux/macOS)
├── run.bat                          (Launcher for Windows)
├── DISTRIBUTION.md                 (Detailed installation guide)
├── README-DOWNLOAD.md              (Quick start guide)
└── JAVA_21_UPGRADE.md              (Technical details)
```

## Key Features

✅ **Self-Contained**: All dependencies bundled in the JAR  
✅ **Cross-Platform**: Runs on Windows, macOS, Linux  
✅ **No Compilation Needed**: Just download and run  
✅ **Java 21 LTS**: Modern Java with long-term support  
✅ **Network Analysis**: Packet capture and analysis using pcap4j  

## System Information

| Aspect | Details |
|--------|---------|
| **Package Type** | Executable Fat JAR |
| **Dependencies Bundled** | Yes (all included) |
| **Java Requirement** | Java 21 LTS or later |
| **External Dependencies** | LibPcap only |
| **Package Size** | 2.9 MB (JAR), ~3 MB (compressed) |
| **Installation** | Extract and run |
| **Configuration** | Automatic |

## Support

For more detailed information:
- **Installation Details**: See `DISTRIBUTION.md` in the package
- **Java Upgrade Info**: See `JAVA_21_UPGRADE.md` in the package
- **Quick Start**: See `README-DOWNLOAD.md` in the package

## Summary

1. ✅ Download the package for your OS
2. ✅ Install Java 21 (if needed)
3. ✅ Install LibPcap (if needed)
4. ✅ Extract the package
5. ✅ Run with `./run.sh` (Linux/macOS) or `run.bat` (Windows)
6. ✅ Enjoy network packet analysis!

---

**Questions?** Refer to the included documentation or see the troubleshooting section above.
