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
        if (userRepository.count() > 0) return; // Already seeded

        // Create admin
        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@taskflow.com");
        admin.setPassword("admin123");
        admin.setRole(Role.ADMIN);
        userService.register(admin);
        admin = userRepository.findByEmail("admin@taskflow.com").get();

        // Create members
        User alice = new User();
        alice.setName("Alice Johnson");
        alice.setEmail("alice@taskflow.com");
        alice.setPassword("member123");
        alice.setRole(Role.MEMBER);
        userService.register(alice);
        alice = userRepository.findByEmail("alice@taskflow.com").get();

        User bob = new User();
        bob.setName("Bob Smith");
        bob.setEmail("bob@taskflow.com");
        bob.setPassword("member123");
        bob.setRole(Role.MEMBER);
        userService.register(bob);
        bob = userRepository.findByEmail("bob@taskflow.com").get();

        // Project 1
        Project p1 = new Project();
        p1.setName("E-Commerce Platform");
        p1.setDescription("Build a full-stack e-commerce platform with payment integration.");
        p1.setStatus(ProjectStatus.ACTIVE);
        p1.setOwner(admin);
        projectRepository.save(p1);

        // Project 2
        Project p2 = new Project();
        p2.setName("Mobile App Redesign");
        p2.setDescription("Redesign the mobile app UI/UX for better user engagement.");
        p2.setStatus(ProjectStatus.ACTIVE);
        p2.setOwner(admin);
        projectRepository.save(p2);

        // Project 3
        Project p3 = new Project();
        p3.setName("Data Pipeline Automation");
        p3.setDescription("Automate ETL data pipelines using Apache Spark.");
        p3.setStatus(ProjectStatus.ON_HOLD);
        p3.setOwner(admin);
        projectRepository.save(p3);

        // Tasks for Project 1
        createTask("Setup Spring Boot backend", "Initialize project with all dependencies", TaskStatus.DONE, Priority.HIGH, p1, alice, LocalDate.now().minusDays(5));
        createTask("Design database schema", "Create ER diagram and JPA entities", TaskStatus.DONE, Priority.HIGH, p1, bob, LocalDate.now().minusDays(3));
        createTask("Build REST APIs", "Create product, cart, order endpoints", TaskStatus.IN_PROGRESS, Priority.HIGH, p1, alice, LocalDate.now().plusDays(3));
        createTask("Payment gateway integration", "Integrate Stripe/Razorpay payment", TaskStatus.TODO, Priority.HIGH, p1, bob, LocalDate.now().plusDays(7));
        createTask("Write unit tests", "Cover all service layer methods", TaskStatus.TODO, Priority.MEDIUM, p1, alice, LocalDate.now().minusDays(1)); // overdue

        // Tasks for Project 2
        createTask("User research & wireframes", "Conduct user interviews and create wireframes", TaskStatus.DONE, Priority.HIGH, p2, bob, LocalDate.now().minusDays(10));
        createTask("Create Figma prototypes", "Design all screens in Figma", TaskStatus.IN_PROGRESS, Priority.MEDIUM, p2, alice, LocalDate.now().plusDays(2));
        createTask("Frontend implementation", "Convert designs to React Native", TaskStatus.TODO, Priority.HIGH, p2, bob, LocalDate.now().plusDays(10));

        // Tasks for Project 3
        createTask("Define data sources", "Identify all input data sources", TaskStatus.TODO, Priority.LOW, p3, alice, LocalDate.now().minusDays(2)); // overdue
        createTask("Setup Spark cluster", "Configure Apache Spark on AWS EMR", TaskStatus.TODO, Priority.MEDIUM, p3, bob, LocalDate.now().plusDays(15));

        System.out.println("============================================");
        System.out.println("  TaskFlow demo data initialized!");
        System.out.println("  Admin:  admin@taskflow.com / admin123");
        System.out.println("  Member: alice@taskflow.com / member123");
        System.out.println("  Member: bob@taskflow.com   / member123");
        System.out.println("  App running at: http://localhost:8080");
        System.out.println("============================================");
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
