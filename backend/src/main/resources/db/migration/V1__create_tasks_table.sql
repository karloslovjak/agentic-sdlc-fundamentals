-- Initial schema creation for Task Manager
-- Creates the tasks table with all necessary columns and constraints

-- ========================================
-- Tasks Table
-- ========================================
CREATE TABLE tasks (
    -- Primary key: auto-incrementing ID
    id BIGSERIAL PRIMARY KEY,

    -- Task content fields
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),

    -- Status: enum-like constraint
    status VARCHAR(20) NOT NULL CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE')),

    -- Due date
    due_date DATE,

    -- Audit timestamps (timezone-aware for distributed systems)
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- ========================================
-- Indexes for Performance
-- ========================================

-- Index on status for filtering tasks by status
CREATE INDEX idx_tasks_status ON tasks(status);

-- Index on due_date for sorting and filtering by date
CREATE INDEX idx_tasks_due_date ON tasks(due_date);

-- Composite index for common query: filter by status and sort by due date
CREATE INDEX idx_tasks_status_due_date ON tasks(status, due_date);

-- ========================================
-- Comments for Documentation
-- ========================================

COMMENT ON TABLE tasks IS 'Main table for storing task information';
COMMENT ON COLUMN tasks.id IS 'Unique identifier for the task';
COMMENT ON COLUMN tasks.title IS 'Task title (max 200 characters)';
COMMENT ON COLUMN tasks.description IS 'Detailed task description (max 2000 characters)';
COMMENT ON COLUMN tasks.status IS 'Current status: TODO, IN_PROGRESS, or DONE';
COMMENT ON COLUMN tasks.due_date IS 'Target completion date';
COMMENT ON COLUMN tasks.created_at IS 'Timestamp when task was created';
COMMENT ON COLUMN tasks.updated_at IS 'Timestamp when task was last modified';
