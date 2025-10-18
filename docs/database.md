# Database Guide

This document covers the database architecture, schema design, migrations, and best practices for the Task Manager application.

## Table of Contents
- [Overview](#overview)
- [Database Options](#database-options)
- [Schema Design](#schema-design)
- [Flyway Migrations](#flyway-migrations)
- [Indexes and Performance](#indexes-and-performance)
- [Data Types and Best Practices](#data-types-and-best-practices)
- [Common Operations](#common-operations)

---

## Overview

Task Manager uses a relational database to store task information with:
- **Development**: H2 in-memory database (default)
- **Production**: PostgreSQL 14+
- **Migrations**: Flyway for version-controlled schema changes
- **ORM**: Spring Data JPA with Hibernate

---

## Database Options

### H2 In-Memory Database (Development)

**Advantages:**
- ✅ Zero setup required
- ✅ Fast test execution
- ✅ Lightweight
- ✅ Built-in web console

**Disadvantages:**
- ❌ Data lost on application restart
- ❌ Not production-ready
- ❌ Different SQL dialect than PostgreSQL

**Configuration:**
```yaml
# application.yml (default profile)
spring:
  datasource:
    url: jdbc:h2:mem:taskmanager
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
```

**Access H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:taskmanager`
- Username: `sa`
- Password: (leave empty)

### PostgreSQL (Production)

**Advantages:**
- ✅ Production-grade RDBMS
- ✅ ACID compliance
- ✅ Advanced features (JSON, full-text search, etc.)
- ✅ Better performance at scale

**Setup with Docker:**
```bash
docker run --name taskmanager-postgres \
  -p 5432:5432 \
  -e POSTGRES_DB=taskmanager \
  -e POSTGRES_USER=taskuser \
  -e POSTGRES_PASSWORD=taskpass \
  -d postgres:16
```

**Configuration:**
```yaml
# application-prod.yml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/taskmanager}
    username: ${SPRING_DATASOURCE_USERNAME:taskuser}
    password: ${SPRING_DATASOURCE_PASSWORD:taskpass}
    driver-class-name: org.postgresql.Driver
```

**Run with PostgreSQL:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## Schema Design

### Tasks Table

The core entity storing task information.

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

### Column Details

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `id` | BIGSERIAL | NO | Auto-incrementing primary key |
| `title` | VARCHAR(200) | NO | Task name/title (max 200 chars) |
| `description` | VARCHAR(2000) | YES | Detailed task description (max 2000 chars) |
| `status` | VARCHAR(20) | NO | Current state: TODO, IN_PROGRESS, or DONE |
| `due_date` | DATE | YES | When task should be completed |
| `created_at` | TIMESTAMP WITH TIME ZONE | NO | When task was created (UTC) |
| `updated_at` | TIMESTAMP WITH TIME ZONE | NO | Last modification time (UTC) |

### Constraints

**Primary Key:**
- `id` - Unique identifier for each task

**Check Constraints:**
- `status` - Must be one of: `TODO`, `IN_PROGRESS`, `DONE`

**Not Null Constraints:**
- `id`, `title`, `status`, `created_at`, `updated_at`

---

## Flyway Migrations

### Overview

Flyway manages database schema versions through SQL migration scripts.

**Location:** `backend/src/main/resources/db/migration/`

**Naming Convention:** `V{version}__{description}.sql`
- Example: `V1__create_tasks_table.sql`

### Configuration

**Development (H2):**
```yaml
spring:
  flyway:
    enabled: false  # Using Hibernate DDL auto
```

**Production (PostgreSQL):**
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    baseline-version: 0
    locations: classpath:db/migration
    validate-on-migrate: true
    create-schemas: true
    default-schema: public
```

### Existing Migrations

**V1__create_tasks_table.sql:**
```sql
-- Create tasks table with all columns and constraints
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    status VARCHAR(20) NOT NULL CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE')),
    due_date DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Create indexes for common queries
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_status_due_date ON tasks(status, due_date);
```

### Creating New Migrations

1. **Create file** in `db/migration/`:
   ```bash
   touch V2__add_priority_column.sql
   ```

2. **Write SQL migration:**
   ```sql
   -- V2__add_priority_column.sql
   ALTER TABLE tasks ADD COLUMN priority VARCHAR(10) DEFAULT 'MEDIUM';
   ALTER TABLE tasks ADD CONSTRAINT check_priority
       CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'));
   ```

3. **Restart application** - Flyway runs automatically

4. **Verify:**
   ```sql
   SELECT * FROM flyway_schema_history;
   ```

### Best Practices

✅ **DO:**
- Use sequential version numbers (V1, V2, V3...)
- Make migrations idempotent when possible
- Test migrations on development database first
- Include rollback scripts in comments
- Keep migrations small and focused

❌ **DON'T:**
- Modify existing migration files (create new ones instead)
- Use database-specific syntax if possible
- Skip version numbers
- Mix DDL and DML in same migration

---

## Indexes and Performance

### Existing Indexes

**Single Column Indexes:**

1. **idx_tasks_status** - Filter by status
   ```sql
   CREATE INDEX idx_tasks_status ON tasks(status);
   ```
   - Query: `SELECT * FROM tasks WHERE status = 'TODO'`
   - Use case: Filter tasks by completion state

2. **idx_tasks_due_date** - Filter by due date
   ```sql
   CREATE INDEX idx_tasks_due_date ON tasks(due_date);
   ```
   - Query: `SELECT * FROM tasks WHERE due_date < NOW()`
   - Use case: Find overdue tasks

**Composite Index:**

3. **idx_tasks_status_due_date** - Filter by status AND due date
   ```sql
   CREATE INDEX idx_tasks_status_due_date ON tasks(status, due_date);
   ```
   - Query: `SELECT * FROM tasks WHERE status = 'TODO' AND due_date < '2025-12-31'`
   - Use case: Find incomplete tasks due soon

### Performance Tips

**Query Optimization:**
- Use `EXPLAIN ANALYZE` to check query plans
- Ensure queries use indexes (check execution plan)
- Avoid `SELECT *` in production code
- Use pagination for large result sets

**Index Maintenance:**
- PostgreSQL auto-vacuums, but monitor performance
- Consider `VACUUM ANALYZE tasks` after bulk operations
- Monitor index usage with `pg_stat_user_indexes`

---

## Data Types and Best Practices

### Timestamps with Time Zones

**Why TIMESTAMP WITH TIME ZONE?**
- ✅ Timezone-independent storage (always UTC)
- ✅ Automatic timezone conversion
- ✅ Supports distributed systems
- ✅ No DST (Daylight Saving Time) issues

**Java Mapping:**
```java
@Column(name = "created_at", nullable = false, updatable = false)
private Instant createdAt;

@Column(name = "updated_at", nullable = false)
private Instant updatedAt;
```

**Automatic Management:**
```java
@PrePersist
protected void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
}

@PreUpdate
protected void onUpdate() {
    updatedAt = Instant.now();
}
```

### VARCHAR Sizing

**Title (200 chars):**
- Short, concise task names
- Prevents excessively long titles
- Indexed efficiently

**Description (2000 chars):**
- Detailed information
- ~400 words in English
- Not indexed (full-text search if needed)

### ENUM via CHECK Constraint

```sql
status VARCHAR(20) NOT NULL CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE'))
```

**Advantages:**
- ✅ Database-level validation
- ✅ Portable across databases
- ✅ Easy to add new values (migration)

**Alternative (PostgreSQL ENUM):**
```sql
CREATE TYPE task_status AS ENUM ('TODO', 'IN_PROGRESS', 'DONE');
ALTER TABLE tasks ALTER COLUMN status TYPE task_status USING status::task_status;
```

---

## Common Operations

### Query Tasks

**All tasks:**
```sql
SELECT * FROM tasks ORDER BY created_at DESC;
```

**Tasks by status:**
```sql
SELECT * FROM tasks WHERE status = 'TODO';
```

**Overdue tasks:**
```sql
SELECT * FROM tasks
WHERE status != 'DONE'
  AND due_date < CURRENT_DATE;
```

**Tasks due this week:**
```sql
SELECT * FROM tasks
WHERE due_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days'
ORDER BY due_date;
```

### Bulk Operations

**Mark all tasks as done:**
```sql
UPDATE tasks SET status = 'DONE', updated_at = NOW() WHERE status != 'DONE';
```

**Delete old completed tasks:**
```sql
DELETE FROM tasks
WHERE status = 'DONE'
  AND updated_at < NOW() - INTERVAL '90 days';
```

### Backup and Restore

**Backup (PostgreSQL):**
```bash
docker exec taskmanager-postgres pg_dump -U taskuser taskmanager > backup.sql
```

**Restore:**
```bash
docker exec -i taskmanager-postgres psql -U taskuser taskmanager < backup.sql
```

---

## Troubleshooting

### Connection Issues

**Check PostgreSQL is running:**
```bash
docker ps --filter name=taskmanager-postgres
```

**Check logs:**
```bash
docker logs taskmanager-postgres
```

**Verify connection:**
```bash
psql -h localhost -U taskuser -d taskmanager
```

### Migration Failures

**Check Flyway history:**
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

**Repair (if migration failed):**
```bash
mvn flyway:repair
```

**Baseline existing database:**
```bash
mvn flyway:baseline
```

### Performance Issues

**Check slow queries (PostgreSQL):**
```sql
SELECT query, calls, total_time, mean_time
FROM pg_stat_statements
ORDER BY total_time DESC
LIMIT 10;
```

**Analyze table statistics:**
```sql
ANALYZE tasks;
```

**Check index usage:**
```sql
SELECT schemaname, tablename, indexname, idx_scan
FROM pg_stat_user_indexes
WHERE schemaname = 'public';
```

---

## Future Enhancements

Potential database improvements:

- [ ] Add full-text search on title and description
- [ ] Add soft delete (deleted_at timestamp)
- [ ] Add user ownership (user_id foreign key)
- [ ] Add tags/categories (many-to-many relationship)
- [ ] Add task priority levels
- [ ] Add task dependencies (parent_id self-reference)
- [ ] Add audit log table
- [ ] Add database-level triggers for audit
- [ ] Implement read replicas for scaling
- [ ] Add connection pooling optimization

---

## Related Documentation

- [Main README](../README.md) - Project overview
- [API Documentation](api.md) - REST endpoints
- [Backend Guide](../backend/README.md) - Architecture and development
- [Deployment Guide](../deployment.md) - Production deployment
