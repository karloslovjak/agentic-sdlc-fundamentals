# Database Migrations

This directory contains Flyway migration scripts for the Task Manager application.

## Naming Convention

Migration files follow Flyway's naming convention:
```
V{version}__{description}.sql
```

- **V**: Versioned migration prefix (required)
- **{version}**: Numeric version (e.g., 1, 2, 3 or 1.0, 1.1, 2.0)
- **__**: Two underscores separator (required)
- **{description}**: Human-readable description (use underscores for spaces)
- **.sql**: File extension

### Examples
- `V1__create_tasks_table.sql` - Initial schema
- `V2__add_priority_column.sql` - Add new column
- `V3__add_status_index.sql` - Add performance index
- `V4__seed_initial_data.sql` - Insert initial data

## Migration Workflow

### 1. Development
```bash
# H2 database with Hibernate DDL auto (Flyway disabled)
mvn spring-boot:run
```

### 2. Production
```bash
# PostgreSQL with Flyway migrations (Hibernate validates only)
mvn spring-boot:run -Dspring.profiles.active=prod
```

### 3. Creating New Migrations

When you modify the database schema:

1. **Create new migration file** (next version number):
   ```sql
   -- V2__add_priority_column.sql
   ALTER TABLE tasks ADD COLUMN priority VARCHAR(10);
   ```

2. **Test locally** with PostgreSQL:
   ```bash
   # Start PostgreSQL
   docker run -d -p 5432:5432 \
     -e POSTGRES_DB=taskmanager \
     -e POSTGRES_USER=taskmanager_user \
     -e POSTGRES_PASSWORD=changeme \
     postgres:16

   # Run with prod profile
   mvn spring-boot:run -Dspring.profiles.active=prod
   ```

3. **Verify migration**:
   - Check application logs for Flyway migration output
   - Connect to database and verify schema changes
   - Run integration tests

## Flyway Commands

Flyway tracks migrations in the `flyway_schema_history` table:

```sql
-- View migration history
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

### Manual Flyway Operations (if needed)

```bash
# Validate migrations
mvn flyway:validate

# Get migration info
mvn flyway:info

# Migrate to latest version
mvn flyway:migrate

# Clean database (DANGEROUS - only for dev!)
mvn flyway:clean
```

## Best Practices

### ✅ DO
- **Always create new migration files** - never modify existing ones
- **Use descriptive names** for migration files
- **Test migrations** on local PostgreSQL before deploying
- **Keep migrations small** and focused on one change
- **Add comments** to explain complex changes
- **Use transactions** (Flyway wraps each migration automatically)
- **Create indexes** for frequently queried columns
- **Add constraints** to enforce data integrity

### ❌ DON'T
- **Never modify executed migrations** - create new ones instead
- **Don't mix DDL and DML** in the same migration (unless necessary)
- **Don't use database-specific features** without good reason
- **Don't forget to test rollback strategy** (if using paid Flyway)
- **Don't skip version numbers** - keep them sequential

## Migration Examples

### Add Column
```sql
-- V2__add_priority_column.sql
ALTER TABLE tasks
ADD COLUMN priority VARCHAR(10)
CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'));

UPDATE tasks SET priority = 'MEDIUM' WHERE priority IS NULL;

ALTER TABLE tasks ALTER COLUMN priority SET NOT NULL;
```

### Add Index
```sql
-- V3__add_title_index.sql
CREATE INDEX idx_tasks_title ON tasks(title);
```

### Modify Column
```sql
-- V4__increase_description_length.sql
ALTER TABLE tasks ALTER COLUMN description TYPE VARCHAR(5000);
```

### Add Foreign Key
```sql
-- V5__add_users_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE
);

ALTER TABLE tasks ADD COLUMN user_id BIGINT;
ALTER TABLE tasks ADD CONSTRAINT fk_tasks_users
    FOREIGN KEY (user_id) REFERENCES users(id);
```

## Troubleshooting

### Migration Failed
If a migration fails:
1. Check application logs for error details
2. Fix the SQL in the migration file
3. Manually clean the failed migration from `flyway_schema_history`
4. Re-run the application

### Out-of-Order Migrations
Flyway detects out-of-order migrations. Set `out-of-order: true` in config if intentional.

### Baseline Existing Database
If adding Flyway to existing database:
```yaml
spring:
  flyway:
    baseline-on-migrate: true
    baseline-version: 0
```

## Current Migrations

| Version | Description | Date | Status |
|---------|-------------|------|--------|
| V1 | Create tasks table | 2025-10-18 | ✅ Ready |

## Resources

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Spring Boot Flyway Integration](https://docs.spring.io/spring-boot/reference/data/sql.html#data.sql.datasource.migration)
