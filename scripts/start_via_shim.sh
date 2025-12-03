#!/usr/bin/env sh
# PUBLIC_INTERFACE
# start_via_shim.sh
# Purpose: Compatibility start script using the local ./mvn shim which proxies to ./mvnw.
# Preferred to use: ./mvnw directly. This shim path is for environments that insist on 'mvn'.
# Usage: ./scripts/start_via_shim.sh
# Notes:
# - Binds to 0.0.0.0 and uses $PORT (default 3001).
# - Works even if execute bits are stripped by using 'sh mvn' fallback.
set -e

# Prefer Java 21 for running the mvn shim so maven-compiler can use --release 21.
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

if [ -x "./mvn" ]; then
  exec ./mvn spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
else
  exec sh mvn spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
fi
