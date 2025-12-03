package com.coding.exercise.bankapp.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * Spring Security configuration for BankApp.
 *
 * Summary of policy:
 * - Permit access to Swagger UI, OpenAPI docs, Actuator health, H2 console, and static resources.
 * - Disable CSRF (API-centric usage) and frame options (required for H2 console rendering).
 * - Disable form login and HTTP Basic to avoid login prompts.
 *
 * Important: Patterns use MVC-based request matching tied to Spring's PathPattern syntax,
 * avoiding invalid ant-style patterns such as "/**/*.css" or "/**/swagger-ui/**".
 * We rely on PathRequest for static assets and H2 console matching to ensure correctness.
 *
 * Context path:
 * - The application runs under server.servlet.context-path=/bank-api.
 * - MVC matchers here are written WITHOUT "/bank-api" since the context-path is applied automatically.
 *   For example, external "/bank-api/swagger-ui" is matched with mvc.pattern("/swagger-ui").
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // PUBLIC_INTERFACE
    /**
     * Configure the Spring Security filter chain.
     *
     * @param http           the HTTP security builder
     * @param introspector   Spring MVC HandlerMappingIntrospector used by MvcRequestMatcher
     * @return the configured SecurityFilterChain
     * @throws Exception on configuration errors
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        // Use MVC matchers so patterns align with Spring MVC PathPattern rules.
        // Do NOT use ant-style patterns like "/**/*.css" which are invalid with PathPattern.
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

        http
            // Allow frames to enable H2 console UI
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            // Disable CSRF for API style interactions and H2 console convenience
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Static resources at common locations (e.g., /css/**, /js/**, /images/**)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // H2 console under /h2-console/**
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                // OpenAPI / Swagger UI (external paths include /bank-api prefix due to context-path)
                .requestMatchers(
                    mvc.pattern("/v3/api-docs"),
                    mvc.pattern("/v3/api-docs/**"),
                    mvc.pattern("/swagger-ui"),
                    mvc.pattern("/swagger-ui/**"),
                    mvc.pattern("/swagger-ui.html")
                ).permitAll()
                // Actuator health endpoint (external: /bank-api/actuator/health)
                .requestMatchers(mvc.pattern("/actuator/health")).permitAll()
                // Root and index page (if served)
                .requestMatchers(
                    mvc.pattern("/"),
                    mvc.pattern("/index.html")
                ).permitAll()
                // Current policy: allow all other requests (aligns with tests and dev convenience)
                .anyRequest().permitAll()
            )
            // Avoid any auth prompts
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
