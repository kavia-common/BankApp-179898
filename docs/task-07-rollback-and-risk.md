# Task 07 – Rollback Strategy, Risks, and Troubleshooting

## Purpose

This document provides a structured view of migration risks, rollback options, and a troubleshooting table mapping likely errors to root causes and suggested fixes.

## Files Changed

- `pom.xml`
- `src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java`
- `src/main/java/com/coding/exercise/bankapp/config/OpenApiConfig.java`
- `src/main/resources/application.yml`

## High-Level Risks

- **Runtime compatibility:** Environments without Java 21 will fail to compile or run.
- **Dependency conflicts:** Transitive dependencies may introduce `NoSuchMethodError` or `ClassNotFoundException`.
- **Security exposure:** Documentation and H2 console endpoints are public by default.
- **Operational differences:** Changes in Spring Boot 3.2.x behavior (e.g., Actuator defaults, error handling) may affect monitoring or clients.

## Troubleshooting Table

| Symptom / Error                                                                 | Likely Cause                                                                 | Suggested Fix                                                                 |
|---------------------------------------------------------------------------------|------------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| `Fatal error compiling: error: release version 21 not supported`               | Build environment is using JDK < 21 while `maven-compiler-plugin` targets 21 | Upgrade JDK to 21 or configure a Java 21 toolchain; verify `java -version`.  |
| `java.lang.NoClassDefFoundError: jakarta/xml/bind/JAXBException`               | Missing JAXB runtime on Java 21                                             | Ensure `jakarta.xml.bind-api` and `org.glassfish.jaxb:jaxb-runtime` are present; run `./mvnw dependency:tree`. |
| `404 Not Found` for `/bank-api/swagger-ui.html`                                | Path changed under springdoc-openapi; canonical UI now `/swagger-ui/index.html` | Use `/bank-api/swagger-ui/index.html`; keep `/swagger-ui.html` redirect; update docs and bookmarks. |
| `404 Not Found` for `/v3/api-docs`                                             | Wrong context path or app not running                                       | Confirm app is running, and context path is `/bank-api`; use `/bank-api/v3/api-docs`. |
| `403 Forbidden` for `/bank-api/h2-console`                                     | Security configuration blocking H2 console                                  | Confirm `SecurityConfig` permits `/h2-console/**`, disables CSRF, and disables frame options. |
| `401 Unauthorized` for business APIs even with credentials                     | HTTP Basic credentials mismatch or missing                                   | Ensure username/password match `spring.security.user.*` in `application.yml`; verify Authorization header. |
| `NoSuchMethodError` referencing Spring classes (e.g., `org.springframework...`) | Mixed or conflicting Spring Boot/Spring Framework versions                  | Inspect `./mvnw dependency:tree` for multiple versions; ensure all align with Boot 3.2.10; exclude conflicting transitive dependencies. |
| `404` for `/bank-api/actuator/health`                                          | Actuator endpoint not exposed or context path mismatch                       | Verify `spring-boot-starter-actuator` is present; confirm context path; use `/bank-api/actuator/health`. |
| `curl` to `/bank-api/v3/api-docs` returns 200 but body lacks `openapi` field   | A non-springdoc endpoint or proxy interference                               | Confirm `springdoc-openapi-starter-webmvc-ui` is on classpath; check reverse proxy configuration. |
| Tests fail only in CI with class version errors                                | CI JDK version differs from local (older JDK in CI)                          | Align CI to Java 21; confirm both `java -version` and `mvn -version` use JDK 21. |

## Rollback Strategy

If migration-related issues are severe and need immediate rollback:

1. **Identify the last known-good commit** before Java 21 / Boot 3.2 changes.

2. **Create a rollback branch** (optional but recommended):

   ```bash
   git checkout -b rollback/java17-known-good <good-commit-sha>
   ```

3. **Redeploy** from the rollback branch using established pipelines.

4. **Preserve the migration branch** (for investigation and fixes):

   - Do not discard the migration changes; continue to iterate until issues are resolved.
   - Use `git diff` between rollback and migration branches to focus on problematic areas.

For quick local rollback of specific files:

```bash
# Restore pom.xml
git checkout HEAD~1 -- pom.xml

# Restore SecurityConfig and OpenApiConfig
git checkout HEAD~1 -- src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java
git checkout HEAD~1 -- src/main/java/com/coding/exercise/bankapp/config/OpenApiConfig.java

# Restore application.yml
git checkout HEAD~1 -- src/main/resources/application.yml
```

Rebuild and retest after rollback:

```bash
./mvnw -DskipTests clean package
./mvnw test
```

## Manual Review Notes

- **Security review:**
  - Re-assess if `/bank-api/v3/api-docs`, `/bank-api/swagger-ui/**`, and `/bank-api/h2-console/**` should remain publicly accessible in production.
  - Consider whitelisting by IP or securing with HTTP Basic in production-like environments.
- **Infrastructure readiness:**
  - Confirm all target environments (local, CI, staging, production) support Java 21.
  - Ensure container images and PaaS runtimes are updated or replaced accordingly.
- **Monitoring and alerts:**
  - Validate monitoring dashboards and alert rules after migration, particularly around:
    - Health endpoints (`/bank-api/healthz`, `/bank-api/actuator/health`).
    - Error rates, latency, and 5xx responses following deployment.
- **Document follow-ups:**
  - Update `docs/changelog.md` with any post-migration fixes.
  - Record any manual edits (e.g., security tightening) in `docs/manual-review-list.md` for traceability.
