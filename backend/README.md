# Backend Guide

Complete guide to the Task Manager backend application built with Spring Boot.

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Setup and Running](#setup-and-running)
- [Testing](#testing)
- [Code Coverage](#code-coverage)
- [Development Workflow](#development-workflow)
- [Adding New Features](#adding-new-features)

---

## Overview

The Task Manager backend is a **Spring Boot 3.5.6** application providing a RESTful API for task management.

**Key Technologies:**
- **Java 21** - LTS version with modern features
- **Spring Boot 3.5.6** - Application framework
- **Spring Data JPA** - Data persistence layer
- **Maven** - Build and dependency management
- **PostgreSQL / H2** - Database options
- **Flyway** - Database migration tool
- **MapStruct** - DTO mapping
- **Lombok** - Boilerplate reduction
- **JUnit 5 + Mockito** - Testing framework

---

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← REST endpoints, request/response
├─────────────────────────────────────┤
│          Service Layer              │  ← Business logic
├─────────────────────────────────────┤
│        Repository Layer             │  ← Data access (Spring Data JPA)
├─────────────────────────────────────┤
│          Database Layer             │  ← PostgreSQL / H2
└─────────────────────────────────────┘
```

### Design Patterns

**1. Repository Pattern:**
```java
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
}
```

**2. Service Pattern:**
```java
@Service
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        log.debug("Fetching all tasks");
        return taskRepository.findAll();
    }
}
```

**3. DTO Pattern:**
```java
// Request DTO (client → server)
@Data
public class TaskRequest {
    @NotBlank
    @Size(min = 1, max = 200)
    private String title;
    private String description;
    @NotNull
    private TaskStatus status;
}

// Response DTO (server → client)
@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
```

**4. Mapper Pattern (MapStruct):**
```java
@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskResponse toResponse(Task task);
    Task toEntity(TaskRequest request);
}
```

---

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/accenture/taskmanager/
│   │   │   ├── controller/          # REST endpoints
│   │   │   │   └── TaskController.java
│   │   │   ├── service/             # Business logic
│   │   │   │   └── TaskService.java
│   │   │   ├── repository/          # Data access
│   │   │   │   └── TaskRepository.java
│   │   │   ├── model/               # Domain entities
│   │   │   │   ├── Task.java
│   │   │   │   └── TaskStatus.java
│   │   │   ├── dto/                 # Request/Response DTOs
│   │   │   │   ├── TaskRequest.java
│   │   │   │   └── TaskResponse.java
│   │   │   ├── mapper/              # DTO ↔ Entity mapping
│   │   │   │   └── TaskMapper.java
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── CorsConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── exception/           # Exception handling
│   │   │   │   ├── TaskNotFoundException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── TaskManagerApplication.java
│   │   └── resources/
│   │       ├── application.yml               # Default config (H2)
│   │       ├── application-prod.yml          # Production config (PostgreSQL)
│   │       ├── db/migration/                 # Flyway migrations
│   │       │   └── V1__create_tasks_table.sql
│   │       └── openapi/
│   │           └── task-manager-api.yml      # OpenAPI spec
│   └── test/
│       └── java/com/accenture/taskmanager/
│           ├── controller/
│           │   └── TaskControllerTest.java   # API endpoint tests
│           ├── service/
│           │   └── TaskServiceTest.java      # Business logic tests
│           ├── repository/
│           │   └── TaskRepositoryTest.java   # Data access tests
│           ├── mapper/
│           │   └── TaskMapperTest.java       # Mapping tests
│           ├── config/
│           │   ├── CorsConfigTest.java
│           │   └── OpenApiConfigTest.java
│           └── TaskManagerApplicationTests.java
├── pom.xml                          # Maven dependencies
├── Dockerfile                       # Docker image build
└── README.md                        # This file
```

### Package Responsibilities

| Package | Responsibility |
|---------|----------------|
| `controller` | HTTP request/response handling, input validation |
| `service` | Business logic, transaction management |
| `repository` | Database queries, data persistence |
| `model` | Domain entities, JPA mappings |
| `dto` | Data transfer objects (API contracts) |
| `mapper` | DTO ↔ Entity conversion |
| `config` | Application configuration (CORS, OpenAPI, etc.) |
| `exception` | Custom exceptions, global error handling |

---

## Setup and Running

### Prerequisites

- **Java 21** (verify: `java --version`)
- **Maven 3.8+** (verify: `mvn --version`)
- **PostgreSQL 14+** (optional, for production profile)

### Quick Start (H2 Database)

```bash
# Navigate to backend directory
cd backend

# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

**Application starts on:** http://localhost:8080

### PostgreSQL Setup

**1. Start PostgreSQL (Docker):**
```bash
docker run --name taskmanager-postgres \
  -p 5432:5432 \
  -e POSTGRES_DB=taskmanager \
  -e POSTGRES_USER=taskuser \
  -e POSTGRES_PASSWORD=taskpass \
  -d postgres:16
```

**2. Run with production profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Build Options

**Standard build (with tests):**
```bash
mvn clean install
```

**Skip tests:**
```bash
mvn clean install -DskipTests
```

**Package JAR only:**
```bash
mvn package
```

**Run JAR:**
```bash
java -jar target/task-manager-0.0.1-SNAPSHOT.jar
```

---

## Testing

### Test Structure

```
63 Total Tests:
├── TaskControllerTest      (11 tests)  ← REST API integration tests
├── TaskServiceTest         (18 tests)  ← Business logic unit tests
├── TaskRepositoryTest      (15 tests)  ← Data access tests
├── TaskMapperTest          (13 tests)  ← DTO mapping tests
├── CorsConfigTest          ( 2 tests)  ← CORS configuration tests
├── OpenApiConfigTest       ( 3 tests)  ← OpenAPI configuration tests
└── ApplicationTests        ( 1 test)   ← Context loading test
```

### Running Tests

**All tests:**
```bash
mvn test
```

**Specific test class:**
```bash
mvn test -Dtest=TaskServiceTest
```

**Specific test method:**
```bash
mvn test -Dtest=TaskServiceTest#getAllTasks_shouldReturnAllTasks
```

**With coverage:**
```bash
mvn verify
```

### Test Categories

**1. Controller Tests (MockMvc):**
```java
@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "title": "New Task",
                    "status": "TODO"
                }
                """))
            .andExpect(status().isCreated());
    }
}
```

**2. Service Tests (Mockito):**
```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task1, task2));
        List<Task> result = taskService.getAllTasks();
        assertThat(result).hasSize(2);
    }
}
```

**3. Repository Tests (DataJpaTest):**
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findByStatus_shouldReturnTasksWithGivenStatus() {
        List<Task> tasks = taskRepository.findByStatus(TaskStatus.TODO);
        assertThat(tasks).hasSize(2);
    }
}
```

---

## Code Coverage

### Requirements

- **100% coverage** on all new code
- **100% coverage** on modified code
- Covers: lines, branches, methods

### Generate Report

```bash
mvn verify
```

**Report location:** `target/site/jacoco/index.html`

### View Coverage

```bash
open target/site/jacoco/index.html  # macOS
xdg-open target/site/jacoco/index.html  # Linux
start target/site/jacoco/index.html  # Windows
```

### Coverage by Package

| Package | Coverage |
|---------|----------|
| controller | 100% |
| service | 100% |
| repository | 100% |
| mapper | 100% |
| config | 100% |
| exception | 100% |
| **Total** | **100%** |

### CI/CD Enforcement

GitHub Actions verifies coverage on every push:
```yaml
- name: Verify test coverage
  run: mvn jacoco:report jacoco:check
```

---

## Development Workflow

### 1. Create Feature Branch
```bash
git checkout -b feature/add-task-priority
```

### 2. Make Changes

Follow TDD (Test-Driven Development):
1. Write failing test
2. Implement feature
3. Make test pass
4. Refactor

### 3. Run Tests Locally
```bash
mvn test
mvn verify  # includes coverage
```

### 4. Commit Changes
```bash
git add .
git commit -m "feat: add task priority field"
```

### 5. Push and Create PR
```bash
git push origin feature/add-task-priority
```

GitHub Actions runs automatically:
- Build
- Run tests
- Verify coverage
- Package JAR

---

## Adding New Features

### Example: Add Task Priority

**Step 1: Update Entity**
```java
// model/Task.java
@Entity
@Table(name = "tasks")
public class Task {
    // ... existing fields

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 10, nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;
}

// model/TaskPriority.java
public enum TaskPriority {
    LOW, MEDIUM, HIGH
}
```

**Step 2: Create Migration**
```sql
-- db/migration/V2__add_task_priority.sql
ALTER TABLE tasks ADD COLUMN priority VARCHAR(10) DEFAULT 'MEDIUM' NOT NULL;
ALTER TABLE tasks ADD CONSTRAINT check_priority
    CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'));
```

**Step 3: Update DTOs**
```java
// dto/TaskRequest.java
public class TaskRequest {
    // ... existing fields

    @NotNull
    private TaskPriority priority;
}

// dto/TaskResponse.java
public class TaskResponse {
    // ... existing fields

    private TaskPriority priority;
}
```

**Step 4: Update Mapper**
```java
// Mapper automatically handles new field
// No changes needed if fields have same name
```

**Step 5: Add Repository Query (Optional)**
```java
// repository/TaskRepository.java
List<Task> findByPriority(TaskPriority priority);
List<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority);
```

**Step 6: Update Service (Optional)**
```java
// service/TaskService.java
public List<Task> getTasksByPriority(TaskPriority priority) {
    return taskRepository.findByPriority(priority);
}
```

**Step 7: Update Controller (Optional)**
```java
// controller/TaskController.java
@GetMapping(params = "priority")
public ResponseEntity<List<TaskResponse>> getTasksByPriority(
        @RequestParam TaskPriority priority) {
    List<Task> tasks = taskService.getTasksByPriority(priority);
    return ResponseEntity.ok(tasks.stream()
        .map(taskMapper::toResponse)
        .collect(Collectors.toList()));
}
```

**Step 8: Write Tests**
```java
// Test entity, service, repository, controller, mapper
// Ensure 100% coverage
```

**Step 9: Update OpenAPI Spec**
```yaml
# openapi/task-manager-api.yml
TaskRequest:
  properties:
    priority:
      type: string
      enum: [LOW, MEDIUM, HIGH]
```

**Step 10: Run Tests**
```bash
mvn verify
```

---

## Configuration Profiles

### Default Profile (Development)
```yaml
# application.yml
spring:
  profiles:
    active: default
  datasource:
    url: jdbc:h2:mem:taskmanager
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    enabled: false
```

### Production Profile
```yaml
# application-prod.yml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/taskmanager}
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

### Activate Profile
```bash
# Command line
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Environment variable
export SPRING_PROFILES_ACTIVE=prod

# JAR execution
java -jar app.jar --spring.profiles.active=prod
```

---

## Logging

### Configuration

```yaml
logging:
  level:
    root: INFO
    com.accenture.taskmanager: DEBUG
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG
```

### Usage

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskService {
    public Task createTask(Task task) {
        log.info("Creating new task with title: {}", task.getTitle());
        Task savedTask = taskRepository.save(task);
        log.info("Task created with id: {}", savedTask.getId());
        return savedTask;
    }
}
```

### Log Levels

- **ERROR** - Application errors, exceptions
- **WARN** - Warning messages (task not found, etc.)
- **INFO** - Application lifecycle events (startup, shutdown, task created)
- **DEBUG** - Detailed flow information (SQL queries, method entry/exit)
- **TRACE** - Very detailed information (rarely used)

---

## Troubleshooting

### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Tests Failing
```bash
# Clean and rebuild
mvn clean install

# Update dependencies
mvn dependency:purge-local-repository

# Check test logs
cat target/surefire-reports/*.txt
```

### Database Connection Issues
```bash
# Check PostgreSQL is running
docker ps --filter name=taskmanager-postgres

# Check logs
docker logs taskmanager-postgres

# Restart PostgreSQL
docker restart taskmanager-postgres
```

---

## Best Practices

### Code Style
- ✅ Use Lombok to reduce boilerplate
- ✅ Follow Spring conventions (@Service, @Repository, etc.)
- ✅ Use constructor injection (not field injection)
- ✅ Keep controllers thin (delegate to services)
- ✅ Use DTOs for API contracts (not entities)

### Testing
- ✅ Write tests first (TDD)
- ✅ Test happy paths, error paths, edge cases
- ✅ Use meaningful test names (should_X_when_Y)
- ✅ One assertion per test (prefer specific assertions)
- ✅ Mock external dependencies

### Database
- ✅ Use Flyway for all schema changes
- ✅ Never modify existing migrations
- ✅ Index frequently queried columns
- ✅ Use appropriate column types and sizes

### Security
- ✅ Validate all inputs (@Valid, @NotNull, etc.)
- ✅ Use prepared statements (JPA does this)
- ✅ Log security events
- ✅ Don't log sensitive data (passwords, tokens)

---

**Related Documentation:**
- [Main README](../README.md) - Project overview
- [API Documentation](../docs/api.md) - REST endpoints
- [Database Guide](../docs/database.md) - Schema and migrations
- [Deployment Guide](../deployment.md) - Production deployment
