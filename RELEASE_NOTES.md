# Release Notes

## NetworkPacketAnalyzer 1.0-SNAPSHOT

### Release Date
2026-06-16

### Summary
This release delivers NetworkPacketAnalyzer as a downloadable application with a standalone executable JAR and cross-platform launch scripts.

### Highlights
- Packaged as a self-contained fat JAR for easy distribution
- Added `run.sh` for Linux/macOS and `run.bat` for Windows
- Updated project to target Java 23
- Included detailed distribution and user documentation
- Added `create-distribution.sh` to generate tar.gz and zip archives

### Files Included
- `target/NetworkPacketAnalyzer.jar`
- `run.sh`
- `run.bat`
- `DISTRIBUTION.md`
- `USER-GUIDE.md`
- `README-DOWNLOAD.md`
- `create-distribution.sh`
- `dist/NetworkPacketAnalyzer-1.0-SNAPSHOT.tar.gz`
- `dist/NetworkPacketAnalyzer-1.0-SNAPSHOT.zip`
- `dist/DISTRIBUTION_SUMMARY.txt`

### Notes
- The JAR bundles Java dependencies, but libpcap must still be installed separately on target systems.
- Packet capture requires appropriate permissions or root access on Unix-like systems.

### Upgrade Notes
- If deploying to new systems, install Java 23 and libpcap before running the app.
- For Windows users, simply extract the zip and run `run.bat`.
