@echo off
REM NetworkPacketAnalyzer Application Launcher
REM Windows version

setlocal enabledelayedexpansion

REM Get the directory where this script is located
set SCRIPT_DIR=%~dp0

REM JAR file location
set JAR_FILE=%SCRIPT_DIR%target\NetworkPacketAnalyzer.jar

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java 21 or later is not installed.
    echo Please install Java 21 LTS from https://jdk.java.net/21/
    pause
    exit /b 1
)

REM Check if JAR file exists
if not exist "%JAR_FILE%" (
    echo Error: JAR file not found at %JAR_FILE%
    echo Please build the project first:
    echo   mvn clean package
    pause
    exit /b 1
)

REM Run the application
echo Starting NetworkPacketAnalyzer...
java -jar "%JAR_FILE%" %*
pause

endlocal
