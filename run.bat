@echo off
setlocal enabledelayedexpansion
set SCRIPT_DIR=%~dp0
set JAR_FILE=%SCRIPT_DIR%target\NetworkPacketAnalyzer.jar

java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java 23+ is required. Download from https://jdk.java.net/23/
    pause & exit /b 1
)

if not exist "%JAR_FILE%" (
    echo JAR not found. Building...
    cd "%SCRIPT_DIR%" && mvn clean package -q
)

echo Starting NetPulse on http://localhost:8080 ...
start "" java -jar "%JAR_FILE%" %*

echo Waiting for server to start...
:WAIT
timeout /t 2 /nobreak >nul
curl -s http://localhost:8080 >nul 2>&1
if errorlevel 1 goto WAIT

echo Opening browser...
start http://localhost:8080
endlocal
