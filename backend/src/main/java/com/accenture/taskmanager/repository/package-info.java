/**
 * Repository layer - Data access and persistence.
 *
 * This package will contain repository interfaces that:
 * - Extend Spring Data JPA interfaces (JpaRepository, CrudRepository)
 * - Define custom query methods using method naming conventions
 * - Use @Query annotations for complex queries
 * - Provide database abstraction
 *
 * Repositories handle all database operations.
 *
 * Example repository responsibilities:
 * - TaskRepository: CRUD operations, find by status, find by due date
 * - Custom queries (e.g., findOverdueTasks, findTasksByStatus)
 * - Database-specific operations
 */
package com.accenture.taskmanager.repository;
