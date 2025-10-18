package com.accenture.taskmanager.mapper;

import com.accenture.taskmanager.api.model.TaskRequest;
import com.accenture.taskmanager.api.model.TaskResponse;
import com.accenture.taskmanager.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for TaskMapper MapStruct mapper.
 *
 * Uses SpringBootTest to load the MapStruct-generated implementation.
 * Tests bidirectional mapping between API models and Entity.
 */
@SpringBootTest
@ActiveProfiles("test")
class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void testToEntityFromRequest() {
        // Given
        TaskRequest request = TaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .status(com.accenture.taskmanager.api.model.TaskStatus.TODO)
                .dueDate(LocalDate.of(2025, 12, 31))
                .build();

        // When
        Task entity = taskMapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo("Test Task");
        assertThat(entity.getDescription()).isEqualTo("Test Description");
        assertThat(entity.getStatus()).isEqualTo(com.accenture.taskmanager.model.TaskStatus.TODO);
        assertThat(entity.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(entity.getId()).isNull(); // Not set from request
        assertThat(entity.getCreatedAt()).isNull(); // Set by @PrePersist
        assertThat(entity.getUpdatedAt()).isNull(); // Set by @PrePersist
    }

    @Test
    void testToEntityNullDescription() {
        // Given
        TaskRequest request = TaskRequest.builder()
                .title("Test Task")
                .description(null)
                .status(com.accenture.taskmanager.api.model.TaskStatus.IN_PROGRESS)
                .dueDate(LocalDate.of(2025, 6, 15))
                .build();

        // When
        Task entity = taskMapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo("Test Task");
        assertThat(entity.getDescription()).isNull();
        assertThat(entity.getStatus()).isEqualTo(com.accenture.taskmanager.model.TaskStatus.IN_PROGRESS);
    }

    @Test
    void testToResponse() {
        // Given
        Task entity = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(com.accenture.taskmanager.model.TaskStatus.DONE)
                .dueDate(LocalDate.of(2025, 12, 31))
                .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 1, 2, 15, 30))
                .build();

        // When
        TaskResponse response = taskMapper.toResponse(entity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Task");
        assertThat(response.getDescription()).isEqualTo("Test Description");
        assertThat(response.getStatus()).isEqualTo(com.accenture.taskmanager.api.model.TaskStatus.DONE);
        assertThat(response.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 31));

        // Check timestamp conversions
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(response.getCreatedAt().toLocalDateTime())
                .isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0));
        assertThat(response.getUpdatedAt().toLocalDateTime())
                .isEqualTo(LocalDateTime.of(2025, 1, 2, 15, 30));
    }

    @Test
    void testToResponseNullDescription() {
        // Given
        Task entity = Task.builder()
                .id(2L)
                .title("Task without description")
                .description(null)
                .status(com.accenture.taskmanager.model.TaskStatus.TODO)
                .dueDate(LocalDate.of(2025, 3, 15))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        TaskResponse response = taskMapper.toResponse(entity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getDescription()).isNull();
    }

    @Test
    void testUpdateEntityFromRequest() {
        // Given - existing entity
        Task existingEntity = Task.builder()
                .id(1L)
                .title("Old Title")
                .description("Old Description")
                .status(com.accenture.taskmanager.model.TaskStatus.TODO)
                .dueDate(LocalDate.of(2025, 1, 1))
                .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();

        // Given - update request
        TaskRequest request = TaskRequest.builder()
                .title("New Title")
                .description("New Description")
                .status(com.accenture.taskmanager.api.model.TaskStatus.IN_PROGRESS)
                .dueDate(LocalDate.of(2025, 12, 31))
                .build();

        // When
        taskMapper.updateEntityFromRequest(request, existingEntity);

        // Then - entity updated
        assertThat(existingEntity.getId()).isEqualTo(1L); // ID preserved
        assertThat(existingEntity.getTitle()).isEqualTo("New Title");
        assertThat(existingEntity.getDescription()).isEqualTo("New Description");
        assertThat(existingEntity.getStatus()).isEqualTo(com.accenture.taskmanager.model.TaskStatus.IN_PROGRESS);
        assertThat(existingEntity.getDueDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(existingEntity.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0)); // Preserved
        assertThat(existingEntity.getUpdatedAt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0)); // Preserved (will be
                                                                                                  // updated by
                                                                                                  // @PreUpdate)
    }

    @Test
    void testUpdateEntityFromRequestNullDescription() {
        // Given - existing entity
        Task existingEntity = Task.builder()
                .id(1L)
                .title("Title")
                .description("Old Description")
                .status(com.accenture.taskmanager.model.TaskStatus.TODO)
                .dueDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Given - request with null description
        TaskRequest request = TaskRequest.builder()
                .title("Updated Title")
                .description(null)
                .status(com.accenture.taskmanager.api.model.TaskStatus.DONE)
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        // When
        taskMapper.updateEntityFromRequest(request, existingEntity);

        // Then
        assertThat(existingEntity.getTitle()).isEqualTo("Updated Title");
        assertThat(existingEntity.getDescription()).isNull();
        assertThat(existingEntity.getStatus()).isEqualTo(com.accenture.taskmanager.model.TaskStatus.DONE);
    }

    @Test
    void testMapApiStatusToEntityStatus() {
        // When/Then
        assertThat(taskMapper.mapApiStatusToEntityStatus(com.accenture.taskmanager.api.model.TaskStatus.TODO))
                .isEqualTo(com.accenture.taskmanager.model.TaskStatus.TODO);
        assertThat(taskMapper.mapApiStatusToEntityStatus(com.accenture.taskmanager.api.model.TaskStatus.IN_PROGRESS))
                .isEqualTo(com.accenture.taskmanager.model.TaskStatus.IN_PROGRESS);
        assertThat(taskMapper.mapApiStatusToEntityStatus(com.accenture.taskmanager.api.model.TaskStatus.DONE))
                .isEqualTo(com.accenture.taskmanager.model.TaskStatus.DONE);
    }

    @Test
    void testMapApiStatusToEntityStatusNull() {
        // When/Then
        assertThat(taskMapper.mapApiStatusToEntityStatus(null)).isNull();
    }

    @Test
    void testMapEntityStatusToApiStatus() {
        // When/Then
        assertThat(taskMapper.mapEntityStatusToApiStatus(com.accenture.taskmanager.model.TaskStatus.TODO))
                .isEqualTo(com.accenture.taskmanager.api.model.TaskStatus.TODO);
        assertThat(taskMapper.mapEntityStatusToApiStatus(com.accenture.taskmanager.model.TaskStatus.IN_PROGRESS))
                .isEqualTo(com.accenture.taskmanager.api.model.TaskStatus.IN_PROGRESS);
        assertThat(taskMapper.mapEntityStatusToApiStatus(com.accenture.taskmanager.model.TaskStatus.DONE))
                .isEqualTo(com.accenture.taskmanager.api.model.TaskStatus.DONE);
    }

    @Test
    void testMapEntityStatusToApiStatusNull() {
        // When/Then
        assertThat(taskMapper.mapEntityStatusToApiStatus(null)).isNull();
    }

    @Test
    void testLocalDateTimeToOffsetDateTime() {
        // Given
        LocalDateTime localDateTime = LocalDateTime.of(2025, 6, 15, 14, 30, 45);

        // When
        OffsetDateTime offsetDateTime = taskMapper.localDateTimeToOffsetDateTime(localDateTime);

        // Then
        assertThat(offsetDateTime).isNotNull();
        assertThat(offsetDateTime.toLocalDateTime()).isEqualTo(localDateTime);
        // Offset should be system default (not necessarily UTC)
        assertThat(offsetDateTime.getOffset())
                .isEqualTo(ZoneOffset.systemDefault().getRules().getOffset(localDateTime));
    }

    @Test
    void testLocalDateTimeToOffsetDateTimeNull() {
        // When/Then
        assertThat(taskMapper.localDateTimeToOffsetDateTime(null)).isNull();
    }

    @Test
    void testRoundTripMapping() {
        // Given - original request
        TaskRequest originalRequest = TaskRequest.builder()
                .title("Round Trip Task")
                .description("Testing bidirectional mapping")
                .status(com.accenture.taskmanager.api.model.TaskStatus.IN_PROGRESS)
                .dueDate(LocalDate.of(2025, 8, 20))
                .build();

        // When - convert to entity and back to response
        Task entity = taskMapper.toEntity(originalRequest);
        entity.setId(100L);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        TaskResponse response = taskMapper.toResponse(entity);

        // Then - verify data integrity
        assertThat(response.getTitle()).isEqualTo(originalRequest.getTitle());
        assertThat(response.getDescription()).isEqualTo(originalRequest.getDescription());
        assertThat(response.getStatus().toString()).isEqualTo(originalRequest.getStatus().toString());
        assertThat(response.getDueDate()).isEqualTo(originalRequest.getDueDate());
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }
}
