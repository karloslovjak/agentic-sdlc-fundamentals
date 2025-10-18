package com.accenture.taskmanager.mapper;

import com.accenture.taskmanager.api.model.TaskRequest;
import com.accenture.taskmanager.api.model.TaskResponse;
import com.accenture.taskmanager.model.Task;
import org.mapstruct.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * MapStruct mapper for converting between API models and Entity.
 *
 * Handles bidirectional mapping between:
 * - TaskRequest (API input) ↔ Task (Entity)
 * - Task (Entity) ↔ TaskResponse (API output)
 *
 * Architecture:
 * - MapStruct generates implementation at compile time (no reflection)
 * - Type-safe conversions with compile-time validation
 * - Automatic mapping for fields with matching names
 * - Custom mappings for fields with different names or types
 * - Handles enum conversion between API and Entity TaskStatus
 *
 * Component model = "spring" makes this a Spring bean for dependency injection.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper {

    /**
     * Convert TaskRequest (API input) to Task entity.
     *
     * Used when creating or updating a task.
     * Maps all request fields to entity fields.
     * Does NOT map id, createdAt, updatedAt (these are managed by JPA).
     *
     * Status enum mapping:
     * - API TaskStatus → Entity TaskStatus (same values, different packages)
     *
     * @param request the API request object
     * @return Task entity ready for persistence
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(TaskRequest request);

    /**
     * Convert Task entity to TaskResponse (API output).
     *
     * Used when returning task data to the client.
     * Maps all entity fields to response fields.
     * Converts Instant to OffsetDateTime for ISO 8601 compliance.
     *
     * @param task the Task entity from database
     * @return TaskResponse for API output
     */
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "instantToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "instantToOffsetDateTime")
    TaskResponse toResponse(Task task);

    /**
     * Update existing Task entity from TaskRequest.
     *
     * Used for PUT operations to update all fields.
     * Does NOT update id, createdAt, updatedAt.
     * Null values in request WILL be mapped to entity
     * (NullValuePropertyMappingStrategy.SET_TO_NULL).
     *
     * @param request the API request with updated values
     * @param task    the existing Task entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntityFromRequest(TaskRequest request, @MappingTarget Task task);

    /**
     * Custom mapping: Convert Instant to OffsetDateTime.
     *
     * Instant is used in entity (timezone-independent, UTC-based).
     * OffsetDateTime is required by OpenAPI spec for ISO 8601 format.
     * Converts to UTC (ZoneOffset.UTC) for consistent API responses.
     *
     * @param instant the Instant from entity
     * @return OffsetDateTime in UTC for API response
     */
    @Named("instantToOffsetDateTime")
    default OffsetDateTime instantToOffsetDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atOffset(ZoneOffset.UTC);
    }

    /**
     * Map API TaskStatus enum to Entity TaskStatus enum.
     *
     * Both enums have the same values (TODO, IN_PROGRESS, DONE) but different
     * packages.
     * MapStruct handles this automatically by matching enum constant names.
     *
     * @param apiStatus the API model TaskStatus
     * @return Entity TaskStatus
     */
    default com.accenture.taskmanager.model.TaskStatus mapApiStatusToEntityStatus(
            com.accenture.taskmanager.api.model.TaskStatus apiStatus) {
        if (apiStatus == null) {
            return null;
        }
        return com.accenture.taskmanager.model.TaskStatus.valueOf(apiStatus.name());
    }

    /**
     * Map Entity TaskStatus enum to API TaskStatus enum.
     *
     * @param entityStatus the Entity TaskStatus
     * @return API model TaskStatus
     */
    default com.accenture.taskmanager.api.model.TaskStatus mapEntityStatusToApiStatus(
            com.accenture.taskmanager.model.TaskStatus entityStatus) {
        if (entityStatus == null) {
            return null;
        }
        return com.accenture.taskmanager.api.model.TaskStatus.valueOf(entityStatus.name());
    }

}
