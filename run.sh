#!/bin/bash
# Run SignalShow from packaged JAR (package first if needed)

cd "$(dirname "$0")"

JAR_FILE="target/signalshow-1.0.0-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found. Building..."
    mvn clean package
fi

java -jar "$JAR_FILE"
