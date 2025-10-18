package com.accenture.taskmanager.model;

/**
 * Task status enumeration.
 *
 * Represents the current state of a task in its lifecycle.
 *
 * Lifecycle flow:
 * TODO → IN_PROGRESS → DONE
 *
 * Note: This enum is for the entity layer.
 * The API layer uses com.accenture.taskmanager.api.model.TaskStatus (generated
 * from OpenAPI spec).
 * MapStruct handles conversion between the two.
 */
public enum TaskStatus {

    /**
     * Task is created but not yet started.
     * Initial state for new tasks.
     */
    TODO,

    /**
     * Task is currently being worked on.
     * Indicates active progress.
     */
    IN_PROGRESS,

    /**
     * Task is completed.
     * Final state.
     */
    DONE

}
