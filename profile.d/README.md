This directory may contain platform-specific startup hooks. The application’s canonical startup must not cd into any subfolder.

Canonical start command (from repository root):
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"
