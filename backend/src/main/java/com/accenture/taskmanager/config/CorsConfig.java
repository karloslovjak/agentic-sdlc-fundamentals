package com.accenture.taskmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration.
 *
 * Enables frontend applications to communicate with the Spring Boot backend
 * from different origins (domains/ports).
 *
 * Architecture choice:
 * - Global CORS configuration via CorsFilter bean
 * - Configurable via environment variable CORS_ALLOWED_ORIGINS
 * - Supports multiple environments (dev, test, UAT, prod)
 * - Allows credentials for authentication (future feature)
 *
 * Configuration:
 * - Set CORS_ALLOWED_ORIGINS env var with comma-separated origins
 * - Example:
 * "http://localhost:3000,https://app.example.com,https://uat.example.com"
 * - Patterns supported: use * for wildcards (e.g., "https://*.onrender.com")
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:*,https://*.onrender.com}")
    private String allowedOrigins;

    /**
     * Creates a CORS filter that allows cross-origin requests from configured
     * origins.
     *
     * Configuration via application.yml or environment variable:
     * - cors.allowed-origins: comma-separated list of allowed origins/patterns
     * - Supports wildcards: http://localhost:*, https://*.onrender.com
     * - Default: localhost and Render domains
     *
     * @return configured CorsFilter bean
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Parse and add allowed origins from configuration
        // Supports both exact origins and patterns (with wildcards)
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        for (String origin : origins) {
            String trimmed = origin.trim();
            if (trimmed.contains("*")) {
                // Pattern with wildcard
                config.addAllowedOriginPattern(trimmed);
            } else {
                // Exact origin
                config.addAllowedOrigin(trimmed);
            }
        }

        // Allow all headers (Content-Type, Authorization, etc.)
        config.addAllowedHeader("*");

        // Allow standard HTTP methods for REST API
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");

        // Apply configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

}
