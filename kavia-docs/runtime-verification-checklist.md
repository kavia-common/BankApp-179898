# Runtime Verification Checklist for BankApp-179898

## 1. Runtime configuration and ports

The BankApp-179898 backend is a Spring Boot application with a servlet context path of `/bank-api`. In the application configuration (`application.yml`) the default server port is `8989`. In the preview manifest, the container is configured with `PORT='3001'` and `HOST='0.0.0.0`, and the `startCommand` explicitly passes these values via `--server.port=<port>` and `--server.address=<host>`, along with the context path `--server.servlet.context-path=/bank-api`. This means that in the preview environment the API is expected to be reachable at `http://0.0.0.0:3001/bank-api` (or via the external URL the platform provides), while local runs can still use port `8989` by default.

The Spring Boot Actuator dependency is present, so the health endpoint is exposed at `/actuator/health`, which becomes `/bank-api/actuator/health` when the servlet context path is applied. The OpenAPI documentation is provided by `springdoc-openapi`, which exposes `/v3/api-docs` and a Swagger UI at `/swagger-ui/index.html`, again under the `/bank-api` context path.

## 2. Aligning runtime to port 8989 (optional)

If you want to align runtime behavior to the original `8989` port instead of `3001`, there are two common scenarios.

For local development, you can simply rely on the existing defaults, or override them explicitly:

- Run with the helper scripts:
  - `./start` or `./start.sh` will bind to `0.0.0.0:${PORT:-8989}`. If you do not set `PORT`, the application will run on `8989` with context path `/bank-api`.
  - To be explicit, run: `PORT=8989 ./start`.
- Run directly via Maven Wrapper:
  - `./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8989 --server.address=0.0.0.0 --server.servlet.context-path=/bank-api"`

For the preview manifest, the default is configured to use port `3001`. If you must change the preview to run on port `8989` instead, you should:

1. Update the `env.PORT` value in `project_manifest.yaml` from `'3001'` to `'8989'`.
2. Adjust any URL‑style environment variables to match, for example:
   - `BACKEND_URL: https://<host>:8989`
3. Ensure that any upstream services or frontends that call this backend are also updated to use port `8989`.

Because the manifest drives how the platform wires routing and health checks, changing the port in the manifest should always be done deliberately and in sync with other configuration.

## 3. startCommand placeholders and environment mapping

The manifest `startCommand` is defined as:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=<port> --server.address=<host> --server.servlet.context-path=/bank-api" \
  || sh mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=<port> --server.address=<host> --server.servlet.context-path=/bank-api"
```

The `<port>` and `<host>` placeholders are resolved by the runtime platform using the `PORT` and `HOST` environment variables defined in the top‑level `env` section of `project_manifest.yaml`. You generally should not edit these placeholders in the command. Instead, you control the runtime binding by changing the environment:

- `PORT` controls the effective value passed to `--server.port=<port>`.
- `HOST` controls the effective value passed to `--server.address=<host>`.

As long as these variable names match and the platform supports the placeholder substitution, the manifest and runtime remain aligned without changing the command string itself.

## 4. Endpoint verification checklist

This section lists the primary endpoints you should verify once the application is running. Replace `<host>` and `<port>` with the actual host and port for your environment (for example, `localhost:8989` for local runs or the preview URL on port `3001`).

### 4.1 Health and diagnostics

1. Confirm the health endpoint:
   - URL: `http://<host>:<port>/bank-api/actuator/health`
   - Expected: HTTP 200 and a JSON body containing at least `"status": "UP"`.

2. Confirm the OpenAPI JSON is served:
   - URL: `http://<host>:<port>/bank-api/v3/api-docs`
   - Expected: HTTP 200 with an OpenAPI 3 JSON document describing the API.

3. Confirm the Swagger UI is accessible:
   - URL: `http://<host>:<port>/bank-api/swagger-ui/index.html`
   - Expected: HTTP 200 with the Swagger UI page. This should be accessible without authentication per the security configuration.

4. Confirm the H2 console is reachable:
   - URL: `http://<host>:<port>/bank-api/h2-console`
   - Expected: HTTP 200 with the H2 database console UI. This endpoint is permitted without authentication, and frame options are disabled to allow rendering.

### 4.2 Customer API

5. Retrieve all customers:
   - Method: `GET`
   - URL: `http://<host>:<port>/bank-api/customers/all`
   - Expected: HTTP 200 and a JSON array (possibly empty at first).

6. Create a new customer:
   - Method: `POST`
   - URL: `http://<host>:<port>/bank-api/customers/add`
   - Body: JSON similar to the sample files under `src/test/resources/AddCustomer_*.json`.
   - Expected: HTTP 201 (or 200) and a confirmation message such as “New Customer created successfully.”

7. Fetch a specific customer:
   - Method: `GET`
   - URL: `http://<host>:<port>/bank-api/customers/{customerNumber}`
   - Expected: HTTP 200 with customer details when the customer exists; HTTP 404 or an appropriate error when not.

8. Update a customer:
   - Method: `PUT`
   - URL: `http://<host>:<port>/bank-api/customers/{customerNumber}`
   - Body: JSON payload with updated fields.
   - Expected: HTTP 200 with a success message when the customer exists; HTTP 404 when the customer does not exist.

9. Delete a customer:
   - Method: `DELETE`
   - URL: `http://<host>:<port>/bank-api/customers/{customerNumber}`
   - Expected: HTTP 200 with “Success: Customer deleted.” when deletion succeeds; HTTP 400 if the customer does not exist.

### 4.3 Account and transaction API

10. Create a new account for a customer:
    - Method: `POST`
    - URL: `http://<host>:<port>/bank-api/accounts/add/{customerNumber}`
    - Body: JSON similar to the sample files under `src/test/resources/addAccountInformation*_Customer*.json`.
    - Expected: HTTP 201 and “New Account created successfully.” when the customer exists.

11. Retrieve an account by account number:
    - Method: `GET`
    - URL: `http://<host>:<port>/bank-api/accounts/{accountNumber}`
    - Expected: HTTP 302/200 with account information when the account exists; HTTP 404 with “Account Number … not found.” when it does not.

12. Transfer funds between accounts for a customer:
    - Method: `PUT`
    - URL: `http://<host>:<port>/bank-api/accounts/transfer/{customerNumber}`
    - Body: JSON with `fromAccountNumber`, `toAccountNumber`, and `transferAmount`.
    - Expected:
      - HTTP 200 with a success message when both accounts exist and sufficient funds are available.
      - HTTP 400 with “Insufficient Funds.” when the source account balance is too low.
      - HTTP 404 when either the customer or one of the accounts does not exist.

13. Retrieve transactions for an account:
    - Method: `GET`
    - URL: `http://<host>:<port>/bank-api/accounts/transactions/{accountNumber}`
    - Expected: HTTP 200 with a JSON array of transactions for that account (empty if none).

## 5. Security expectations

The security configuration permits unauthenticated access to the OpenAPI, Swagger UI, and H2 console, while other endpoints require HTTP Basic authentication. By default, credentials are:

- Username: `bankapp`
- Password: `changeit`

For verification, you should confirm that:

1. `/bank-api/swagger-ui/index.html`, `/bank-api/v3/api-docs`, and `/bank-api/h2-console` are accessible without authentication.
2. Protected endpoints such as `/bank-api/customers/all` and `/bank-api/accounts/{accountNumber}` respond with HTTP 401 when called without credentials and with HTTP 200 when called using the correct `bankapp/changeit` credentials.

Once these checks pass on the chosen port and host, you can consider the runtime aligned with the manifest and the banking API verified.
