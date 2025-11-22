@echo off
REM Build native Windows installer (.exe) for SignalShow
REM Must be run on Windows with Java 17+ and WiX Toolset installed
REM https://wixtoolset.org/

setlocal enabledelayedexpansion

echo Building SignalShow Windows Installer...
echo.

REM Check Java version
java -version 2>&1 | findstr /R "version" > temp.txt
set /p JAVA_VERSION_LINE=<temp.txt
del temp.txt

echo Java detected
echo.

REM Clean and build
echo Building JAR with Maven...
call mvn clean package -q

if not exist "target\signalshow-1.0.0-SNAPSHOT.jar" (
    echo Error: JAR file not found after build
    exit /b 1
)

echo JAR built successfully
echo.

REM Create distribution directory
if not exist "target\dist" mkdir target\dist

REM Build Windows installer
echo Creating Windows installer...
jpackage ^
  --type exe ^
  --name SignalShow ^
  --app-version 1.0.0 ^
  --input target ^
  --main-jar signalshow-1.0.0-SNAPSHOT.jar ^
  --main-class SignalShow ^
  --dest target\dist ^
  --vendor "SignalShow" ^
  --copyright "Copyright (c) 2005-2025 SignalShow" ^
  --description "Educational signal and image processing application" ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --win-menu-group SignalShow ^
  --java-options -Xmx2g

echo.
echo Build complete!
echo.
echo Installer location:
dir target\dist\SignalShow*.exe
echo.
echo To distribute:
echo   Upload the installer to your download server
echo   Users run the installer to install SignalShow
echo.
