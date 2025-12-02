package com.coding.exercise.bankapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * PUBLIC_INTERFACE
 * RootController
 *
 * Purpose:
 * - Provides a landing route for the application root within the configured context-path.
 * - Redirects "/" to the Swagger UI so the preview (which typically opens the base URL) shows a usable page.
 *
 * Routes:
 * - GET "/" -> HTTP 302 redirect to "/swagger-ui/index.html"
 *
 * Notes:
 * - SecurityConfig explicitly permits "/" so this redirect is reachable without authentication.
 * - The redirect is relative to the application's context-path (configured in application.yml as /bank-api).
 */
@Controller
public class RootController {

    // PUBLIC_INTERFACE
    @GetMapping({"/", ""})
    public String index() {
        // Relative redirect under the same context path to springdoc Swagger UI
        return "redirect:/swagger-ui/index.html";
    }
}
