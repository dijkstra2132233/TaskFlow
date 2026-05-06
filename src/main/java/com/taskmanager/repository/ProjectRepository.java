package com.taskmanager.repository;

import com.taskmanager.model.Project;
import com.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByOrderByCreatedAtDesc();

    // Projects where user has at least one task assigned
    @Query("SELECT DISTINCT t.project FROM Task t WHERE t.assignedTo = :user ORDER BY t.project.createdAt DESC")
    List<Project> findProjectsWithTasksAssignedTo(User user);
}
