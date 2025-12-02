package com.coding.exercise.bankapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Home and health endpoints for readiness/liveness checks.
 * Mounted at application root (context-path is forced to "/" by start scripts).
 */
@RestController
public class HomeController {

    // PUBLIC_INTERFACE
    /**
     * Root endpoint primarily for preview health checks.
     * Returns a simple "OK" string with HTTP 200.
     *
     * @return 200 OK with body "OK"
     */
    @GetMapping(path = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("OK");
    }

    // PUBLIC_INTERFACE
    /**
     * Simple health endpoint indicating service is UP.
     *
     * @return 200 OK with JSON body {"status": "UP"}
     */
    @GetMapping(path = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> body = new HashMap<>();
        body.put("status", "UP");
        return ResponseEntity.ok(body);
    }

    // PUBLIC_INTERFACE
    /**
     * Simple readiness endpoint indicating service is ready to serve traffic.
     *
     * @return 200 OK with JSON body {"status": "READY"}
     */
    @GetMapping(path = "/ready", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> ready() {
        Map<String, String> body = new HashMap<>();
        body.put("status", "READY");
        return ResponseEntity.ok(body);
    }
}
