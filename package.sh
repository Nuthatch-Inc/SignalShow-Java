#!/bin/bash
# Package SignalShow as an executable JAR

cd "$(dirname "$0")"
mvn clean package
echo ""
echo "Executable JAR created at: target/signalshow-1.0.0-SNAPSHOT.jar"
echo "Run with: java -jar target/signalshow-1.0.0-SNAPSHOT.jar"
