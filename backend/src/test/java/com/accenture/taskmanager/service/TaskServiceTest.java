package com.accenture.taskmanager.service;

import com.accenture.taskmanager.exception.TaskNotFoundException;
import com.accenture.taskmanager.model.Task;
import com.accenture.taskmanager.model.TaskStatus;
import com.accenture.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Service layer tests for TaskService.
 *
 * Tests business logic, exception handling, and repository interactions.
 * Uses Mockito to mock repository dependencies.
 *
 * Test Coverage:
 * - Happy paths: CRUD operations work correctly
 * - Unhappy paths: TaskNotFoundException handling
 * - Edge cases: Empty lists, null handling, boundary conditions
 * - Repository interactions: Verify correct method calls
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    // ========================================
    // Happy Path Tests
    // ========================================

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        // Given
        Task task1 = createTask(1L, "Task 1", TaskStatus.TODO);
        Task task2 = createTask(2L, "Task 2", TaskStatus.IN_PROGRESS);
        List<Task> expectedTasks = Arrays.asList(task1, task2);

        when(taskRepository.findAll()).thenReturn(expectedTasks);

        // When
        List<Task> actualTasks = taskService.getAllTasks();

        // Then
        assertThat(actualTasks)
                .hasSize(2)
                .containsExactly(task1, task2);
        verify(taskRepository).findAll();
    }

    @Test
    void getAllTasks_shouldReturnEmptyListWhenNoTasks() {
        // Given
        when(taskRepository.findAll()).thenReturn(List.of());

        // When
        List<Task> actualTasks = taskService.getAllTasks();

        // Then
        assertThat(actualTasks).isEmpty();
        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_shouldReturnTaskWhenExists() {
        // Given
        Task expectedTask = createTask(1L, "Test Task", TaskStatus.TODO);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(expectedTask));

        // When
        Task actualTask = taskService.getTaskById(1L);

        // Then
        assertThat(actualTask).isEqualTo(expectedTask);
        assertThat(actualTask.getId()).isEqualTo(1L);
        assertThat(actualTask.getTitle()).isEqualTo("Test Task");
        verify(taskRepository).findById(1L);
    }

    @Test
    void createTask_shouldSaveAndReturnTask() {
        // Given
        Task newTask = createTask(null, "New Task", TaskStatus.TODO);
        Task savedTask = createTask(1L, "New Task", TaskStatus.TODO);

        when(taskRepository.save(newTask)).thenReturn(savedTask);

        // When
        Task actualTask = taskService.createTask(newTask);

        // Then
        assertThat(actualTask).isNotNull();
        assertThat(actualTask.getId()).isEqualTo(1L);
        assertThat(actualTask.getTitle()).isEqualTo("New Task");
        assertThat(actualTask.getStatus()).isEqualTo(TaskStatus.TODO);
        verify(taskRepository).save(newTask);
    }

    @Test
    void updateTask_shouldUpdateAndReturnTask() {
        // Given
        Task existingTask = createTask(1L, "Old Title", TaskStatus.TODO);
        Task updateData = createTask(null, "New Title", TaskStatus.IN_PROGRESS);
        updateData.setDescription("Updated description");
        updateData.setDueDate(LocalDate.of(2025, 12, 31));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Task updatedTask = taskService.updateTask(1L, updateData);

        // Then
        assertThat(updatedTask.getTitle()).isEqualTo("New Title");
        assertThat(updatedTask.getDescription()).isEqualTo("Updated description");
        assertThat(updatedTask.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(updatedTask.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(existingTask);
    }

    @Test
    void deleteTask_shouldDeleteWhenTaskExists() {
        // Given
        Task task = createTask(1L, "Task to delete", TaskStatus.TODO);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(task);
    }

    // ========================================
    // Unhappy Path Tests - TaskNotFoundException
    // ========================================

    @Test
    void getTaskById_shouldThrowExceptionWhenTaskNotFound() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found with id: 999");
        verify(taskRepository).findById(999L);
    }

    @Test
    void updateTask_shouldThrowExceptionWhenTaskNotFound() {
        // Given
        Task updateData = createTask(null, "Updated Title", TaskStatus.DONE);
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> taskService.updateTask(999L, updateData))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found with id: 999");
        verify(taskRepository).findById(999L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_shouldThrowExceptionWhenTaskNotFound() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Task not found with id: 999");
        verify(taskRepository).findById(999L);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    // ========================================
    // Edge Case Tests
    // ========================================

    @Test
    void updateTask_shouldHandleNullDescription() {
        // Given
        Task existingTask = createTask(1L, "Task", TaskStatus.TODO);
        existingTask.setDescription("Original description");

        Task updateData = createTask(null, "Updated Task", TaskStatus.IN_PROGRESS);
        updateData.setDescription(null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Task updatedTask = taskService.updateTask(1L, updateData);

        // Then
        assertThat(updatedTask.getDescription()).isNull();
        verify(taskRepository).save(existingTask);
    }

    @Test
    void updateTask_shouldHandleNullDueDate() {
        // Given
        Task existingTask = createTask(1L, "Task", TaskStatus.TODO);
        existingTask.setDueDate(LocalDate.of(2025, 12, 31));

        Task updateData = createTask(null, "Updated Task", TaskStatus.DONE);
        updateData.setDueDate(null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Task updatedTask = taskService.updateTask(1L, updateData);

        // Then
        assertThat(updatedTask.getDueDate()).isNull();
        verify(taskRepository).save(existingTask);
    }

    @Test
    void updateTask_shouldUpdateAllStatusTypes() {
        // Given
        Task existingTask = createTask(1L, "Task", TaskStatus.TODO);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Test updating to each status
        for (TaskStatus status : TaskStatus.values()) {
            // Given
            Task updateData = createTask(null, "Task", status);

            // When
            Task updatedTask = taskService.updateTask(1L, updateData);

            // Then
            assertThat(updatedTask.getStatus()).isEqualTo(status);
        }

        verify(taskRepository, times(TaskStatus.values().length)).save(existingTask);
    }

    @Test
    void createTask_shouldPreserveAllFields() {
        // Given
        Task newTask = new Task();
        newTask.setTitle("Comprehensive Task");
        newTask.setDescription("Detailed description");
        newTask.setStatus(TaskStatus.IN_PROGRESS);
        newTask.setDueDate(LocalDate.of(2025, 12, 31));

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Comprehensive Task");
        savedTask.setDescription("Detailed description");
        savedTask.setStatus(TaskStatus.IN_PROGRESS);
        savedTask.setDueDate(LocalDate.of(2025, 12, 31));
        savedTask.setCreatedAt(Instant.now());
        savedTask.setUpdatedAt(Instant.now());

        when(taskRepository.save(newTask)).thenReturn(savedTask);

        // When
        Task result = taskService.createTask(newTask);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Comprehensive Task");
        assertThat(result.getDescription()).isEqualTo("Detailed description");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(result.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    // ========================================
    // Repository Interaction Tests
    // ========================================

    @Test
    void getAllTasks_shouldCallRepositoryOnce() {
        // Given
        when(taskRepository.findAll()).thenReturn(List.of());

        // When
        taskService.getAllTasks();

        // Then
        verify(taskRepository, times(1)).findAll();
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void getTaskById_shouldCallRepositoryOnce() {
        // Given
        Task task = createTask(1L, "Task", TaskStatus.TODO);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // When
        taskService.getTaskById(1L);

        // Then
        verify(taskRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void createTask_shouldCallSaveOnce() {
        // Given
        Task task = createTask(null, "New Task", TaskStatus.TODO);
        when(taskRepository.save(task)).thenReturn(task);

        // When
        taskService.createTask(task);

        // Then
        verify(taskRepository, times(1)).save(task);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void updateTask_shouldCallFindByIdAndSaveOnce() {
        // Given
        Task existingTask = createTask(1L, "Old", TaskStatus.TODO);
        Task updateData = createTask(null, "New", TaskStatus.DONE);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        // When
        taskService.updateTask(1L, updateData);

        // Then
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(existingTask);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void deleteTask_shouldCallFindByIdAndDeleteOnce() {
        // Given
        Task task = createTask(1L, "Task", TaskStatus.TODO);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).delete(task);
        verifyNoMoreInteractions(taskRepository);
    }

    // ========================================
    // Helper Methods
    // ========================================

    private Task createTask(Long id, String title, TaskStatus status) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("Description for " + title);
        task.setStatus(status);
        task.setDueDate(LocalDate.of(2025, 10, 31));
        task.setCreatedAt(Instant.parse("2025-10-18T10:00:00Z"));
        task.setUpdatedAt(Instant.parse("2025-10-18T10:00:00Z"));
        return task;
    }
}
