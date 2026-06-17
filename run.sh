#!/bin/bash

# NetworkPacketAnalyzer Application Launcher
# Linux/Unix/macOS version

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# JAR file location
JAR_FILE="$SCRIPT_DIR/target/NetworkPacketAnalyzer.jar"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java 21 or later is not installed."
    echo "Please install Java 21 LTS from https://jdk.java.net/21/"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F'"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "Error: Java 21 or later is required."
    echo "Current version: $(java -version 2>&1 | head -1)"
    exit 1
fi

# Check if JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found at $JAR_FILE"
    echo "Please build the project first:"
    echo "  mvn clean package"
    exit 1
fi

# Check if running as root (required for packet capture)
if [ "$EUID" -ne 0 ]; then 
    echo "Warning: This application captures network packets."
    echo "Running without root/sudo may fail to capture packets."
    echo "Consider running with: sudo $0"
    echo ""
fi

# Run the application
echo "Starting NetworkPacketAnalyzer..."
java -jar "$JAR_FILE" "$@"
