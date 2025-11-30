#!/usr/bin/env sh
# PUBLIC_INTERFACE
# run.sh - Wrapper to run the Spring Boot app using Maven Wrapper on port 8989.
# Usage:
#   ./run.sh             -> run with ./mvnw
#   sh run.sh            -> same as above, portable
#   RUN_MODE=build ./run.sh  -> perform build only

set -e

if [ "${RUN_MODE}" = "build" ]; then
  if [ -x "./mvnw" ]; then
    ./mvnw -q -DskipTests clean package
  else
    sh mvnw -q -DskipTests clean package
  fi
  exit $?
fi

if [ -x "./mvnw" ]; then
  ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"
else
  sh mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"
fi
