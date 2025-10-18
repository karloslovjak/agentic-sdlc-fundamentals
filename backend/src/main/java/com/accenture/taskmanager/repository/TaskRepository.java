package com.accenture.taskmanager.repository;

import com.accenture.taskmanager.model.Task;
import com.accenture.taskmanager.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Task entity.
 *
 * Extends JpaRepository to provide CRUD operations and custom queries.
 * Spring Data JPA generates implementation at runtime.
 *
 * Architecture:
 * - Provides data access abstraction
 * - Method names follow Spring Data naming conventions for automatic query
 * generation
 * - Custom queries can be added with @Query annotation if needed
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks with a specific status.
     *
     * Query generated from method name: WHERE status = :status
     *
     * @param status the task status to filter by
     * @return list of tasks with the given status
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Find all tasks with due date before a given date.
     *
     * Useful for finding overdue tasks.
     * Query generated: WHERE due_date < :date
     *
     * @param date the date to compare against
     * @return list of tasks due before the given date
     */
    List<Task> findByDueDateBefore(LocalDate date);

    /**
     * Find all tasks with due date between two dates.
     *
     * Useful for date range queries.
     * Query generated: WHERE due_date BETWEEN :start AND :end
     *
     * @param start start date (inclusive)
     * @param end   end date (inclusive)
     * @return list of tasks within the date range
     */
    List<Task> findByDueDateBetween(LocalDate start, LocalDate end);

}
