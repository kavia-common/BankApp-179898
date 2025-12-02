# Banking Application using Java 21, Spring Boot 3, Spring Security 6, and H2 DB

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

* Java 21 (OpenJDK/Temurin recommended)
* Spring Tool Suite 4 or similar IDE
* [Maven](https://maven.apache.org/) - Dependency Management

## Java 21 Toolchains & Build Troubleshooting

This project targets **Java 21** and uses **Maven Toolchains** to ensure compilation with a JDK 21 even if the default `JAVA_HOME` points to an older JDK.

If you see an error like:

> `Fatal error compiling: error: release version 21 not supported`

it means Maven is running with a JDK that does **not** support `--release 21` (e.g. JDK 17 or 11).

### 1. Verify your Java and Maven JDK

From the project root:

```bash
java -version
mvn -version
```

Both commands should report a **Java 21** runtime/JDK. If Maven shows a different Java home or version, adjust your environment.

Example (Linux/macOS):

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

Make sure `JAVA_21_HOME` points at a real JDK 21 install. Maven’s Toolchains plugin (configured in `pom.xml`) will then pick this JDK for compilation.

If your environment does **not** honor project-local toolchains for some reason, you can also configure a global `${user.home}/.m2/toolchains.xml` with equivalent content.

### 3. Useful build commands

Quick compile (no tests):

```bash
./mvnw -q -DskipTests clean compile
```

Verbose build with debug output (helpful for diagnosing toolchain/JDK issues):

```bash
./mvnw -e -X -DskipTests clean package
```

If you're using system Maven instead of the wrapper, replace `./mvnw` with `mvn`.

> **Note:** As a last resort in environments where Java 21 is not available at all, you could temporarily lower the compiler `<release>` in `pom.xml` (for example to 17). This is **not** recommended for production; the project is intended to run on Java 21.

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
