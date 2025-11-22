#!/bin/bash
# Compile SignalShow with Maven

cd "$(dirname "$0")"
mvn clean compile
