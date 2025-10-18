# Task Manager

> **Part of Accenture's "Agentic SDLC Fundamentals" Certification**
>
> This project is developed using **GitHub Copilot in Agent Mode** with **Claude Sonnet 4.5** as the primary AI model, demonstrating agentic software development practices and AI-assisted workflows.
>
> **Note:** Detailed development instructions and guidelines are provided in `.github/copilot-instructions.md`, which ensures alignment with project standards and provides context for AI-assisted development.

A full-stack web application for managing tasks with CRUD operations, built with modern technologies and best practices.

## 🎯 Overview

Task Manager is a simple yet robust application that allows users to create, read, update, and delete tasks. Each task includes:
- **ID**: Unique identifier
- **Title**: Task name (required)
- **Description**: Detailed information
- **Status**: Current state (TODO, IN_PROGRESS, DONE)
- **Due Date**: When the task should be completed

## 🚀 Quick Start

### Backend (Spring Boot)

**Option 1: H2 In-Memory Database (Fastest)**
```bash
cd backend
mvn spring-boot:run
```
✅ Backend running at http://localhost:8080

**Option 2: PostgreSQL (Production-like)**
```bash
# Start PostgreSQL
docker run --name taskmanager-postgres \
  -p 5432:5432 \
  -e POSTGRES_DB=taskmanager \
  -e POSTGRES_USER=taskuser \
  -e POSTGRES_PASSWORD=taskpass \
  -d postgres:16

# Run backend
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**Access Points:**
- 🔌 API: http://localhost:8080/api/tasks
- 📚 Swagger UI: http://localhost:8080/swagger-ui.html
- ❤️ Health Check: http://localhost:8080/actuator/health

### Frontend (React + TypeScript)

```bash
cd frontend
npm install
npm run dev
```
✅ Frontend running at http://localhost:5173

## 📚 Documentation

### Core Guides
- 📖 **[API Documentation](docs/api.md)** - Complete REST API reference with examples
- 🗄️ **[Database Guide](docs/database.md)** - Schema, Flyway migrations, best practices
- 🚀 **[Backend Guide](backend/README.md)** - Architecture, development workflow, testing
- 🚢 **[Deployment Guide](deployment.md)** - CI/CD with GitHub Actions + Render.com

### Development
- 📝 **[Logbook](logbook.md)** - Development history and decisions

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.5.6** (Java 21)
- **PostgreSQL** / H2 (database)
- **Flyway** (migrations)
- **JUnit + Mockito** (testing)
- **JaCoCo** (coverage)

### Frontend
- **React** + **TypeScript**
- **Vite** (build tool)
- **Jest** + **React Testing Library** (testing)

### DevOps
- **GitHub Actions** (CI/CD)
- **Docker** (containerization)
- **Render.com** (hosting)

## 🧪 Testing

### Backend
```bash
cd backend
mvn test              # Run tests
mvn verify            # Tests + coverage report
```
📊 Coverage: `target/site/jacoco/index.html`

### Frontend
```bash
cd frontend
npm test              # Run tests
npm run test:coverage # Tests + coverage
```
📊 Coverage: `coverage/index.html`

**Standard:** 100% coverage on all code changes

## 🚀 Deployment

Push to `main` branch triggers automatic deployment:

1. 🔨 GitHub Actions builds and tests
2. ✅ Tests pass (63 tests, 100% coverage)
3. 📦 Docker image created
4. 🚀 Deployed to Render.com
5. ❤️ Health checks verify deployment

**Live App:** `https://your-app.onrender.com`

### Deployment

See **[Deployment Guide](deployment.md)** for setup instructions.

## 📁 Project Structure

```
.
├── backend/              # Spring Boot application
│   ├── src/
│   │   ├── main/java    # Source code
│   │   ├── main/resources # Config + migrations
│   │   └── test/        # Tests (63 tests)
│   ├── Dockerfile        # Production image
│   └── README.md         # Backend guide
│
├── frontend/             # React application (future)
│   └── ...
│
├── docs/
│   ├── api.md            # API reference
│   └── database.md       # Database guide
│
├── .github/
│   └── workflows/
│       └── ci.yml        # GitHub Actions
│
├── render.yaml           # Render.com config
├── deployment.md         # Deployment guide
└── logbook.md            # Development log
```

## 🎯 Development Workflow

All significant development work is tracked in [`logbook.md`](logbook.md) with:
- Task descriptions and goals
- Implementation plans
- Changes made
- Test results and coverage
- Lessons learned

## 🏗️ Code Quality Standards

- ✅ **100% test coverage** on all changes
- ✅ Comprehensive testing (happy/error/edge cases)
- ✅ Bean Validation on all inputs
- ✅ Structured logging (slf4j)
- ✅ TypeScript strict mode (no `any`)
- ✅ Clean code principles

## 🤝 Contributing

1. Create feature branch (`git checkout -b feature/amazing-feature`)
2. Make changes with tests (maintain 100% coverage)
3. Commit (`git commit -m 'feat: add amazing feature'`)
4. Push (`git push origin feature/amazing-feature`)
5. Open Pull Request

See **[Backend Guide](backend/README.md)** for detailed development workflow.

## 📄 License

*(To be determined)*

---

**🎓 Learning Resource:** This project demonstrates modern full-stack development with emphasis on testing, automation, and agentic AI-assisted workflows.
