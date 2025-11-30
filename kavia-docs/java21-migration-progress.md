# Java 21 Migration Progress Tracker

## Overview

This document tracks the step-by-step progress of migrating the BankApp-179898 project to Java 21 and Spring Boot 3.x. After completing each step, we will update the Status, and add any outputs, notes, and links to diffs or pull requests in the Notes/Links column. The intent is to keep a single source of truth for the migration state that is easy to scan and share.

Status values:
- To-do: Not started yet
- In-progress: Work underway
- Success: Completed and validated
- Blocked: Cannot proceed due to a dependency or issue (add explanation in Notes)

## Planned Steps

| Step ID | Description | Status | Notes/Links to diffs |
|---|---|---|---|
| 01.01 | Create tracker | To-do |  |
| 02.01 | Update Java version/toolchain | Success | pom.xml updated to target Java 21; compiler release set to 21. |
| 02.02 | Upgrade Spring Boot | Success | Parent upgraded to Spring Boot 3.3.4; removed incompatible Springfox deps. |
| 02.03 | Update dependencies (H2, Spring Security, springdoc-openapi) | Success | springdoc-openapi starter added; Boot-managed H2/Security versions applied. |
| 02.04 | Update Maven Wrapper | To-do |  |
| 02.05 | Adjust .mvn/jvm.config | To-do |  |
| 03.01 | Code refactor to jakarta and Security 6 | Success | Completed. Changed imports in JPA entities from javax.persistence.* to jakarta.persistence.*. Verified no javax.validation or javax.servlet usages remain. Files changed: src/main/java/com/coding/exercise/bankapp/model/*.java |
| 03.02 | Update Spring Security to Spring Security 6 style | Success | Completed. Replaced WebSecurityConfigurerAdapter with SecurityFilterChain bean, using authorizeHttpRequests + requestMatchers. Allowed public: "/", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**", "/actuator/**". Kept HTTP Basic. Files changed: src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java |
| 03.03 | Replace Springfox with springdoc-openapi | Success | Completed. Removed Springfox entirely and added springdoc-openapi-starter-webmvc-ui. Minimal ApplicationConfig retained (placeholder). README updated with new Swagger endpoints. Files changed: pom.xml, src/main/java/com/coding/exercise/bankapp/config/ApplicationConfig.java, README.md |
| 03.04 | Verify H2 console path and datasource settings | Success | Completed. Ensured H2 console path is /h2-console (context path applied → /bank-api/h2-console). application.yml configured with spring.h2.console.enabled and web-allow-others; Security config permits console and disables frameOptions. README updated. Files changed: src/main/resources/application.yml, src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java, README.md |
| 04.01 | Clean build on Java 21 | Blocked | Blocked by environment: preview hardcodes 'mvn' not found; requires using ./mvn or ./mvnw and JDK 21 (see Diagnostics). |
| 05.01 | Run and smoke-test | Blocked | Blocked by environment: preview start uses 'mvn' directly. Use ./start or ./mvnw once preview is fixed. |
| 06.01 | Update docs | To-do |  |

## Step Updates

- 02.01 Update Java version/toolchain — Success
  - pom.xml: set <java.version>21</java.version>.
  - maven-compiler-plugin: configured <release>21</release> (version 3.11.0).
  - Removed legacy Java 8 settings.

- 02.02 Upgrade Spring Boot — Success
  - Upgraded parent to Spring Boot 3.3.4.
  - Removed deprecated/incompatible Springfox dependencies.

- 02.03 Update dependencies — Success
  - Added org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0.
  - Relied on Boot 3-managed versions for Spring Security 6, Hibernate 6, and H2.

- 03.01 javax → jakarta migration — Success
  - Verified all JPA entities import jakarta.persistence.* (no javax.* remains).
  - Verified no javax.validation.* or javax.servlet.* usages across codebase.
  - Files confirmed/covered:
    - src/main/java/com/coding/exercise/bankapp/model/Account.java
    - src/main/java/com/coding/exercise/bankapp/model/Address.java
    - src/main/java/com/coding/exercise/bankapp/model/BankInfo.java
    - src/main/java/com/coding/exercise/bankapp/model/Contact.java
    - src/main/java/com/coding/exercise/bankapp/model/Customer.java
    - src/main/java/com/coding/exercise/bankapp/model/CustomerAccountXRef.java
    - src/main/java/com/coding/exercise/bankapp/model/Transaction.java

- 03.02 Spring Security 6 migration — In-progress
  - Refactoring config to SecurityFilterChain, requestMatchers, and HTTP Basic.
  - Public paths accounted for (OpenAPI/Swagger/H2).
- 03.02 Spring Security 6 migration — Success
  - Implemented SecurityFilterChain with authorizeHttpRequests + requestMatchers; httpBasic; CSRF disabled; frameOptions disabled for H2 console.
  - Permitted: "/", "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**".
  - Files changed:
    - src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java

- 03.03 Replace Springfox with Springdoc — In-progress
  - Removing Springfox deps/config and adding springdoc starter.
- 03.3 Replace Springfox with Springdoc — Success
  - Removed Springfox dependencies and configs; added springdoc-openapi-starter-webmvc-ui:2.6.0.
  - Controllers no longer use io.swagger.annotations; springdoc auto-generates OpenAPI.
  - Swagger UI reachable at:
    - /bank-api/swagger-ui.html
    - /bank-api/swagger-ui/index.html
  - Files changed:
    - pom.xml (removed springfox, added springdoc)
    - src/main/java/com/coding/exercise/bankapp/config/ApplicationConfig.java
    - src/main/java/com/coding/exercise/bankapp/controller/AccountController.java
    - src/main/java/com/coding/exercise/bankapp/controller/CustomerController.java
    - README.md (updated endpoints and dependency notes)

- 03.04 H2 console and datasource verification — In-progress
  - Verifying Boot 3 path and security allowances for H2 console.
- 03.04 H2 console and datasource verification — Success
  - Confirmed Boot 3 compatible H2 console config; context-path aware (/bank-api/h2-console).
  - Security updated to allow console; frame options disabled.
  - Files verified/updated:
    - src/main/resources/application.yml
    - src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java
    - README.md

- 04.01 Clean build on Java 21 — Blocked
  - Expected command: ./mvn -q -DskipTests clean package (shim → wrapper) or ./mvnw ...
  - Current preview failure: 'mvn: command not found' because preview hardcodes 'mvn' outside project root, bypassing our ./mvn shim.
  - Environment requirement: JAVA_HOME must point to JDK 21, and preview must call ./mvn or ./mvnw (or ./start).
  - Logs: ./logs/build-04.01.txt (use scripts/run_build_04_01.sh locally/CI).

- 05.01 Run and smoke-test — Blocked
  - Expected command: ./start or ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=$PORT --server.address=0.0.0.0"
  - Blocked because preview currently attempts 'mvn' directly; once preview uses ./mvn (shim) or ./mvnw, run should succeed.

## Diagnostics

- mvn-not-found: Preview environment appears to hardcode 'mvn' and invoke it from outside the repo root, so our './mvn' shim is not picked up. Repository already includes:
  - ./mvn (shim that proxies to ./mvnw)
  - ./mvnw and ./mvnw.cmd (Maven Wrapper)
  - project_manifest.yaml configured to use './mvn' and fallbacks
  - start/start.sh and run.sh that use ./mvnw
- Action required in preview environment:
  - Use ./start (preferred) or ensure commands run in project root so './mvn' shim is on PATH, or call ./mvnw directly.
  - Ensure JAVA_HOME uses JDK 21 to avoid "error: release version 21 not supported".
- Once preview uses ./mvn or ./mvnw, steps 04.01 and 05.01 can proceed.
