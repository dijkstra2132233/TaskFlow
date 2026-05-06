package com.taskmanager.controller;

import com.taskmanager.model.*;
import com.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired private UserService userService;

    @GetMapping
    public String list(Authentication auth, Model model) {
        User currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAdmin", true);
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @PostMapping("/{id}/role")
    public String changeRole(@PathVariable Long id,
                             @RequestParam String role,
                             Authentication auth,
                             RedirectAttributes ra) {
        User currentUser = userService.findByEmail(auth.getName()).orElseThrow();
        if (currentUser.getId().equals(id)) {
            ra.addFlashAttribute("error", "You cannot change your own role.");
            return "redirect:/users";
        }
        User user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.valueOf(role));
        userService.save(user);
        ra.addFlashAttribute("success", "Role updated for " + user.getName());
        return "redirect:/users";
    }
}
