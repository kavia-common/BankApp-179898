# Java 17 to Java 21 Migration and Modernization Guide

## Overview
This guide describes the process to migrate and modernize the BankApp-179898 Spring Boot application from Java 17 to Java 21. It covers prerequisites, local toolchain setup, Maven configuration and plugin updates, dependency alignment for Spring Boot and major libraries, compiler settings, code-level checks for newer language features, test updates, CI/CD changes, container and runtime command alignment, verification steps, rollback plan, and a final checklist.

The repository currently targets Java 17 (maven-compiler-plugin release 17) with Spring Boot 3.2.10, springdoc-openapi 2.6.0, and an H2 in-memory database. The migration path described here moves compilation and runtime to Java 21 while keeping a compatible Spring Boot line (3.2.x or higher), with recommended upgrades to 3.3.x for best long-term support.

## Prerequisites
Before starting:
- Ensure you have a JDK 21 distribution installed (e.g., Temurin/OpenJDK 21).
- Ensure Maven 3.9+ is installed or use the provided Maven Wrapper (preferred).
- Verify current application runs on Java 17 and has a clean build and passing tests:
  - ./mvnw -q -DskipTests clean package
  - ./mvnw test

Confirm versions:
- java -version should show Java 21 once migration is enabled.
- ./mvnw -q -e --version should show a Java 21 runtime or a configured toolchain for Java 21.

## Local Toolchain Setup (Java 21)
Two supported approaches exist:

1) Native Java 21 runtime (simplest)
- Set your JAVA_HOME to the JDK 21 installation and ensure java -version reports 21.
- Builds and runs will naturally use Java 21.

2) Maven Toolchains (useful when Maven runs on JDK 17 but compiles with JDK 21)
- Create ~/.m2/toolchains.xml to point to your JDK 21 installation:
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
      <jdkHome>/absolute/path/to/jdk-21</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```
- If your environment uses an environment variable, keep a local .mvn/toolchains.xml that references ${env.JAVA_21_HOME} and set JAVA_21_HOME accordingly.

Preflight script (optional):
- If you add a helper script such as scripts/java21-preflight.sh, use it to verify your toolchain variables (JAVA_21_HOME, JAVA_HOME) and the effective Java for Maven.

## Maven Configuration Updates
The current POM uses:
- Spring Boot parent: 3.2.10
- maven-compiler-plugin: 3.13.0 with <release>17</release>
- maven-surefire-plugin: 3.2.5

Step-by-step changes:

1) Set the Java version to 21
- Update project properties and compiler settings to use Java 21:
```xml
<properties>
  <java.version>21</java.version>
  <maven.compiler.release>21</maven.compiler.release>
  <maven.compiler.parameters>true</maven.compiler.parameters>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

2) Configure maven-compiler-plugin for Java 21
- Ensure the compiler plugin uses <release>21</release>:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.13.0</version>
  <configuration>
    <release>21</release>
    <encoding>${project.build.sourceEncoding}</encoding>
    <parameters>${maven.compiler.parameters}</parameters>
  </configuration>
</plugin>
```

3) Ensure a modern surefire plugin for JUnit 5
- Keep maven-surefire-plugin >= 3.2.x:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.2.5</version>
</plugin>
```

4) Optional: Spring Boot upgrade
- Java 21 is supported on Spring Boot 3.2.x. For modernization, consider upgrading to Spring Boot 3.3.x (or newer 3.4.x once broadly available) to align with the latest Java versions and improvements.
- Example parent update (if you choose to upgrade):
```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.3.5</version> <!-- Example -->
  <relativePath/>
</parent>
```
- Review the Spring Boot migration notes for any property or actuator endpoint exposure changes between minor versions.

5) JAXB and related dependencies
- The project already includes Jakarta XML Bind API (4.0.x) and jaxb-runtime (4.0.x) which are Java 21–compatible. Retain them unless you confirm they are no longer required by your runtime or data serialization.

## Dependency Alignment
Target an aligned stack that supports Java 21:

- Spring Boot: 3.2.x (supported) or recommended 3.3.x
- Spring Framework: managed by Spring Boot parent
- Spring Security 6.x: managed by Spring Boot parent; current SecurityFilterChain usage is compatible
- Spring Data JPA: managed by Spring Boot parent
- Springdoc OpenAPI: 2.6.0 (current); remains compatible with Boot 3.2/3.3 and Java 21
- Actuator: managed by Spring Boot parent
- H2: managed by Spring Boot parent (runtime)

After upgrading Boot, allow its BOM to manage versions whenever possible. Override only if required and compatible with Boot.

## Compiler and Release Settings
- Use <release>21</release> in the compiler plugin to emit Java 21 bytecode and link against Java 21 standard APIs.
- Remove legacy <source>/<target> if present to avoid conflicting configuration.
- Keep parameter metadata enabled for Spring MVC reflection: <maven.compiler.parameters>true</maven.compiler.parameters>

## Code-level Checks and Modern Java Features
While migrating, verify code compatibility and consider modernizing where safe:

- Records: Consider using records for immutable DTOs where appropriate in the domain layer. Ensure Jackson or OpenAPI bindings are compatible if you switch from standard classes to records.
- Switch expressions: Java 21 supports enhanced switch expressions; refactor any verbose switch statements, as appropriate, while keeping readability.
- Pattern matching: Use pattern matching for instanceof and switch on types judiciously to simplify code; ensure readability remains high.
- Sealed classes/interfaces (optional): If your model benefits from constrained inheritance, sealed types can improve safety. Balance this against JPA and runtime serialization needs.
- Var handles and virtual threads (Project Loom, preview in earlier JDKs, stable in 21 for virtual threads): While not required for this app, be aware of Java 21 virtual threads via Thread.ofVirtual().start(...) if you plan to experiment with concurrency. Integrating virtual threads into Spring Boot requires careful evaluation and is an optional enhancement.

Note: No source changes are strictly required to run on Java 21 for this codebase, but the above are modernization opportunities.

## Tests and Tooling Updates
- Ensure tests run under Java 21 with JUnit 5 (already provided via spring-boot-starter-test).
- If tests rely on deprecated APIs or reflection quirks, adapt them to modern APIs.
- Verify integration tests (e.g., endpoint checks) still pass when the app runs under Java 21.
- Keep surefire 3.2.x (or newer) to ensure Java 21 compatibility.

Sample commands:
- ./mvnw -q -DskipTests clean package
- ./mvnw test
- ./mvnw -Pendpoint-checks test (with the app already running)

## CI/CD Changes
Update your pipelines to use Java 21.

### GitHub Actions (example)
```yaml
name: CI
on:
  push:
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
          cache: 'maven'
      - name: Build (skip tests)
        run: ./mvnw -q -DskipTests clean package
      - name: Unit tests
        run: ./mvnw -q test
      - name: Run app for endpoint checks (optional)
        run: nohup ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0" &
      - name: Endpoint checks (optional)
        run: ./mvnw -q -Pendpoint-checks test || true
```

### GitLab CI (example)
```yaml
stages:
  - build
  - test

variables:
  MAVEN_OPTS: "-DskipTests"

build:
  stage: build
  image: eclipse-temurin:21
  script:
    - ./mvnw -q -DskipTests clean package
  artifacts:
    paths:
      - target/*.jar

test:
  stage: test
  image: eclipse-temurin:21
  script:
    - ./mvnw -q test
```

If you use Maven Toolchains instead of setting the runner’s Java to 21, ensure your pipeline writes a ~/.m2/toolchains.xml with a valid JDK 21 path on the runner image.

## Container and Manifest Start/Build Command Updates
- If your platform uses Procfile or start scripts, no change is required other than ensuring the runtime JDK is 21.
- Continue to use the wrapper for consistency:
  - Build: ./mvnw -q -DskipTests clean package
  - Run: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0"
- If your platform injects JAVA_HOME, point it to JDK 21. If you rely on a buildpack or Docker base image, select a Java 21 variant (e.g., eclipse-temurin:21-jre or 21-jdk depending on needs).

## Verification Steps
After the migration, verify at multiple levels.

1) Build and unit tests
- ./mvnw -q clean package
- ./mvnw test

2) Run the app locally (Java 21 runtime)
- ./mvnw spring-boot:run
- Confirm logs show Java 21 in the startup banner.

3) Verify core endpoints (no auth required in current config)
- Health:
  - GET http://localhost:3001/bank-api/healthz
  - GET http://localhost:3001/bank-api/actuator/health
- OpenAPI/Swagger:
  - GET http://localhost:3001/bank-api/v3/api-docs
  - GET http://localhost:3001/bank-api/swagger-ui/index.html
- H2 console:
  - GET http://localhost:3001/bank-api/h2-console

4) Verify key business endpoints
- GET http://localhost:3001/bank-api/customers
- GET http://localhost:3001/bank-api/accounts

5) Optional automated endpoint checks
- With app running:
  - ./mvnw -q -Pendpoint-checks test
- Or script:
  - bash kavia-scripts/check_endpoints.sh

## Rollback Plan
If issues arise after moving to Java 21:
1) Revert the maven-compiler-plugin <release> to 17 and set <java.version> to 17.
2) Ensure JAVA_HOME or toolchains.xml points back to a JDK 17 installation.
3) If you upgraded Spring Boot, revert to the previously working parent version (3.2.10 currently).
4) Rebuild and retest:
   - ./mvnw -q -DskipTests clean package
   - ./mvnw test
5) Triage failures and reattempt the migration once blockers are resolved.

## Final Checklist
- Prerequisites
  - [ ] JDK 21 installed on developer machines and CI runners
  - [ ] java -version reports 21 in the target environments
- Maven configuration
  - [ ] <java.version> set to 21
  - [ ] maven-compiler-plugin <release> set to 21
  - [ ] maven-surefire-plugin is 3.2.x or newer
  - [ ] Optional: Spring Boot parent upgraded to 3.3.x if desired
- Dependencies
  - [ ] springdoc-openapi-starter-webmvc-ui remains compatible (2.6.x)
  - [ ] JAXB artifacts (jakarta.xml.bind-api and jaxb-runtime) validated under Java 21
- Code-level
  - [ ] No incompatible APIs or reflection tricks tied to older JDKs
  - [ ] Optional refactors to modern Java language features reviewed
- Tests
  - [ ] Unit and integration tests pass under Java 21
- CI/CD
  - [ ] Runners use Java 21 or provide toolchains.xml for Java 21
  - [ ] Pipelines updated to use JDK 21 images or actions/setup-java with 21
- Run/Container
  - [ ] Start scripts/Procfile work with Java 21 images/runtimes
  - [ ] Port and context path unchanged: 3001, /bank-api
- Verification
  - [ ] Health endpoints return expected responses
  - [ ] Swagger UI and OpenAPI JSON are accessible
  - [ ] H2 console reachable
  - [ ] Business endpoints operate correctly
- Rollback
  - [ ] Clear plan to revert to Java 17 if issues occur

## Notes for BankApp-179898
- The current repository uses Spring Boot 3.2.10, Java 17, and springdoc-openapi 2.6.0. These are compatible with a Java 21 runtime once the compiler release is set to 21.
- Security is currently configured to permit all requests and disable CSRF and frame options to allow H2 console. This remains unaffected by the Java upgrade. Re-enable authentication later as needed with explicit requestMatchers for Swagger and H2 console.
- The in-memory H2 setup, actuator, and Swagger endpoints require no changes for Java 21 beyond ensuring the runtime is Java 21.

