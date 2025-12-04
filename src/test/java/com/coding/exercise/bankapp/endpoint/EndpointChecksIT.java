package com.coding.exercise.bankapp.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration-style endpoint checks that probe a running BankApp instance over HTTP.
 * <p>
 * Assumptions:
 * <ul>
 *   <li>The application is already running on port 3001 with context-path {@code /bank-api}.</li>
 *   <li>No authentication headers are required for the probed endpoints (per SecurityConfig).</li>
 * </ul>
 * <p>
 * Base URL resolution:
 * <ul>
 *   <li>Defaults to {@code http://localhost:3001/bank-api}.</li>
 *   <li>Can be overridden via the {@code BASE_URL} environment variable (e.g.
 *       {@code BASE_URL=http://localhost:8080/bank-api}). Trailing slashes are trimmed.</li>
 * </ul>
 * <p>
 * This class is wired to run only via the {@code endpoint-checks} Maven profile, which
 * configures Surefire to include only {@code EndpointChecksIT.java}. It uses plain
 * HTTP calls to the running app, and does not start an embedded server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DisabledIfEnvironmentVariable(
        named = "DISABLE_ENDPOINT_CHECKS",
        matches = "true|TRUE|1"
)
class EndpointChecksIT {

    // Default remained as 3001 historically; override via BASE_URL if app runs on a different port (e.g., 3002).
    private static final String DEFAULT_BASE_URL = "http://localhost:3002/bank-api";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            // We handle redirects manually so that tests can assert on redirect behavior when needed.
            .followRedirects(Redirect.NEVER)
            .build();

    /**
     * Resolve the base URL for checks, preferring the BASE_URL environment variable.
     *
     * @return a base URL without a trailing slash (e.g. http://localhost:3001/bank-api)
     */
    private String getBaseUrl() {
        String env = System.getenv("BASE_URL");
        String base = (env == null || env.trim().isEmpty()) ? DEFAULT_BASE_URL : env.trim();
        // Trim any trailing slashes to avoid double-slash when concatenating paths.
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base;
    }

    /**
     * Perform a simple GET request against the provided path using the shared HttpClient.
     *
     * @param path         either an absolute URL or a path relative to the resolved base URL
     * @param acceptHeader optional Accept header value, or null/empty to omit
     * @return the HTTP response with body as a String
     */
    private HttpResponse<String> get(String path, String acceptHeader)
            throws IOException, InterruptedException {

        String base = getBaseUrl();
        String url;

        if (path.startsWith("http://") || path.startsWith("https://")) {
            url = path;
        } else {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            url = base + path;
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).GET();
        if (acceptHeader != null && !acceptHeader.isEmpty()) {
            builder.header("Accept", acceptHeader);
        }

        HttpRequest request = builder.build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * @param statusCode HTTP status code
     * @return true if the status code represents an HTTP redirect
     */
    private boolean isRedirect(int statusCode) {
        return statusCode == 301
                || statusCode == 302
                || statusCode == 303
                || statusCode == 307
                || statusCode == 308;
    }

    /**
     * Follow a single redirect from the provided response, asserting that a Location header exists.
     *
     * @param response     initial response (expected to be a redirect)
     * @param acceptHeader Accept header to apply when following the redirect
     * @return the response from the redirected URL (or the original response if not a redirect)
     */
    private HttpResponse<String> followRedirectOnce(HttpResponse<String> response, String acceptHeader)
            throws IOException, InterruptedException {

        if (!isRedirect(response.statusCode())) {
            return response;
        }

        HttpHeaders headers = response.headers();
        String location = headers.firstValue("Location").orElse("");
        assertFalse(location.isEmpty(), "Redirect location must not be empty");

        String redirectUrl;
        if (location.startsWith("http://") || location.startsWith("https://")) {
            redirectUrl = location;
        } else {
            // Relative redirect; resolve against the base URL.
            if (!location.startsWith("/")) {
                location = "/" + location;
            }
            redirectUrl = getBaseUrl() + location;
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(redirectUrl)).GET();
        if (acceptHeader != null && !acceptHeader.isEmpty()) {
            builder.header("Accept", acceptHeader);
        }

        HttpRequest followUp = builder.build();
        return httpClient.send(followUp, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void healthzReturnsOkJson() throws Exception {
        HttpResponse<String> response = get("/healthz", "application/json");

        assertEquals(200, response.statusCode(), "Expected HTTP 200 from /healthz");

        JsonNode json = OBJECT_MAPPER.readTree(response.body());
        assertEquals("ok", json.path("status").asText(),
                "Expected JSON body {\"status\":\"ok\"} from /healthz");
    }

    @Test
    void actuatorHealthReportsUpStatus() throws Exception {
        HttpResponse<String> response = get("/actuator/health", "application/json");

        assertEquals(200, response.statusCode(), "Expected HTTP 200 from /actuator/health");

        JsonNode json = OBJECT_MAPPER.readTree(response.body());
        assertTrue(json.has("status"), "Expected 'status' field in actuator health JSON");
        assertEquals("UP", json.path("status").asText(),
                "Expected actuator health status to be 'UP'");
    }

    @Test
    void openApiDocsContainOpenApiFieldOrSkipIfSwagger2() throws Exception {
        HttpResponse<String> response = get("/v3/api-docs", "application/json");

        assertEquals(200, response.statusCode(), "Expected HTTP 200 from /v3/api-docs");

        JsonNode json;
        try {
            json = OBJECT_MAPPER.readTree(response.body());
        } catch (IOException ex) {
            fail("Response from /v3/api-docs was not valid JSON: " + ex.getMessage());
            return;
        }

        if (json.has("openapi")) {
            assertFalse(json.path("openapi").asText().isEmpty(),
                    "OpenAPI document should contain a non-empty 'openapi' field");
        } else if (json.has("swagger")) {
            // Gracefully skip assertion if this is a Swagger 2.x (Springfox) document.
            Assumptions.assumeTrue(false,
                    "Detected Swagger 2.x (Springfox) JSON; skipping OpenAPI 3 'openapi' assertion.");
        } else {
            fail("OpenAPI JSON does not contain 'openapi' or 'swagger' field");
        }
    }

    @Test
    void swaggerUiIsReachable() throws Exception {
        HttpResponse<String> response = get("/swagger-ui.html", "text/html");

        if (isRedirect(response.statusCode())) {
            response = followRedirectOnce(response, "text/html");
        }

        assertEquals(200, response.statusCode(), "Expected HTTP 200 from Swagger UI endpoint");

        String bodyLower = response.body().toLowerCase();
        assertTrue(bodyLower.contains("swagger ui"),
                "Swagger UI HTML should contain 'Swagger UI'");
    }

    @Test
    void h2ConsoleIsReachable() throws Exception {
        HttpResponse<String> response = get("/h2-console", "text/html");

        if (isRedirect(response.statusCode())) {
            response = followRedirectOnce(response, "text/html");
        }

        assertEquals(200, response.statusCode(), "Expected HTTP 200 from H2 console endpoint");

        String bodyLower = response.body().toLowerCase();
        assertTrue(bodyLower.contains("h2 console"),
                "H2 console page should contain 'H2 Console'");
    }
}
