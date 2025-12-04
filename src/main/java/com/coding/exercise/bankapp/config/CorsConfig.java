package com.coding.exercise.bankapp.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Centralized CORS configuration.
 *
 * PUBLIC_INTERFACE
 * This configuration exposes a CorsConfigurationSource bean that is picked up by Spring Security.
 * Allowed origins are read from the "app.cors.allowed-origins" property (comma-separated), which
 * can be overridden via the ALLOWED_ORIGINS environment variable. Defaults align with the preview
 * environment and local development:
 *  - https://vscode-internal-20390-qa.qa01.cloud.kavia.ai:3002
 *  - http://localhost:3002
 *  - http://localhost:3000
 *  - http://localhost:4000
 *  - https://vscode-internal-20390-qa.qa01.cloud.kavia.ai:3000 (kept if used elsewhere)
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOriginsCsv;

    // PUBLIC_INTERFACE
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Parse comma-separated origins, trimming whitespace
        List<String> allowedOrigins = Arrays.stream(allowedOriginsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.OPTIONS.name()
        ));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        // Expose common headers for clients (optional)
        configuration.setExposedHeaders(Arrays.asList("Location", "Link"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply to all endpoints under the context path
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
