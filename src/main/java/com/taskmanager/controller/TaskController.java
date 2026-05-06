package com.taskmanager.controller;

import com.taskmanager.model.*;
import com.taskmanager.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired private TaskService taskService;
    @Autowired private ProjectService projectService;
    @Autowired private UserService userService;

    @PostMapping("/new")
    public String create(@RequestParam Long projectId,
                         @Valid @ModelAttribute("newTask") Task task,
                         BindingResult result,
                         @RequestParam(required = false) Long assignedUserId,
                         Authentication auth,
                         RedirectAttributes ra) {
        User currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        if (currentUser.getRole() != Role.ADMIN) {
            ra.addFlashAttribute("error", "Only Admins can create tasks.");
            return "redirect:/projects/" + projectId;
        }
        Project project = projectService.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        task.setProject(project);
        if (assignedUserId != null) {
            userService.findById(assignedUserId).ifPresent(task::setAssignedTo);
        }
        taskService.save(task);
        ra.addFlashAttribute("success", "Task created!");
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               Authentication auth,
                               RedirectAttributes ra) {
        Task task = taskService.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        User currentUser = userService.findByEmail(auth.getName()).orElseThrow();

        // Admin or assigned member can update status
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isAssigned = task.getAssignedTo() != null && task.getAssignedTo().getId().equals(currentUser.getId());
        if (!isAdmin && !isAssigned) {
            ra.addFlashAttribute("error", "You can only update your own tasks.");
            return "redirect:/projects/" + task.getProject().getId();
        }
        task.setStatus(TaskStatus.valueOf(status));
        taskService.save(task);
        ra.addFlashAttribute("success", "Task status updated!");
        return "redirect:/projects/" + task.getProject().getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Authentication auth, Model model) {
        Task task = taskService.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        User currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isAdmin) return "redirect:/projects/" + task.getProject().getId();
        model.addAttribute("task", task);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAdmin", isAdmin);
        return "tasks/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("task") Task updated,
                         BindingResult result,
                         @RequestParam(required = false) Long assignedUserId,
                         Authentication auth,
                         RedirectAttributes ra,
                         Model model) {
        Task existing = taskService.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        User currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        if (currentUser.getRole() != Role.ADMIN) return "redirect:/projects/" + existing.getProject().getId();

        if (result.hasErrors()) {
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("allUsers", userService.getAllUsers());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isAdmin", true);
            return "tasks/edit";
        }
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setPriority(updated.getPriority());
        existing.setDueDate(updated.getDueDate());
        if (assignedUserId != null) {
            userService.findById(assignedUserId).ifPresent(existing::setAssignedTo);
        } else {
            existing.setAssignedTo(null);
        }
        taskService.save(existing);
        ra.addFlashAttribute("success", "Task updated!");
        return "redirect:/projects/" + existing.getProject().getId();
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        Task task = taskService.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        User currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        if (currentUser.getRole() != Role.ADMIN) {
            ra.addFlashAttribute("error", "Only Admins can delete tasks.");
            return "redirect:/projects/" + task.getProject().getId();
        }
        Long projectId = task.getProject().getId();
        taskService.delete(id);
        ra.addFlashAttribute("success", "Task deleted.");
        return "redirect:/projects/" + projectId;
    }
}
