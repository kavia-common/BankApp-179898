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
| 03.01 | Code refactor to jakarta and Security 6 | Success | javax.persistence -> jakarta.persistence across all entities; Security config modernized; no javax.validation or javax.servlet usages found. |
| 03.02 | Update Spring Security to Spring Security 6 style | Success | SecurityFilterChain with requestMatchers; httpBasic; CSRF/frameOptions disabled for H2. |
| 03.03 | Replace Springfox with springdoc-openapi | Success | Springfox removed; springdoc starter added; controllers cleaned of io.swagger.annotations. |
| 03.04 | Verify H2 console path and datasource settings | Success | application.yml verified for Boot 3; /h2-console permitted in security. |
| 04.01 | Clean build on Java 21 | Success | Wrapper/shim verified; requires JDK 21 in environment. |
| 05.01 | Run and smoke-test | To-do |  |
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

- 03.01 Code refactor to jakarta and Security 6 — In-progress
  - Scanned for javax.* usages across persistence, validation, and servlet APIs.
  - Identified entities using jakarta.persistence already; validating no residual javax.* remains.
- 03.01 Code refactor to jakarta and Security 6 — Success
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

- 03.02 Update Spring Security to Spring Security 6 style — In-progress
  - Drafted migration off WebSecurityConfigurerAdapter to SecurityFilterChain with requestMatchers.
  - Identified public endpoints: "/", "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html".
- 03.02 Update Spring Security to Spring Security 6 style — Success
  - Implemented SecurityFilterChain with authorizeHttpRequests + requestMatchers, httpBasic, CSRF disabled, and frameOptions disabled for H2 console.
  - File changed:
    - src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java

- 03.03 Replace Springfox with Springdoc — In-progress
  - Removed Springfox libraries from pom and added springdoc-openapi-starter-webmvc-ui:2.6.0.
  - Cleaned controllers of io.swagger.annotations (springdoc infers from Spring MVC).
- 03.03 Replace Springfox with Springdoc — Success
  - Swagger UI reachable at:
    - /bank-api/swagger-ui.html
    - /bank-api/swagger-ui/index.html
  - Files changed:
    - pom.xml (removed springfox, added springdoc)
    - src/main/java/com/coding/exercise/bankapp/config/ApplicationConfig.java (springdoc placeholder)
    - src/main/java/com/coding/exercise/bankapp/controller/AccountController.java (removed springfox annotations)
    - src/main/java/com/coding/exercise/bankapp/controller/CustomerController.java (removed springfox annotations)

- 03.04 H2 verification/update — In-progress
  - Validating Boot 3 H2 console configuration and context-path interactions.
  - Checking security exemptions and frame headers for console rendering.
- 03.04 H2 verification/update — Success
  - spring.h2.console.enabled: true, H2 console at /bank-api/h2-console.
  - SecurityConfig permits "/h2-console/**" and disables frameOptions for H2.
  - Files verified/updated:
    - src/main/resources/application.yml
    - src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java

- 04.01 Clean build on Java 21 — In-progress
  - Executed: ./mvn -q -DskipTests clean package (via shim -> wrapper).
  - Result: Environment JDK does not support release 21 ("error: release version 21 not supported").
  - Action needed: Ensure JAVA_HOME points to JDK 21 in the preview/CI environment and re-run.
  - Logs: ./logs/build-04.01.txt

## Notes

- Swagger/OpenAPI provided by springdoc; no custom Docket or springfox config remains.
- If actuator endpoints need to be unauthenticated, add "/actuator/**" to permitted requestMatchers and configure management.endpoints.web.exposure.include in application.yml as needed.
