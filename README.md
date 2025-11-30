# Banking Application using Java 21, Spring Boot 3, Spring Security 6 and H2 DB

RESTful API to simulate simple banking operations. 

## Requirements

*	CRUD operations for customers and accounts.
*	Support deposits and withdrawals on accounts.
*	Internal transfer support (i.e. a customer may transfer funds from one account to another).


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
- Build with Maven Wrapper: ./mvnw -q -DskipTests clean package
  (or: sh mvnw ... if execution permission is blocked)
- If using STS, Run As Spring Boot App
```

4. Build and Run without system Maven

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

Run on port 8989 bound to 0.0.0.0:
```
# Preferred: Maven Wrapper
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"

# Or via mvn shim (still uses wrapper):
./mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"

# Makefile helper:
make run

# Generic run script:
./run.sh

# Definitive preview entrypoint (supports CLEAN_PACKAGE=true to build first):
./start
# or:
./start.sh

# Platforms supporting Procfile (for reference):
web: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-8989} --server.address=0.0.0.0"
```

5. Default port for the api is 8989


### Prerequisites

* Java 8
* Spring Tool Suite 4 or similar IDE
* [Maven](https://maven.apache.org/) - Dependency Management

### Maven Dependencies

```
spring-boot-starter-actuator
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-web
spring-boot-devtools
h2 - Inmemory database
lombok - to reduce boilerplate code
springdoc-openapi-starter-webmvc-ui
spring-boot-starter-test
spring-security-test
```

## Swagger (OpenAPI)

The API documentation is provided by springdoc-openapi (Springfox removed). Access via:
- http://localhost:8989/bank-api/swagger-ui.html
- http://localhost:8989/bank-api/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8989/bank-api/v3/api-docs

Security configuration (Spring Security 6) permits these paths without authentication and uses HTTP Basic for protected endpoints. CSRF is disabled and frame options are turned off for the H2 console.

Allowed without authentication (Boot 3/Security 6):
- /bank-api/v3/api-docs/**
- /bank-api/swagger-ui/**
- /bank-api/swagger-ui.html
- /bank-api/h2-console/**
- /bank-api/actuator/** (if actuator is enabled)
Note: Security config uses requestMatchers for these paths and enables HTTP Basic for others.

## H2 In-Memory Database

The H2 console is enabled and reachable at:
- http://localhost:8989/bank-api/h2-console/

Security configuration permits the console and disables frame options for iframe rendering. Default in-memory JDBC URL (Boot 3 managed):
- jdbc:h2:mem:testdb

If using a custom database name, add datasource settings in application.yml, for example:
```
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
```

Notes on migration:
- javax.* packages migrated to jakarta.* (JPA).
- Spring Security migrated to SecurityFilterChain + requestMatchers.
- Springfox removed; springdoc-openapi-starter-webmvc-ui added.

## Testing the Bank APP Rest Api

1. Please use the Swagger url to perform CRUD operations. 

2. Browse to <project-root>/src/test/resources to find sample requests to add customer and accounts.


## Authors

* **Shyam Bathina**

