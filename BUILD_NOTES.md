# Build Notes

- Removed maven-compiler-plugin `--add-opens jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED` arguments from pom.xml.
  These flags are not allowed when targeting Java 8 (`target=1.8` / `release=8`) and caused:
  "Fatal error compiling: error: option --add-opens not allowed with target 8".
- Project remains on Spring Boot 2.1.4.RELEASE and is configured for Java 8:
  - maven.compiler.source=1.8
  - maven.compiler.target=1.8
  - maven.compiler.release=8

Maven Wrapper standardization:
- The project uses Maven Wrapper exclusively. No system 'mvn' is required.
- A shim script named 'mvn' is provided at the project root to proxy any 'mvn ...' calls to './mvnw'.
- Preview/CI entrypoints (start, start.sh, run.sh, Makefile, Procfile) are wired to use './mvnw' so 'bash: mvn: command not found' is avoided.
- project_manifest.yaml is configured to use './mvnw' with 'sh mvnw' fallback; if a preview insists on invoking 'mvn', the 'mvn' shim proxies to './mvnw'.
- Definitive entry file for previews: ./start (supports CLEAN_PACKAGE to pre-build).

Build:
  ./mvnw -q -DskipTests clean package
  # or using the mvn shim (proxies to ./mvnw):
  ./mvn -q -DskipTests clean package
  # or using Makefile:
  make build
  # or if execution permission is blocked:
  sh mvnw -q -DskipTests clean package

Run (port 8989, bind 0.0.0.0):
  ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"
  # or using the mvn shim (equivalent):
  ./mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"
  # or using Makefile:
  make run
  # or using Procfile (platform-dependent):
  web: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-8989} --server.address=0.0.0.0"
  # or start entry recognized by some preview systems (supports CLEAN_PACKAGE=true):
  ./start
  # or:
  ./run.sh

Permissions note:
- If preview environment strips execute bits, use: sh mvnw ... or sh start.sh
- Ensure the 'mvn' shim is executable: chmod +x mvn (if needed)

Verification (clean package):
  # Verifies wrapper and shim both work
  ./mvnw -q -DskipTests clean package
  ./mvn  -q -DskipTests clean package

Migration Step 04.01 build log:
  # Generate/capture the clean build log for the migration tracker
  ./scripts/run_build_04_01.sh
  # Log is saved to:
  ./logs/build-04.01.txt

Note on Java versions:
- Migrated to target Java 21 and Spring Boot 3.3.x. Build requires JDK 21 in the environment.
- If you see 'error: release version 21 not supported', your JDK is older than 21; upgrade JAVA_HOME to JDK 21 and rebuild.
- Previous note for Java 8 has been superseded by the Java 21 migration.

Wrapper usage in previews:
- The preview manifest (project_manifest.yaml) uses './mvnw' with a fallback to 'sh mvnw'.
- If a platform hardcodes 'mvn', the root-level './mvn' shim ensures the call ultimately runs via the Maven Wrapper.
