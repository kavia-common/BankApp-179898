# Uses Maven Wrapper so no system 'mvn' is needed
# PUBLIC_INTERFACE
# Procfile entry for platforms that honor Procfile (e.g. preview systems)
# Runs Spring Boot via Maven Wrapper and binds to 0.0.0.0 at $PORT or 3001 by default.
# Ensure context-path is set to /bank-api to match preview routing.
# This Procfile is placed at repo root (BankApp-179898) so it works without any 'cd' path assumptions.
web: ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"
