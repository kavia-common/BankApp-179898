package com.coding.exercise.bankapp.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple health check controller.
 * <p>
 * Exposes a lightweight {@code GET /healthz} endpoint which, when combined with the
 * configured servlet context path {@code /bank-api}, is reachable externally at
 * {@code /bank-api/healthz}.
 * <p>
 * The endpoint returns HTTP 200 with a minimal JSON body {@code {"status":"ok"}} and is
 * configured to be publicly accessible (no authentication required) via Spring Security.
 */
@RestController
public class HealthController {

    // PUBLIC_INTERFACE
    /**
     * Health check endpoint used by load balancers and uptime monitors.
     * <p>
     * Effective external URL: {@code /bank-api/healthz}.
     *
     * @return an immutable JSON body {@code {"status":"ok"}} indicating the application is healthy
     */
    @GetMapping(path = "/healthz", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getHealth() {
        return Collections.singletonMap("status", "ok");
    }
}
