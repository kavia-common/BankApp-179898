package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration to allow anonymous access across the application.
 *
 * Key points:
 * - All endpoints are accessible without authentication (permitAll).
 * - HTTP Basic and form login are explicitly disabled to prevent auth prompts.
 * - CSRF is disabled to allow non-browser/API clients to POST/PUT/DELETE without tokens.
 * - Frame options are disabled to allow the H2 console to render in an iframe.
 * - CORS is enabled and driven by the CorsConfigurationSource bean (see CorsConfig).
 *
 * With server.servlet.context-path=/bank-api, routes will be served under /bank-api/**,
 * and this configuration applies to all of them.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // PUBLIC_INTERFACE
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Allow frames for H2 console rendering
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            // Enable CORS with the CorsConfigurationSource bean (Spring Security 6+)
            .cors(Customizer.withDefaults())
            // Disable CSRF (suitable for stateless APIs and for allowing H2 console interaction)
            .csrf(AbstractHttpConfigurer::disable)
            // Permit all requests without authentication
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            // Explicitly disable basic and form-based authentication to avoid any login prompts
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
