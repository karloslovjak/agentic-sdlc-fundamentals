# Using Flyway with PostgreSQL

This guide shows how to run the Task Manager application with Flyway-managed PostgreSQL migrations.

## Quick Start with Docker

### 1. Start PostgreSQL Container

```bash
docker run -d \
  --name taskmanager-postgres \
  -p 5432:5432 \
  -e POSTGRES_DB=taskmanager \
  -e POSTGRES_USER=taskmanager_user \
  -e POSTGRES_PASSWORD=changeme \
  postgres:16
```

### 2. Run Application with Production Profile

```bash
cd backend
mvn spring-boot:run -Dspring.profiles.active=prod
```

### 3. Verify Flyway Migration

Check the application logs for:
```
Flyway Community Edition x.x.x by Redgate
Database: jdbc:postgresql://localhost:5432/taskmanager (PostgreSQL 16.x)
Successfully validated 1 migration (execution time 00:00.012s)
Creating Schema History table "public"."flyway_schema_history" ...
Current version of schema "public": << Empty Schema >>
Migrating schema "public" to version "1 - create tasks table"
Successfully applied 1 migration to schema "public", now at version v1 (execution time 00:00.056s)
```

### 4. Verify Database Schema

Connect to PostgreSQL:
```bash
docker exec -it taskmanager-postgres psql -U taskmanager_user -d taskmanager
```

Check tables:
```sql
-- List all tables
\dt

-- View tasks table structure
\d tasks

-- View migration history
SELECT * FROM flyway_schema_history;
```

Expected output:
```
                      Table "public.tasks"
   Column    |            Type             | Collation | Nullable |
-------------+-----------------------------+-----------+----------+
 id          | bigint                      |           | not null |
 title       | character varying(200)      |           | not null |
 description | character varying(2000)     |           |          |
 status      | character varying(20)       |           | not null |
 due_date    | date                        |           |          |
 created_at  | timestamp without time zone |           | not null |
 updated_at  | timestamp without time zone |           | not null |

Indexes:
    "tasks_pkey" PRIMARY KEY, btree (id)
    "idx_tasks_due_date" btree (due_date)
    "idx_tasks_status" btree (status)
    "idx_tasks_status_due_date" btree (status, due_date)
```

## Configuration Profiles

### Development (Default)
- **Database**: H2 in-memory
- **Flyway**: Disabled
- **DDL Auto**: create-drop
- **Use Case**: Local development, fast iteration

```bash
mvn spring-boot:run
# or
java -jar target/task-manager-0.0.1-SNAPSHOT.jar
```

### Production
- **Database**: PostgreSQL
- **Flyway**: Enabled
- **DDL Auto**: validate
- **Use Case**: Production deployment, controlled migrations

```bash
mvn spring-boot:run -Dspring.profiles.active=prod
# or
java -jar target/task-manager-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Test
- **Database**: H2 in-memory
- **Flyway**: Disabled
- **DDL Auto**: create-drop
- **Use Case**: Running tests

```bash
mvn test
```

## Environment Variables

Override database credentials via environment variables:

```bash
export DB_USERNAME=my_user
export DB_PASSWORD=my_secure_password

mvn spring-boot:run -Dspring.profiles.active=prod
```

Or inline:
```bash
DB_USERNAME=my_user DB_PASSWORD=my_pass mvn spring-boot:run -Dspring.profiles.active=prod
```

## Creating New Migrations

### Example: Add Priority Column

1. **Create migration file**:
```bash
touch backend/src/main/resources/db/migration/V2__add_priority_column.sql
```

2. **Write SQL**:
```sql
-- V2__add_priority_column.sql
ALTER TABLE tasks
ADD COLUMN priority VARCHAR(10)
CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'));

-- Set default for existing rows
UPDATE tasks SET priority = 'MEDIUM' WHERE priority IS NULL;

-- Make it required
ALTER TABLE tasks ALTER COLUMN priority SET NOT NULL;

-- Add index
CREATE INDEX idx_tasks_priority ON tasks(priority);
```

3. **Restart application**:
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

Flyway will automatically detect and apply V2 migration.

## Troubleshooting

### Connection Refused
```
Error: Connection to localhost:5432 refused
```
**Solution**: Ensure PostgreSQL container is running
```bash
docker ps | grep taskmanager-postgres
# If not running, start it
docker start taskmanager-postgres
```

### Authentication Failed
```
Error: password authentication failed for user "taskmanager_user"
```
**Solution**: Check credentials in `application-prod.yml` or environment variables

### Migration Checksum Mismatch
```
Error: Migration checksum mismatch
```
**Solution**: Never modify applied migrations. Create new migration to fix issues.

### Clean Database (Development Only!)
```bash
# WARNING: This deletes ALL data!
docker exec -it taskmanager-postgres psql -U taskmanager_user -d taskmanager -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

# Then restart application
mvn spring-boot:run -Dspring.profiles.active=prod
```

## Useful Commands

### View Flyway Status
```bash
mvn flyway:info -Dflyway.url=jdbc:postgresql://localhost:5432/taskmanager \
  -Dflyway.user=taskmanager_user \
  -Dflyway.password=changeme
```

### Validate Migrations
```bash
mvn flyway:validate -Dflyway.url=jdbc:postgresql://localhost:5432/taskmanager \
  -Dflyway.user=taskmanager_user \
  -Dflyway.password=changeme
```

### Manual Migration (Not Recommended)
```bash
mvn flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/taskmanager \
  -Dflyway.user=taskmanager_user \
  -Dflyway.password=changeme
```

## Testing with Swagger UI

1. **Start application** with PostgreSQL
2. **Open Swagger UI**: http://localhost:8080/swagger-ui.html
3. **Create a task**:
   ```json
   {
     "title": "Test Task",
     "description": "Created via Swagger",
     "status": "TODO",
     "dueDate": "2025-12-31"
   }
   ```
4. **Query database**:
   ```sql
   SELECT * FROM tasks;
   ```

## Production Deployment

### Docker Compose (Recommended)

Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: taskmanager
      POSTGRES_USER: taskmanager_user
      POSTGRES_PASSWORD: changeme
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_USERNAME: taskmanager_user
      DB_PASSWORD: changeme
    depends_on:
      - postgres

volumes:
  postgres_data:
```

Run:
```bash
docker-compose up -d
```

## Summary

- **Development**: H2 + Hibernate DDL auto → Fast iteration
- **Production**: PostgreSQL + Flyway → Safe, versioned migrations
- **Testing**: H2 + Hibernate DDL auto → Isolated, repeatable tests

Flyway ensures your database schema is versioned, auditable, and consistently deployed across environments.
