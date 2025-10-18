package com.accenture.taskmanager.controller;

import com.accenture.taskmanager.api.model.TaskRequest;
import com.accenture.taskmanager.api.model.TaskResponse;
import com.accenture.taskmanager.exception.TaskNotFoundException;
import com.accenture.taskmanager.mapper.TaskMapper;
import com.accenture.taskmanager.model.Task;
import com.accenture.taskmanager.model.TaskStatus;
import com.accenture.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller layer tests for TaskController using MockMvc.
 * Tests the `/api/tasks` endpoint paths, verifying correct HTTP mapping and
 * routing.
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskMapper taskMapper;

    @Test
    void getAllTasks_shouldReturnEmptyList() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllTasks_shouldReturnTaskList() throws Exception {
        Task task1 = createTask(1L, "Task 1", TaskStatus.TODO);
        Task task2 = createTask(2L, "Task 2", TaskStatus.IN_PROGRESS);
        TaskResponse response1 = createTaskResponse(1L, "Task 1");
        TaskResponse response2 = createTaskResponse(2L, "Task 2");

        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));
        when(taskMapper.toResponse(task1)).thenReturn(response1);
        when(taskMapper.toResponse(task2)).thenReturn(response2);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Task 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Task 2")));
    }

    @Test
    void getTaskById_shouldReturnTask() throws Exception {
        Task task = createTask(1L, "Test Task", TaskStatus.TODO);
        TaskResponse response = createTaskResponse(1L, "Test Task");

        when(taskService.getTaskById(1L)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")));
    }

    @Test
    void getTaskById_shouldReturn404WhenNotFound() throws Exception {
        when(taskService.getTaskById(999L)).thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Task not found")));
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        String requestJson = """
                {
                    "title": "New Task",
                    "description": "Task description",
                    "status": "TODO",
                    "dueDate": "2025-12-31"
                }
                """;

        Task task = createTask(null, "New Task", TaskStatus.TODO);
        Task createdTask = createTask(1L, "New Task", TaskStatus.TODO);
        TaskResponse response = createTaskResponse(1L, "New Task");

        when(taskMapper.toEntity(any(TaskRequest.class))).thenReturn(task);
        when(taskService.createTask(any(Task.class))).thenReturn(createdTask);
        when(taskMapper.toResponse(createdTask)).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("New Task")));
    }

    @Test
    void createTask_shouldReturn400WhenTitleIsBlank() throws Exception {
        String requestJson = """
                {
                    "title": "",
                    "description": "Task description",
                    "status": "TODO"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateTask_shouldReturnUpdatedTask() throws Exception {
        String requestJson = """
                {
                    "title": "Updated Task",
                    "description": "Updated description",
                    "status": "IN_PROGRESS",
                    "dueDate": "2025-12-31"
                }
                """;

        Task task = createTask(null, "Updated Task", TaskStatus.IN_PROGRESS);
        Task updatedTask = createTask(1L, "Updated Task", TaskStatus.IN_PROGRESS);
        TaskResponse response = createTaskResponse(1L, "Updated Task");

        when(taskMapper.toEntity(any(TaskRequest.class))).thenReturn(task);
        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toResponse(updatedTask)).thenReturn(response);

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Task")));
    }

    @Test
    void updateTask_shouldReturn404WhenNotFound() throws Exception {
        String requestJson = """
                {
                    "title": "Updated Task",
                    "description": "Updated description",
                    "status": "IN_PROGRESS"
                }
                """;

        Task task = createTask(null, "Updated Task", TaskStatus.IN_PROGRESS);
        when(taskMapper.toEntity(any(TaskRequest.class))).thenReturn(task);
        when(taskService.updateTask(eq(999L), any(Task.class)))
                .thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(put("/api/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Task not found")));
    }

    @Test
    void deleteTask_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    void deleteTask_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new TaskNotFoundException(999L)).when(taskService).deleteTask(999L);

        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Task not found")));
    }

    @Test
    void createTask_shouldHandleTimestampFormatCorrectly() throws Exception {
        String requestJson = """
                {
                    "title": "New Task",
                    "description": "Test timestamps",
                    "status": "TODO"
                }
                """;

        Task task = createTask(null, "New Task", TaskStatus.TODO);
        Task createdTask = createTask(1L, "New Task", TaskStatus.TODO);
        TaskResponse response = createTaskResponse(1L, "New Task");

        when(taskMapper.toEntity(any(TaskRequest.class))).thenReturn(task);
        when(taskService.createTask(any(Task.class))).thenReturn(createdTask);
        when(taskMapper.toResponse(createdTask)).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdAt", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*Z")))
                .andExpect(jsonPath("$.updatedAt", matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*Z")));
    }

    private Task createTask(Long id, String title, TaskStatus status) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("Description for " + title);
        task.setStatus(status);
        task.setDueDate(LocalDate.of(2025, 12, 31));
        task.setCreatedAt(Instant.parse("2025-10-18T10:00:00Z"));
        task.setUpdatedAt(Instant.parse("2025-10-18T10:00:00Z"));
        return task;
    }

    private TaskResponse createTaskResponse(Long id, String title) {
        TaskResponse response = new TaskResponse();
        response.setId(id);
        response.setTitle(title);
        response.setDescription("Description for " + title);
        response.setStatus(com.accenture.taskmanager.api.model.TaskStatus.TODO);
        response.setDueDate(LocalDate.of(2025, 12, 31));
        response.setCreatedAt(OffsetDateTime.of(2025, 10, 18, 10, 0, 0, 0, ZoneOffset.UTC));
        response.setUpdatedAt(OffsetDateTime.of(2025, 10, 18, 10, 0, 0, 0, ZoneOffset.UTC));
        return response;
    }
}
