# NetworkPacketAnalyzer

[![Latest Release](https://img.shields.io/github/v/release/kinarocarlos-hub/NetworkPacketAnalyzer?label=v1.0.0&color=blue)](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/kinarocarlos-hub/NetworkPacketAnalyzer/total?label=Downloads&color=brightgreen)](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/releases)

NetworkPacketAnalyzer is a lightweight Java packet capture and analysis application packaged as a standalone executable JAR for Windows, macOS, and Linux.

## 📥 Download v1.0.0

**Get the latest release here**: https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/releases/tag/v1.0.0

- **Windows/Universal (.zip)**: [NetworkPacketAnalyzer-1.0-SNAPSHOT.zip](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/releases/download/v1.0.0/NetworkPacketAnalyzer-1.0-SNAPSHOT.zip)
- **macOS/Linux (.tar.gz)**: [NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz](https://github.com/kinarocarlos-hub/NetworkPacketAnalyzer/releases/download/v1.0.0/NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz)

For detailed download and installation instructions, see [DOWNLOAD.md](DOWNLOAD.md).

## What this release includes

- Executable fat JAR: `target/NetworkPacketAnalyzer.jar`
- Cross-platform launch scripts: `run.sh` and `run.bat`
- Java 23 target runtime
- Bundled dependencies via Maven Shade plugin
- Distribution archives created in the `dist/` folder
- Comprehensive user documentation in `DISTRIBUTION.md` and `USER-GUIDE.md`

## Getting Started

### Prerequisites

- Java 23 or later
- LibPcap installed on the host system

### Run the Application

#### Linux/macOS

```bash
chmod +x run.sh
sudo ./run.sh
```

#### Windows

```cmd
run.bat
```

#### Direct JAR execution

```bash
java -jar target/NetworkPacketAnalyzer.jar
```

## Packaging

The application is packaged as a fat JAR so users do not need to resolve dependencies manually. Use the `create-distribution.sh` script to generate download-ready archives.

```bash
./create-distribution.sh
```

## Distribution files

- `dist/NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz`
- `dist/NetworkPacketAnalyzer-1.0-SNAPSHOT.zip`
- `dist/DISTRIBUTION_SUMMARY.txt`

## Documentation

- `DISTRIBUTION.md` — detailed distribution guide
- `USER-GUIDE.md` — end-user quick start and troubleshooting
- `JAVA_23_UPGRADE.md` — Java version upgrade details
- `README-DOWNLOAD.md` — download guide

## Support

If you experience issues, first verify that Java 23 is installed and that libpcap is available on your operating system.
