#!/usr/bin/env sh
# PUBLIC_INTERFACE
# start.sh - Generic start script for preview environments; delegates to Maven Wrapper.
# - Binds to 0.0.0.0 and uses $PORT (default 3001).
# - Optional CLEAN_PACKAGE=true to verify wrapper by building before running.
set -e

# Prefer Java 21 for running Maven so that --release 21 is supported.
JAVA_21_CANDIDATE=""
if [ -n "${JAVA_21_HOME:-}" ] && [ -x "${JAVA_21_HOME}/bin/java" ]; then
  JAVA_21_CANDIDATE="${JAVA_21_HOME}"
elif [ -d "/usr/lib/jvm/java-21-openjdk-amd64" ] && [ -x "/usr/lib/jvm/java-21-openjdk-amd64/bin/java" ]; then
  JAVA_21_CANDIDATE="/usr/lib/jvm/java-21-openjdk-amd64"
fi

if [ -n "${JAVA_21_CANDIDATE}" ]; then
  export JAVA_HOME="${JAVA_21_CANDIDATE}"
fi

PORT_VALUE="${PORT:-3001}"
ARGS="--server.port=${PORT_VALUE} --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"

if [ "${CLEAN_PACKAGE:-false}" = "true" ]; then
  if [ -x "./mvnw" ]; then
    ./mvnw -q -DskipTests clean package
  else
    sh mvnw -q -DskipTests clean package
  fi
fi

if [ -x "./mvnw" ]; then
  exec ./mvnw spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
else
  exec sh mvnw spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
fi
