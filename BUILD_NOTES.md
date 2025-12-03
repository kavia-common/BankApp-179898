# Build and Run Notes

These notes explain startup from the repository root, avoiding any `cd BankApp-179898` requirements in preview configuration.

- From repository root (preferred):
  - Unix/macOS: `./start`
  - Or directly: `./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"`
  - Windows (PowerShell/CMD): `mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"`

- Procfile (for platforms that honor Procfile):
  - `web: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"`

The app binds to 0.0.0.0 on port 3001 (or $PORT if provided) and uses context path `/bank-api`.

Open endpoints:
- Swagger UI: http://localhost:3001/bank-api/swagger-ui/index.html
- OpenAPI JSON: http://localhost:3001/bank-api/v3/api-docs
- Health: http://localhost:3001/bank-api/actuator/health
- H2 Console: http://localhost:3001/bank-api/h2-console
