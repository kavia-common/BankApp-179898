#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Maven Wrapper Startup Script (3.2.x)
# ----------------------------------------------------------------------------

set -e

# OS specific support.
cygwin=false
darwin=false
mingw=false
case "$(uname)" in
  CYGWIN*) cygwin=true ;;
  MINGW*)  mingw=true ;;
  Darwin*) darwin=true ;;
esac

# resolve basedir (project root containing .mvn)
find_maven_basedir() {
  dir="$1"
  while [ "$dir" != "/" ] ; do
    if [ -d "$dir/.mvn" ] ; then
      echo "$dir"
      return
    fi
    dir=$(cd "$dir/.."; pwd)
  done
  echo ""
}

BASE_DIR="$(find_maven_basedir "$(pwd)")"
if [ -z "$BASE_DIR" ]; then
  echo "Error: could not find .mvn directory in parent hierarchy" >&2
  exit 1
fi

# Read jvm.config if present and append to MAVEN_OPTS
if [ -f "$BASE_DIR/.mvn/jvm.config" ]; then
  MAVEN_OPTS="$(tr -s '\n' ' ' < "$BASE_DIR/.mvn/jvm.config") $MAVEN_OPTS"
fi

# prepare paths for Cygwin
if $cygwin ; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  BASE_DIR="$(cygpath --unix "$BASE_DIR")"
fi

WRAPPER_DIR="$BASE_DIR/.mvn/wrapper"
WRAPPER_JAR="$WRAPPER_DIR/maven-wrapper.jar"
WRAPPER_PROPERTIES="$WRAPPER_DIR/maven-wrapper.properties"

# Ensure wrapper directory exists
if [ ! -d "$WRAPPER_DIR" ]; then
  mkdir -p "$WRAPPER_DIR"
fi

# Resolve wrapper jar download URL and optional checksum from properties
DEFAULT_WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
WRAPPER_URL="$DEFAULT_WRAPPER_URL"
WRAPPER_SHA256=""
if [ -f "$WRAPPER_PROPERTIES" ]; then
  while IFS='=' read -r key value
  do
    case "$key" in
      wrapperUrl) WRAPPER_URL="$value" ;;
      wrapperSha256Sum) WRAPPER_SHA256="$value" ;;
    esac
  done < "$WRAPPER_PROPERTIES"
fi

download_with() {
  url="$1"; out="$2"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$url" -o "$out"
    return $?
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$url" -O "$out"
    return $?
  else
    return 127
  fi
}

verify_sha256() {
  file="$1"; expected="$2"
  [ -z "$expected" ] && return 0
  if command -v sha256sum >/dev/null 2>&1; then
    actual="$(sha256sum "$file" | awk '{print $1}')"
  elif command -v shasum >/dev/null 2>&1; then
    actual="$(shasum -a 256 "$file" | awk '{print $1}')"
  else
    # No checksum tool; skip verification
    return 0
  fi
  [ "$actual" = "$expected" ]
}

# Download wrapper jar if missing
if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Downloading Maven Wrapper jar from: $WRAPPER_URL"
  download_with "$WRAPPER_URL" "$WRAPPER_JAR" || {
    echo "Failed to download Maven Wrapper jar. Ensure curl or wget is available." >&2
    exit 1
  }
  if ! verify_sha256 "$WRAPPER_JAR" "$WRAPPER_SHA256"; then
    echo "SHA-256 checksum verification failed for maven-wrapper.jar" >&2
    rm -f "$WRAPPER_JAR"
    exit 1
  fi
fi

# Determine Java command
if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD="$(command -v java || true)"
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly or java not found on PATH." >&2
  echo "  We cannot execute $JAVACMD" >&2
  exit 1
fi

# Export base dir for wrapper main
export MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$BASE_DIR}"

# Windows path conversion for Cygwin before Java exec
if $cygwin ; then
  MAVEN_PROJECTBASEDIR="$(cygpath --windows "$MAVEN_PROJECTBASEDIR")"
fi

WRAPPER_MAIN=org.apache.maven.wrapper.MavenWrapperMain

exec "$JAVACMD" \
  $MAVEN_OPTS \
  -classpath "$WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
  $WRAPPER_MAIN "$@"
