# Build Notes

## Java & Spring Boot Baseline

- **Spring Boot:** 3.3.4 (via `spring-boot-starter-parent`)
- **Java:** 21 (LTS) – this project is intended to be compiled and run on **Java 21+**.
- The POM **does not** set `maven.compiler.source` / `maven.compiler.target`. Instead it uses:
  - `maven.compiler.release=21`
  - Maven Toolchains + a project-local `.mvn/toolchains.xml`

If you see:

> `Fatal error compiling: error: release version 21 not supported`

it means Maven is running with a JDK that does **not** support `--release 21` (for example, JDK 11 or 17). The configuration below is designed to fail fast and guide you to the correct JDK.

## Maven Wrapper & Shim

- The project uses **Maven Wrapper** exclusively – no system `mvn` installation is required.
- Wrapper details:
  - `.mvn/wrapper/maven-wrapper.properties`:
    - `distributionUrl` points to **Apache Maven 3.9.9**.
    - `wrapperUrl` points to **Maven Wrapper 3.2.x**.
  - `mvnw` and `mvnw.cmd` are the standard Wrapper scripts.
- A root-level `mvn` shim script proxies any `mvn ...` calls to `./mvnw` so that environments which hardcode `mvn` still end up using the wrapper.
- Preview/CI entrypoints (`start`, `start.sh`, `run.sh`, `Makefile`, `Procfile`) are wired to use `./mvnw` or the shim, so `"bash: mvn: command not found"` is avoided.

### Typical build & run commands

Build (preferred):

```bash
./mvnw -q -DskipTests clean package
# or if execution permission is blocked:
sh mvnw -q -DskipTests clean package
# or using Makefile:
make build
```

Run (port 3001, bind 0.0.0.0):

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0"
# or if execution permission is blocked:
sh mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0"
# or using Makefile:
make run
# or using Procfile (platform-dependent):
web: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0"
# or start entry recognized by some preview systems (supports CLEAN_PACKAGE=true):
./start
# or:
./run.sh
```

Verification (clean package):

```bash
# Verifies wrapper and shim both work
./mvnw -q -DskipTests clean package
./mvn  -q -DskipTests clean package
```

## Java 21 Toolchains

### POM configuration

The `pom.xml` is configured to ensure a Java 21 JDK is used for compilation:

- Properties:

  ```xml
  <properties>
    <!-- Target Java 21 (LTS). Do not use maven.compiler.source/target; rely on release + toolchains. -->
    <java.version>21</java.version>
    <maven.compiler.release>21</maven.compiler.release>
    <maven.compiler.parameters>true</maven.compiler.parameters>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>
  ```

- **Maven Toolchains Plugin**:

  ```xml
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-toolchains-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
      <execution>
        <goals>
          <goal>toolchain</goal>
        </goals>
      </execution>
    </executions>
    <configuration>
      <toolchains>
        <jdk>
          <!-- Require at least Java 21; vendor-agnostic -->
          <version>[21,)</version>
          <vendor>any</vendor>
        </jdk>
      </toolchains>
    </configuration>
  </plugin>
  ```

- **Maven Compiler Plugin** (Java 21–aware):

  ```xml
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
      <!-- Rely on --release; do not set source/target separately -->
      <release>${maven.compiler.release}</release>
      <parameters>${maven.compiler.parameters}</parameters>
    </configuration>
  </plugin>
  ```

There are **no** `maven.compiler.source` / `maven.compiler.target` properties or conflicting plugin definitions. Compilation is driven solely by `--release 21` plus the toolchain configuration.

### Project-local toolchains.xml

A project-local toolchains file is provided at:

- `.mvn/toolchains.xml`

It binds the **Java 21** toolchain to `JAVA_21_HOME`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains xmlns="http://maven.apache.org/TOOLCHAINS/1.1.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/TOOLCHAINS/1.1.0 https://maven.apache.org/xsd/toolchains-1.1.0.xsd">
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>21</version>
      <vendor>any</vendor>
    </provides>
    <configuration>
      <jdkHome>${env.JAVA_21_HOME}</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

To use this correctly:

- Set `JAVA_21_HOME` to the root of a **JDK 21** installation.
- Optionally align `JAVA_HOME` to match `JAVA_21_HOME` for tooling that does not use toolchains explicitly.

Examples:

```bash
# Linux/macOS
export JAVA_21_HOME=/path/to/jdk-21
export JAVA_HOME="$JAVA_21_HOME"
```

```bat
REM Windows (cmd)
set "JAVA_21_HOME=C:\Program Files\Java\jdk-21"
set "JAVA_HOME=%JAVA_21_HOME%"
```

Some platforms expose a variable like `JAVA_TOOLCHAIN_JAVA21_HOME`. In that case you can align:

```bash
export JAVA_21_HOME="$JAVA_TOOLCHAIN_JAVA21_HOME"
export JAVA_HOME="$JAVA_21_HOME"
```

## Java 21 Preflight Script

A small preflight script is provided to fail fast when any configured JDK is below 21:

- Script path: `scripts/java21-preflight.sh`

It checks, in order:

1. `JAVA_TOOLCHAIN_JAVA21_HOME` (if defined)
2. `JAVA_21_HOME` (used by `.mvn/toolchains.xml`)
3. `JAVA_HOME`
4. `java` from `PATH`

If any of these resolve to a Java version lower than 21, the script prints a clear error and exits with status code `1`.

Run it from the project root:

```bash
bash scripts/java21-preflight.sh
```

This is a good first step before:

```bash
./mvnw -q -DskipTests clean package
```

Using this in CI/preview pipelines will surface misconfigured JDKs early, before the compiler emits `release version 21 not supported`.

## Maven Enforcer

To make the requirement explicit at build time, the POM includes **maven-enforcer-plugin**:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-enforcer-plugin</artifactId>
  <version>3.4.1</version>
  <executions>
    <execution>
      <id>enforce-java-version</id>
      <goals>
        <goal>enforce</goal>
      </goals>
      <configuration>
        <rules>
          <requireJavaVersion>
            <version>[21,)</version>
            <message>
              This project requires Java 21 or newer. Please set JAVA_HOME or JAVA_21_HOME to a JDK 21
              installation and ensure Maven runs with that JDK. See README.md for details.
            </message>
          </requireJavaVersion>
        </rules>
        <fail>true</fail>
      </configuration>
    </execution>
  </executions>
</plugin>
```

This ensures that even if the toolchain is misconfigured, Maven will stop with a clear error before attempting compilation.

## Test Plugin Compatibility with Java 21

- The project inherits test plugin versions from the Spring Boot 3.3.4 parent BOM, which uses **Maven Surefire/Failsafe 3.2.x**, compatible with Java 21.
- A profile-specific Surefire configuration is present (`endpoint-checks` profile) but does not override the plugin version; it only scopes which tests run.

No additional Surefire/Failsafe plugin declarations are necessary for Java 21 compatibility.

## Troubleshooting Java 21 Builds

1. **Preflight**:

   ```bash
   bash scripts/java21-preflight.sh
   ```

   Fix any errors reported (usually setting `JAVA_21_HOME` or `JAVA_HOME` correctly).

2. **Verify toolchain resolution** (optional, verbose):

   ```bash
   ./mvnw -e -X -DskipTests clean package
   ```

   In the debug output, verify that the toolchains plugin resolves a JDK with version `21`.

3. **Common issues**:

   - *"release version 21 not supported"*  
     -> JDK used by Maven is < 21. Fix `JAVA_21_HOME` / `JAVA_HOME` and rerun the preflight script.

   - *Maven prints only usage/help for `spring-boot:run`*  
     -> Ensure the value for `-Dspring-boot.run.arguments` is quoted as a single string (see README).

With these settings, the following should succeed on any machine with a properly installed JDK 21:

```bash
./mvnw -q -DskipTests clean package
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0"
```
