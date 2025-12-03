# Banking Application using Java 17+ (Java 21-ready), Spring Boot 3, Spring Security 6, and H2 DB

RESTful API to simulate simple banking operations.

IMPORTANT (temporary): Authentication is fully disabled globally
- All endpoints under /bank-api/** are publicly accessible without credentials.
- HTTP Basic authentication is disabled.
- CSRF protection is disabled.
- Frame options are disabled so the H2 console renders in a browser.
This is intentional for a temporary period to simplify testing and debugging.

## Requirements

* CRUD operations for customers and accounts.
* Support deposits and withdrawals on accounts.
* Internal transfer support (i.e. a customer may transfer funds from one account to another).

## Getting Started

1. Checkout the project from GitHub

```
git clone https://github.com/sbathina/BankApp
```

2. Enable Lombok support on your IDE

Refer to the following link for instructions:

```
https://projectlombok.org/setup/eclipse
```

3. Open IDE of your choice and Import as existing maven project in your workspace

```
- Import existing maven project
- Build with Maven Wrapper (preferred): ./mvnw -q -DskipTests clean package
  (or: sh mvnw ... if execution permission is blocked)
- If using STS, Run As Spring Boot App
```

4. Build and Run without system Maven

Default runtime:
- Port: 3001
- Context path: /bank-api

Build:
```
# Preferred: Maven Wrapper
./mvnw -q -DskipTests clean package

# Alternatively use the mvn shim (proxies to ./mvnw; fixes previews that call 'mvn'):
./mvn -q -DskipTests clean package

# Using Makefile:
make build

# If execute permission is blocked:
sh mvnw -q -DskipTests clean package
```

Run on port 3001 bound to 0.0.0.0 and context-path /bank-api:
```
# Preferred: Maven Wrapper
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"

# Or via mvn shim (still uses wrapper):
./mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"

# Makefile helper:
make run-ctx

# Generic run script:
./run.sh

# Definitive preview entrypoint (supports CLEAN_PACKAGE=true to build first):
./start
# or:
./start.sh

# Platforms supporting Procfile (for reference):
web: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"
```

Tip: If you see Maven printing only usage/help and exiting with code 1 when starting, ensure the -Dspring-boot.run.arguments value is quoted as a single string. Do not pass unquoted application args directly to Maven. Correct forms:
```
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"
```
On Windows `cmd` (e.g., Procfile.windows), escape inner quotes as:
```
-Dspring-boot.run.arguments=^\\"--server.port=%PORT% --server.address=0.0.0.0 --server.servlet.context-path=/bank-api^\\"
```

Verification commands (ensure these succeed from the project root and require NO auth):
```
# 1) Build/install (prefer Maven Wrapper)
./mvnw -q -DskipTests clean install
# If execute permission is blocked:
sh mvnw -q -DskipTests clean install

# 2) Run with defaults from application.yml (port 3001, context-path /bank-api)
./mvnw spring-boot:run

# 3) Verify public endpoints (all should return 200 without credentials)
curl -i http://localhost:3001/bank-api/healthz
curl -i http://localhost:3001/bank-api/actuator/health
curl -i http://localhost:3001/bank-api/v3/api-docs
curl -i http://localhost:3001/bank-api/swagger-ui/index.html
curl -i http://localhost:3001/bank-api/h2-console

# 4) Verify business endpoints
curl -i http://localhost:3001/bank-api/customers
```

5. Default port for the API (and the default container) is 3001, as configured in `src/main/resources/application.yml`

### Prerequisites

* Java 17+ (OpenJDK/Temurin recommended; Java 21 recommended for the `java21` profile)
* Spring Tool Suite 4 or similar IDE
* [Maven](https://maven.apache.org/) - Dependency Management

## Swagger (OpenAPI)

The API documentation is provided by springdoc-openapi. Access via:
- http://localhost:3001/bank-api/ (automatically redirects to the Swagger UI)
- http://localhost:3001/bank-api/swagger-ui/index.html
- OpenAPI JSON: http://localhost:3001/bank-api/v3/api-docs

Note: http://localhost:3001/bank-api/swagger-ui.html also redirects to the Swagger UI, but `/swagger-ui/index.html` is the canonical path.
A lightweight MVC controller is registered to redirect requests for the servlet context root (`/bank-api` and `/bank-api/`)
to `/bank-api/swagger-ui/index.html`, preserving the configured `server.servlet.context-path`.

Current security note: All endpoints, including Swagger UI and the OpenAPI JSON, are public. HTTP Basic is disabled globally for now. CSRF is disabled and frame options are turned off for the H2 console.

## H2 In-Memory Database

The H2 console is enabled and reachable at:
- http://localhost:3001/bank-api/h2-console/

Security configuration permits the console and disables frame options for iframe rendering. Default in-memory JDBC URL (Boot 3 managed):
- jdbc:h2:mem:testdb
