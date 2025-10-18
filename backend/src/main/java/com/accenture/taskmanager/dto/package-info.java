/**
 * DTO (Data Transfer Object) layer - Request and response objects.
 *
 * This package will contain DTO classes that:
 * - Define API contracts for requests and responses
 * - Include validation annotations (@NotBlank, @Size, @Pattern, etc.)
 * - Decouple API structure from internal domain models
 * - Prevent over-exposure of entity internals
 *
 * DTOs control what data flows in and out of the API.
 *
 * Example DTO responsibilities:
 * - TaskRequest: Validated input for creating/updating tasks
 * - TaskResponse: Formatted output for client consumption
 * - ErrorResponse: Standardized error format with message, field, code
 * - API versioning and evolution without breaking domain model
 */
package com.accenture.taskmanager.dto;
