package com.taskmanager.repository;

import com.taskmanager.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectOrderByCreatedAtDesc(Project project);
    List<Task> findByAssignedToOrderByDueDateAsc(User user);
    List<Task> findByStatus(TaskStatus status);
    long countByStatus(TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.status != 'DONE'")
    List<Task> findOverdueTasks(LocalDate today);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < :today AND t.status != 'DONE'")
    long countOverdueTasks(LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user AND t.dueDate < :today AND t.status != 'DONE'")
    List<Task> findOverdueTasksForUser(User user, LocalDate today);

    List<Task> findTop10ByOrderByCreatedAtDesc();
}
