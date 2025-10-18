package com.accenture.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS (Cross-Origin Resource Sharing) configuration.
 *
 * Enables the React frontend (running on localhost:5173) to communicate
 * with the Spring Boot backend (running on localhost:8080).
 *
 * Architecture choice:
 * - Global CORS configuration via CorsFilter bean
 * - Allows all origins in development (should be restricted in production)
 * - Permits standard HTTP methods for REST API operations
 * - Allows credentials for authentication (future feature)
 */
@Configuration
public class CorsConfig {

    /**
     * Creates a CORS filter that allows cross-origin requests from the frontend.
     *
     * Development configuration:
     * - Allows all origins (*) - should be restricted to specific origins in
     * production
     * - Allows all headers - needed for content-type, authorization, etc.
     * - Allows credentials - needed for cookies/sessions
     * - Permits GET, POST, PUT, DELETE, OPTIONS methods
     *
     * @return configured CorsFilter bean
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Allow specific origins (localhost for dev, Render for production)
        // When allowCredentials=true, cannot use "*" - must be explicit
        config.addAllowedOriginPattern("http://localhost:*"); // Local development
        config.addAllowedOriginPattern("https://*.onrender.com"); // Render deployments
        config.addAllowedOriginPattern("https://task-manager-api-*.onrender.com"); // Specific app pattern

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
