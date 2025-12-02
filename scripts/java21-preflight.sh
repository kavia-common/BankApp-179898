#!/usr/bin/env bash
# Simple preflight check to ensure Java 21+ is available before running Maven builds.
# It validates:
#   - JAVA_TOOLCHAIN_JAVA21_HOME (if defined by the platform)
#   - JAVA_21_HOME (used by .mvn/toolchains.xml)
#   - JAVA_HOME
#   - and finally `java` from PATH
#
# The script exits with a non-zero status if any of the configured homes point
# to a Java version lower than 21, so CI/preview systems can fail fast with a
# clear error message rather than a later "release version 21 not supported".

set -euo pipefail

MIN_MAJOR=21

# PUBLIC_INTERFACE
print_usage() {
  cat <<EOF
Java 21 preflight check

Usage:
  bash scripts/java21-preflight.sh

This script verifies that your environment variables and PATH Java point to
a JDK 21+ installation. It checks, in order:

  - JAVA_TOOLCHAIN_JAVA21_HOME (if defined)
  - JAVA_21_HOME               (used by .mvn/toolchains.xml)
  - JAVA_HOME
  - 'java' on PATH

If any of these resolve to a Java version lower than ${MIN_MAJOR}, the script
exits with code 1 and prints a detailed error message.

EOF
}

# Parse the effective major version from the first line of `java -version`.
# Handles both legacy "1.8.0_xxx" and modern "21.0.1" style strings.
parse_major_version() {
  local version_line="$1"
  local major="0"
  local minor="0"

  # Try to capture "X.Y.Z" or "X.Y"
  if [[ "$version_line" =~ \"([0-9]+)\.([0-9]+)\. ]]; then
    major="${BASH_REMATCH[1]}"
    minor="${BASH_REMATCH[2]}"
  elif [[ "$version_line" =~ \"([0-9]+)\.([0-9]+)\" ]]; then
    major="${BASH_REMATCH[1]}"
    minor="${BASH_REMATCH[2]}"
  elif [[ "$version_line" =~ \"([0-9]+)\" ]]; then
    major="${BASH_REMATCH[1]}"
    minor="0"
  fi

  # Normalize "1.x" (e.g., "1.8" => 8)
  if [[ "$major" == "1" ]]; then
    echo "$minor"
  else
    echo "$major"
  fi
}

check_env_home() {
  local env_name="$1"
  local home="${!env_name:-}"

  if [[ -z "$home" ]]; then
    # Not set, nothing to validate.
    return 0
  fi

  if [[ ! -x "$home/bin/java" ]]; then
    echo "ERROR: $env_name is set to '$home' but '$home/bin/java' does not exist or is not executable." >&2
    exit 1
  fi

  local header
  header="$("$home/bin/java" -version 2>&1 | head -n1)" || {
    echo "ERROR: Unable to execute '$home/bin/java' for $env_name." >&2
    exit 1
  }

  local major
  major="$(parse_major_version "$header")"

  if [[ -z "$major" || "$major" == "0" ]]; then
    echo "WARNING: Could not reliably parse Java version from: $header" >&2
    return 0
  fi

  if (( major < MIN_MAJOR )); then
    echo "ERROR: $env_name points to Java $major but this project requires Java ${MIN_MAJOR} or newer." >&2
    echo "       Update $env_name to a JDK ${MIN_MAJOR}+ installation before building." >&2
    exit 1
  else
    echo "OK: $env_name points to Java $major (>= ${MIN_MAJOR})." >&2
  fi
}

check_path_java() {
  if ! command -v java >/dev/null 2>&1; then
    echo "NOTE: 'java' not found on PATH; assuming builds will rely solely on JAVA_21_HOME / toolchains." >&2
    return 0
  fi

  local header
  header="$(java -version 2>&1 | head -n1)" || {
    echo "ERROR: Unable to execute 'java -version' from PATH." >&2
    exit 1
  }

  local major
  major="$(parse_major_version "$header")"

  if [[ -z "$major" || "$major" == "0" ]]; then
    echo "WARNING: Could not reliably parse Java version from: $header" >&2
    return 0
  fi

  if (( major < MIN_MAJOR )); then
    echo "ERROR: 'java' on PATH is Java $major but this project targets Java ${MIN_MAJOR}." >&2
    echo "       Either adjust PATH or ensure Maven uses a Java ${MIN_MAJOR}+ toolchain (JAVA_21_HOME)." >&2
    exit 1
  else
    echo "OK: 'java' on PATH is Java $major (>= ${MIN_MAJOR})." >&2
  fi
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  print_usage
  exit 0
fi

echo "Running Java 21 preflight check..." >&2

# Validate environment-based homes first so misconfigured toolchain homes fail fast.
check_env_home "JAVA_TOOLCHAIN_JAVA21_HOME"
check_env_home "JAVA_21_HOME"
check_env_home "JAVA_HOME"

# Then check the default PATH 'java' command.
check_path_java

echo "Java 21 preflight check passed: all detected JDK homes are Java ${MIN_MAJOR}+." >&2
