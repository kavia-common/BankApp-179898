# Runtime Verification Checklist for BankApp-179898

## 1. Runtime configuration and ports

The BankApp-179898 backend is a Spring Boot application with a servlet context path of `/bank-api`. In the application configuration (`application.yml`) the default server port is `3001`. In preview or local runs, invoke:

```
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"
```

This ensures the API is reachable at `http://0.0.0.0:3001/bank-api` (or via the external URL the platform provides).

The Spring Boot Actuator dependency exposes `/actuator/health` which becomes `/bank-api/actuator/health`. Springdoc provides `/v3/api-docs` and Swagger UI `/swagger-ui/index.html`, both under `/bank-api`.

## 2. Ensuring runtime uses port 3001

Local development:

- Scripts:
  - `./start` or `./start.sh` binds to `0.0.0.0:${PORT:-3001}` and context path `/bank-api`.
  - To be explicit: `PORT=3001 ./start`.
- Direct Maven Wrapper:
  - `./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"`

Preview manifest:

- Keep `PORT=3001` (or set as needed) and ensure the start command uses the canonical form above.

## 3. Endpoint verification checklist

Replace `<host>` / `<port>` appropriately (e.g., `localhost:3001`).

### 3.1 Health and diagnostics

1. `http://<host>:<port>/bank-api/actuator/health` → HTTP 200, JSON `{ "status": "UP" }`.
2. `http://<host>:<port>/bank-api/v3/api-docs` → HTTP 200 (OpenAPI 3 JSON).
3. `http://<host>:<port>/bank-api/swagger-ui/index.html` → HTTP 200 (Swagger UI).
4. `http://<host>:<port>/bank-api/h2-console` → HTTP 200/302→200 (H2 Console HTML).

### 3.2 Example application endpoints

- Customers: `GET http://<host>:<port>/bank-api/customers` (or `/customers/all` depending on controller mappings).
- Other endpoints per README and controller docs.

## 4. Security expectations

Current configuration (for development) permits access to:

- `/bank-api/v3/api-docs/**`
- `/bank-api/swagger-ui/**`
- `/bank-api/swagger-ui.html`
- `/bank-api/h2-console/**`
- `/bank-api/healthz`
- `/bank-api/actuator/health`

Business endpoints may be open in dev profile. For hardened environments, re-enable authentication as needed and update verification steps accordingly.
