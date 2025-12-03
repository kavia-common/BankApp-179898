# Task 06 – Tests, CI, and Verification Commands

## Purpose

This document explains how to build, run, and test the application after migrating to Java 21 and Spring Boot 3.2.x. It provides commands for CI pipelines, local verification, and dependency inspection.

## Files Changed

- `pom.xml` (compiler, Surefire, and dependencies)
- No direct changes to test classes, but they are expected to run under Java 21 and Spring Boot 3.2.x.

## Build and Test Commands

Run all commands from the `BankApp-179898` project root.

### 1. Build without tests

```bash
./mvnw -DskipTests clean package
```

**Expected output:**

- Build completes successfully.
- Logs show:
  - `maven-compiler-plugin` at `3.13.0` using `--release 21`.
  - `maven-surefire-plugin` at `3.2.5`.

### 2. Run application (dev mode)

```bash
./mvnw -DskipTests spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=8989 --server.servlet.context-path=/bank-api"
```

**Expected output:**

- Application starts on `http://localhost:8989/bank-api`.
- Log shows:
  - Spring Boot version `3.2.10`.
  - H2 console and security filter chain initialized.

### 3. Run unit and integration tests

```bash
./mvnw test
```

**Expected output:**

- All tests pass, including:
  - `BankingApplicationTests`
  - `HealthControllerTest`
  - `EndpointChecksIT`
  - `OpenApiDocsAccessibilityTest`

### 4. Dependency tree inspection

```bash
./mvnw dependency:tree | sed -n '1,200p'
```

**Expected output:**

- Tree includes:
  - `spring-boot-starter-parent:3.2.10`
  - `springdoc-openapi-starter-webmvc-ui:2.6.0`
  - `jakarta.xml.bind-api:4.0.2`
  - `org.glassfish.jaxb:jaxb-runtime:4.0.5`
- No obvious duplicate or conflicting versions of core Spring libraries.

## Smoke Test Commands (curl)

With the app running on port 8989:

```bash
# Health
curl -i http://localhost:8989/bank-api/healthz
curl -i http://localhost:8989/bank-api/actuator/health

# Swagger UI
curl -i http://localhost:8989/bank-api/swagger-ui/index.html

# OpenAPI docs
curl -i http://localhost:8989/bank-api/v3/api-docs

# H2 Console
curl -i http://localhost:8989/bank-api/h2-console
```

**Expected behavior:**

- Health endpoints return HTTP 200 and JSON with a `status` field.
- `/bank-api/v3/api-docs` returns HTTP 200 and JSON with an `openapi` field.
- `/bank-api/swagger-ui/index.html` returns HTTP 200 and HTML content.
- `/bank-api/h2-console` returns HTTP 200 (may include redirects) and HTML containing “H2 Console”.

## CI Integration Guidance

Typical CI pipeline steps:

```bash
# 1) Validate Java version (ensure Java 21)
java -version
mvn -version

# 2) Clean build and run tests
./mvnw -B clean verify

# 3) Optional: dependency analysis
./mvnw -B dependency:tree
```

Ensure CI images or agents:

- Have Java 21 installed and set as default `JAVA_HOME`.
- Do not override the Maven Wrapper with an incompatible system Maven version.

## Rollback Steps

To disable or roll back Java 21-specific CI changes:

1. Restore previous `pom.xml` (Java 17, older Boot):

   ```bash
   git checkout HEAD~1 -- pom.xml
   ```

2. Update CI scripts to target older Java where necessary.

3. Re-run the pipeline and confirm green builds.

## Manual Review Notes

- Verify that all CI environments are running Java 21; failing to do so may cause:
  - `Fatal error compiling: error: release version 21 not supported`
- If you see `NoSuchMethodError` in CI but not locally, compare dependency trees (`./mvnw dependency:tree`) to ensure consistent versions across environments.
- For environments that cannot yet support Java 21, consider maintaining a long-lived branch targeting Java 17 and document the difference in `docs/changelog.md`.
