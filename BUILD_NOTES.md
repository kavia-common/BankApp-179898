# Build and Run Notes

These notes explain startup from both the repository root and from inside the `BankApp-179898` directory, without relying on `cd BankApp-179898` in any preview configuration.

- From repository root:
  - Unix/macOS: `./start`
  - Windows (PowerShell/CMD): `pushd BankApp-179898 && mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"`

- From inside BankApp-179898 (preferred in previews):
  - Procfile: `web: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"`
  - Shell: `./start` or `./start.sh` or `./run.sh`

The app binds to 0.0.0.0 on port 3001 (or $PORT if provided) and uses context path `/bank-api`.

Open endpoints:
- Swagger UI: http://localhost:3001/bank-api/swagger-ui/index.html
- OpenAPI JSON: http://localhost:3001/bank-api/v3/api-docs
- Health: http://localhost:3001/bank-api/actuator/health
- H2 Console: http://localhost:3001/bank-api/h2-console
