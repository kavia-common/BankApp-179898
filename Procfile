# Uses Maven Wrapper so no system 'mvn' is needed
# PUBLIC_INTERFACE
# Procfile entry for platforms that honor Procfile (e.g. preview systems)
# Runs Spring Boot via Maven Wrapper and binds to 0.0.0.0 at $PORT or 8989 by default.
web: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-8989} --server.address=0.0.0.0"
