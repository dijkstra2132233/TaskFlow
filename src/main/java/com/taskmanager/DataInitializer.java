package com.taskmanager;

import com.taskmanager.model.*;
import com.taskmanager.repository.*;
import com.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private TaskRepository taskRepository;

    @Override
    public void run(String... args) {
        try {
            if (userRepository.count() > 0) return;

            // Admin
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail("admin@taskflow.com");
            admin.setPassword("admin123");
            admin.setRole(Role.ADMIN);
            userService.register(admin);
            admin = userRepository.findByEmail("admin@taskflow.com").get();

            // Member Alice
            User alice = new User();
            alice.setName("Alice Johnson");
            alice.setEmail("alice@taskflow.com");
            alice.setPassword("member123");
            alice.setRole(Role.MEMBER);
            userService.register(alice);
            alice = userRepository.findByEmail("alice@taskflow.com").get();

            // Member Bob
            User bob = new User();
            bob.setName("Bob Smith");
            bob.setEmail("bob@taskflow.com");
            bob.setPassword("member123");
            bob.setRole(Role.MEMBER);
            userService.register(bob);
            bob = userRepository.findByEmail("bob@taskflow.com").get();

            // Projects
            Project p1 = new Project();
            p1.setName("E-Commerce Platform");
            p1.setDescription("Build a full-stack e-commerce platform.");
            p1.setStatus(ProjectStatus.ACTIVE);
            p1.setOwner(admin);
            projectRepository.save(p1);

            Project p2 = new Project();
            p2.setName("Mobile App Redesign");
            p2.setDescription("Redesign the mobile app UI/UX.");
            p2.setStatus(ProjectStatus.ACTIVE);
            p2.setOwner(admin);
            projectRepository.save(p2);

            // Tasks
            createTask("Setup backend", "Init project", TaskStatus.DONE, Priority.HIGH, p1, alice, LocalDate.now().minusDays(5));
            createTask("Build REST APIs", "Create endpoints", TaskStatus.IN_PROGRESS, Priority.HIGH, p1, alice, LocalDate.now().plusDays(3));
            createTask("Payment integration", "Integrate Razorpay", TaskStatus.TODO, Priority.HIGH, p1, bob, LocalDate.now().plusDays(7));
            createTask("UI Wireframes", "Create Figma designs", TaskStatus.DONE, Priority.MEDIUM, p2, bob, LocalDate.now().minusDays(3));
            createTask("Frontend build", "React Native screens", TaskStatus.TODO, Priority.HIGH, p2, alice, LocalDate.now().plusDays(10));

            System.out.println("==========================================");
            System.out.println("  TaskFlow started successfully!");
            System.out.println("  Admin:  admin@taskflow.com / admin123");
            System.out.println("  Member: alice@taskflow.com / member123");
            System.out.println("  Member: bob@taskflow.com   / member123");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("DataInitializer warning: " + e.getMessage());
            // Don't crash the app if seeding fails
        }
    }

    private void createTask(String title, String desc, TaskStatus status, Priority priority,
                            Project project, User assignee, LocalDate dueDate) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(desc);
        task.setStatus(status);
        task.setPriority(priority);
        task.setProject(project);
        task.setAssignedTo(assignee);
        task.setDueDate(dueDate);
        taskRepository.save(task);
    }
}
