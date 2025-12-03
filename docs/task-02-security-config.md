# Task 02 – Spring Security 6 Configuration (SecurityConfig)

## Purpose

This document describes the Security 6 configuration implemented in `SecurityConfig`, including permitted endpoints, HTTP Basic authentication, CSRF and frame options settings, and how they interact with the `/bank-api` context path.

## Files Changed

- `src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java`

## Changes Applied

The project now uses the Spring Security 6 `SecurityFilterChain` bean model. This replaces the older `WebSecurityConfigurerAdapter` and `antMatchers` style configuration.

### New SecurityFilterChain configuration

```diff
+@Configuration
+@EnableWebSecurity
+public class SecurityConfig {
+
+    // PUBLIC_INTERFACE
+    @Bean
+    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
+        http
+            // CSRF is disabled to simplify API interactions and allow H2 console to work with POSTs
+            .csrf(csrf -> csrf.disable())
+            // H2 console requires frames
+            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
+            .authorizeHttpRequests(auth -> auth
+                .requestMatchers(
+                    // OpenAPI/Swagger endpoints
+                    "/v3/api-docs/**",
+                    "/swagger-ui/**",
+                    "/swagger-ui.html",
+                    // H2 console
+                    "/h2-console/**",
+                    // Health endpoints under context path (/bank-api/healthz, /bank-api/actuator/health)
+                    "/healthz",
+                    "/actuator/health"
+                ).permitAll()
+                .anyRequest().authenticated()
+            )
+            // Keep HTTP Basic for simplicity
+            .httpBasic(Customizer.withDefaults());
+
+        return http.build();
+    }
+}
```

Key behaviors:

- **Public endpoints** (no authentication):
  - `/bank-api/healthz`
  - `/bank-api/actuator/health`
  - `/bank-api/v3/api-docs/**`
  - `/bank-api/swagger-ui/**`
  - `/bank-api/swagger-ui.html`
  - `/bank-api/h2-console/**`
- **All other endpoints** require HTTP Basic authentication with credentials configured in `application.yml`.
- **CSRF** is disabled to simplify stateless API interactions and H2 console usage.
- **Frame options** are disabled to allow the H2 console UI to render correctly in browsers.

Because `server.servlet.context-path=/bank-api` is configured at the server level, the `requestMatchers` above are specified without the context path prefix; Spring will apply the context path automatically.

## Commands to Test

### 1. Start the application

```bash
./mvnw -DskipTests spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=8989 --server.servlet.context-path=/bank-api"
```

### 2. Verify public endpoints (no auth)

```bash
# Healthz
curl -i http://localhost:8989/bank-api/healthz

# Actuator health
curl -i http://localhost:8989/bank-api/actuator/health

# OpenAPI JSON
curl -i http://localhost:8989/bank-api/v3/api-docs

# Swagger UI HTML
curl -i http://localhost:8989/bank-api/swagger-ui/index.html

# H2 console (HTML)
curl -i http://localhost:8989/bank-api/h2-console
```

### 3. Verify protected endpoints (require HTTP Basic)

Replace `user:password` below with the values from `application.yml` (`bankapp:changeit` by default):

```bash
# Example protected business endpoint (replace with a real one, e.g. /customers)
curl -i http://localhost:8989/bank-api/customers

# Same endpoint with HTTP Basic
curl -i -u bankapp:changeit http://localhost:8989/bank-api/customers
```

## Expected Output

- Requests to `/bank-api/healthz`, `/bank-api/actuator/health`, `/bank-api/v3/api-docs`, `/bank-api/swagger-ui/**`, and `/bank-api/h2-console/**` should return HTTP 200 (or a redirect chain to 200) without requiring authentication.
- Requests to business endpoints (e.g., `/bank-api/customers`) should:
  - Return HTTP 401 Unauthorized without credentials.
  - Return HTTP 200 when called with valid HTTP Basic credentials.
- Browser-based access to H2 console should work at:
  - `http://localhost:8989/bank-api/h2-console`

## Rollback Steps

To revert the new security configuration:

1. Restore the previous `SecurityConfig` implementation from Git history:

   ```bash
   git checkout HEAD~1 -- src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java
   ```

2. Rebuild and re-run tests:

   ```bash
   ./mvnw -DskipTests clean package
   ./mvnw test
   ```

3. Manually re-verify public and protected endpoints behave as before.

## Manual Review Notes

- Confirm that exposing `/bank-api/v3/api-docs`, `/bank-api/swagger-ui/**`, and `/bank-api/h2-console/**` without authentication is acceptable in your deployment context. For stricter environments, you may:
  - Require authentication for documentation or H2 console endpoints.
  - Restrict IP ranges at the reverse proxy or network layer.
- Validate that any reverse proxies or API gateways in front of this service correctly forward the `/bank-api` context path and do not strip it in a way that invalidates the configured `requestMatchers`.
- Consider enabling CSRF protection if the API is ever used by browser-based clients with cookies. The current configuration is optimized for stateless API and internal tooling.
