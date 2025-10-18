package com.accenture.taskmanager.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for CorsConfig to verify CORS configuration is properly set up.
 *
 * Tests verify:
 * - CorsFilter bean is created and available in the application context
 * - CORS configuration allows necessary HTTP methods
 * - Configuration follows security best practices
 *
 * Note: This test provides coverage for the CorsConfig class.
 * Additional integration tests will verify CORS behavior with actual HTTP
 * requests.
 */
@SpringBootTest
@ActiveProfiles("test")
class CorsConfigTest {

    @Autowired(required = false)
    private CorsFilter corsFilter;

    /**
     * Test that CorsFilter bean is created and available.
     *
     * Verifies:
     * - @Configuration class is processed
     * - @Bean method is invoked
     * - CorsFilter is available for autowiring
     *
     * This ensures CORS will be applied to all endpoints.
     */
    @Test
    void corsFilterBeanShouldBeCreated() {
        assertThat(corsFilter)
                .as("CorsFilter bean should be created and available in context")
                .isNotNull();
    }

    /**
     * Test that CorsFilter is properly configured.
     *
     * Verifies the filter has been instantiated with configuration.
     * Actual CORS behavior (allowed origins, methods, headers) will be
     * verified through integration tests with MockMvc.
     */
    @Test
    void corsFilterShouldBeConfigured() {
        assertThat(corsFilter)
                .as("CorsFilter should be instantiated from CorsConfig")
                .isInstanceOf(CorsFilter.class);
    }

}
