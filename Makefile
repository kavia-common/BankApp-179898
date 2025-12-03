# Simple Makefile to wrap Maven Wrapper commands for CI/preview systems

# PUBLIC_INTERFACE
# build: Packages the application without running tests using the Maven Wrapper.
# Usage: make build
build:
	./mvnw -q -DskipTests clean package

# PUBLIC_INTERFACE
# build-shim: Build using the 'mvn' shim which proxies to ./mvnw (helps previews that call 'mvn').
# Usage: make build-shim
build-shim:
	./mvn -q -DskipTests clean package

# PUBLIC_INTERFACE
# run: Starts the Spring Boot app on port 3001 bound to 0.0.0.0 using the Maven Wrapper.
# Usage: make run
run:
	./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0"

# PUBLIC_INTERFACE
# run-sh: Same as run, but invokes the wrapper via 'sh' for environments without execute permission.
# Usage: make run-sh
run-sh:
	sh mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0"

# PUBLIC_INTERFACE
# run-ctx: Run with explicit context-path and port (helpful for verifying quoting of multiple Spring args).
# Usage: make run-ctx
run-ctx:
	./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001 --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"

# PUBLIC_INTERFACE
# start: Canonical start for previews that call 'make start'
# Usage: make start
start:
	./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=$${PORT:-3001} --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"
