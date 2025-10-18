/**
 * Model layer - Domain entities and enums.
 *
 * This package will contain JPA entity classes that:
 * - Map to database tables using @Entity
 * - Define relationships (@OneToMany, @ManyToOne, etc.)
 * - Use @Id for primary keys
 * - Include validation annotations for database constraints
 *
 * Models represent the core domain objects.
 *
 * Example model responsibilities:
 * - Task: Main entity with id, title, description, status, dueDate
 * - TaskStatus: Enum for TODO, IN_PROGRESS, DONE
 * - Database-level constraints and relationships
 */
package com.accenture.taskmanager.model;
