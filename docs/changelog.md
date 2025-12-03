# Changelog – Java 21 / Spring Boot 3.2 Migration

## Purpose

This changelog records key changes made during the migration of the BankApp backend to Java 21 and Spring Boot 3.2.x.

## Version 1.0.0 – Java 21 / Spring Boot 3.2 Migration

### Summary

- Upgraded build and runtime to Java 21 and Spring Boot 3.2.10.
- Modernized security configuration to Spring Security 6 patterns.
- Standardized OpenAPI documentation via springdoc-openapi.
- Ensured compatibility by explicitly adding JAXB API and runtime dependencies.

### Details

#### Build and Dependencies

- Updated `pom.xml`:
  - Parent POM: `org.springframework.boot:spring-boot-starter-parent:3.2.10`.
  - Java configuration:
    - `<java.version>21</java.version>`
    - `maven-compiler-plugin` with `<release>21</release>`.
  - Test runtime:
    - `maven-surefire-plugin` version `3.2.5`.
  - Added JAXB dependencies:
    - `jakarta.xml.bind:jakarta.xml.bind-api:4.0.2`
    - `org.glassfish.jaxb:jaxb-runtime:4.0.5`
  - Added OpenAPI tooling:
    - `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0`

#### Security

- Introduced `SecurityConfig` using Spring Security 6’s `SecurityFilterChain`:
  - Public endpoints:
    - `/healthz`
    - `/actuator/health`
    - `/v3/api-docs/**`
    - `/swagger-ui/**`
    - `/swagger-ui.html`
    - `/h2-console/**`
  - HTTP Basic authentication required for business endpoints.
  - CSRF disabled and frame options turned off for H2 console.

#### OpenAPI

- Added `OpenApiConfig` with `@OpenAPIDefinition`:
  - Title: `BankApp API`
  - Description: Banking API for customers, accounts, and transactions.
  - Version: `1.0.0`
  - License: MIT
  - Server base URL: `/bank-api`

- Springdoc endpoints:
  - OpenAPI JSON: `/bank-api/v3/api-docs`
  - Swagger UI: `/bank-api/swagger-ui/index.html` (canonical)

#### Configuration

- Ensured `application.yml` sets:
  - `server.port: 3001`
  - `server.servlet.context-path: /bank-api`
  - H2 console enabled under `/bank-api/h2-console`.

#### javax → jakarta Migration Status

- Confirmed no remaining `javax.*` imports.
- All JPA annotations use `jakarta.persistence.*`.

### Verification

- Build and tests:

  ```bash
  ./mvnw -DskipTests clean package
  ./mvnw test
  ```

- Runtime smoke tests (with app running):

  ```bash
  curl -i http://localhost:8989/bank-api/healthz
  curl -i http://localhost:8989/bank-api/actuator/health
  curl -i http://localhost:8989/bank-api/v3/api-docs
  curl -i http://localhost:8989/bank-api/swagger-ui/index.html
  curl -i http://localhost:8989/bank-api/h2-console
  ```

### Manual Review Notes

- Security posture of public endpoints should be periodically reviewed, especially when exposing the service externally.
- If future changes add new dependencies, re-check for `javax.*` imports and conflicting Jakarta API versions.
