package com.accenture.taskmanager.service;

import com.accenture.taskmanager.exception.TaskNotFoundException;
import com.accenture.taskmanager.model.Task;
import com.accenture.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Task business logic.
 *
 * Handles all business operations related to tasks.
 * Acts as the intermediary between controllers and repositories.
 *
 * Architecture:
 * - @Transactional ensures database consistency
 * - Business logic and validation beyond simple field checks
 * - Orchestrates repository operations
 * - Throws domain exceptions (TaskNotFoundException)
 * - Logging for observability
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Retrieve all tasks.
     *
     * @return list of all tasks
     */
    public List<Task> getAllTasks() {
        log.debug("Fetching all tasks");
        return taskRepository.findAll();
    }

    /**
     * Retrieve a task by ID.
     *
     * @param id the task ID
     * @return the task
     * @throws TaskNotFoundException if task not found
     */
    public Task getTaskById(Long id) {
        log.debug("Fetching task with id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    /**
     * Create a new task.
     *
     * Persists the task to the database.
     * createdAt and updatedAt are set automatically by JPA @PrePersist.
     *
     * @param task the task to create (without ID)
     * @return the created task (with generated ID and timestamps)
     */
    @Transactional
    public Task createTask(Task task) {
        log.info("Creating new task with title: {}", task.getTitle());
        Task savedTask = taskRepository.save(task);
        log.info("Task created with id: {}", savedTask.getId());
        return savedTask;
    }

    /**
     * Update an existing task.
     *
     * Updates all fields of the task.
     * updatedAt is set automatically by JPA @PreUpdate.
     *
     * @param id   the task ID to update
     * @param task the task with updated values
     * @return the updated task
     * @throws TaskNotFoundException if task not found
     */
    @Transactional
    public Task updateTask(Long id, Task task) {
        log.info("Updating task with id: {}", id);

        // Verify task exists
        Task existingTask = getTaskById(id);

        // Update fields
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setStatus(task.getStatus());
        existingTask.setDueDate(task.getDueDate());

        Task updatedTask = taskRepository.save(existingTask);
        log.info("Task updated with id: {}", updatedTask.getId());
        return updatedTask;
    }

    /**
     * Delete a task.
     *
     * Removes the task from the database.
     *
     * @param id the task ID to delete
     * @throws TaskNotFoundException if task not found
     */
    @Transactional
    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);

        // Verify task exists before deleting
        Task task = getTaskById(id);

        taskRepository.delete(task);
        log.info("Task deleted with id: {}", id);
    }

}
