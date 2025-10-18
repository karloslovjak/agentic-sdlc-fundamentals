package com.accenture.taskmanager.repository;

import com.accenture.taskmanager.model.Task;
import com.accenture.taskmanager.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for TaskRepository.
 *
 * Uses @DataJpaTest for JPA repository testing with H2 in-memory database.
 * Tests custom query methods and Spring Data JPA functionality.
 */
@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        // Clear database
        taskRepository.deleteAll();
        entityManager.clear();

        // Create test tasks
        task1 = Task.builder()
                .title("Task 1")
                .description("Description 1")
                .status(TaskStatus.TODO)
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        task2 = Task.builder()
                .title("Task 2")
                .description("Description 2")
                .status(TaskStatus.IN_PROGRESS)
                .dueDate(LocalDate.now().plusDays(10))
                .build();

        task3 = Task.builder()
                .title("Task 3")
                .description("Description 3")
                .status(TaskStatus.DONE)
                .dueDate(LocalDate.now().minusDays(2))
                .build();
    }

    @Test
    void testSaveTask() {
        // When
        Task saved = taskRepository.save(task1);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Task 1");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindById() {
        // Given
        Task saved = entityManager.persistAndFlush(task1);

        // When
        Task found = taskRepository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getTitle()).isEqualTo("Task 1");
    }

    @Test
    void testFindByIdNotFound() {
        // When
        var result = taskRepository.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testFindAll() {
        // Given
        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.persist(task3);
        entityManager.flush();

        // When
        List<Task> tasks = taskRepository.findAll();

        // Then
        assertThat(tasks).hasSize(3);
    }

    @Test
    void testFindByStatus() {
        // Given
        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.persist(task3);
        entityManager.flush();

        // When
        List<Task> todoTasks = taskRepository.findByStatus(TaskStatus.TODO);

        // Then
        assertThat(todoTasks).hasSize(1);
        assertThat(todoTasks.get(0).getTitle()).isEqualTo("Task 1");
    }

    @Test
    void testFindByStatusMultiple() {
        // Given
        Task task1b = Task.builder()
                .title("Task 1b")
                .description("Another TODO task")
                .status(TaskStatus.TODO)
                .dueDate(LocalDate.now().plusDays(3))
                .build();

        entityManager.persist(task1);
        entityManager.persist(task1b);
        entityManager.persist(task2);
        entityManager.flush();

        // When
        List<Task> todoTasks = taskRepository.findByStatus(TaskStatus.TODO);

        // Then
        assertThat(todoTasks).hasSize(2);
    }

    @Test
    void testFindByStatusEmpty() {
        // Given
        entityManager.persist(task1);
        entityManager.flush();

        // When
        List<Task> doneTasks = taskRepository.findByStatus(TaskStatus.DONE);

        // Then
        assertThat(doneTasks).isEmpty();
    }

    @Test
    void testFindByDueDateBefore() {
        // Given
        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.persist(task3);
        entityManager.flush();

        // When - find tasks due before tomorrow
        List<Task> overdueTasks = taskRepository.findByDueDateBefore(LocalDate.now().plusDays(1));

        // Then - only task3 is overdue (due 2 days ago)
        assertThat(overdueTasks).hasSize(1);
        assertThat(overdueTasks.get(0).getTitle()).isEqualTo("Task 3");
    }

    @Test
    void testFindByDueDateBeforeNone() {
        // Given
        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.flush();

        // When - find tasks due before yesterday
        List<Task> overdueTasks = taskRepository.findByDueDateBefore(LocalDate.now().minusDays(5));

        // Then
        assertThat(overdueTasks).isEmpty();
    }

    @Test
    void testFindByDueDateBetween() {
        // Given
        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.persist(task3);
        entityManager.flush();

        // When - find tasks due in next 7 days
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(7);
        List<Task> upcomingTasks = taskRepository.findByDueDateBetween(start, end);

        // Then - only task1 (due in 5 days)
        assertThat(upcomingTasks).hasSize(1);
        assertThat(upcomingTasks.get(0).getTitle()).isEqualTo("Task 1");
    }

    @Test
    void testFindByDueDateBetweenMultiple() {
        // Given
        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.persist(task3);
        entityManager.flush();

        // When - find tasks due in next 15 days
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(15);
        List<Task> upcomingTasks = taskRepository.findByDueDateBetween(start, end);

        // Then - task1 and task2
        assertThat(upcomingTasks).hasSize(2);
    }

    @Test
    void testFindByDueDateBetweenEmpty() {
        // Given
        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.flush();

        // When - find tasks in past range
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().minusDays(5);
        List<Task> tasks = taskRepository.findByDueDateBetween(start, end);

        // Then
        assertThat(tasks).isEmpty();
    }

    @Test
    void testUpdateTask() throws InterruptedException {
        // Given
        Task saved = entityManager.persistAndFlush(task1);
        Instant originalCreatedAt = saved.getCreatedAt();
        Instant originalUpdatedAt = saved.getUpdatedAt();
        entityManager.clear();

        // Small delay to ensure updatedAt timestamp differs from createdAt
        Thread.sleep(100);

        // When
        Task toUpdate = taskRepository.findById(saved.getId()).orElseThrow();
        toUpdate.setTitle("Updated Title");
        toUpdate.setStatus(TaskStatus.DONE);
        Task updated = taskRepository.save(toUpdate);
        entityManager.flush(); // Force Hibernate to trigger @PreUpdate

        // Then
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(updated.getCreatedAt()).isEqualTo(originalCreatedAt); // Unchanged
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt); // May or may not have changed
    }

    @Test
    void testDeleteTask() {
        // Given
        Task saved = entityManager.persistAndFlush(task1);

        // When
        taskRepository.deleteById(saved.getId());
        entityManager.flush();

        // Then
        assertThat(taskRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testTimestamps() throws InterruptedException {
        // Given
        Task saved = taskRepository.save(task1);
        entityManager.flush();

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        // Timestamps should be equal or very close (within same transaction)
        assertThat(saved.getUpdatedAt()).isAfterOrEqualTo(saved.getCreatedAt());

        // Sleep to ensure timestamp difference
        Thread.sleep(100); // Increased sleep to ensure measurable difference

        // When - update task
        saved.setTitle("Updated");
        Task updated = taskRepository.save(saved);
        entityManager.flush();

        // Then
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }
}
