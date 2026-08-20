#!/bin/bash
export JAVA_HOME=/home/flynn/.local/jdks/jdk-26.0.1
export PATH=$JAVA_HOME/bin:$PATH

scriptdir=$(dirname "${BASH_SOURCE[0]}")
MODULE_PATH="$scriptdir/lib/linux"
ADD_MODULES="javafx.controls"
JAR_FILE="$scriptdir/target/opentierlist.jar"
LOG_FOLDER="$scriptdir/log"
LOG_FILE="$LOG_FOLDER/otl.log"

if [ ! -d "$MODULE_PATH" ]; then
  echo "[ERROR] --- lib/linux folder does not exist ---"
  exit 1
fi

if [ ! -f "$JAR_FILE" ]; then
  echo "[ERROR] --- Jar file not found at path: $JAR_FILE ---"
  exit 1
fi

if [ ! -d "$LOG_FOLDER" ]; then
  mkdir "$LOG_FOLDER"
fi

nohup java --enable-native-access=javafx.graphics --module-path "$MODULE_PATH" \
  --add-modules $ADD_MODULES \
  -jar "$JAR_FILE" \
  >>"$LOG_FILE" 2>&1 &
