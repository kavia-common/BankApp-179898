package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 6 configuration using {@link SecurityFilterChain}.
 *
 * This class defines two filter chains:
 * - A DEV-ONLY open chain (active when profile = 'dev') that makes ALL endpoints public.
 * - A secure chain (active when profile != 'dev') that keeps HTTP Basic for protected APIs
 *   and allows unauthenticated access only to docs, health, and H2 console.
 *
 * Note: Matchers are defined relative to the servlet context path (/bank-api).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // PUBLIC_INTERFACE
    @Bean
    @Profile("dev")
    public SecurityFilterChain securityFilterChainDev(HttpSecurity http) throws Exception {
        /*
         * DEV ONLY: All endpoints are public
         * - CSRF disabled for convenience with tools and H2 console
         * - Frame options disabled to allow H2 console rendering
         * - Any request is permitted without authentication
         *
         * IMPORTANT: This configuration is scoped to the 'dev' profile only.
         * Do not enable in production environments.
         */
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            // Intentionally do NOT configure httpBasic in dev

        return http.build();
    }

    // PUBLIC_INTERFACE
    @Bean
    @Profile("!dev")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*
         * Non-dev (default/production-like) security:
         * - Permit Swagger/OpenAPI, health, and H2 console
         * - Require authentication for all other endpoints
         * - Keep HTTP Basic for simplicity
         */
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    // OpenAPI/Swagger endpoints
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    // H2 console
                    "/h2-console/**",
                    // Health endpoints under context path (/bank-api/healthz, /bank-api/actuator/health)
                    "/healthz",
                    "/actuator/health"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
