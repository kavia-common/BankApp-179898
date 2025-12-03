package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 6 configuration using SecurityFilterChain.
 *
 * Public resources:
 * - "/" and static assets for the landing page
 * - Swagger UI and OpenAPI docs
 * - H2 console (for development)
 * - Actuator health
 *
 * Note:
 * - We allow unauthenticated access to the above, and keep API endpoints authenticated by default.
 * - Frames are allowed to enable H2 console rendering.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // PUBLIC_INTERFACE
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Allow frames for H2 console
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            // If CSRF protection is desired, it can be enabled with ignoringRequestMatchers("/h2-console/**")
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .authorizeHttpRequests(auth -> auth
                // Public: landing page and static assets
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/favicon.ico",
                    "/assets/**",
                    "/static/**",
                    "/webjars/**"
                ).permitAll()
                // Public: API docs and Swagger UI
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/bank-api/swagger-ui/**"
                ).permitAll()
                // Public: H2 console
                .requestMatchers("/h2-console/**").permitAll()
                // Public: actuator health
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                // Secure API (within servlet context path) by default
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                .requestMatchers("/api/**").authenticated()
                // Any other requests require authentication
                .anyRequest().authenticated()
            )
            // Enable HTTP Basic for simplicity
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
