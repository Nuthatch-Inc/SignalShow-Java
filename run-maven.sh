#!/bin/bash
# Convenience script to run SignalShow with Maven

cd "$(dirname "$0")"
mvn exec:java
