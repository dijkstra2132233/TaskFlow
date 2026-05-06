# ⚡ TaskFlow - Project & Task Management System

A role-based task management web app built with **Spring Boot + Thymeleaf + H2/MySQL**.

---

## 🚀 Run in Eclipse (Local Development)

### Prerequisites
- Java 17+
- Maven 3.6+ (or use Eclipse's built-in)
- Eclipse IDE for Enterprise Java (2023+)

### Steps

1. **Import Project**
   - Open Eclipse → `File` → `Import` → `Existing Maven Projects`
   - Browse to the `taskmanager` folder → Click `Finish`
   - Wait for Maven to download dependencies

2. **Run the App**
   - Expand project → `src/main/java` → `com.taskmanager`
   - Right-click `TaskManagerApplication.java` → `Run As` → `Spring Boot App`

3. **Access the App**
   - Open browser: `http://localhost:8080`
   - H2 Console (database viewer): `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:file:./taskflowdb`
     - Username: `sa` | Password: *(empty)*

### Demo Accounts (auto-created on first run)
| Role   | Email                  | Password   |
|--------|------------------------|------------|
| Admin  | admin@taskflow.com     | admin123   |
| Member | alice@taskflow.com     | member123  |
| Member | bob@taskflow.com       | member123  |

---

## 🌐 Deploy to Railway

1. Push code to GitHub
2. Go to [railway.app](https://railway.app) → New Project → Deploy from GitHub
3. Add a **MySQL** plugin from Railway dashboard
4. Set environment variables:
   ```
   SPRING_PROFILES_ACTIVE=prod
   DB_URL=jdbc:mysql://<host>:<port>/<dbname>
   DB_USERNAME=<from railway>
   DB_PASSWORD=<from railway>
   ```
5. Railway auto-detects the Maven project and builds it

---

## 🔑 Features

### Authentication
- Signup / Login with email & password
- Passwords hashed with BCrypt
- Session-based authentication (Spring Security)

### Role-Based Access
| Feature                    | Admin | Member |
|----------------------------|-------|--------|
| View Dashboard             | ✅    | ✅     |
| View all Projects          | ✅    | ✅*    |
| Create / Edit / Delete Projects | ✅ | ❌  |
| Create / Edit / Delete Tasks    | ✅ | ❌  |
| Update task status         | ✅    | ✅ (own tasks) |
| Manage User Roles          | ✅    | ❌     |

*Members see only projects where they have assigned tasks

### Dashboard
- Stats: Total projects, tasks by status, overdue count
- Overdue task list with red highlighting
- Admin: Recent activity across all tasks
- Member: Personal task list with quick status update

### Projects
- Create, edit, delete projects
- Status: Active / On Hold / Completed
- Per-project task counts and overdue alerts

### Tasks
- Create tasks inside projects
- Assign to any team member
- Priority: Low / Medium / High
- Status: Todo / In Progress / Done
- Due date with overdue detection
- Inline status update dropdown

### Team Management (Admin)
- View all registered users
- Promote/demote roles (Admin ↔ Member)

---

## 🗂️ Project Structure

```
taskmanager/
├── src/main/java/com/taskmanager/
│   ├── TaskManagerApplication.java
│   ├── DataInitializer.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── DashboardController.java
│   │   ├── ProjectController.java
│   │   ├── TaskController.java
│   │   └── UserController.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Project.java
│   │   ├── Task.java
│   │   ├── Role.java
│   │   ├── TaskStatus.java
│   │   ├── Priority.java
│   │   └── ProjectStatus.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── ProjectRepository.java
│   │   └── TaskRepository.java
│   └── service/
│       ├── UserService.java
│       ├── UserDetailsServiceImpl.java
│       ├── ProjectService.java
│       └── TaskService.java
└── src/main/resources/
    ├── application.properties       ← Local H2
    ├── application-prod.properties  ← Railway MySQL
    └── templates/
        ├── login.html
        ├── register.html
        ├── dashboard.html
        ├── fragments/layout.html
        ├── projects/
        │   ├── list.html
        │   ├── form.html
        │   └── detail.html
        ├── tasks/edit.html
        └── users/list.html
```

---

## 🛠 Tech Stack

- **Backend**: Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, plain HTML/CSS (no external CSS framework)
- **Database**: H2 (local dev) / MySQL (production)
- **Build**: Maven
- **Deployment**: Railway
