#!/usr/bin/env sh
# PUBLIC_INTERFACE
# capture_build_log_04_01.sh
# Purpose: Run a clean build using Maven Wrapper and capture logs for migration step 04.01.
# Usage: ./scripts/capture_build_log_04_01.sh
# Notes:
# - Uses ./mvnw (or sh mvnw) so no system 'mvn' is required.
# - Logs are stored under ./logs/build-04.01.txt

set -e

LOG_DIR="./logs"
LOG_FILE="${LOG_DIR}/build-04.01.txt"

mkdir -p "${LOG_DIR}"

echo "=== Java 21 Migration Step 04.01 Clean Build Log ===" > "${LOG_FILE}"
echo "Date: $(date -u)" >> "${LOG_FILE}"
echo "Command: mvnw -DskipTests clean package" >> "${LOG_FILE}"
echo "" >> "${LOG_FILE}"

# Prefer executable mvnw; fall back to sh mvnw if needed
if [ -x "./mvnw" ]; then
  # Use verbose output to capture useful diagnostics; do not use -q here.
  ./mvnw -DskipTests clean package 2>&1 | tee -a "${LOG_FILE}"
else
  sh mvnw -DskipTests clean package 2>&1 | tee -a "${LOG_FILE}"
fi

echo "" >> "${LOG_FILE}"
echo "=== End of Log ===" >> "${LOG_FILE}"
echo "Build log saved to ${LOG_FILE}"
