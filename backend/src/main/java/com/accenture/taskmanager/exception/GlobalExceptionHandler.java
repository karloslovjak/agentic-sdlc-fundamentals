package com.accenture.taskmanager.exception;

import com.accenture.taskmanager.api.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers.
 *
 * Catches exceptions thrown by controllers and converts them to appropriate
 * HTTP responses.
 * Uses @RestControllerAdvice to apply to all @RestController classes.
 *
 * Architecture:
 * - Centralizes error handling logic
 * - Returns consistent ErrorResponse format from OpenAPI spec
 * - Logs errors for debugging and monitoring
 * - Maps exceptions to appropriate HTTP status codes
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle TaskNotFoundException.
     *
     * Returns 404 NOT_FOUND with error details.
     *
     * @param ex the exception
     * @return 404 response with error message
     */
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex) {
        log.warn("Task not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code("NOT_FOUND")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle validation errors from @Valid annotations.
     *
     * Triggered when request body fails Bean Validation.
     * Returns 400 BAD_REQUEST with field-specific error details.
     *
     * @param ex the validation exception
     * @return 400 response with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        // Get the first field error for simplicity
        FieldError fieldError = ex.getBindingResult().getFieldError();

        String message = fieldError != null
                ? fieldError.getDefaultMessage()
                : "Validation failed";
        String field = fieldError != null
                ? fieldError.getField()
                : null;

        log.warn("Validation error: {} on field: {}", message, field);

        ErrorResponse error = ErrorResponse.builder()
                .message(message)
                .field(field)
                .code("VALIDATION_ERROR")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle all other unexpected exceptions.
     *
     * Returns 500 INTERNAL_SERVER_ERROR.
     * Logs full stack trace for debugging.
     *
     * @param ex the exception
     * @return 500 response with generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ErrorResponse error = ErrorResponse.builder()
                .message("An unexpected error occurred")
                .code("INTERNAL_ERROR")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
