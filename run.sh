#!/usr/bin/env sh
# PUBLIC_INTERFACE
# run.sh - Wrapper to run the Spring Boot app using Maven Wrapper on port 8989.
# Summary:
#   - Uses ./mvnw so no system Maven is required. A root-level 'mvn' shim also proxies to ./mvnw.
#   - Exposes the app on 0.0.0.0:8989 for preview environments.
# Usage:
#   ./run.sh                     -> run with ./mvnw
#   sh run.sh                    -> same as above, portable
#   RUN_MODE=build ./run.sh      -> perform build only (clean package, skip tests)

set -e

if [ "${RUN_MODE}" = "build" ]; then
  if [ -x "./mvnw" ]; then
    ./mvnw -q -DskipTests clean package
  else
    sh mvnw -q -DskipTests clean package
  fi
  exit $?
fi

ARGS='--server.port=8989 --server.address=0.0.0.0'

if [ -x "./mvnw" ]; then
  exec ./mvnw spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
else
  exec sh mvnw spring-boot:run -Dspring-boot.run.arguments="${ARGS}"
fi
