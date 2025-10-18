package com.accenture.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Task Manager application.
 *
 * @SpringBootApplication combines:
 *                        - @Configuration: Indicates this is a Spring
 *                        configuration class
 *                        - @EnableAutoConfiguration: Enables Spring Boot's
 *                        auto-configuration mechanism
 *                        - @ComponentScan: Scans for components in
 *                        com.accenture.taskmanager package
 *
 *                        Architecture choices:
 *                        - Package-by-layer structure (controller, service,
 *                        repository, model, dto, config)
 *                        - Follows Spring Boot best practices for clean
 *                        separation of concerns
 *                        - Enables component scanning for automatic bean
 *                        discovery
 */
@SpringBootApplication
public class TaskManagerApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }

}
