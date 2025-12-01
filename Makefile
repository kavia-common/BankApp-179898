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
# run: Starts the Spring Boot app on port 8989 bound to 0.0.0.0 using the Maven Wrapper.
# Usage: make run
run:
	./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"

# PUBLIC_INTERFACE
# run-sh: Same as run, but invokes the wrapper via 'sh' for environments without execute permission.
# Usage: make run-sh
run-sh:
	sh mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0"

# PUBLIC_INTERFACE
# build-sh: Same as build, but invokes the wrapper via 'sh' for environments without execute permission.
# Usage: make build-sh
build-sh:
	sh mvnw -q -DskipTests clean package
