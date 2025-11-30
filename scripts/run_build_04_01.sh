#!/usr/bin/env sh
# PUBLIC_INTERFACE
# run_build_04_01.sh
# Runs: ./mvn -q -DskipTests clean package and captures full logs to ./logs/build-04.01.txt
# Uses local shim/wrapper ensuring no system 'mvn' is required.
set -e

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"
cd "${ROOT_DIR}"

mkdir -p ./logs

# Record header
{
  echo "=== Java 21 Migration Step 04.01 Clean Build Log ==="
  echo "Date: $(date -u)"
  echo "Command: ./mvn -q -DskipTests clean package (with wrapper fallback)"
  echo ""
} > ./logs/build-04.01.txt

# Prefer ./mvn shim; it proxies to ./mvnw and falls back to 'sh mvnw' automatically
if [ -x "./mvn" ]; then
  ./mvn -q -DskipTests clean package >> ./logs/build-04.01.txt 2>&1 || true
else
  # In case execute bit is stripped, fall back to 'sh mvn ...'
  sh mvn -q -DskipTests clean package >> ./logs/build-04.01.txt 2>&1 || true
fi

echo "" >> ./logs/build-04.01.txt
echo "=== End of Log ===" >> ./logs/build-04.01.txt

echo "Build log saved to ./logs/build-04.01.txt"
