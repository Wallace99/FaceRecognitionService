#!/usr/bin/env bash

set -e

env | sort

JAVA_OPTS=" -XX:InitialRAMPercentage=60.0"
JAVA_OPTS+=" -XX:MaxRAMPercentage=60.0"
JAVA_OPTS+=" -XX:MinRAMPercentage=60.0"
JAVA_OPTS+=" -Djava.awt.headless=true"
JAVA_OPTS+=" -Dfile.encoding=UTF8"
JAVA_OPTS+=" -Dlogback.configurationFile=logback.xml"

echo "\$JAVA_OPTS set to: \"${JAVA_OPTS}\""


exec java ${JAVA_OPTS} -jar /app/facedetection.jar