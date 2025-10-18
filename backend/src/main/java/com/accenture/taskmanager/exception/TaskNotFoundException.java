package com.accenture.taskmanager.exception;

/**
 * Exception thrown when a requested task is not found.
 *
 * This is a runtime exception that will be caught by the global exception
 * handler
 * and converted to a 404 NOT_FOUND HTTP response.
 *
 * Architecture:
 * - Unchecked exception for cleaner service method signatures
 * - Carries the task ID for error message context
 * - Handled by @ControllerAdvice global exception handler
 */
public class TaskNotFoundException extends RuntimeException {

    private final Long taskId;

    /**
     * Create exception with task ID.
     *
     * @param taskId the ID of the task that was not found
     */
    public TaskNotFoundException(Long taskId) {
        super("Task not found with id: " + taskId);
        this.taskId = taskId;
    }

    /**
     * Get the task ID that was not found.
     *
     * @return the task ID
     */
    public Long getTaskId() {
        return taskId;
    }

}
