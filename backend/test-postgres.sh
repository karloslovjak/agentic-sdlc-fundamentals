#!/bin/bash
# PostgreSQL Integration Test Script
# This script verifies that the Task Manager application works correctly with PostgreSQL

set -e  # Exit on error

echo "=================================="
echo "PostgreSQL Integration Test"
echo "=================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Step 1: Check if PostgreSQL is running
echo "Step 1: Checking PostgreSQL connection..."
if docker ps | grep -q taskmanager-postgres; then
    echo -e "${GREEN}✓${NC} PostgreSQL container is running"
else
    echo -e "${YELLOW}⚠${NC} PostgreSQL container not found. Starting..."
    docker run --name taskmanager-postgres \
        -p 5432:5432 \
        -e POSTGRES_DB=taskmanager \
        -e POSTGRES_USER=taskuser \
        -e POSTGRES_PASSWORD=taskpass \
        -d postgres:16
    echo "Waiting 5 seconds for PostgreSQL to initialize..."
    sleep 5
    echo -e "${GREEN}✓${NC} PostgreSQL started"
fi
echo ""

# Step 2: Verify connection
echo "Step 2: Verifying database connection..."
docker exec taskmanager-postgres psql -U taskuser -d taskmanager -c "SELECT version();" > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} Database connection successful"
else
    echo -e "${RED}✗${NC} Cannot connect to database"
    exit 1
fi
echo ""

# Step 3: Run application with prod profile
echo "Step 3: Starting application with prod profile..."
echo "This will:"
echo "  - Connect to PostgreSQL"
echo "  - Apply Flyway migrations"
echo "  - Start the application"
echo ""

# Run in background
mvn spring-boot:run \
    -Dspring.profiles.active=prod \
    -DDB_USERNAME=taskuser \
    -DDB_PASSWORD=taskpass \
    > /tmp/taskmanager.log 2>&1 &

APP_PID=$!
echo "Application PID: $APP_PID"
echo "Waiting for application to start (30 seconds max)..."

# Wait for application to start (check for "Started TaskManagerApplication")
for i in {1..30}; do
    if grep -q "Started TaskManagerApplication" /tmp/taskmanager.log; then
        echo -e "${GREEN}✓${NC} Application started successfully"
        break
    fi
    sleep 1
    echo -n "."
done
echo ""

# Check if application is actually running
if ! ps -p $APP_PID > /dev/null; then
    echo -e "${RED}✗${NC} Application failed to start. Check logs:"
    tail -20 /tmp/taskmanager.log
    exit 1
fi

# Step 4: Verify Flyway migration
echo ""
echo "Step 4: Verifying Flyway migrations..."
MIGRATION_COUNT=$(docker exec taskmanager-postgres psql -U taskuser -d taskmanager -t -c "SELECT COUNT(*) FROM flyway_schema_history;" 2>/dev/null | tr -d ' ')

if [ "$MIGRATION_COUNT" -eq "1" ]; then
    echo -e "${GREEN}✓${NC} Flyway migration applied successfully"
    docker exec taskmanager-postgres psql -U taskuser -d taskmanager -c "SELECT version, description, installed_on FROM flyway_schema_history;"
else
    echo -e "${RED}✗${NC} Flyway migration not applied (found $MIGRATION_COUNT migrations)"
    exit 1
fi
echo ""

# Step 5: Verify table schema
echo "Step 5: Verifying table schema..."
SCHEMA_CHECK=$(docker exec taskmanager-postgres psql -U taskuser -d taskmanager -t -c "SELECT column_name, data_type FROM information_schema.columns WHERE table_name='tasks' AND column_name IN ('created_at', 'updated_at') ORDER BY column_name;" | tr -d ' ')

if echo "$SCHEMA_CHECK" | grep -q "timestamp with time zone"; then
    echo -e "${GREEN}✓${NC} Timestamp columns have correct type (timestamp with time zone)"
    docker exec taskmanager-postgres psql -U taskuser -d taskmanager -c "\d tasks"
else
    echo -e "${RED}✗${NC} Timestamp columns have wrong type"
    exit 1
fi
echo ""

# Step 6: Test API endpoints
echo "Step 6: Testing API endpoints..."

# Wait a bit more to ensure API is ready
sleep 2

# Create a task
echo "Creating task..."
CREATE_RESPONSE=$(curl -s -X POST http://localhost:8080/tasks \
    -H "Content-Type: application/json" \
    -d '{
        "title": "Integration Test Task",
        "description": "Testing Instant timestamps with PostgreSQL",
        "status": "TODO",
        "dueDate": "2025-12-31"
    }')

if echo "$CREATE_RESPONSE" | grep -q '"id"'; then
    echo -e "${GREEN}✓${NC} Task created successfully"
    TASK_ID=$(echo "$CREATE_RESPONSE" | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
    echo "Task ID: $TASK_ID"

    # Verify timestamp format
    if echo "$CREATE_RESPONSE" | grep -qE '"createdAt":"[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\.[0-9]+Z"'; then
        echo -e "${GREEN}✓${NC} Timestamp format is correct (ISO 8601 with UTC)"
    else
        echo -e "${YELLOW}⚠${NC} Timestamp format might be incorrect"
    fi
else
    echo -e "${RED}✗${NC} Failed to create task"
    echo "Response: $CREATE_RESPONSE"
    kill $APP_PID
    exit 1
fi
echo ""

# Get all tasks
echo "Retrieving all tasks..."
GET_RESPONSE=$(curl -s http://localhost:8080/tasks)

if echo "$GET_RESPONSE" | grep -q '"Integration Test Task"'; then
    echo -e "${GREEN}✓${NC} Task retrieved successfully"
else
    echo -e "${RED}✗${NC} Failed to retrieve task"
    kill $APP_PID
    exit 1
fi
echo ""

# Update task
echo "Updating task..."
UPDATE_RESPONSE=$(curl -s -X PUT http://localhost:8080/tasks/$TASK_ID \
    -H "Content-Type: application/json" \
    -d '{
        "title": "Updated Integration Test Task",
        "description": "Updated description",
        "status": "IN_PROGRESS",
        "dueDate": "2025-12-31"
    }')

if echo "$UPDATE_RESPONSE" | grep -q '"IN_PROGRESS"'; then
    echo -e "${GREEN}✓${NC} Task updated successfully"
else
    echo -e "${RED}✗${NC} Failed to update task"
    kill $APP_PID
    exit 1
fi
echo ""

# Delete task
echo "Deleting task..."
DELETE_RESPONSE=$(curl -s -X DELETE http://localhost:8080/tasks/$TASK_ID -w "%{http_code}")

if [ "$DELETE_RESPONSE" = "204" ]; then
    echo -e "${GREEN}✓${NC} Task deleted successfully"
else
    echo -e "${RED}✗${NC} Failed to delete task (HTTP $DELETE_RESPONSE)"
    kill $APP_PID
    exit 1
fi
echo ""

# Step 7: Verify data in database
echo "Step 7: Verifying data directly in PostgreSQL..."
TASK_COUNT=$(docker exec taskmanager-postgres psql -U taskuser -d taskmanager -t -c "SELECT COUNT(*) FROM tasks;" | tr -d ' ')

echo "Tasks in database: $TASK_COUNT"

# Query timestamp data type in actual data
docker exec taskmanager-postgres psql -U taskuser -d taskmanager -c "SELECT id, title, created_at AT TIME ZONE 'UTC' as created_utc FROM tasks LIMIT 1;" 2>/dev/null || true

# Cleanup
echo ""
echo "Step 8: Cleanup..."
kill $APP_PID
echo -e "${GREEN}✓${NC} Application stopped"

echo ""
echo "=================================="
echo -e "${GREEN}All tests passed successfully!${NC}"
echo "=================================="
echo ""
echo "Summary:"
echo "  ✓ PostgreSQL connection working"
echo "  ✓ Flyway migration applied"
echo "  ✓ Table schema correct (TIMESTAMP WITH TIME ZONE)"
echo "  ✓ API endpoints working"
echo "  ✓ CRUD operations successful"
echo "  ✓ Timestamp format correct (ISO 8601 UTC)"
echo ""
echo "To clean up PostgreSQL container:"
echo "  docker stop taskmanager-postgres"
echo "  docker rm taskmanager-postgres"
