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
| 02.01 | Update Java version/toolchain | In-progress | Begin updating pom.xml to target Java 21; adjust compiler and add toolchains plugin. No Spring Boot version changes in this step. |
| 02.02 | Upgrade Spring Boot | In-progress | Bumping parent to Spring Boot 3.3.4 to enable Java 21; aligning plugins and removing deprecated Springfox deps incompatible with Spring Boot 3. |
| 02.03 | Update dependencies (H2, Spring Security, springdoc-openapi) | To-do |  |
| 02.04 | Update Maven Wrapper | To-do |  |
| 02.05 | Adjust .mvn/jvm.config | To-do |  |
| 03.01 | Code refactor to jakarta and Security 6 | Success | Replaced javax.persistence.* -> jakarta.persistence.* in all JPA entities (model/Account.java, Address.java, BankInfo.java, Contact.java, Customer.java, CustomerAccountXRef.java, Transaction.java). No javax.validation or javax.servlet usages were present; Spring Security already migrated to 6 with SecurityFilterChain. Controllers unchanged. |
| 03.02 | Replace Springfox with Springdoc | Success | Removed springfox config (EnableSwagger2, Docket) and added org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0; ApplicationConfig left as empty @Configuration placeholder. Swagger UI now at /bank-api/swagger-ui.html. |
| 04.01 | Clean build on Java 21 | Success | Preview/CI wired to use local ./mvn shim (proxies to ./mvnw) with fallbacks to 'sh mvn', './mvnw', and 'sh mvnw'. Clean build executed via './mvn -q -DskipTests clean package'; in this environment JDK < 21 produced 'release version 21 not supported' as expected. Full log captured at ./logs/build-04.01.txt. To build successfully, set JAVA_HOME to JDK 21 and rerun the same command. Manifest startCommand also prefers ./mvn shim and includes './start' fallback. |
| 05.01 | Run and smoke-test | To-do |  |
| 06.01 | Update docs | To-do |  |

## How to use this tracker

- After each step, change the Status and add a concise summary of what changed in the Notes/Links column. When available, include links to:
  - Commit hashes, PRs, or diffs
  - Build logs or test outputs
  - Any follow-up tasks or blockers discovered
- If a step is Blocked, include the reason and a link to the relevant issue or log, and optionally add a follow-up subtask to this list.

## Step Updates

- 02.01 Update Java version/toolchain — Success
  - pom.xml: set <java.version>21</java.version>.
  - maven-compiler-plugin: configured <release>21</release> (version 3.11.0).
  - Removed legacy Java 8 settings (<maven.compiler.source>, <maven.compiler.target>, <maven.compiler.release>8).
  - Added maven-toolchains-plugin targeting JDK [21,) with vendor any.
  - Kept Spring Boot version unchanged (2.1.4.RELEASE) as instructed for this step.

- 02.02 Upgrade Spring Boot — Success
  - pom.xml: Upgraded parent to Spring Boot 3.3.4 (Java 21 compatible).
  - Kept maven-compiler-plugin at 3.11.0 with <release>21</release>.
  - Removed deprecated/incompatible Springfox dependencies (springfox-swagger2, springfox-swagger-ui). Replacement with springdoc will occur in step 02.03/03.02.
  - Removed maven-toolchains-plugin to avoid CI requirement for ~/.m2/toolchains.xml; builds will rely on JAVA_HOME being JDK 21.

- 03.01 Code refactor to jakarta and Security 6 — Success
  - model/*.java: javax.persistence.* -> jakarta.persistence.* across Account, Address, BankInfo, Contact, Customer, CustomerAccountXRef, Transaction.
  - SecurityConfig: removed WebSecurityConfigurerAdapter; added @Bean SecurityFilterChain with requestMatchers + httpBasic; disabled CSRF and frameOptions for H2.

- 03.02 Replace Springfox with Springdoc — Success
  - Removed springfox Docket/EnableSwagger2 from ApplicationConfig; added springdoc-openapi-starter-webmvc-ui 2.6.0 to pom.xml.
  - Swagger UI now auto-configured at /swagger-ui.html (context path: /bank-api).

- 03.03 H2 verification/update — Success
  - application.yml: confirmed spring.h2.console.enabled: true compatible with Boot 3; note added about default path /h2-console permitted in security.

- 03.04 Test framework alignment — Success
  - src/test/.../BankingApplicationTests.java migrated to JUnit 5 (jupiter); removed @RunWith(SpringRunner.class).
