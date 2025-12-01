# Build Notes

Important: If Maven shows only its help/options and exits with code 1 when starting the app, it is almost always due to incorrect quoting of -Dspring-boot.run.arguments by the preview runner. Ensure the entire value is passed as one string, e.g.:
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"
The project_manifest.yaml has been updated to use single quotes at YAML level and double quotes around the inner Spring arguments to avoid misparsing.

- Removed maven-compiler-plugin `--add-opens jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED` arguments from pom.xml.
  These flags are not allowed when targeting Java 8 (`target=1.8` / `release=8`) and caused:
  "Fatal error compiling: error: option --add-opens not allowed with target 8".
- Project remains on Spring Boot 2.1.4.RELEASE and is configured for Java 8:
  - maven.compiler.source=1.8
  - maven.compiler.target=1.8
  - maven.compiler.release=8

Maven Wrapper standardization:
- The project uses Maven Wrapper exclusively. No system 'mvn' is required.
- Maven Wrapper upgraded to wrapper 3.2.x with Maven distribution 3.9.9.
  - .mvn/wrapper/maven-wrapper.properties:
    - distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
    - wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
  - mvnw and mvnw.cmd refreshed to 3.2.x (no Takari references).
- A shim script named 'mvn' is provided at the project root to proxy any 'mvn ...' calls to './mvnw'.
- Preview/CI entrypoints (start, start.sh, run.sh, Makefile, Procfile) are wired to use './mvnw' so 'bash: mvn: command not found' is avoided.
- project_manifest.yaml is configured to use './mvnw' with 'sh mvnw' fallback; if a preview insists on invoking 'mvn', the 'mvn' shim proxies to './mvnw'.
- Definitive entry file for previews: ./start (supports CLEAN_PACKAGE to pre-build).

Project name: BankAppPro

Build (preferred):
  ./mvnw -q -DskipTests clean package
  # or if execution permission is blocked:
  sh mvnw -q -DskipTests clean package
  # or using Makefile:
  make build

Run (port 8989, bind 0.0.0.0) (preferred):
  ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"
  # or if execution permission is blocked:
  sh mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"
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

Verify Maven version (should be 3.9.9 via wrapper):
  ./mvnw -v
  # Apache Maven 3.9.9 (via Maven Wrapper 3.2.x)

Migration Step 04.01 build log:
  # Generate/capture the clean build log for the migration tracker (uses ./mvn shim)
  ./scripts/run_build_04_01.sh
  # Log is saved to:
  ./logs/build-04.01.txt

Environment note for build:
- If you encounter "error: release version 21 not supported" or a similar message, the runtime JDK is older than the configured release.
- The project is configured to compile for Java 17 to match common environments. Ensure JAVA_HOME points to a JDK 17+ installation and re-run:
  ./mvn -q -DskipTests clean package

Note on Java versions:
- Baseline set to Java 17 with Spring Boot 3.3.x (Boot 3 supports Java 17 and Java 21).
- Builds and runs on JDK 17+. To compile with release 21 locally, override with: -Dmaven.compiler.release=21 and ensure JAVA_HOME points to a JDK 21 installation.
- Previous note requiring Java 21 has been superseded by this alignment to Java 17 for compatibility in preview/CI environments.

Wrapper usage in previews:
- The preview manifest (project_manifest.yaml) uses './mvn' (shim) with fallbacks to 'sh mvn', './mvnw', and 'sh mvnw'.
- If a platform hardcodes 'mvn', the root-level './mvn' shim ensures the call ultimately runs via the Maven Wrapper.
- If a preview invokes 'mvn' from outside the project root (and thus misses the shim), use './start' as the entrypoint which internally calls the Maven Wrapper.
- Updated startCommand binds to ${PORT:-8989} and 0.0.0.0 and quotes -Dspring-boot.run.arguments properly to avoid Maven usage/exit code 1 issues on some runners.

Migration steps 03.01–03.04 summary:
- javax → jakarta: All entities use jakarta.persistence; no javax.validation/servlet present.
- Spring Security 6: SecurityFilterChain with requestMatchers; HTTP Basic; CSRF disabled; H2 frame options disabled; permitted: /v3/api-docs/**, /swagger-ui/**, /swagger-ui.html, /h2-console/**.
- Swagger: Springfox fully removed; using springdoc-openapi-starter-webmvc-ui (Swagger UI at /bank-api/swagger-ui.html and /index.html).
- H2 console verified at /bank-api/h2-console; Boot 3 datasource defaults apply.

Preview diagnostic (mvn-not-found):

- If a preview logs "bash: mvn: command not found", it is hardcoding 'mvn' and invoking it from outside the project root, bypassing our './mvn' shim.
- Resolution options:
  1) Use './start' as the preview entrypoint (internally uses ./mvnw).
  2) Ensure commands are executed from the project root so './mvn' and './mvnw' are available.
  3) Explicitly call './mvnw ...' or 'sh mvnw ...' in preview configuration.
- The repository already contains an mvn shim and mvnw wrapper; no system Maven is needed.
