package com.accenture.taskmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration.
 *
 * Configures Swagger UI and OpenAPI documentation for the Task Manager API.
 *
 * Access points:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 * - OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
 *
 * Architecture choice:
 * - Uses SpringDoc OpenAPI for automatic API documentation
 * - API specification defined in YAML
 * (src/main/resources/openapi/task-manager-api.yml)
 * - Models and API interfaces generated at build time
 * - Controllers implement generated interfaces for type safety
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures OpenAPI documentation metadata.
     *
     * This supplements the YAML specification with runtime information
     * and provides metadata for the Swagger UI.
     *
     * @return configured OpenAPI bean
     */
    @Bean
    public OpenAPI taskManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("REST API for Task Manager - Accenture Agentic SDLC Fundamentals")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Task Manager Team")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Local development server")));
    }

}
