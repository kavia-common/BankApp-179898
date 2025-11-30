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
- A Procfile is included to start the app via './mvnw' in environments that support Procfile.

Build:
  ./mvnw -q -DskipTests clean package
  # or if execution permission is blocked:
  sh mvnw -q -DskipTests clean package
  # or using Makefile:
  make build
  # or using the mvn shim (proxies to ./mvnw):
  ./mvn -q -DskipTests clean package

Run (port 8989, bind 0.0.0.0):
  ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"
  # or if execution permission is blocked:
  sh mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"
  # or using Makefile:
  make run
  # or using Procfile (platform-dependent):
  web: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-8989} --server.address=0.0.0.0"
  # or start entry recognized by some preview systems:
  ./start
  # or:
  ./run.sh

Verification (clean package):
  # Verifies wrapper and shim both work
  ./mvnw -q -DskipTests clean package
  ./mvn  -q -DskipTests clean package

Note on Java versions:
- The project targets Java 8 bytecode but can be built on newer JDKs (e.g., 17).
- To avoid module access issues with annotation processing on JDK 17+, the necessary --add-opens flags are provided via .mvn/jvm.config.
