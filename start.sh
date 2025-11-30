#!/usr/bin/env sh
# PUBLIC_INTERFACE
# start.sh - Generic start script for preview environments; delegates to Maven Wrapper.
# Binds to 0.0.0.0 and uses port 8989 by default (or $PORT if provided).
# Note: Previews that attempt 'mvn' will be handled by the root-level 'mvn' shim.

set -e

PORT_VALUE="${PORT:-8989}"
ARGS="--server.port=${PORT_VALUE} --server.address=0.0.0.0"

if [ -x "./mvnw" ]; then
  exec ./mvnw spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
else
  exec sh mvnw spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
fi
