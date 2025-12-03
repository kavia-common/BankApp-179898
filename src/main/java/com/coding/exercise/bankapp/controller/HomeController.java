package com.coding.exercise.bankapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home routing controller.
 *
 * The static index.html under resources/static serves the root path ("/") automatically.
 * This controller provides a helper route to reach Swagger UI without conflicting with "/".
 */
@Controller
public class HomeController {

    // PUBLIC_INTERFACE
    @GetMapping("/home")
    /** This helper endpoint redirects to Swagger UI as a convenience. */
    public String homeFallback() {
        // Use redirect (not forward) and avoid including context-path; Spring adds it automatically.
        return "redirect:/swagger-ui/index.html";
    }
}
