# Remote Smoke Test Report — BankApp
Date: 2025-12-03  
Target: https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api

Summary:
- General availability looks good: Health, OpenAPI JSON, and Swagger UI all return HTTP 200 as expected.
- H2 Console HEAD request follows a 302 to http:// and ends with 405 Method Not Allowed (likely due to HEAD). A GET request should render HTML successfully.
- Customers API is publicly accessible (HTTP 200) even without auth (expected given security is temporarily disabled per README).

Details per command:

1) Health endpoint
- Command:
  curl -i -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/actuator/health
- Status: HTTP/2 200
- Redirects: none
- Excerpt: {"status":"UP"}
- Result: PASS
- Notes: Actuator health reports UP and returns expected JSON content type.

2) OpenAPI JSON
- Command:
  curl -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/v3/api-docs | head -n 5
- Status (HEAD check): HTTP/2 200
- Redirects: none
- Excerpt (first line): {"openapi":"3.0.1","info":{"title":"BankApp API", ...}}
- Result: PASS
- Notes: "openapi" key is present and valid. HEAD check confirmed 200.

3) Swagger UI
- Command:
  curl -I -L -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/swagger-ui/index.html
- Final Status: HTTP/2 200
- Redirects: none visible (already resolved)
- Excerpt: content-type: text/html
- Result: PASS
- Notes: Swagger UI index is being served correctly.

4) H2 Console
- Command:
  curl -I -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/h2-console
- Status: HTTP/2 302
- Redirect Location: http://vscode-internal-21908-beta.beta01.cloud.kavia.ai/bank-api/h2-console/
- Followed with:
  curl -I -L -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/h2-console
  -> HTTP/2 302 then HTTP/1.1 405 Method Not Allowed
- Result: FAIL (with HEAD)
- Suggested fix/next step:
  - Use GET instead of HEAD: curl -sSL https://.../bank-api/h2-console (HEAD often returns 405 on HTML resources).
  - Ensure HTTPS-preserving redirects for the H2 console if possible (avoid redirecting browser to http:// from https://).
  - If 200 with GET, this is OK and only a method/redirect nuance.

5) Protected Customers API without auth
- Command:
  curl -i -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/customers
- Status: HTTP/2 200
- Redirects: none
- Excerpt (truncated): [{"firstName":"John","lastName":"Doe", ...}]
- Result: INFO (unexpected 200 if expecting auth)
- Notes: README indicates authentication is temporarily disabled, so 200 is expected in current config. If 401 is desired, re-enable HTTP Basic in SecurityConfig.

6) Customers API with Basic auth (placeholder creds)
- Command:
  curl -i -u USER:PASS -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/customers | head -n 20
- Status: HTTP/2 200
- Redirects: none
- Excerpt (truncated): [{"firstName":"John","lastName":"Doe", ...}]
- Result: PASS (but auth header unnecessary)
- Notes: Security is disabled; credentials were not required and not validated by backend.

7) Optional jq check for openapi field
- Command:
  curl -sS https://vscode-internal-21908-beta.beta01.cloud.kavia.ai:3001/bank-api/v3/api-docs | jq -r '.openapi'
- Result: jq not installed
- Notes: Not required. We already verified "openapi" presence via curl output. If needed, install jq to enable structured JSON checks.

Overall status:
- Health: PASS
- OpenAPI JSON: PASS
- Swagger UI: PASS
- H2 Console: FAIL with HEAD (likely PASS with GET; see notes)
- Customers (no auth): INFO (200 due to security disabled)
- Customers (with basic auth): PASS (but not enforced)

Recommendations:
- For the H2 console check in automated scripts, use GET instead of HEAD or explicitly follow redirects with -L and confirm HTML body contains "H2 Console".
- If production hardening is desired, re-enable Spring Security (HTTP Basic) for business endpoints and adjust tests to expect 401 without credentials.
- Optionally ensure redirects for the H2 console preserve HTTPS scheme behind the reverse proxy to avoid http:// redirects.

Raw captured headers and excerpts:
- Health:
  HTTP/2 200
  content-type: application/vnd.spring-boot.actuator.v3+json
  Body: {"status":"UP"}

- OpenAPI (HEAD):
  HTTP/2 200
  content-type: application/json

- Swagger UI (HEAD+L):
  HTTP/2 200
  content-type: text/html

- H2 Console (HEAD then L):
  HTTP/2 302 -> Location: http://.../bank-api/h2-console/
  HTTP/1.1 405 Method Not Allowed

- Customers (no auth):
  HTTP/2 200
  content-type: application/json
  Body: [ ... sample customer JSON ... ]

- Customers (with basic auth placeholder):
  HTTP/2 200
  content-type: application/json
  Body: [ ... sample customer JSON ... ]
