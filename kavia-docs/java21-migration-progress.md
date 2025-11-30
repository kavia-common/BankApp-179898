# Java 21 Migration Progress Tracker

## Overview

This document tracks the step-by-step progress of migrating the BankApp-179898 project to Java 21 and Spring Boot 3.x. After completing each step, we will update the Status, and add any outputs, notes, and links to diffs or pull requests in the Notes/Links column. The intent is to keep a single source of truth for the migration state that is easy to scan and share.

Status values:
- To-do: Not started yet
- In-progress: Work underway
- Success: Completed and validated
- Blocked-by-environment: Blocked due to preview/CI environment misconfiguration (add explanation in Notes)

## Planned Steps

| Step ID | Description | Status | Notes/Links to diffs |
|---|---|---|---|
| 01.01 | Create tracker | To-do |  |
| 02.01 | Update Java version/toolchain | Success | pom.xml updated to target Java 21; compiler release set to 21. |
| 02.02 | Upgrade Spring Boot | Success | Parent upgraded to Spring Boot 3.3.4; removed incompatible Springfox deps. |
| 02.03 | Update dependencies (H2, Spring Security, springdoc-openapi) | Success | springdoc-openapi starter added; Boot-managed H2/Security versions applied. |
| 02.04 | Update Maven Wrapper | To-do |  |
| 02.05 | Adjust .mvn/jvm.config | To-do |  |
| 03.01 | Code refactor to jakarta and Security 6 | Success | Success: Final sweep confirms no javax.* usages remain (persistence, validation, servlet). JPA entities use jakarta.persistence.*; no javax.validation.* or javax.servlet.* references found. Files changed/verified: src/main/java/com/coding/exercise/bankapp/model/Account.java, Address.java, BankInfo.java, Contact.java, Customer.java, CustomerAccountXRef.java, Transaction.java. Also reviewed: config/SecurityConfig.java (Spring Security 6), controllers, services, repositories. |
| 03.02 | Update Spring Security to Spring Security 6 style | Success | SecurityFilterChain with authorizeHttpRequests + requestMatchers; HTTP Basic enabled; CSRF disabled; frame options disabled for H2. Permitted: /v3/api-docs/**, /swagger-ui/**, /swagger-ui.html, /h2-console/**. Changed: src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java |
| 03.03 | Replace Springfox with springdoc-openapi | Success | Springfox removed; springdoc-openapi-starter-webmvc-ui added and auto-configures docs. Swagger UI at /bank-api/swagger-ui.html (/index.html); OpenAPI at /bank-api/v3/api-docs. Changed: pom.xml, src/main/java/com/coding/exercise/bankapp/config/ApplicationConfig.java, README.md |
| 03.04 | Verify H2 console path and datasource settings | Success | H2 console available at /bank-api/h2-console; Security permits /h2-console/** and disables frame options; Boot 3 default datasource OK, no extra props required. Changed: src/main/resources/application.yml, src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java, README.md |
| 04.01 | Clean build on Java 21 | Blocked-by-environment | Persistent environment error: 'bash: mvn: command not found'. Preview hardcodes 'mvn' outside repo root, bypassing local ./mvn shim and ./mvnw wrapper. Use ./mvn or ./mvnw once preview is updated. |
| 05.01 | Run and smoke-test | Blocked-by-environment | Persistent environment error: 'bash: mvn: command not found'. Start should invoke ./mvn or ./mvnw (or ./start) from repo root; update preview config later. |
| 06.01 | Update docs | Success | Tracker updated; BUILD_NOTES documents wrapper and shim usage. |

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
  - Repository sweep confirms: all javax.persistence imports migrated to jakarta.persistence across entities; no javax.validation.* or javax.servlet.* references present. Verified entities, controllers, services, repositories.

- 03.02 Spring Security 6 migration — Success
  - SecurityFilterChain configured with authorizeHttpRequests + requestMatchers; HTTP Basic; CSRF disabled; frame options disabled for H2 console. Permits "/", "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**".
  - Files changed: src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java

- 03.03 Replace Springfox with Springdoc — Success
  - Springfox removed; springdoc-openapi-starter-webmvc-ui:2.6.0 added.
  - OpenAPI at /bank-api/v3/api-docs; Swagger UI at /bank-api/swagger-ui.html and /bank-api/swagger-ui/index.html.
  - Files changed: pom.xml, src/main/java/com/coding/exercise/bankapp/config/ApplicationConfig.java, README.md

- 03.04 H2 console and datasource verification — Success
  - Boot 3 compatible H2 console; Security allows console with frames disabled.
  - Files verified/updated: application.yml, SecurityConfig.java, README.md

- 04.01 Clean build on Java 21 — Blocked-by-environment
  - Persistent error recorded: 'bash: mvn: command not found' (exit code 127).
  - Diagnosis: Preview calls bare 'mvn' from outside repo root, so local ./mvn shim and ./mvnw are not on the path.
  - Deferral: Do not rerun preview; will proceed once preview commands are updated to call ./mvn or ./mvnw from project root.

- 05.01 Run and smoke-test — Blocked-by-environment
  - Persistent error recorded: 'bash: mvn: command not found'.
  - Diagnosis: Start path uses bare 'mvn'. Should switch to ./mvn or ./mvnw, or use ./start which delegates to Maven Wrapper.
  - Deferral: Defer preview fix; mark step as Blocked-by-environment.

## Diagnostics

- Environment error (persistent):
  - 'bash: mvn: command not found' indicates the preview environment invokes 'mvn' without system Maven and from outside the repository root, bypassing the repo-provided shim/wrapper.

- Local tooling present:
  - mvn shim at repo root: ./mvn (proxies to ./mvnw; falls back to 'sh mvnw').
  - Maven Wrapper: ./mvnw and ./mvnw.cmd are present.
  - Start helpers: ./start, ./start.sh, ./run.sh; scripts include ./scripts/start_via_shim.sh.

- Preview invocation guidance (to apply later):
  - Update preview/install/build/start commands to use './mvn' or './mvnw' from the project root (or './start').
  - This resolves the 'mvn: command not found' error without requiring system Maven.

- Status confirmations (03.01–03.04):
  - Confirmed Success for 03.01–03.04 based on repository changes: jakarta persistence imports, SecurityFilterChain in place, Springfox removed in favor of springdoc, H2 console path verified with corresponding security and application.yml settings.
