package com.taskmanager.controller;

import com.taskmanager.model.*;
import com.taskmanager.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired private ProjectService projectService;
    @Autowired private TaskService taskService;
    @Autowired private UserService userService;

    @GetMapping
    public String list(Authentication auth, Model model) {
        User currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("projects", isAdmin
            ? projectService.getAllProjects()
            : projectService.getProjectsForUser(currentUser));
        return "projects/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("statuses", ProjectStatus.values());
        return "projects/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute("project") Project project,
                         BindingResult result,
                         Authentication auth,
                         RedirectAttributes ra,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", ProjectStatus.values());
            return "projects/form";
        }
        User owner = userService.findByEmail(auth.getName()).orElseThrow();
        project.setOwner(owner);
        projectService.save(project);
        ra.addFlashAttribute("success", "Project created successfully!");
        return "redirect:/projects";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication auth, Model model) {
        Project project = projectService.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        User currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        model.addAttribute("project", project);
        model.addAttribute("tasks", taskService.getTasksByProject(project));
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("newTask", new Task());
        return "projects/detail";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Project project = projectService.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        model.addAttribute("project", project);
        model.addAttribute("statuses", ProjectStatus.values());
        return "projects/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("project") Project updated,
                         BindingResult result,
                         RedirectAttributes ra,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", ProjectStatus.values());
            return "projects/form";
        }
        Project existing = projectService.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        projectService.save(existing);
        ra.addFlashAttribute("success", "Project updated!");
        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        projectService.delete(id);
        ra.addFlashAttribute("success", "Project deleted.");
        return "redirect:/projects";
    }
}
