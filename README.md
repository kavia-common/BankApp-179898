# Banking Application using Java 17+ (Java 21-ready), Spring Boot 3, Spring Security 6, and H2 DB

RESTful API to simulate simple banking operations. 

## Requirements

* CRUD operations for customers and accounts.
* Support deposits and withdrawals on accounts.
* Internal transfer support (i.e. a customer may transfer funds from one account to another).


## Getting Started

1. Checkout the project from GitHub

```
git clone https://github.com/sbathina/BankApp

```
2. Enable Lombok support on your IDE

Refer to the following link for instructions:

```
https://projectlombok.org/setup/eclipse

```
3. Open IDE of your choice and Import as existing maven project in your workspace

```
- Import existing maven project
- Build with Maven Wrapper (preferred): ./mvnw -q -DskipTests clean package
  (or: sh mvnw ... if execution permission is blocked)
- If using STS, Run As Spring Boot App
```

4. Build and Run without system Maven

Build:
```
# Preferred: Maven Wrapper
./mvnw -q -DskipTests clean package

# Alternatively use the mvn shim (proxies to ./mvnw; fixes previews that call 'mvn'):
./mvn -q -DskipTests clean package

# Using Makefile:
make build

# If execute permission is blocked:
sh mvnw -q -DskipTests clean package
```

Run on port 3001 bound to 0.0.0.0:
```
# Preferred: Maven Wrapper
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0"

# Or via mvn shim (still uses wrapper):
./mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0"

# Makefile helper:
make run

# Generic run script:
./run.sh

# Definitive preview entrypoint (supports CLEAN_PACKAGE=true to build first):
./start
# or:
./start.sh

# Platforms supporting Procfile (for reference):
web: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0"
```

Tip: If you see Maven printing only usage/help and exiting with code 1 when starting, ensure the -Dspring-boot.run.arguments value is quoted as a single string. Do not pass unquoted application args directly to Maven. Correct forms:
```
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0"
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0"
```
On Windows `cmd` (e.g., Procfile.windows), escape inner quotes as:
```
-Dspring-boot.run.arguments=^\"--server.port=%PORT% --server.address=0.0.0.0^\"
```

Verification commands (ensure these succeed from the project root):
```
# 1) Build/install
mvn clean install

# 2) Run with defaults from application.yml
mvn spring-boot:run

# 3) Run with explicit port and context-path (overrides application.yml)
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.servlet.context-path=/bank-api"

# 4) Run with explicit port, bind address, and context-path
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"
```

5. Default port for the api is 3001


### Prerequisites

* Java 17+ (OpenJDK/Temurin recommended; Java 21 recommended for the `java21` profile)
* Spring Tool Suite 4 or similar IDE
* [Maven](https://maven.apache.org/) - Dependency Management

## Java versions & build profiles

This project uses Spring Boot 3.x and therefore requires **Java 17 or newer**.

The `pom.xml` is configured with two main build paths:

1. A **default path** that compiles with Java 17 source/target (works on any JDK ≥ 17).
2. An optional **strict Java 21 path** that compiles with `--release 21` via the `java21` profile,
   optionally combined with the `with-toolchain` profile when using Maven Toolchains.

### Path A (default): Build with JDK 17+ (no Java 21 required)

By default, the Maven Compiler Plugin is configured to:

- `source = 17`
- `target = 17`

This means:

- You can build the project with **any JDK ≥ 17** (including JDK 21).
- No Maven toolchains configuration is required for the default path.
- You will **not** see `javac: release version 21 not supported` errors on environments that only have JDK 17.

Typical default builds:

```bash
# Using system Maven
mvn -q -DskipTests clean package

# Using the Maven Wrapper
./mvnw -q -DskipTests clean package
```

If you are running on JDK 21, the default build still emits Java 17 bytecode for maximum compatibility.

### Path B: Strict Java 21 build (`java21` profile, optional `with-toolchain`)

When you want to compile the application specifically as Java 21 bytecode (for example, for production),
enable the `java21` profile. This profile:

- Switches the compiler to **`--release 21`**.
- Enforces that the JDK resolved by Maven is **Java 21 or newer** via Maven Enforcer.
- Activates automatically when Maven is running on JDK 21+ (via `<activation><jdk>[21,)</jdk>`),
  or can be forced explicitly with `-Pjava21`.

Examples (no toolchain, Maven itself on JDK 21+):

```bash
# System Maven
mvn -q -Pjava21 -DskipTests clean package

# Maven Wrapper
./mvnw -q -Pjava21 -DskipTests clean package
```

If you are on **JDK 17** but have a separate **JDK 21** installed, you can use Maven Toolchains and
the `with-toolchain` profile together with `java21`. In this mode Maven may run on JDK 17, but the
compiler uses a JDK 21 toolchain:

```bash
# System Maven
mvn -q -Pwith-toolchain,java21 -DskipTests clean package

# Maven Wrapper
./mvnw -q -Pwith-toolchain,java21 -DskipTests clean package
```

If the environment lacks a Java 21 JDK or properly configured toolchain and you force `-Pjava21`,
you should expect a clear failure such as:

> `Fatal error compiling: error: release version 21 not supported`

That is intentional: the `java21` path is **strict** and only meant to succeed when a Java 21
toolchain or runtime is available.

### Java 21 toolchains & preflight checks

To make the `with-toolchain` + `java21` combination work when Maven itself is running on JDK 17,
set up a Maven toolchain that points to your JDK 21 installation.

1. Create (or update) your **user-level** toolchains file:

   * Path: `${user.home}/.m2/toolchains.xml` (for example `~/.m2/toolchains.xml` on Unix-like systems).

   Example contents:

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
         <jdkHome>/absolute/path/to/your/jdk-21</jdkHome>
       </configuration>
     </toolchain>
   </toolchains>
   ```

   You may also keep using a project-local `.mvn/toolchains.xml` if your environment already relies on it;
   the `maven-toolchains-plugin` will read toolchains from either location when the `with-toolchain` profile
   is active.

2. Build with the toolchain-enabled profile (property-activated or explicit profile):

   ```bash
   # Property-based activation (as used by some environments)
   ./mvnw -q -DskipTests -DUSE_TOOLCHAIN=true -Pjava21 clean package

   # Or explicitly combine the profiles
   ./mvnw -q -Pwith-toolchain,java21 -DskipTests clean package
   ```

   The `with-toolchain` Maven profile is automatically activated when `USE_TOOLCHAIN=true` is present;
   you do **not** need to pass `-Pwith-toolchain` in that case, but it is harmless if you do.

### 0. Quick Java 21 preflight

Before doing a strict Java 21 build, you can run a small script that checks which JDKs your environment
variables and Maven toolchain (if any) point at and fails fast if any are below Java 21:

```bash
bash scripts/java21-preflight.sh
```

The script checks (in order):

- `JAVA_TOOLCHAIN_JAVA21_HOME` (if defined by the platform)
- `JAVA_21_HOME` (used by `.mvn/toolchains.xml`)
- `JAVA_HOME`
- and finally `java` from your `PATH`

If any of these point to a Java version lower than 21, the script prints a clear error and exits with
a non-zero status so CI or preview systems can stop early **for the Java 21 path**.

> Tip: On some platforms a variable like `JAVA_TOOLCHAIN_JAVA21_HOME` is already defined; you can align it
> with the project conventions via:
> ```bash
> export JAVA_21_HOME="$JAVA_TOOLCHAIN_JAVA21_HOME"
> export JAVA_HOME="$JAVA_21_HOME"
> ```

### 1. Verify your Java and Maven JDK

From the project root:

```bash
java -version
mvn -version
```

For the **default** build path, both commands should report **Java 17 or newer**.

For the **Java 21** path (`-Pjava21`), ensure that either:

- Maven itself is running on Java 21+, or
- a Java 21 toolchain is configured and the `with-toolchain` profile is active.

Example (Linux/macOS) for a Java 21 setup:

```bash
# Point to your JDK 21 installation
export JAVA_21_HOME=/path/to/jdk-21
export JAVA_HOME="$JAVA_21_HOME"
```

Example (Windows, cmd):

```bat
set "JAVA_21_HOME=C:\Program Files\Java\jdk-21"
set "JAVA_HOME=%JAVA_21_HOME%"
```

### 2. Project-local toolchains.xml

This repository includes a project-local toolchains configuration at:

- `.mvn/toolchains.xml`

It is configured to use a JDK 21 installation via the `JAVA_21_HOME` environment variable:

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

Make sure `JAVA_21_HOME` points at a real JDK 21 install when using the `java21` / `with-toolchain`
profile combination. Maven’s Toolchains plugin (configured in `pom.xml`) will then pick this JDK for
compilation.

If your environment does **not** honor project-local toolchains for some reason, you can also configure
a global `${user.home}/.m2/toolchains.xml` with equivalent content.

### 3. Useful build commands

Quick compile (no tests, default Java 17 path):

```bash
./mvnw -q -DskipTests clean compile
```

Verbose build with debug output (helpful for diagnosing toolchain/JDK issues):

```bash
./mvnw -e -X -DskipTests clean package
```

If you're using system Maven instead of the wrapper, replace `./mvnw` with `mvn`.

### Maven Dependencies

```
spring-boot-starter-actuator
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-web
spring-boot-devtools
h2 - Inmemory database
lombok - to reduce boilerplate code
jakarta.xml.bind-api - Jakarta XML Binding API for Java 21
jaxb-runtime - JAXB runtime implementation for Java 21
springdoc-openapi-starter-webmvc-ui
spring-boot-starter-test
spring-security-test
```

## Swagger (OpenAPI)

The API documentation is provided by springdoc-openapi (Springfox removed). Access via:
- http://localhost:3001/bank-api/swagger-ui/index.html
- OpenAPI JSON: http://localhost:3001/bank-api/v3/api-docs

Note: http://localhost:3001/bank-api/swagger-ui.html also redirects to the Swagger UI, but `/swagger-ui/index.html` is the canonical path.

Security configuration (Spring Security 6) permits these paths without authentication and uses HTTP Basic for protected endpoints. CSRF is disabled and frame options are turned off for the H2 console.

Allowed without authentication (Boot 3/Security 6):
- /bank-api/healthz (lightweight JSON health probe)
- /bank-api/actuator/health (Spring Boot Actuator health endpoint)
- /bank-api/v3/api-docs/**
- /bank-api/swagger-ui/** (canonical UI at /bank-api/swagger-ui/index.html)
- /bank-api/h2-console/**
Note: Security config uses requestMatchers for these paths (plus `/swagger-ui.html` redirect support) and enables HTTP Basic for other business APIs.

## H2 In-Memory Database

The H2 console is enabled and reachable at:
- http://localhost:3001/bank-api/h2-console/

Security configuration permits the console and disables frame options for iframe rendering. Default in-memory JDBC URL (Boot 3 managed):
- jdbc:h2:mem:testdb

If using a custom database name, add datasource settings in application.yml, for example:
```
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
```

Notes on migration:
- javax.* packages migrated to jakarta.* (JPA).
- Spring Security migrated to SecurityFilterChain + requestMatchers.
- Springfox removed; springdoc-openapi-starter-webmvc-ui added.

## Endpoint Verification

Once the application is running on port 3001 with context path `/bank-api`, you can run lightweight automated checks against the key infrastructure endpoints.

Using the Maven `endpoint-checks` profile (JUnit-based probes):

```bash
# From the project root, with the app already running
./mvnw -q -Pendpoint-checks test
# or, if execute permission is blocked:
sh mvnw -q -Pendpoint-checks test
```

Using the curl-based helper script:

```bash
# Default base URL is http://localhost:3001/bank-api
bash kavia-scripts/check_endpoints.sh

# Override the base URL (for example, when the app runs on a different host/port)
BASE_URL=http://localhost:8080/bank-api bash kavia-scripts/check_endpoints.sh
```

Both options perform HTTP GET requests (no authentication headers) and verify:

- `/healthz` responds with HTTP 200 and `{"status":"ok"}`.
- `/actuator/health` responds with HTTP 200 and a JSON `status` field (typically `UP`).
- `/v3/api-docs` responds with HTTP 200 and an `openapi` field when using springdoc-openapi (or is skipped gracefully if only Swagger 2.x is present).
- `/swagger-ui.html` serves the Swagger UI (following redirects to `/swagger-ui/index.html` if necessary).
- `/h2-console` is reachable (HTTP 200 after following redirects, and contains `H2 Console` in the HTML).

## Testing the Bank APP Rest Api

1. Please use the Swagger url to perform CRUD operations. 

2. Browse to <project-root>/src/test/resources to find sample requests to add customer and accounts.


## Authors

* **Shyam Bathina**
