# Project Manifest Updates: BankApp-179898

## Overview
This document summarizes the key updates applied to project_manifest.yaml for BankApp-179898 and outlines the practical actions required to align the Spring Boot application and its runtime with the manifest. It focuses on ports, context path, CORS, credentials exposure, routes, commands, and observability flags. The intent is to ensure developers and DevOps can run and verify the application consistently across local and preview environments.

Sources:
- project_manifest.yaml
- src/main/resources/application.yml
- src/main/java/com/coding/exercise/bankapp/config/CorsConfig.java
- src/main/java/com/coding/exercise/bankapp/config/SecurityConfig.java
- src/main/java/com/coding/exercise/bankapp/config/OpenApiConfig.java
- Procfile
- start / start.sh
- README.md

## Key Changes
### Port and Binding
- Before (effective defaults):
  - application.yml set server.port: 3002 and address: 0.0.0.0 for local runs (README mentions both 3001 and 3002 in different places).
- After (manifest-aligned runtime):
  - PORT: 3001 (env and container port)
  - HOST: 0.0.0.0
  - Start commands bind to 0.0.0.0 on $PORT.
- Intent: Standardize preview/default container port to 3001 while still allowing overrides via $PORT for platforms.

### Context Path
- Before:
  - server.servlet.context-path=/bank-api already in application.yml.
- After:
  - Manifest startCommand enforces --server.servlet.context-path=/bank-api to ensure consistency with routing and OpenAPI config.
- Intent: Guarantee that all routes remain under /bank-api across environments.

### CORS
- Before:
  - CorsConfig reads app.cors.allowed-origins from application.yml; override via ALLOWED_ORIGINS supported.
  - application.yml default included vscode internal hosts and localhost ports.
- After:
  - Manifest declares ALLOWED_ORIGINS explicitly and adds ALLOWED_HEADERS, ALLOWED_METHODS, CORS_MAX_AGE for clarity.
- Intent: Ensure predictable cross-origin behavior in previews and local dev, with a single env entry point to override origins.

### Credentials and Security
- Before:
  - SecurityConfig permits all endpoints, disables CSRF, HTTP Basic, and frame options for H2 (public for now).
- After:
  - No new credentials added to the manifest; settings remain open by design for current phase.
- Intent: Keep current unauthenticated posture for testing. Future hardening can be done without changing manifest structure.

### Routes
- Before:
  - Endpoints available under /bank-api/** based on context path and controllers.
- After:
  - Manifest explicitly enumerates key routes (health, actuator, OpenAPI, Swagger, H2, resource roots).
- Intent: Assist proxies/preview routers and provide a clear contract for what should be reachable.

### Commands
- Before:
  - README provided several run options; Procfile existed; start scripts present.
- After:
  - Manifest’s buildCommand, startCommand, testCommand, installCommand standardized on Maven Wrapper with robust fallbacks (./mvnw then sh mvnw).
  - startCommand includes explicit server.port/address/context-path arguments.
- Intent: Ensure reliable builds/starts on environments that may not honor execute bits or may prefer sh.

### Observability/Operational Flags
- New or clarified envs:
  - TRUST_PROXY=true for proxy-aware operation.
  - LOG_LEVEL=info as a sensible default.
  - HEALTHCHECK_PATH=/bank-api/actuator/health.
  - FEATURE_FLAGS and EXPERIMENTS_ENABLED placeholders for future toggles.
  - BACKEND_URL, FRONTEND_URL, SITE_URL documented for cross-service references.
- Intent: Make base operational behavior explicit and tunable via environment.

## Rationale and Impact
- Unification around port 3001 reduces confusion between README and default YAML values and aligns with platform previews. application.yml still specifies 3002 for local convenience; start scripts and manifest enforce $PORT value, so runtime will honor 3001 under platform-managed environments.
- Enforcing context-path in start commands ensures routing and documentation (OpenAPI server url=/bank-api) remain consistent, preventing broken links and CORS mismatches.
- Centralizing CORS via env simplifies changing allowed origins for previews without modifying code; aligns with CorsConfig’s environment override mechanism.
- Explicit routes in the manifest help reverse proxies and automated checks discover the correct endpoints to expose.
- Healthcheck path and logging level can be consumed by orchestrators to monitor and triage issues consistently.

## Action Items for Codebase Alignment
1. Context Path
   - Keep server.servlet.context-path=/bank-api in application.yml (already present).
   - Ensure any hard-coded links or redirects respect the context path (OpenApiConfig already sets server url=/bank-api).

2. Ports and Binding
   - For local runs via mvnw spring-boot:run without manifest, rely on application.yml (3002). For preview/container runs, rely on manifest startCommand passing --server.port=$PORT and HOST=0.0.0.0.
   - If you need local alignment to 3001, either:
     - run with -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0", or
     - temporarily update application.yml server.port to 3001 for your local branch.

3. CORS
   - Use ALLOWED_ORIGINS env to align with the manifest (comma-separated). CorsConfig honors app.cors.allowed-origins which is sourced from ${ALLOWED_ORIGINS:...} fallback in application.yml.
   - If you need custom headers or methods beyond defaults, extend CorsConfig to read ALLOWED_HEADERS, ALLOWED_METHODS from env (optional; currently allowedHeaders="*" and methods set comprehensively).

4. Security posture (temporary)
   - SecurityConfig currently permits all; if introducing authentication later, update both:
     - SecurityConfig to enforce credentials selectively.
     - Manifest and docs to reflect any new protected routes and health endpoint accessibility.

5. Routes
   - Validate that controllers continue to expose:
     - /bank-api/healthz
     - /bank-api/actuator/health
     - /bank-api/v3/api-docs
     - /bank-api/swagger-ui/index.html
     - /bank-api/h2-console
     - /bank-api/customers, /bank-api/accounts
   - Update kavia-scripts/check_endpoints.sh or tests if routes are changed.

6. Commands
   - Prefer using the start or start.sh scripts in preview contexts.
   - Maintain Procfile with context-path set to /bank-api for platforms that honor Procfile.

## Verification Checklist
- Build
  - ./mvnw -q -DskipTests clean package succeeds.
- Start (preview-aligned)
  - Run with: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"
- Health
  - GET http://localhost:3001/bank-api/actuator/health returns 200 with status UP.
  - GET http://localhost:3001/bank-api/healthz returns 200 with {"status":"ok"}.
- OpenAPI and UI
  - GET http://localhost:3001/bank-api/v3/api-docs returns 200; contains "openapi".
  - GET http://localhost:3001/bank-api/swagger-ui/index.html serves the UI.
- H2 Console
  - GET http://localhost:3001/bank-api/h2-console loads (frame options disabled).
- Business Endpoints
  - GET http://localhost:3001/bank-api/customers returns 200 (no auth required).
- CORS
  - From an allowed origin (e.g., http://localhost:3000), a browser OPTIONS preflight to /bank-api/customers succeeds with Access-Control-Allow-Origin and proper methods/headers.
- Port Consistency
  - In preview/container: app listens on 0.0.0.0:$PORT (3001 default).
  - Local: either 3002 (application.yml) or overridden to 3001 via run arguments.

## Notes
- Current README shows examples on port 3002 and also refers to 3001 as the default container port. The manifest standardizes preview/container usage at 3001, while local can keep 3002 unless you choose to align locally via the arguments.
- No credentials are exposed or required in this phase; if you re-enable auth later, update the manifest and this document accordingly.
