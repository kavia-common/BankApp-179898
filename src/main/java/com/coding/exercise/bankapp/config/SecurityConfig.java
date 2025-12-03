package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 6 configuration using SecurityFilterChain.
 *
 * Temporary global configuration:
 * - All endpoints are publicly accessible (no authentication).
 * - HTTP Basic is disabled.
 * - CSRF is disabled.
 * - Frame options are disabled to allow H2 console rendering.
 *
 * This applies regardless of the active Spring profile and is intended
 * only for temporary use during development/debugging.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // PUBLIC_INTERFACE
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*
         * Global temporary configuration: fully disable authentication and CSRF, allow frames for H2,
         * and permit all requests.
         */
        http
            // Disable CSRF for simplicity and to allow H2 console interactions
            .csrf(AbstractHttpConfigurer::disable)
            // Allow frames to enable H2 console rendering
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            // Permit everything without authentication
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            // Explicitly disable HTTP Basic
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
