# Java 21 / Spring Boot 3.2 Migration Summary

## Purpose

This document summarizes the completed migration of the BankApp backend to Java 21 and Spring Boot 3.2.x, including key dependency updates, security configuration, OpenAPI integration, and runtime verification steps.

## Files Changed

- `pom.xml`
- `src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java`
- `src/main/java/com/coding/exercise/bankapp/config/OpenApiConfig.java`
- `src/main/resources/application.yml`

## Key Changes

The migration has already been applied to the repository and includes:

1. **Java and Spring Boot versions**
   - Parent: `org.springframework.boot:spring-boot-starter-parent:3.2.10`
   - Global Java version: `<java.version>21</java.version>`
   - Compiler configured with `--release 21` via `maven-compiler-plugin`.

2. **Testing and runtime plugins**
   - `maven-surefire-plugin` upgraded to `3.2.5` to ensure compatibility with Java 21 and JUnit 5.
   - `spring-boot-maven-plugin` configured to support `spring-boot:run` and fat-jar packaging.

3. **OpenAPI / Swagger and JAXB**
   - Removed reliance on Springfox (if present historically).
   - Added `springdoc-openapi-starter-webmvc-ui` for OpenAPI 3 + Swagger UI.
   - Added `jakarta.xml.bind:jakarta.xml.bind-api:4.0.2` and `org.glassfish.jaxb:jaxb-runtime:4.0.5` to restore JAXB on Java 21.

4. **Security configuration (Spring Security 6)**
   - `SecurityFilterChain` bean introduced in `SecurityConfig`.
   - Uses `authorizeHttpRequests` with `requestMatchers`.
   - HTTP Basic authentication enforced for business APIs.
   - Unauthenticated access explicitly allowed for:
     - `/healthz`
     - `/actuator/health`
     - `/v3/api-docs/**`
     - `/swagger-ui/**`
     - `/swagger-ui.html`
     - `/h2-console/**`
   - CSRF disabled and frame options turned off for H2 console.

5. **OpenAPI configuration**
   - `OpenApiConfig` provides OpenAPI metadata and server configuration, registering `/bank-api` as the base path.

6. **Context path and runtime behavior**
   - Servlet context path fixed at `/bank-api` in `application.yml`.
   - H2 console enabled and aligned with the new Spring Security configuration.
   - No remaining `javax.*` imports; entities already use `jakarta.persistence.*`.

## Representative Diff Snippets

### pom.xml

```diff
-  <parent>
-    <groupId>org.springframework.boot</groupId>
-    <artifactId>spring-boot-starter-parent</artifactId>
-    <version>2.x.y</version>
-  </parent>
+  <parent>
+    <groupId>org.springframework.boot</groupId>
+    <artifactId>spring-boot-starter-parent</artifactId>
+    <!-- Spring Boot parent constrained to 3.2.x line -->
+    <version>3.2.10</version>
+    <relativePath/>
+  </parent>
@@
-  <properties>
-    <java.version>17</java.version>
-  </properties>
+  <properties>
+    <!-- Global Java version used across the project -->
+    <java.version>21</java.version>
+    <maven.compiler.release>21</maven.compiler.release>
+    <maven.compiler.source>21</maven.compiler.source>
+    <maven.compiler.target>21</maven.compiler.target>
+    <maven.compiler.parameters>true</maven.compiler.parameters>
+    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
+  </properties>
@@
+    <!-- JAXB (Jakarta XML Binding) for Java 21 -->
+    <dependency>
+      <groupId>jakarta.xml.bind</groupId>
+      <artifactId>jakarta.xml.bind-api</artifactId>
+      <version>4.0.2</version>
+    </dependency>
+    <dependency>
+      <groupId>org.glassfish.jaxb</groupId>
+      <artifactId>jaxb-runtime</artifactId>
+      <version>4.0.5</version>
+    </dependency>
@@
+    <!-- OpenAPI/Swagger UI via springdoc -->
+    <dependency>
+      <groupId>org.springdoc</groupId>
+      <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
+      <version>2.6.0</version>
+    </dependency>
@@
+    <pluginManagement>
+      <plugins>
+        <plugin>
+          <groupId>org.apache.maven.plugins</groupId>
+          <artifactId>maven-surefire-plugin</artifactId>
+          <version>3.2.5</version>
+        </plugin>
+      </plugins>
+    </pluginManagement>
```

### SecurityConfig.java

```diff
+@Configuration
+@EnableWebSecurity
+public class SecurityConfig {
+
+    @Bean
+    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
+        http
+            .csrf(csrf -> csrf.disable())
+            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
+            .authorizeHttpRequests(auth -> auth
+                .requestMatchers(
+                    "/v3/api-docs/**",
+                    "/swagger-ui/**",
+                    "/swagger-ui.html",
+                    "/h2-console/**",
+                    "/healthz",
+                    "/actuator/health"
+                ).permitAll()
+                .anyRequest().authenticated()
+            )
+            .httpBasic(Customizer.withDefaults());
+
+        return http.build();
+    }
+}
```

### OpenApiConfig.java

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
+}
```

## Commands to Test

From the `BankApp-179898` project root:

```bash
# Build without running tests
./mvnw -DskipTests clean package

# Run on port 8989 under /bank-api
./mvnw -DskipTests spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=8989 --server.servlet.context-path=/bank-api"

# Run tests
./mvnw test

# Inspect dependency tree (first 200 lines)
./mvnw dependency:tree | sed -n '1,200p'
```

## Expected Output

- `./mvnw -DskipTests clean package` completes successfully with:
  - Java 21 reported in the build log (via `maven-compiler-plugin` release 21).
  - No errors related to JAXB or `javax.xml.bind`.
- `./mvnw -DskipTests spring-boot:run ...` starts the application, logging:
  - Spring Boot 3.2.10.
  - H2 console enabled.
  - Security filter chain initialized with HTTP Basic.
- `./mvnw test` completes with all tests passing, including endpoint and OpenAPI checks.

## Rollback Steps

To rollback the migration (for example, to investigate regressions):

1. Restore the previous `pom.xml` (Java 17 / older Spring Boot) from `git`:

   ```bash
   git checkout HEAD~1 -- pom.xml
   ```

2. Restore previous `SecurityConfig` and `OpenApiConfig` if they existed differently:

   ```bash
   git checkout HEAD~1 -- src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java
   git checkout HEAD~1 -- src/main/java/com/coding/exercise/bankapp/config/OpenApiConfig.java
   ```

3. Revert `application.yml` changes if necessary:

   ```bash
   git checkout HEAD~1 -- src/main/resources/application.yml
   ```

4. Rebuild and retest:

   ```bash
   ./mvnw -DskipTests clean package
   ./mvnw test
   ```

## Manual Review Notes

- Confirm that all target runtime environments (local, CI, preview, production) support Java 21.
- Validate that security exposure of:
  - `/bank-api/v3/api-docs`,
  - `/bank-api/swagger-ui/**`,
  - `/bank-api/h2-console/**`,
  - `/bank-api/healthz`,
  - `/bank-api/actuator/health`
  is acceptable for your deployment context.
- Confirm that no `javax.*` imports remain. The current codebase already uses `jakarta.*`, but you may re-run grep checks as described in `docs/task-04-javax-to-jakarta.md`.
