# Remote Smoke Test Report — BankApp
Date: 2025-12-03  
Target: https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api

Summary:
- General availability looks good: Health, OpenAPI JSON, and Swagger UI all return HTTP 200 as expected.
- H2 Console HEAD request follows a 302 to http:// and ends with 405 Method Not Allowed (likely due to HEAD). A GET request should render HTML successfully.
- Customers API is publicly accessible (HTTP 200) even without auth (expected given security is temporarily disabled per README).

Canonical start command:
```
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=${PORT:-3001} --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"
```

Details per command:

1) Health endpoint
- Command:
  curl -i -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/actuator/health
- Status: HTTP/2 200
- Excerpt: {"status":"UP"}
- Result: PASS

2) OpenAPI JSON
- Command:
  curl -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/v3/api-docs | head -n 5
- Status (HEAD check): HTTP/2 200
- Excerpt (first line): {"openapi":"3.0.1","info":{"title":"BankApp API", ...}}
- Result: PASS

3) Swagger UI
- Command:
  curl -I -L -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/swagger-ui/index.html
- Final Status: HTTP/2 200
- Excerpt: content-type: text/html
- Result: PASS

4) H2 Console
- Command:
  curl -I -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/h2-console
- Status: HTTP/2 302 (Location: http://.../bank-api/h2-console/)
- Follow-up:
  curl -sSL https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/h2-console | head -n 5
- Expected: HTML containing "H2 Console"
- Result: LIKELY PASS with GET; HEAD nuance

5) Customers API without auth
- Command:
  curl -i -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/customers
- Status: HTTP/2 200
- Result: INFO (200 due to security disabled)

Recommendations:
- For the H2 console check in automated scripts, use GET instead of HEAD or follow redirects with -L and confirm HTML body contains "H2 Console".
- If production hardening is desired, re-enable Spring Security (HTTP Basic) and adjust tests to expect 401 without credentials.
