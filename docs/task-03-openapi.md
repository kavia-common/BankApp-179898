# Task 03 – OpenAPI / Swagger Configuration (springdoc-openapi)

## Purpose

This document explains how OpenAPI 3 and Swagger UI are configured using `springdoc-openapi-starter-webmvc-ui` and `OpenApiConfig`, and how these settings relate to the `/bank-api` context path.

## Files Changed

- `pom.xml`
- `src/main/java/com/coding/exercise/bankapp/config/OpenApiConfig.java`
- `src/main/resources/application.yml` (documentation comments only regarding URLs)

## Changes Applied

### 1. Dependency switch to springdoc-openapi

OpenAPI documentation is now provided by `springdoc-openapi-starter-webmvc-ui`:

```diff
+    <!-- OpenAPI/Swagger UI via springdoc -->
+    <dependency>
+      <groupId>org.springdoc</groupId>
+      <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
+      <version>2.6.0</version>
+    </dependency>
```

Historical Springfox dependencies (if they existed locally) must be removed to avoid conflicts.

### 2. OpenApiConfig class

`OpenApiConfig` centralizes OpenAPI metadata and server information:

```diff
+@Configuration
+@OpenAPIDefinition(
+    info = @Info(
+        title = "BankApp API",
+        description = "RESTful Spring Boot banking API providing customer, account, and transaction "
+                    + "management backed by an in-memory H2 database.",
+        version = "1.0.0",
+        contact = @Contact(
+            name = "BankApp Team"
+        ),
+        license = @License(
+            name = "MIT License",
+            url = "https://opensource.org/licenses/MIT"
+        )
+    ),
+    servers = {
+        @Server(
+            url = "/bank-api",
+            description = "BankApp API base path (servlet context-path)"
+        )
+    }
+)
+public class OpenApiConfig {
+    /*
+     * No explicit @Bean definitions are required here. The presence of this
+     * configuration class and its {@link OpenAPIDefinition} annotation is
+     * sufficient for springdoc-openapi to augment the generated OpenAPI
+     * document with the provided metadata and server information.
+     */
+}
```

### 3. Documented endpoints

The effective HTTP URLs (with `server.servlet.context-path=/bank-api`) are:

- OpenAPI JSON: `GET /bank-api/v3/api-docs`
- Swagger UI:
  - Canonical: `GET /bank-api/swagger-ui/index.html`
  - Legacy redirect: `GET /bank-api/swagger-ui.html`

`SecurityConfig` is configured to allow anonymous access to these paths.

## Commands to Test

Assuming the app is running on port 8989 with `/bank-api` context path:

```bash
./mvnw -DskipTests spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=8989 --server.servlet.context-path=/bank-api"
```

Then from another terminal:

```bash
# OpenAPI JSON
curl -i http://localhost:8989/bank-api/v3/api-docs

# Swagger UI HTML
curl -i http://localhost:8989/bank-api/swagger-ui/index.html

# Legacy redirect (if users still visit this path)
curl -i http://localhost:8989/bank-api/swagger-ui.html
```

You can also run the existing OpenAPI test:

```bash
./mvnw test -Dtest='com.coding.exercise.bankapp.openapi.OpenApiDocsAccessibilityTest'
```

## Expected Output

- `GET /bank-api/v3/api-docs`:
  - HTTP status: 200
  - `Content-Type: application/json`
  - Body includes a top-level `openapi` field (e.g., `"openapi": "3.0.1"`).

- `GET /bank-api/swagger-ui/index.html`:
  - HTTP status: 200 (or 302 followed by 200, depending on deployment).
  - Body includes HTML containing the Swagger UI application.

- `OpenApiDocsAccessibilityTest` passes without error.

## Rollback Steps

To revert OpenAPI configuration:

1. Remove or revert `springdoc-openapi-starter-webmvc-ui` in `pom.xml`:

   ```bash
   git checkout HEAD~1 -- pom.xml
   ```

2. Restore any prior Swagger configuration (if desired):

   ```bash
   git checkout HEAD~1 -- src/main/java/com/coding/exercise/bankapp/config/OpenApiConfig.java
   ```

3. Rebuild and re-run tests:

   ```bash
   ./mvnw -DskipTests clean package
   ./mvnw test
   ```

Be aware that rolling back may affect existing automated checks that rely on `/bank-api/v3/api-docs`.

## Manual Review Notes

- Confirm that any external integrations (documentation portals, API gateways, developer portals) that relied on earlier Swagger endpoints are updated to the new `springdoc-openapi` paths.
- Review API metadata (title, description, version) in `OpenApiConfig` and adjust to match your release naming and versioning strategy.
- If you need environment-specific server URLs (for example, full hostnames), consider using `springdoc` properties or additional `@Server` entries, but ensure they stay synchronized with reverse proxy configuration.
