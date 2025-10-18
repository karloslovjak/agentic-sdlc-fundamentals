# Copilot: Repository Instructions

## Project context (what we're building)
We are building a simple full-stack **Task Manager** web app with CRUD over a `Task` entity (id, title, description, status = TODO | IN_PROGRESS | DONE, dueDate).
Frontend: React (TypeScript preferred).
Backend: Spring Boot (Java 17+ — 21 preferred).
DB: PostgreSQL (preferred) or H2 (in-memory).
Communication: REST API with JSON.
Build tools: Vite for frontend, Maven for backend.
Include validation and CORS.

## How to help me (process + collaboration)
- Prefer **small, iterative steps**. When I ask for a feature, propose a short plan first, then implement.
- When unsure, ask **one clarifying question** before generating large diffs.
- Always keep changes **scoped** and **coherent** (one feature/fix per diff).

## Code & architecture preferences
- Frontend: React + TypeScript + Vite; organize by feature: `src/features/tasks/{components,api,types}`.
- Backend: Spring Boot + Maven; packages: `controller`, `service`, `repository`, `model`, `config`, `dto`.
- Validation: Bean Validation (`@NotBlank`, `@Size`, etc). Return errors as JSON `{message, field?, code?}`.
- Testing: prefer **Jest + React Testing Library** on frontend; **Spring Boot tests** (JUnit + MockMvc) on backend.
- Style: use async/await; no `any` in TS; no unused code; meaningful names; avoid god classes/components.

## Application logging (implementation requirement)
- **Backend:** add structured logging with slf4j. Use INFO for lifecycle events, DEBUG for flow, WARN/ERROR with context. No secrets in logs.
- **Frontend:** log only when useful for user flows (e.g., API boundary). Prefer small debug helpers over console spam. Strip or gate verbose logs in production builds.

## Tests & coverage (strict)
- **Every diff must maintain 100% coverage** of changed code (statements/branches for backend; lines/branches for frontend).
- Add tests for **happy paths, unhappy paths, and edge cases**. Test behavior (not implementation details).
- If refactoring, **update or add tests first** (TDD encouraged) and keep coverage at 100%.
- Include API tests (validation errors, 4xx/5xx), and serialization contracts for DTOs.
- If coverage tooling is missing, **add it**:
  - Backend: JaCoCo with fail-on-minimum (100% on changed files or overall if simpler).
  - Frontend: Jest with `--coverage` and thresholds set to 100% (lines, branches, functions, statements).

## Logging our collaboration (`/logbook.md`)
- **Always update `/logbook.md` in the repository root** after completing a task or debugging session. **Do not create any other markdowns** for logging. Do not place entries anywhere else.
- **Do NOT** log simple Q&A or pure “explain this code” conversations if they did not lead to code or config changes.
- **DO** log debugging work, incidents, missteps, and fixes (e.g., wrong assumptions, build failures, flaky tests, misconfigurations), including root cause and resolution.
- When I ask for follow-ups that **don’t** change the repository, **do not** update the logbook.
- Append **a concise entry per task or debugging session**, newest entries at the top.

Use this template per entry (no backticks in the actual logbook):
## <YYYY-MM-DDTHH:mm> – <topic>
**Request (paraphrased):** ...
**Context/goal:** ...
**Plan:** ...
**Changes:** ...
**Result:** ...
**Next steps:** ...

> “Changes” should list key files/sections edited. “Result” should include test evidence (e.g., command + summary) and relevant logs if it was a debugging entry.

## Definition of Done (for each task)
- Code compiles/builds, lints cleanly, and passes all tests locally.
- **Coverage 100%** on affected areas; new/changed code fully tested (happy/unhappy/edge).
- API contracts validated and docs/comments updated if applicable.
- Relevant logs added/adjusted (no secrets).
- `/logbook.md` updated as per the template (unless explicitly a no-change Q&A).

## Non-goals / avoid
- Don’t copy my prompt verbatim into the logbook; paraphrase instead.
- Don’t add secrets/tokens anywhere.
- Don’t create additional markdown files for notes or logs—**only** `/logbook.md`.
- Don’t reduce coverage or skip tests to “get it working”.

## How to collaborate in practice
- When I request a feature or fix: propose **a short plan**, ask one clarifying question if needed, **implement**, run tests/coverage, then **update `/logbook.md`**.
- For bugs: reproduce, add a failing test, fix, verify, and **log the debugging session** (symptoms → root cause → fix → proof).
