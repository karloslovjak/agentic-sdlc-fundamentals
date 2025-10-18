# Task Manager

> **Part of Accenture's "Agentic SDLC Fundamentals" Certification**
>
> This project is developed using **GitHub Copilot in Agent Mode** with **Claude Sonnet 4.5** as the primary AI model, demonstrating agentic software development practices and AI-assisted workflows.

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

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Configure database connection in `application.properties` (or use H2 default)

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The backend will start on `http://localhost:8080`

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

## API Documentation

*(To be added as endpoints are implemented)*

### Base URL
```
http://localhost:8080/api
```

### Endpoints
- `GET /tasks` - List all tasks
- `GET /tasks/{id}` - Get task by ID
- `POST /tasks` - Create new task
- `PUT /tasks/{id}` - Update existing task
- `DELETE /tasks/{id}` - Delete task

## Development Workflow

All significant development work is tracked in `/logbook.md` with:
- Task descriptions
- Implementation plans
- Changes made
- Test results and coverage
- Lessons learned

## License

*(To be determined)*

## Contributing

*(To be determined)*
