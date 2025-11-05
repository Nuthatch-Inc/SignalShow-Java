#!/bin/bash
# Run SignalShow from packaged JAR (package first if needed)

cd "$(dirname "$0")"

# Extract version from pom.xml
VERSION=$(grep -m1 '<version>' pom.xml | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | tr -d '[:space:]')

JAR_FILE="target/signalshow-${VERSION}.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found. Building..."
    mvn clean package -DskipTests
fi

java -jar "$JAR_FILE"
