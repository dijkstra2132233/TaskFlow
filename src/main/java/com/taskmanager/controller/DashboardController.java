package com.taskmanager.controller;

import com.taskmanager.model.*;
import com.taskmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired private UserService userService;
    @Autowired private ProjectService projectService;
    @Autowired private TaskService taskService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Authentication auth, Model model) {
        User currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            model.addAttribute("projects", projectService.getAllProjects());
            model.addAttribute("totalTasks", taskService.countAll());
            model.addAttribute("totalProjects", projectService.count());
            model.addAttribute("totalUsers", userService.getAllUsers().size());
            model.addAttribute("overdueTasks", taskService.getOverdueTasks());
            model.addAttribute("recentTasks", taskService.getRecentTasks());
        } else {
            List<Project> myProjects = projectService.getProjectsForUser(currentUser);
            List<Task> myTasks = taskService.getTasksForUser(currentUser);
            List<Task> myOverdue = taskService.getOverdueTasksForUser(currentUser);

            model.addAttribute("projects", myProjects);
            model.addAttribute("myTasks", myTasks);
            model.addAttribute("overdueTasks", myOverdue);
            model.addAttribute("todoCnt", myTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count());
            model.addAttribute("inProgressCnt", myTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count());
            model.addAttribute("doneCnt", myTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count());
        }
        model.addAttribute("todoCnt_global", taskService.countByStatus(TaskStatus.TODO));
        model.addAttribute("inProgressCnt_global", taskService.countByStatus(TaskStatus.IN_PROGRESS));
        model.addAttribute("doneCnt_global", taskService.countByStatus(TaskStatus.DONE));
        model.addAttribute("overdueCnt_global", taskService.countOverdue());

        return "dashboard";
    }
}
