package com.taskmanager.service;

import com.taskmanager.model.*;
import com.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> getTasksByProject(Project project) {
        return taskRepository.findByProjectOrderByCreatedAtDesc(project);
    }

    public List<Task> getTasksForUser(User user) {
        return taskRepository.findByAssignedToOrderByDueDateAsc(user);
    }

    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDate.now());
    }

    public List<Task> getOverdueTasksForUser(User user) {
        return taskRepository.findOverdueTasksForUser(user, LocalDate.now());
    }

    public long countByStatus(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }

    public long countOverdue() {
        return taskRepository.countOverdueTasks(LocalDate.now());
    }

    public long countAll() {
        return taskRepository.count();
    }

    public List<Task> getRecentTasks() {
        return taskRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
