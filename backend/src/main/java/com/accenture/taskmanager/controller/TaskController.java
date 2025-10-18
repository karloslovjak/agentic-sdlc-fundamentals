package com.accenture.taskmanager.controller;

import com.accenture.taskmanager.api.TasksApi;
import com.accenture.taskmanager.api.model.TaskRequest;
import com.accenture.taskmanager.api.model.TaskResponse;
import com.accenture.taskmanager.mapper.TaskMapper;
import com.accenture.taskmanager.model.Task;
import com.accenture.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for Task operations.
 *
 * Implements the generated TasksApi interface from OpenAPI specification.
 * Handles HTTP requests and delegates business logic to TaskService.
 *
 * Architecture:
 * - Implements interface from generated OpenAPI code for type safety
 * - Maps between API models (TaskRequest/Response) and Entity (Task) using
 * MapStruct
 * - Thin controller - business logic in service layer
 * - Returns appropriate HTTP status codes
 * - Exception handling delegated to GlobalExceptionHandler
 *
 * Base path: /api/tasks (from OpenAPI spec)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class TaskController implements TasksApi {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    /**
     * GET /api/tasks - Retrieve all tasks.
     *
     * @return 200 OK with list of tasks
     */
    @Override
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        log.debug("REST request to get all tasks");

        List<Task> tasks = taskService.getAllTasks();
        List<TaskResponse> responses = tasks.stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * POST /api/tasks - Create a new task.
     *
     * @param taskRequest the task to create
     * @return 201 CREATED with the created task
     */
    @Override
    public ResponseEntity<TaskResponse> createTask(@Valid TaskRequest taskRequest) {
        log.debug("REST request to create task: {}", taskRequest);

        Task task = taskMapper.toEntity(taskRequest);
        Task createdTask = taskService.createTask(task);
        TaskResponse response = taskMapper.toResponse(createdTask);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/tasks/{id} - Retrieve a task by ID.
     *
     * @param id the task ID
     * @return 200 OK with the task, or 404 NOT_FOUND if not exists
     */
    @Override
    public ResponseEntity<TaskResponse> getTaskById(Long id) {
        log.debug("REST request to get task: {}", id);

        Task task = taskService.getTaskById(id);
        TaskResponse response = taskMapper.toResponse(task);

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/tasks/{id} - Update an existing task.
     *
     * @param id          the task ID
     * @param taskRequest the updated task data
     * @return 200 OK with the updated task, or 404 NOT_FOUND if not exists
     */
    @Override
    public ResponseEntity<TaskResponse> updateTask(Long id, @Valid TaskRequest taskRequest) {
        log.debug("REST request to update task: {}", id);

        Task task = taskMapper.toEntity(taskRequest);
        Task updatedTask = taskService.updateTask(id, task);
        TaskResponse response = taskMapper.toResponse(updatedTask);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/tasks/{id} - Delete a task.
     *
     * @param id the task ID
     * @return 204 NO_CONTENT on success, or 404 NOT_FOUND if not exists
     */
    @Override
    public ResponseEntity<Void> deleteTask(Long id) {
        log.debug("REST request to delete task: {}", id);

        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }

}
