package com.coding.exercise.bankapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home controller that redirects the application root to Swagger UI.
 *
 * <p>With {@code server.servlet.context-path=/bank-api}, requests to:
 * <ul>
 *     <li>{@code /bank-api}</li>
 *     <li>{@code /bank-api/}</li>
 * </ul>
 * are handled as {@code "/"} within the servlet context and are redirected to
 * {@code /bank-api/swagger-ui/index.html}.
 *
 * <p>The redirect uses a relative target ({@code redirect:/swagger-ui/index.html})
 * so that the servlet context path is preserved automatically by Spring MVC.
 */
@Controller
public class HomeController {

    // PUBLIC_INTERFACE
    /**
     * Redirects the servlet context root ("/" within the context path) to the
     * Swagger UI index page.
     *
     * <p>Effective external behavior with {@code /bank-api} context path:
     * <ul>
     *     <li>GET {@code /bank-api}  → HTTP 302 to {@code /bank-api/swagger-ui/index.html}</li>
     *     <li>GET {@code /bank-api/} → HTTP 302 to {@code /bank-api/swagger-ui/index.html}</li>
     * </ul>
     *
     * @return a Spring MVC redirect view name pointing at the Swagger UI index
     */
    @GetMapping(path = {"/", ""})
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}
