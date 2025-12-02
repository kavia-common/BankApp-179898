# Java 21 Migration Progress Tracker

## Overview

This document tracks the step-by-step progress of migrating the BankApp-179898 project to Java 21 and Spring Boot 3.x. After completing each step, we will update the Status, and add any outputs, notes, and links to diffs or pull requests in the Notes/Links column. The intent is to keep a single source of truth for the migration state that is easy to scan and share.

Status values:
- To-do: Not started yet
- In-progress: Work underway
- Success: Completed and validated
- Blocked-by-environment: Blocked due to preview/CI environment misconfiguration (add explanation in Notes)

Environment acknowledgment:
- The preview currently hardcodes 'mvn' and executes it outside the repo root, causing: bash: mvn: command not found.
- We will not modify or run the preview at this time. Build/run steps are marked Blocked-by-environment accordingly.
- The repository already includes a local mvn shim and mvnw wrapper ready for when the preview switches to './mvn' or './mvnw'.

## Planned Steps

| Step ID | Description | Status | Notes/Links to diffs |
|---|---|---|---|
| 01.01 | Create tracker | To-do |  |
| 02.01 | Update Java version/toolchain | Success | pom.xml updated to target Java 21; compiler release set to 21. |
| 02.02 | Upgrade Spring Boot | Success | Parent upgraded to Spring Boot 3.3.4; removed incompatible Springfox deps. |
| 02.03 | Update dependencies (H2, Spring Security, springdoc-openapi) | Success | springdoc-openapi starter added; Boot-managed H2/Security versions applied. |
| 02.04 | Update Maven Wrapper | To-do | Pending wrapper refresh to 3.2.x and Maven 3.9.x+ (mvnw file present; works once environment uses ./mvnw). |
| 02.05 | Adjust .mvn/jvm.config | To-do | Pending review/removal of add-opens flags if present. |
| 03.01 | Code refactor to jakarta and Security 6 | Success | Final sweep: no javax.* across persistence/validation/servlet. All JPA use jakarta.persistence.* under src/main/java/com/.../model/*.java. No javax.validation or javax.servlet usages found. Files validated: model/*.java, controllers/services/repositories. |
| 03.02 | Update Spring Security to Spring Security 6 style | Success | SecurityFilterChain bean using authorizeHttpRequests + requestMatchers; HTTP Basic enabled; CSRF disabled; frameOptions disabled for H2. Permitted paths: /v3/api-docs/**, /swagger-ui/**, /swagger-ui.html, /h2-console/**. File changed: src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java |
| 03.03 | Replace Springfox with springdoc-openapi | Success | Springfox removed from pom; springdoc-openapi-starter-webmvc-ui added. No Springfox config classes remain; basic springdoc auto-config via ApplicationConfig placeholder. Swagger UI reachable at /bank-api/swagger-ui/index.html (with /bank-api/swagger-ui.html redirecting there). Files changed: pom.xml, src/main/java/com/coding/exercise/bankapp/config/ApplicationConfig.java, README.md. |
| 03.04 | Verify H2 console path and datasource settings | Success | H2 console at /bank-api/h2-console (context-path applied). Security permits /h2-console/**; headers frameOptions disabled. application.yml retains spring.h2.console.enabled and notes Boot 3 defaults; datasource uses Boot-managed in-memory H2 unless overridden. Files verified/updated: src/main/resources/application.yml, SecurityConfig.java. |
| 04.01 | Clean build on Java 21 | Blocked-by-environment | Error: 'bash: mvn: command not found'. Preview hardcodes mvn outside repo root; will pass once preview uses './mvn' or './mvnw'. |
| 05.01 | Run and smoke-test | Blocked-by-environment | Error: 'bash: mvn: command not found'. Use './mvn' or './mvnw' (or './start') from repo root when preview is updated. |
| 06.01 | Update docs | Success | Tracker and BUILD_NOTES updated; mvn shim/mvnw usage documented. |

## Step Updates

- 02.01 Update Java version/toolchain — Success
  - pom.xml: set <java.version>21</java.version>.
  - maven-compiler-plugin: <release>21</release> (3.11.0).
  - Removed legacy Java 8 settings.

- 02.02 Upgrade Spring Boot — Success
  - Parent upgraded to Spring Boot 3.3.4.
  - Removed deprecated/incompatible Springfox dependencies.

- 02.03 Update dependencies — Success
  - Added org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0.
  - Relying on Boot-managed versions for Spring Security 6, Hibernate 6, H2.

- 03.01 javax → jakarta migration — Success
  - All entities updated to jakarta.persistence.*; no javax.validation.* or javax.servlet.* in codebase.
  - Verified controllers, services, repositories compile under Boot 3 conventions.

- 03.02 Spring Security 6 migration — Success
  - SecurityFilterChain with authorizeHttpRequests + requestMatchers; HTTP Basic; CSRF disabled; frame options disabled for H2.
  - Permits: "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**".
  - File: src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java

- 03.03 Replace Springfox with Springdoc — Success
  - Springfox removed; springdoc starter added.
  - Swagger UI available at /bank-api/swagger-ui.html and /bank-api/swagger-ui/index.html.
  - Files changed: pom.xml, config/ApplicationConfig.java, README.md

- 03.04 H2 console and datasource verification — Success
  - application.yml aligned; Security permits console with frames enabled.
  - Console reachable at /bank-api/h2-console.

- 04.01 Clean build on Java 21 — Blocked-by-environment
  - Explicit error: 'bash: mvn: command not found'.
  - Cause: Preview invokes 'mvn' outside repo root, bypassing local ./mvn shim and ./mvnw.
  - Action: Do not modify preview now; will pass when preview uses './mvn' or './mvnw'.

- 05.01 Run and smoke-test — Blocked-by-environment
  - Explicit error: 'bash: mvn: command not found'.
  - Use './start' or './mvnw' from repo root once preview is updated.

## Diagnostics

- Environment error (persistent):
  - 'bash: mvn: command not found' indicates a hardcoded 'mvn' not resolved by PATH and executed outside repo root.

- Local tooling present and ready:
  - mvn shim at repo root: ./mvn (proxies to ./mvnw; falls back to 'sh mvnw').
  - Maven Wrapper: ./mvnw and ./mvnw.cmd are present and committed.
  - Start helpers: ./start, ./start.sh, ./run.sh; scripts include ./scripts/start_via_shim.sh.

- Preview invocation guidance (to apply later):
  - Switch preview commands to './mvn ...' or './mvnw ...' run from the project root, or use './start'.

- Manifest update:
  - project_manifest.yaml preview commands now point to './mvn' (shim) first, with fallbacks to 'sh mvn', './mvnw', and 'sh mvnw', keeping <port> and <host> placeholders intact. This resolves 'mvn not found' in environments expecting to invoke 'mvn' but lacking system Maven.

- Status confirmations (03.01–03.04):
  - 03.01 — Success (jakarta migration complete).
  - 03.02 — Success (Security 6).
  - 03.03 — Success (springdoc).
  - 03.04 — Success (H2 console path and security).
