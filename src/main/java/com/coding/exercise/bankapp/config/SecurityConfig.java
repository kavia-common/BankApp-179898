package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Spring Security 6 configuration using SecurityFilterChain.
 * Summary:
 * - Uses authorizeHttpRequests with requestMatchers (replaces deprecated antMatchers).
 * - Permits unauthenticated access to OpenAPI/Swagger/H2 console endpoints:
 *   "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**".
 * - Disables CSRF and disables frame options (required for H2 console).
 * - Enables HTTP Basic for protected endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // PUBLIC_INTERFACE
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF is disabled to simplify API interactions and allow H2 console to work with POSTs
            .csrf(csrf -> csrf.disable())
            // H2 console requires frames
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    // Root redirects and basic landing
                    "/",
                    "/index.html",
                    // OpenAPI/Swagger endpoints
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    // H2 console
                    "/h2-console/**",
                    // Actuator health/info
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll()
                .anyRequest().authenticated()
            )
            // Keep HTTP Basic for simplicity
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
