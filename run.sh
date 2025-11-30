#!/usr/bin/env sh
# PUBLIC_INTERFACE
# run.sh - Wrapper to run the Spring Boot app using Maven Wrapper.
# - Uses ./mvnw so no system Maven is required; 'mvn' shim proxies if called.
# - Exposes the app on 0.0.0.0:$PORT (default 8989) for preview environments.
# Usage:
#   ./run.sh                       -> run with ./mvnw
#   RUN_MODE=build ./run.sh        -> perform build only (clean package, skip tests)
#   CLEAN_PACKAGE=true ./run.sh    -> perform a clean package before run
set -e

PORT_VALUE="${PORT:-8989}"
ARGS="--server.port=${PORT_VALUE} --server.address=0.0.0.0"

if [ "${RUN_MODE}" = "build" ] || [ "${CLEAN_PACKAGE}" = "true" ]; then
  if [ -x "./mvnw" ]; then
    ./mvnw -q -DskipTests clean package
  else
    sh mvnw -q -DskipTests clean package
  fi
  [ "${RUN_MODE}" = "build" ] && exit $?
fi

if [ -x "./mvnw" ]; then
  exec ./mvnw spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
else
  exec sh mvnw spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
fi
