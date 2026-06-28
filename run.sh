#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
JAR_FILE="$SCRIPT_DIR/target/NetworkPacketAnalyzer.jar"

if ! command -v java &> /dev/null; then
    echo "Error: Java 23+ is required. Download from https://jdk.java.net/23/"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F'"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 23 ]; then
    echo "Error: Java 23+ required. Current: $(java -version 2>&1 | head -1)"
    exit 1
fi

if [ ! -f "$JAR_FILE" ]; then
    echo "JAR not found. Building..."
    cd "$SCRIPT_DIR" && mvn clean package -q
fi

if [ "$EUID" -ne 0 ]; then
    echo "Warning: Packet capture may require elevated privileges."
    echo "Run with: sudo $0"
    echo ""
fi

echo "Starting NetPulse on http://localhost:8080 ..."
java -jar "$JAR_FILE" "$@" &
APP_PID=$!

# Wait for server to be ready then open browser
echo "Waiting for server..."
for i in $(seq 1 30); do
    if curl -s http://localhost:8080 > /dev/null 2>&1; then
        echo "Server ready. Opening browser..."
        if command -v xdg-open &> /dev/null; then
            xdg-open http://localhost:8080
        elif command -v open &> /dev/null; then
            open http://localhost:8080
        fi
        break
    fi
    sleep 1
done

wait $APP_PID
