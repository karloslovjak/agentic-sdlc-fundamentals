package com.accenture.taskmanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for OpenApiConfig to verify OpenAPI/Swagger configuration.
 *
 * Tests verify:
 * - OpenAPI bean is created and available
 * - API metadata is properly configured
 * - Server information is set correctly
 *
 * Note: This test provides coverage for the OpenApiConfig class.
 * Swagger UI functionality is tested through integration tests.
 */
@SpringBootTest
@ActiveProfiles("test")
class OpenApiConfigTest {

    @Autowired(required = false)
    private OpenAPI openAPI;

    /**
     * Test that OpenAPI bean is created and available.
     *
     * Verifies:
     * - @Configuration class is processed
     * - @Bean method is invoked
     * - OpenAPI is available for autowiring
     *
     * This ensures Swagger UI will have access to API documentation.
     */
    @Test
    void openAPIBeanShouldBeCreated() {
        assertThat(openAPI)
                .as("OpenAPI bean should be created and available in context")
                .isNotNull();
    }

    /**
     * Test that OpenAPI contains proper API information.
     *
     * Verifies the API metadata is configured correctly:
     * - Title matches application name
     * - Description is present
     * - Version is set
     */
    @Test
    void openAPIShouldContainApiInformation() {
        assertThat(openAPI.getInfo())
                .as("OpenAPI should have info section")
                .isNotNull();

        assertThat(openAPI.getInfo().getTitle())
                .as("API title should be set")
                .isEqualTo("Task Manager API");

        assertThat(openAPI.getInfo().getVersion())
                .as("API version should be set")
                .isEqualTo("1.0.0");

        assertThat(openAPI.getInfo().getDescription())
                .as("API description should be set")
                .isNotBlank();
    }

    /**
     * Test that OpenAPI contains server configuration.
     *
     * Verifies:
     * - At least one server is configured
     * - Server URL points to localhost (base URL only, no /api path)
     * - Controller @RequestMapping defines the /api prefix
     *
     * Note: Server URL should be base URL only. The /api prefix is defined
     * in TaskController's @RequestMapping("/api") annotation.
     */
    @Test
    void openAPIShouldContainServerConfiguration() {
        assertThat(openAPI.getServers())
                .as("OpenAPI should have at least one server configured")
                .isNotEmpty();

        String serverUrl = openAPI.getServers().get(0).getUrl();
        assertThat(serverUrl)
                .as("Server URL should point to localhost base URL")
                .contains("localhost")
                .contains("8080");

        assertThat(serverUrl)
                .as("Server URL should NOT include /api (that's defined in controller @RequestMapping)")
                .doesNotContain("/api");
    }

}
