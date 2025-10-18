# Task Manager

> **Part of Accenture's "Agentic SDLC Fundamentals" Certification**
>
> This project is developed using **GitHub Copilot in Agent Mode** with **Claude Sonnet 4.5** as the primary AI model, demonstrating agentic software development practices and AI-assisted workflows.
>
> **Note:** Detailed development instructions and guidelines are provided in `.github/copilot-instructions.md`, which ensures alignment with project standards and provides context for AI-assisted development.

A full-stack web application for managing tasks with CRUD operations, built with modern technologies and best practices.

## Overview

Task Manager is a simple yet robust application that allows users to create, read, update, and delete tasks. Each task includes:
- **ID**: Unique identifier
- **Title**: Task name
- **Description**: Detailed information about the task
- **Status**: Current state (TODO, IN_PROGRESS, DONE)
- **Due Date**: When the task should be completed

## Technology Stack

### Frontend
- **React** - UI library
- **TypeScript** - Type-safe JavaScript
- **Vite** - Fast build tool and dev server
- **Jest** - Testing framework
- **React Testing Library** - Component testing utilities

### Backend
- **Spring Boot** (Java 21) - Application framework
- **Maven** - Build and dependency management
- **PostgreSQL** - Primary database (H2 for development/testing)
- **JUnit** - Unit testing framework
- **JaCoCo** - Code coverage analysis

### Communication
- REST API with JSON payloads
- CORS enabled for frontend-backend communication
- Bean Validation for request validation

## Project Structure

```
.
├── frontend/                 # React + TypeScript application
│   ├── src/
│   │   ├── features/
│   │   │   └── tasks/       # Task feature module
│   │   │       ├── components/
│   │   │       ├── api/
│   │   │       └── types/
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── package.json
│   └── vite.config.ts
│
├── backend/                  # Spring Boot application
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/taskmanager/
│   │               ├── controller/
│   │               ├── service/
│   │               ├── repository/
│   │               ├── model/
│   │               ├── dto/
│   │               └── config/
│   └── pom.xml
│
└── logbook.md               # Development log
```

## Prerequisites

- **Node.js** 22+ and npm
- **Java** 21
- **Maven** 3.8+
- **PostgreSQL** 14+ (or use H2 in-memory database)

## Getting Started

### Backend Setup

#### Option 1: Quick Start with H2 (Development)

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build and run (uses H2 in-memory database by default):
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The backend will start on `http://localhost:8080`

**Note:** H2 is an in-memory database. Data is lost when the application stops.

#### Option 2: PostgreSQL Setup (Production-like)

1. Start PostgreSQL using Docker:
   ```bash
   docker run --name taskmanager-postgres \
     -p 5432:5432 \
     -e POSTGRES_DB=taskmanager \
     -e POSTGRES_USER=taskuser \
     -e POSTGRES_PASSWORD=taskpass \
     -d postgres:16
   ```

2. Navigate to the backend directory:
   ```bash
   cd backend
   ```

3. Run with production profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

The backend will start on `http://localhost:8080` and connect to PostgreSQL.

**Database Configuration:**
- Default profile: H2 in-memory database (`application.yml`)
- Production profile: PostgreSQL (`application-prod.yml`)
- Flyway migrations run automatically on startup
- Database schema is created from `src/main/resources/db/migration/`

**Access Points:**
- API: `http://localhost:8080/api/tasks`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console (dev only): `http://localhost:8080/h2-console`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

The frontend will start on `http://localhost:5173`

## Testing

### Backend Tests
```bash
cd backend
mvn test
mvn verify  # Includes coverage report
```

Coverage reports are generated in `target/site/jacoco/index.html`

### Frontend Tests
```bash
cd frontend
npm test
npm run test:coverage
```

Coverage reports are generated in `coverage/` directory

## Code Quality Standards

- **100% test coverage** on all code changes
- Comprehensive testing: happy paths, error cases, edge cases
- Bean Validation on all API inputs
- Structured logging (slf4j on backend)
- TypeScript strict mode (no `any` types)
- Clean code: meaningful names, no unused code, single responsibility

## Database

### Schema Management
- **Flyway** for database migrations
- Migrations located in `backend/src/main/resources/db/migration/`
- Automatic migration on application startup
- Version-controlled schema changes

### Schema Overview
```sql
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    status VARCHAR(20) NOT NULL CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE')),
    due_date DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
```

**Indexes:**
- `idx_tasks_status` - for filtering by status
- `idx_tasks_due_date` - for date-based queries
- `idx_tasks_status_due_date` - composite for common queries

**Audit Timestamps:**
- Uses `Instant` (Java) / `TIMESTAMP WITH TIME ZONE` (PostgreSQL)
- Timezone-independent for distributed systems
- Automatically managed by JPA lifecycle callbacks

## API Documentation

The API is documented using OpenAPI 3.0 (Swagger). The specification is defined in `backend/src/main/resources/openapi/task-manager-api.yml`.

### Access Points
- **Swagger UI**: http://localhost:8080/swagger-ui.html (interactive API documentation)
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

### Base URL
```
http://localhost:8080/api
```

### Endpoints
- `GET /api/tasks` - List all tasks
- `GET /api/tasks/{id}` - Get task by ID
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update existing task
- `DELETE /api/tasks/{id}` - Delete task

### Example: Create a Task
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

**Response:**
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

## Development Workflow

All significant development work is tracked in `/logbook.md` with:
- Task descriptions
- Implementation plans
- Changes made
- Test results and coverage
- Lessons learned

## CI/CD Pipeline

### Overview

This project uses **GitHub Actions** for continuous integration and **Render.com** for automatic deployment.

**On every push to `main`:**
1. 🔨 Code is built with Maven
2. 🧪 All tests run
3. 📦 Docker image is built
4. 🚀 Automatically deployed to Render.com
5. ✅ Health checks verify deployment

### GitHub Actions Workflows

#### CI Workflow (`.github/workflows/ci.yml`)
- Triggers on push to `main` and `develop` branches
- Triggers on pull requests to `main`
- Runs on Ubuntu latest with Java 21
- Steps:
  - Checkout code
  - Set up JDK 21 (Temurin distribution)
  - Build with Maven
  - Run tests with coverage
  - Verify 100% coverage threshold
  - Package application (JAR)
  - Upload test results and coverage reports as artifacts

### Deployment to Render.com

#### Setup Instructions

1. **Fork/Clone this repository** to your GitHub account

2. **Create a Render.com account** (free tier):
   - Go to https://render.com
   - Sign up with GitHub

3. **Create a new Blueprint** from `render.yaml`:
   - In Render dashboard, click "New +" → "Blueprint"
   - Connect your GitHub repository
   - Render will automatically detect `render.yaml`
   - Click "Apply" to provision resources

4. **Resources created**:
   - **PostgreSQL database** (taskmanager-postgres)
     - Free tier (90 days, then requires re-creation)
     - Region: Frankfurt (configurable)
   - **Web Service** (taskmanager-db)
     - Deployed from Docker
     - Auto-deploy on push to `main`
     - Environment variables auto-configured from database

5. **Access your deployed app**:
   - Render provides a URL like: `https://taskmanager-db.onrender.com`
   - API: `https://taskmanager-db.onrender.com/api/tasks`
   - Swagger UI: `https://taskmanager-db.onrender.com/swagger-ui.html`
   - Health check: `https://taskmanager-db.onrender.com/actuator/health`

#### Automatic Deployment

Once set up, Render automatically:
- Detects pushes to `main` branch
- Builds Docker image from `backend/Dockerfile`
- Runs database migrations (Flyway)
- Deploys new version with zero-downtime
- Performs health checks before routing traffic

#### Environment Variables

Render automatically configures:
- `SPRING_PROFILES_ACTIVE=prod`
- `SPRING_DATASOURCE_URL` (from PostgreSQL database)
- `SPRING_DATASOURCE_USERNAME` (from PostgreSQL database)
- `SPRING_DATASOURCE_PASSWORD` (from PostgreSQL database)
- `SERVER_PORT=8080`

No manual configuration needed!

#### Monitoring

**Health Check:**
```bash
curl https://your-app.onrender.com/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

**View Logs:**
- Render Dashboard → Your Service → "Logs" tab
- Real-time streaming logs
- Filter by severity (INFO, WARN, ERROR)

#### Cost

**100% Free Tier:**
- GitHub Actions: 2,000 minutes/month (unlimited for public repos)
- Render.com: Free tier includes
  - 750 hours/month web service
  - PostgreSQL database (90 days)
- **Total cost: $0/month** 🎉

**Note:** Render free tier databases expire after 90 days but can be recreated. All data migrations are versioned with Flyway.

### Local Docker Testing

Test the production Docker image locally before deploying:

```bash
# Build the Docker image
cd backend
docker build -t taskmanager:latest .

# Run with PostgreSQL
docker run --name taskmanager-postgres \
  -e POSTGRES_DB=taskmanager \
  -e POSTGRES_USER=taskuser \
  -e POSTGRES_PASSWORD=taskpass \
  -p 5432:5432 \
  -d postgres:16

# Run the application
docker run --name taskmanager-app \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/taskmanager \
  -e SPRING_DATASOURCE_USERNAME=taskuser \
  -e SPRING_DATASOURCE_PASSWORD=taskpass \
  -p 8080:8080 \
  taskmanager:latest

# Test health check
curl http://localhost:8080/actuator/health

# Test API
curl http://localhost:8080/api/tasks
```

## License

*(To be determined)*

## Contributing

*(To be determined)*
