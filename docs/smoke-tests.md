# Smoke Tests – Java 21 / Spring Boot 3.2 Migration

## Purpose

This document defines a lightweight set of smoke tests to quickly validate that the BankApp backend is healthy after deployment, focusing on core infrastructure endpoints and Swagger / OpenAPI availability.

## Files Involved

- `src/main/resources/application.yml`
- `src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java`
- `src/main/java/com/coding/exercise/bankapp/config/OpenApiConfig.java`
- Test helpers:
  - `src/test/java/com/coding/exercise/bankapp/endpoint/EndpointChecksIT.java`
  - `src/test/java/com/coding/exercise/bankapp/openapi/OpenApiDocsAccessibilityTest.java`

## Prerequisites

- Application running with:
  - `server.port=3001`
  - `server.servlet.context-path=/bank-api`
- For example:

  ```bash
  ./mvnw -DskipTests spring-boot:run \
    -Dspring-boot.run.arguments="--server.port=3001 --server.servlet.context-path=/bank-api"
  ```

## Manual curl Smoke Tests

From another terminal:

```bash
# 1) Health probes
curl -i http://localhost:3001/bank-api/healthz
curl -i http://localhost:3001/bank-api/actuator/health

# 2) OpenAPI / Swagger
curl -i http://localhost:3001/bank-api/v3/api-docs
curl -i http://localhost:3001/bank-api/swagger-ui/index.html

# 3) H2 console
curl -i http://localhost:3001/bank-api/h2-console
```

### Expected Output

- `/bank-api/healthz`:
  - HTTP 200
  - JSON like `{"status":"ok"}`

- `/bank-api/actuator/health`:
  - HTTP 200
  - JSON with a `status` field, e.g. `"status":"UP"`

- `/bank-api/v3/api-docs`:
  - HTTP 200
  - `Content-Type: application/json`
  - Body includes `"openapi": "3.x.x"`

- `/bank-api/swagger-ui/index.html`:
  - HTTP 200
  - HTML for Swagger UI

- `/bank-api/h2-console`:
  - HTTP 200 or 302 followed by 200
  - HTML containing text like "H2 Console"

## Automated Smoke Tests (JUnit-based)

You can also run the integration test profile or targeted test classes.

### Endpoint checks profile

```bash
# With app running
./mvnw -q -Pendpoint-checks test
```

### Specific tests

```bash
./mvnw test -Dtest='com.coding.exercise.bankapp.endpoint.EndpointChecksIT'
./mvnw test -Dtest='com.coding.exercise.bankapp.openapi.OpenApiDocsAccessibilityTest'
```

### Expected Output

- Tests complete successfully with no failures.
- Logs show that connections to the local instance are successful and endpoints respond with expected status codes.

## Rollback Steps

If any smoke tests fail in a new deployment:

1. Check logs of the running app for stack traces or binding errors.
2. Validate configuration:
   - Correct `server.port` and `server.servlet.context-path`.
   - Security configuration exposes the intended endpoints.
3. If necessary, rollback to a previous deployment or commit using your standard release procedures.
4. Rerun the same smoke tests to confirm recovery.

## Manual Review Notes

- When changing context path or port, always update smoke test URLs and any automated scripts (e.g., CI health checks).
- If production deployments require authentication for health or documentation endpoints, adapt the curl commands to include `-u user:password` and confirm security behavior matches expectations.
