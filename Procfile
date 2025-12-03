# PUBLIC_INTERFACE
# Module-level Procfile for platforms starting in this directory.
# Runs Spring Boot via Maven Wrapper and binds to 0.0.0.0 at $PORT or 3001 by default.
# Sets context-path to /bank-api to match preview routing. No directory changes occur.
web: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"
