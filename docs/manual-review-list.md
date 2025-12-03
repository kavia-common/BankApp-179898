# Manual Review List – Java 21 / Spring Boot 3.2 Migration

## Purpose

This document tracks locations and concerns that warrant human review following the migration to Java 21 and Spring Boot 3.2.x.

## Items Requiring Manual Review

### 1. Security exposure of public endpoints

**Files:**

- `src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java`
- `src/main/resources/application.yml`

**Description:**

Security configuration permits unauthenticated access to:

- `/bank-api/healthz`
- `/bank-api/actuator/health`
- `/bank-api/v3/api-docs/**`
- `/bank-api/swagger-ui/**`
- `/bank-api/swagger-ui.html`
- `/bank-api/h2-console/**`

**Review Actions:**

- Confirm that this is acceptable for all target environments, especially production.
- Optionally tighten access:
  - Require authentication for Swagger and H2 console in non-development environments.
  - Restrict H2 console to internal networks or disable it entirely.

### 2. Java 21 environment readiness

**Files:**

- `pom.xml`
- CI configuration (outside this repository; e.g. GitHub Actions, Jenkins, etc.)

**Description:**

Build requires Java 21. Older environments will fail with compilation errors.

**Review Actions:**

- Confirm all build agents and runtime environments use Java 21 or a Java 21 toolchain.
- Update infrastructure images and documentation where necessary.

### 3. Dependency tree sanity

**Files:**

- `pom.xml`

**Description:**

Explicit JAXB and springdoc dependencies have been added. While no conflicts are visible in this repository, future changes could introduce duplicate or incompatible versions.

**Review Actions:**

- Periodically run:

  ```bash
  ./mvnw dependency:tree | sed -n '1,200p'
  ```

- Check for:
  - Multiple versions of `jakarta.xml.bind` or `jaxb-runtime`.
  - Mixed Spring Framework / Boot versions that could cause `NoSuchMethodError`.

### 4. javax → jakarta status

**Files:**

- All Java source files under `src/main/java`

**Description:**

Current codebase does not contain `javax.*` imports. All JPA entities already use `jakarta.persistence.*`.

**Review Actions:**

- If new code is imported from older repositories, re-run:

  ```bash
  grep -RIn "javax\." src || echo "No javax.* imports found"
  ```

- If any `javax.*` usage appears, record the affected files here and perform a careful conversion to `jakarta.*`.

## Items Requiring No Special Manual Review

- There are no automated `javax`→`jakarta` conversions performed in this migration, so no `// MANUAL REVIEW REQUIRED` markers are present in the codebase for that concern.
- No complex refactoring was applied to business logic; changes are localized to configuration and build files.

## How to Update This Document

When future changes raise new concerns:

1. Add a new section under “Items Requiring Manual Review”.
2. Include:
   - Affected files.
   - Description of the risk or uncertainty.
   - Concrete review actions for human reviewers.
3. Link the entry from relevant task documents or PR descriptions.
