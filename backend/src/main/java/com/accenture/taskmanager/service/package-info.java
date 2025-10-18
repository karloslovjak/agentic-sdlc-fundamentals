/**
 * Service layer - Business logic and orchestration.
 *
 * This package will contain @Service classes that:
 * - Implement business logic and rules
 * - Orchestrate operations across multiple repositories
 * - Perform validation beyond simple field checks
 * - Handle transactions (implicit via @Transactional)
 * - Transform between domain models and DTOs
 *
 * Services are where the application's core functionality resides.
 *
 * Example service responsibilities:
 * - TaskService: Task business logic, status transitions, due date validation
 * - Complex queries combining multiple repositories
 * - Business rule enforcement
 */
package com.accenture.taskmanager.service;
