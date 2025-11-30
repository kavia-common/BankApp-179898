# Banking Application using Java8, Spring Boot, Spring Security and H2 DB

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

# Common preview entrypoints:
./start
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
springfox-swagger2
springfox-swagger-ui
spring-boot-starter-test
spring-security-test

```

## Swagger

Please find the Rest API documentation in the below url

```
http://localhost:8989/bank-api/swagger-ui.html

```

## H2 In-Memory Database

Make sure to use jdbc:h2:mem:testdb as your jdbc url. If you intend to you use custom database name, please
define datasource properties in application.yml

```
http://localhost:8989/bank-api/h2-console/

```

## Testing the Bank APP Rest Api

1. Please use the Swagger url to perform CRUD operations. 

2. Browse to <project-root>/src/test/resources to find sample requests to add customer and accounts.


## Authors

* **Shyam Bathina**

