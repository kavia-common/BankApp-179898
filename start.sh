#!/usr/bin/env sh
# PUBLIC_INTERFACE
# start.sh - Generic start script for preview environments; delegates to Maven Wrapper.
# - Binds to 0.0.0.0 and uses $PORT (default 3001).
# - Optional CLEAN_PACKAGE=true to verify wrapper by building before running.
# - Previews that attempt 'mvn' will be handled by the root-level 'mvn' shim.
set -e

PORT_VALUE="${PORT:-3001}"
ARGS="--server.port=${PORT_VALUE} --server.address=0.0.0.0 --server.servlet.context-path=/"

if [ "${CLEAN_PACKAGE}" = "true" ]; then
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
