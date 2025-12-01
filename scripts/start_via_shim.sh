#!/usr/bin/env sh
# PUBLIC_INTERFACE
# start_via_shim.sh
# Purpose: Compatibility start script using the local ./mvn shim which proxies to ./mvnw.
# Preferred to use: ./mvnw directly. This shim path is for environments that insist on 'mvn'.
# Usage: ./scripts/start_via_shim.sh
# Notes:
# - Binds to 0.0.0.0 and uses $PORT (default 8989).
# - Works even if execute bits are stripped by using 'sh mvn' fallback.
set -e

PORT_VALUE="${PORT:-8989}"
ARGS="--server.port=${PORT_VALUE} --server.address=0.0.0.0"

if [ -x "./mvn" ]; then
  exec ./mvn spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
else
  exec sh mvn spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
fi
