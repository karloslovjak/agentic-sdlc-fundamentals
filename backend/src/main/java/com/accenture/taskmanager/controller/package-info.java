/**
 * Controller layer - REST API endpoints.
 *
 * This package will contain @RestController classes that:
 * - Handle HTTP requests and responses
 * - Map URLs to handler methods
 * - Validate request DTOs using @Valid
 * - Return appropriate HTTP status codes
 * - Handle exceptions and return error responses
 *
 * Controllers are thin - they delegate business logic to the service layer.
 *
 * Example controller responsibilities:
 * - TaskController: CRUD operations for Task entity
 * - Request/response transformation
 * - HTTP-specific concerns (status codes, headers)
 */
package com.accenture.taskmanager.controller;
