# Download NetworkPacketAnalyzer

## Latest Release: v1.0.0

Published: 2026-06-16

### Direct Download Links

Select your platform and download the appropriate package:

#### Windows & Universal (.zip)
**[Download NetworkPacketAnalyzer-1.0-SNAPSHOT.zip](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/releases/download/v1.0.0/NetworkPacketAnalyzer-1.0-SNAPSHOT.zip)** (2.8 MB)

*Recommended for Windows users; also works on macOS and Linux.*

#### Linux/macOS (.tar.gz)
**[Download NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/releases/download/v1.0.0/NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz)** (2.8 MB)

*Recommended for Linux and macOS users.*

#### Checksums (SHA256)
Verify file integrity before running:

- **ZIP checksum**: [NetworkPacketAnalyzer-1.0-SNAPSHOT.zip.sha256](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/releases/download/v1.0.0/NetworkPacketAnalyzer-1.0-SNAPSHOT.zip.sha256)
- **TAR.GZ checksum**: [NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz.sha256](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/releases/download/v1.0.0/NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz.sha256)

Verify with:
```bash
sha256sum -c NetworkPacketAnalyzer-1.0-SNAPSHOT.zip.sha256
sha256sum -c NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz.sha256
```

### Quick Start

After downloading, follow these steps:

#### Windows
1. Extract the `.zip` file
2. Ensure Java 23 is installed
3. Open Command Prompt in the extracted folder
4. Run: `run.bat`

#### macOS/Linux
1. Extract the `.tar.gz` file
2. Ensure Java 23 and LibPcap are installed:
   ```bash
   # macOS
   brew install openjdk@23 libpcap
   
   # Linux (Debian/Ubuntu)
   sudo apt install openjdk-23-jdk libpcap-dev
   ```
3. Open Terminal in the extracted folder
4. Run: `chmod +x run.sh && sudo ./run.sh`

### Alternative Downloads

**GitHub Release Page**: [View All Assets](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/releases/tag/v1.0.0)

### System Requirements

- **Java**: 23 or later (download from https://jdk.java.net/23/)
- **LibPcap**: Network packet capture library (usually pre-installed on macOS)
- **OS**: Windows, macOS, or Linux

### Troubleshooting

**Java not found?**
Install Java 23 from: https://jdk.java.net/23/

**LibPcap not found?**
```bash
# Ubuntu/Debian
sudo apt install libpcap-dev

# Fedora/RHEL
sudo yum install libpcap-devel

# macOS
brew install libpcap
```

**Permission denied?** (Linux/macOS)
```bash
chmod +x run.sh
sudo ./run.sh
```

### Installation Help

For detailed setup instructions, see:
- **[DISTRIBUTION.md](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/blob/master/DISTRIBUTION.md)** — Complete installation guide
- **[USER-GUIDE.md](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/blob/master/USER-GUIDE.md)** — Quick start for end users
- **[README.md](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/blob/master/README.md)** — Project overview

### Archive Contents

Both `.zip` and `.tar.gz` packages contain:
```
NetworkPacketAnalyzer-1.0-SNAPSHOT/
├── NetworkPacketAnalyzer.jar    (2.9 MB executable)
├── run.sh                        (Linux/macOS launcher)
├── run.bat                       (Windows launcher)
├── DISTRIBUTION.md              (Installation guide)
├── USER-GUIDE.md                (Quick start)
├── README-DOWNLOAD.md           (This file)
└── JAVA_21_UPGRADE.md           (Technical reference)
```

### Release Notes

v1.0.0 highlights:
- ✅ Packaged as standalone executable JAR (fat JAR)
- ✅ Java 23 target runtime
- ✅ Cross-platform launcher scripts (run.sh for Unix, run.bat for Windows)
- ✅ All dependencies bundled
- ✅ SHA256 checksums included for verification
- ✅ Comprehensive documentation included

See [RELEASE_NOTES.md](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/blob/master/RELEASE_NOTES.md) for full details.

### Feedback & Issues

Found a bug or have a suggestion? Open an issue on GitHub:
https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/issues

### License & Attribution

See the repository for license information.

---

**Share this page**: https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/releases/tag/v1.0.0
