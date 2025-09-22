#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
SIG_DIR="$ROOT_DIR/SignalShow"

JARS=("$SIG_DIR/jai_core.jar" "$SIG_DIR/jai_codec.jar")
for j in "${JARS[@]}"; do
  if [ ! -f "$j" ]; then
    echo "Missing jar: $j"
    exit 1
  fi
done

CLASSPATH="$SIG_DIR:${JARS[*]}"
# convert spaces to colons
CLASSPATH="${CLASSPATH// /:}"

echo "Starting SignalShow with classpath: $CLASSPATH"

java -cp "$CLASSPATH" SignalShow
