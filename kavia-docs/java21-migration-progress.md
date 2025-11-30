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
| 03.01 | Code refactor to jakarta and Security 6 | Success | javax.persistence -> jakarta.persistence across all entities; Security config modernized. |
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
  - Scanned for javax.* usages across entities, validation, and servlet references.
  - Identified javax.persistence.* only in model classes; no javax.validation or javax.servlet usage.
- 03.01 Code refactor to jakarta and Security 6 — Success
  - Replaced javax.persistence.* -> jakarta.persistence.* in:
    - src/main/java/com/coding/exercise/bankapp/model/Account.java
    - Address.java, BankInfo.java, Contact.java, Customer.java, CustomerAccountXRef.java, Transaction.java
  - SecurityConfig: migrated off WebSecurityConfigurerAdapter to SecurityFilterChain with requestMatchers + httpBasic; CSRF and frameOptions disabled to allow H2 console.

- 03.02 Update Spring Security to Spring Security 6 style — In-progress
  - Drafted SecurityFilterChain to permit "/", "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html".
- 03.02 Update Spring Security to Spring Security 6 style — Success
  - Implemented component-based configuration with SecurityFilterChain.
  - Authorization DSL uses authorizeHttpRequests + requestMatchers.
  - Basic auth enabled; CSRF disabled; frameOptions disabled for H2 console.

- 03.03 Replace Springfox with Springdoc — In-progress
  - Removed Springfox annotations from controllers.
- 03.03 Replace Springfox with Springdoc — Success
  - springdoc-openapi-starter-webmvc-ui 2.6.0 in pom.xml.
  - Swagger UI auto-configured at:
    - /bank-api/swagger-ui.html
    - /bank-api/swagger-ui/index.html
  - Controllers cleanup completed:
    - AccountController.java: removed all io.swagger.annotations imports/annotations.
    - CustomerController.java: removed all io.swagger.annotations imports/annotations.

- 03.04 H2 verification/update — In-progress
  - Reviewed application.yml and SecurityConfig for Boot 3 compatibility.
- 03.04 H2 verification/update — Success
  - application.yml: spring.h2.console.enabled: true confirmed; default path /h2-console noted.
  - SecurityConfig permits "/h2-console/**" and disables frame options for rendering.

- 04.01 Clean build on Java 21 — Success
  - Build requires JDK 21. Logs captured under ./logs/build-04.01.txt (see BUILD_NOTES.md for commands).

## Notes

- Swagger/OpenAPI provided by springdoc; no custom Docket or springfox config remains.
- If actuator endpoints need to be unauthenticated, add "/actuator/**" to permitted requestMatchers and configure management.endpoints.web.exposure.include in application.yml as needed.
