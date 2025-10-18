# API Documentation

Complete REST API reference for the Task Manager application.

## Table of Contents
- [Overview](#overview)
- [Base URL](#base-url)
- [Authentication](#authentication)
- [Endpoints](#endpoints)
- [Request/Response Examples](#requestresponse-examples)
- [Error Handling](#error-handling)
- [OpenAPI/Swagger](#openapiswagger)

---

## Overview

The Task Manager API is a RESTful JSON API that provides CRUD operations for task management.

**Key Features:**
- ✅ RESTful design principles
- ✅ JSON request/response format
- ✅ Bean Validation for input validation
- ✅ Comprehensive error handling
- ✅ OpenAPI 3.0 specification
- ✅ Interactive Swagger UI

**Technology:**
- Spring Boot 3.5.6
- Spring Web MVC
- Jackson for JSON serialization
- Bean Validation (JSR-380)

---

## Base URL

### Local Development
```
http://localhost:8080/api
```

### Production (Render.com)
```
https://your-app.onrender.com/api
```

---

## Authentication

**Current:** No authentication required (public API)

**Future:** Will implement JWT-based authentication
- Bearer token in Authorization header
- User-specific task filtering
- Role-based access control (RBAC)

---

## Endpoints

### Task Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/tasks` | List all tasks |
| GET | `/tasks/{id}` | Get task by ID |
| POST | `/tasks` | Create new task |
| PUT | `/tasks/{id}` | Update existing task |
| DELETE | `/tasks/{id}` | Delete task |

### Additional Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/tasks?status={status}` | Filter tasks by status |
| GET | `/tasks?dueBefore={date}` | Filter tasks due before date |
| GET | `/tasks?dueAfter={date}` | Filter tasks due after date |
| GET | `/actuator/health` | Health check endpoint |

---

## Request/Response Examples

### 1. List All Tasks

**Request:**
```http
GET /api/tasks HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive README and API docs",
    "status": "TODO",
    "dueDate": "2025-10-20",
    "createdAt": "2025-10-18T12:08:32.289463Z",
    "updatedAt": "2025-10-18T12:08:32.289464Z"
  },
  {
    "id": 2,
    "title": "Implement user authentication",
    "description": "Add JWT-based auth with Spring Security",
    "status": "IN_PROGRESS",
    "dueDate": "2025-10-25",
    "createdAt": "2025-10-18T14:30:15.123456Z",
    "updatedAt": "2025-10-18T15:45:22.654321Z"
  }
]
```

**Response (Empty List):**
```json
[]
```

### 2. Get Task by ID

**Request:**
```http
GET /api/tasks/1 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive README and API docs",
  "status": "TODO",
  "dueDate": "2025-10-20",
  "createdAt": "2025-10-18T12:08:32.289463Z",
  "updatedAt": "2025-10-18T12:08:32.289464Z"
}
```

**Response (404 Not Found):**
```json
{
  "message": "Task not found with id: 999",
  "code": "TASK_NOT_FOUND",
  "field": null
}
```

### 3. Create New Task

**Request:**
```http
POST /api/tasks HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: application/json

{
  "title": "Complete project documentation",
  "description": "Write comprehensive README and API docs",
  "status": "TODO",
  "dueDate": "2025-10-20"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive README and API docs",
  "status": "TODO",
  "dueDate": "2025-10-20",
  "createdAt": "2025-10-18T12:08:32.289463Z",
  "updatedAt": "2025-10-18T12:08:32.289464Z"
}
```

**Validation Error (400 Bad Request):**
```json
{
  "message": "size must be between 1 and 200",
  "code": "VALIDATION_ERROR",
  "field": "title"
}
```

### 4. Update Existing Task

**Request:**
```http
PUT /api/tasks/1 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: application/json

{
  "title": "Complete project documentation",
  "description": "Updated description with more details",
  "status": "IN_PROGRESS",
  "dueDate": "2025-10-25"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Complete project documentation",
  "description": "Updated description with more details",
  "status": "IN_PROGRESS",
  "dueDate": "2025-10-25",
  "createdAt": "2025-10-18T12:08:32.289463Z",
  "updatedAt": "2025-10-18T16:30:45.123456Z"
}
```

**Response (404 Not Found):**
```json
{
  "message": "Task not found with id: 999",
  "code": "TASK_NOT_FOUND",
  "field": null
}
```

### 5. Delete Task

**Request:**
```http
DELETE /api/tasks/1 HTTP/1.1
Host: localhost:8080
```

**Response (204 No Content)**
- Empty body
- Task successfully deleted

**Response (404 Not Found):**
```json
{
  "message": "Task not found with id: 999",
  "code": "TASK_NOT_FOUND",
  "field": null
}
```

### 6. Filter Tasks by Status

**Request:**
```http
GET /api/tasks?status=TODO HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Task 1",
    "status": "TODO",
    "dueDate": "2025-10-20",
    ...
  },
  {
    "id": 3,
    "title": "Task 3",
    "status": "TODO",
    "dueDate": "2025-10-22",
    ...
  }
]
```

### 7. Filter Tasks by Due Date

**Request (tasks due before date):**
```http
GET /api/tasks?dueBefore=2025-10-25 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Request (tasks due after date):**
```http
GET /api/tasks?dueAfter=2025-10-20 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Request (tasks due between dates):**
```http
GET /api/tasks?dueAfter=2025-10-20&dueBefore=2025-10-30 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

---

## cURL Examples

### Create Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Complete project documentation",
    "description": "Write comprehensive README and API docs",
    "status": "TODO",
    "dueDate": "2025-10-20"
  }'
```

### Get All Tasks
```bash
curl http://localhost:8080/api/tasks
```

### Get Task by ID
```bash
curl http://localhost:8080/api/tasks/1
```

### Update Task
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Updated title",
    "description": "Updated description",
    "status": "IN_PROGRESS",
    "dueDate": "2025-10-25"
  }'
```

### Delete Task
```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```

### Filter by Status
```bash
curl http://localhost:8080/api/tasks?status=TODO
```

### Filter by Due Date
```bash
curl http://localhost:8080/api/tasks?dueBefore=2025-10-25
```

---

## Request Schema

### TaskRequest (Create/Update)

```json
{
  "title": "string (required, 1-200 chars)",
  "description": "string (optional, max 2000 chars)",
  "status": "enum (required: TODO | IN_PROGRESS | DONE)",
  "dueDate": "date (optional, format: YYYY-MM-DD)"
}
```

**Validation Rules:**

| Field | Required | Type | Validation |
|-------|----------|------|------------|
| `title` | Yes | String | 1-200 characters, not blank |
| `description` | No | String | Max 2000 characters |
| `status` | Yes | Enum | One of: TODO, IN_PROGRESS, DONE |
| `dueDate` | No | Date | ISO 8601 format (YYYY-MM-DD) |

**Example (Minimal):**
```json
{
  "title": "Simple task",
  "status": "TODO"
}
```

**Example (Complete):**
```json
{
  "title": "Complete task with all fields",
  "description": "This task has a description and due date",
  "status": "IN_PROGRESS",
  "dueDate": "2025-12-31"
}
```

---

## Response Schema

### TaskResponse

```json
{
  "id": "number (auto-generated)",
  "title": "string",
  "description": "string | null",
  "status": "enum (TODO | IN_PROGRESS | DONE)",
  "dueDate": "date | null (format: YYYY-MM-DD)",
  "createdAt": "timestamp (ISO 8601)",
  "updatedAt": "timestamp (ISO 8601)"
}
```

**Field Details:**

| Field | Type | Description |
|-------|------|-------------|
| `id` | Number | Unique identifier (auto-generated) |
| `title` | String | Task name/title |
| `description` | String \| null | Detailed description (may be null) |
| `status` | Enum | Current state: TODO, IN_PROGRESS, or DONE |
| `dueDate` | Date \| null | Due date in YYYY-MM-DD format (may be null) |
| `createdAt` | Timestamp | Creation time in ISO 8601 format (UTC) |
| `updatedAt` | Timestamp | Last update time in ISO 8601 format (UTC) |

---

## Error Handling

### Error Response Format

All errors return a consistent JSON structure:

```json
{
  "message": "Human-readable error message",
  "code": "ERROR_CODE",
  "field": "fieldName (if applicable, otherwise null)"
}
```

### HTTP Status Codes

| Status | Code | Description |
|--------|------|-------------|
| 200 | OK | Request successful (GET, PUT) |
| 201 | Created | Resource created successfully (POST) |
| 204 | No Content | Resource deleted successfully (DELETE) |
| 400 | Bad Request | Validation error or malformed request |
| 404 | Not Found | Resource not found |
| 500 | Internal Server Error | Unexpected server error |

### Common Errors

**Task Not Found (404):**
```json
{
  "message": "Task not found with id: 999",
  "code": "TASK_NOT_FOUND",
  "field": null
}
```

**Validation Error (400):**
```json
{
  "message": "size must be between 1 and 200",
  "code": "VALIDATION_ERROR",
  "field": "title"
}
```

**Missing Required Field (400):**
```json
{
  "message": "must not be blank",
  "code": "VALIDATION_ERROR",
  "field": "title"
}
```

**Invalid Enum Value (400):**
```json
{
  "message": "Invalid status value. Must be one of: TODO, IN_PROGRESS, DONE",
  "code": "VALIDATION_ERROR",
  "field": "status"
}
```

**Malformed JSON (400):**
```json
{
  "message": "JSON parse error: Unexpected character...",
  "code": "BAD_REQUEST",
  "field": null
}
```

---

## OpenAPI/Swagger

### Interactive Documentation

**Swagger UI:** http://localhost:8080/swagger-ui.html

Features:
- ✅ Interactive API testing
- ✅ Request/response examples
- ✅ Schema documentation
- ✅ "Try it out" functionality
- ✅ Response code documentation

### OpenAPI Specification

**JSON:** http://localhost:8080/v3/api-docs
**YAML:** http://localhost:8080/v3/api-docs.yaml

**Download Spec:**
```bash
curl http://localhost:8080/v3/api-docs.yaml > openapi-spec.yaml
```

**YAML Location:**
```
backend/src/main/resources/openapi/task-manager-api.yml
```

---

## CORS Configuration

### Allowed Origins

**Development:**
```
http://localhost:5173 (Vite dev server)
http://localhost:3000 (Alternative frontend)
```

**Production:**
```
https://your-frontend-domain.com
```

### Allowed Methods
```
GET, POST, PUT, DELETE, OPTIONS
```

### Allowed Headers
```
Content-Type, Authorization, X-Requested-With
```

### Credentials
```
Supported (for future authentication)
```

---

## Rate Limiting

**Current:** No rate limiting implemented

**Future Plans:**
- 100 requests per minute per IP
- 1000 requests per hour per authenticated user
- Exponential backoff for repeated violations

---

## Versioning

**Current:** No API versioning (v1 implicit)

**Future:** URL-based versioning
```
/api/v1/tasks  (current)
/api/v2/tasks  (future breaking changes)
```

---

## Health Check

### Endpoint
```
GET /actuator/health
```

### Response (Healthy)
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Response (Unhealthy)
```json
{
  "status": "DOWN",
  "components": {
    "db": {
      "status": "DOWN",
      "details": {
        "error": "Connection refused"
      }
    }
  }
}
```

---

## Testing the API

### Using cURL
See [cURL Examples](#curl-examples) above

### Using Postman
1. Import OpenAPI spec: `http://localhost:8080/v3/api-docs`
2. Collection automatically generated
3. Test all endpoints interactively

### Using Swagger UI
1. Navigate to http://localhost:8080/swagger-ui.html
2. Select endpoint
3. Click "Try it out"
4. Fill in parameters
5. Click "Execute"

### Using HTTPie
```bash
# Install: brew install httpie

# GET request
http localhost:8080/api/tasks

# POST request
http POST localhost:8080/api/tasks \
  title="New task" \
  status="TODO" \
  dueDate="2025-10-20"

# PUT request
http PUT localhost:8080/api/tasks/1 \
  title="Updated task" \
  status="DONE"

# DELETE request
http DELETE localhost:8080/api/tasks/1
```

---

## Future Enhancements

Planned API improvements:

- [ ] Pagination (page, size, sort parameters)
- [ ] Field filtering (sparse fieldsets)
- [ ] Bulk operations (batch create/update/delete)
- [ ] Search endpoint (full-text search)
- [ ] Task attachments/files
- [ ] Task comments/notes
- [ ] Task history/audit log
- [ ] Webhook notifications
- [ ] GraphQL alternative endpoint
- [ ] gRPC support for high-performance clients

---

**Related Documentation:**
- [Main README](../README.md) - Project overview
- [Database Guide](database.md) - Schema and migrations
- [Deployment Guide](../deployment.md) - Production deployment
- [Backend Guide](../backend/README.md) - Backend architecture
