# Design Specification: In-Memory Spring Boot & React To-Do List Application

**Date:** 2026-09-21  
**Status:** Approved  
**Target Audience:** Beginner backend developer transitioning from LeetCode / basic OOP Java to enterprise Spring Boot and React development.

---

## 1. Overview & Goals

The goal of this project is to build a full-stack CRUD To-Do List web application with an **in-memory** data store (no external database required). 

The primary purpose is **educational**:
1. Demonstrate how a Spring Boot 3 backend is organized using standard enterprise layered architecture (Controller -> Service -> Repository -> Domain Model).
2. Teach foundational Spring Boot concepts: Inversion of Control (IoC), Dependency Injection (DI), REST APIs, Request/Response mapping, DTO validation, and Centralized Exception Handling.
3. Contrast algorithmic/LeetCode Java (`main()` method, manual `new` allocations) with declarative, annotation-driven Java.
4. Pair it with a clean, modern, but code-simple React (Vite) frontend.
5. Provide a comprehensive `RUNNING_GUIDE.md` so the user can easily run, inspect, and debug both services in VS Code.

---

## 2. System Architecture

```text
+-----------------------+                    +------------------------------------+
|  React Frontend       |                    |  Spring Boot 3 Backend             |
|  (Vite @ port 5173)   |                    |  (Tomcat @ port 8080)              |
|                       |                    |                                    |
|  +-----------------+  |   HTTP REST/JSON   |  +------------------------------+  |
|  | Components / UI |  | -----------------> |  | TodoController               |  |
|  +-----------------+  |                    |  | (REST endpoints, routes)     |  |
|           |           |                    |  +------------------------------+  |
|  +-----------------+  |                    |                 |                  |
|  | api.js (Fetch)  |  |                    |  +------------------------------+  |
|  +-----------------+  |                    |  | TodoService                  |  |
|                       |                    |  | (Business logic, validation) |  |
|                       |                    |  +------------------------------+  |
|                       |                    |                 |                  |
|                       |                    |  +------------------------------+  |
|                       |                    |  | InMemoryTodoRepository       |  |
|                       |                    |  | (ConcurrentHashMap & IDs)    |  |
|                       |                    |  +------------------------------+  |
+-----------------------+                    +------------------------------------+
```

### Directory Structure

```text
spring_to_do_list/
├── backend/
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── .mvn/
│   └── src/
│       ├── main/
│       │   ├── java/com/example/todolist/
│       │   │   ├── TodoListApplication.java
│       │   │   ├── config/
│       │   │   │   └── CorsConfig.java
│       │   │   ├── controller/
│       │   │   │   └── TodoController.java
│       │   │   ├── dto/
│       │   │   │   ├── CreateTodoRequest.java
│       │   │   │   ├── UpdateTodoRequest.java
│       │   │   │   └── ErrorResponse.java
│       │   │   ├── exception/
│       │   │   │   ├── ResourceNotFoundException.java
│       │   │   │   └── GlobalExceptionHandler.java
│       │   │   ├── model/
│       │   │   │   └── Todo.java
│       │   │   ├── repository/
│       │   │   │   ├── TodoRepository.java
│       │   │   │   └── InMemoryTodoRepository.java
│       │   │   └── service/
│       │   │       └── TodoService.java
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│           └── java/com/example/todolist/
│               ├── controller/
│               │   └── TodoControllerTest.java
│               └── service/
│                   └── TodoServiceTest.java
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── main.jsx
│       ├── App.jsx
│       ├── App.css
│       ├── index.css
│       ├── services/
│       │   └── api.js
│       └── components/
│           ├── TodoForm.jsx
│           ├── TodoItem.jsx
│           ├── TodoList.jsx
│           └── FilterBar.jsx
├── docs/
│   └── superpowers/
│       ├── specs/
│       │   └── 2026-09-21-spring-boot-react-todo-design.md
│       └── plans/
├── RUNNING_GUIDE.md
└── README.md
```

---

## 3. Backend Specification

### 3.1 Tech Stack & Versions
- **Language**: Java 21
- **Framework**: Spring Boot 3.3.x
- **Build Tool**: Maven (with Maven Wrapper included)
- **Dependencies**:
  - `spring-boot-starter-web`: Embedded Tomcat server, Spring MVC for REST controllers.
  - `spring-boot-starter-validation`: Jakarta Bean Validation (`@NotBlank`, `@Size`) for incoming DTOs.
  - `spring-boot-devtools`: Automatic application restart on class changes.
  - `spring-boot-starter-test`: JUnit 5, AssertJ, and MockMvc for unit/integration tests.

### 3.2 Domain Model (`Todo.java`)
Represents an individual To-Do item stored in memory.
```java
public class Todo {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    // Constructors, Getters, Setters
}
```

### 3.3 Data Transfer Objects (DTOs)
Decoupling the API contract from the internal domain model:
- `CreateTodoRequest`:
  - `title`: String (`@NotBlank(message = "Title is required")`, `@Size(max = 100)`)
  - `description`: String (`@Size(max = 500)`)
- `UpdateTodoRequest`:
  - `title`: String (`@NotBlank(message = "Title is required")`, `@Size(max = 100)`)
  - `description`: String (`@Size(max = 500)`)
  - `completed`: Boolean (nullable in partial updates, or validated boolean)
- `ErrorResponse`:
  - `timestamp`: LocalDateTime
  - `status`: int (e.g. 400, 404)
  - `error`: String
  - `message`: String
  - `details`: List<String> (validation error list)

### 3.4 In-Memory Repository
Interface `TodoRepository` defines the storage contract:
- `List<Todo> findAll()`
- `Optional<Todo> findById(Long id)`
- `Todo save(Todo todo)`
- `boolean deleteById(Long id)`
- `void clear()` (useful for tests)

Implementation `InMemoryTodoRepository` annotated with `@Repository`:
- Stores records in a `ConcurrentHashMap<Long, Todo>` to ensure thread safety across concurrent HTTP requests.
- Generates sequential IDs using `AtomicLong idSequence = new AtomicLong(1)`.
- Pre-populates 2-3 sample todos on startup so the UI is immediately interactive.

### 3.5 Service Layer (`TodoService.java`)
Annotated with `@Service`:
- Coordinates operations between Controller and Repository.
- Enforces business rules (e.g., setting `createdAt = LocalDateTime.now()`, toggling `completed = !completed`).
- Throws `ResourceNotFoundException` when an ID does not exist.

### 3.6 REST API Endpoints (`TodoController.java`)
Base path: `/api/todos`

| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/todos` | List all todos | `200 OK` |
| `GET` | `/api/todos/{id}` | Get a single todo by ID | `200 OK`, `404 Not Found` |
| `POST` | `/api/todos` | Create a new todo | `201 Created`, `400 Bad Request` |
| `PUT` | `/api/todos/{id}` | Replace/update todo details | `200 OK`, `400 Bad Request`, `404 Not Found` |
| `PATCH`| `/api/todos/{id}/toggle` | Toggle completion status | `200 OK`, `404 Not Found` |
| `DELETE` | `/api/todos/{id}` | Delete a todo | `204 No Content`, `404 Not Found` |

### 3.7 Cross-Origin Resource Sharing (CORS)
- `CorsConfig.java` implements `WebMvcConfigurer` allowing `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `OPTIONS` from `http://localhost:5173` and `http://127.0.0.1:5173`.

### 3.8 Global Exception Handling
- `@RestControllerAdvice` handling:
  - `ResourceNotFoundException` -> returns `404 Not Found` with structured JSON.
  - `MethodArgumentNotValidException` -> returns `400 Bad Request` with field error descriptions.
  - General `Exception` -> returns `500 Internal Server Error`.

---

## 4. Frontend Specification

### 4.1 Tech Stack
- **Tooling**: Vite (`npm create vite@latest`)
- **Library**: React 18+
- **Styling**: Modern, responsive CSS with CSS custom properties (variables), clean card styling, status badges, and transition effects. No heavy external CSS libraries needed.

### 4.2 Components & Hierarchy
- `App.jsx`: Main container. Holds top-level state (`todos`, `filter`, `loading`, `error`), manages API calls.
- `TodoForm.jsx`: Input fields for `title` and optional `description`, with client-side validation and submit button.
- `FilterBar.jsx`: Tabbed filters (*All*, *Active*, *Completed*) with badge counts.
- `TodoList.jsx`: Renders list of `TodoItem`s, or an empty state when empty.
- `TodoItem.jsx`: Individual card showing title, description, created timestamp, status checkbox, edit modal/form trigger, and delete button.
- `services/api.js`: Clean async functions (`getTodos`, `createTodo`, `updateTodo`, `toggleTodo`, `deleteTodo`).

---

## 5. Documentation & Developer Ergonomics

### 5.1 `RUNNING_GUIDE.md`
A beginner-friendly runbook containing:
1. Prerequisites verification commands (`java -version`, `node -v`).
2. Starting the backend via terminal (`./mvnw spring-boot:run` or `mvn spring-boot:run`).
3. Starting the backend via VS Code (using the Spring Boot Dashboard or Run button on `TodoListApplication.java`).
4. Starting the frontend (`cd frontend && npm install && npm run dev`).
5. Testing the backend independently using `curl` commands for each endpoint.
6. Troubleshooting guide (e.g. port 8080 already in use, CORS errors).

### 5.2 `README.md`
An educational guide covering:
1. Architecture explanation and request lifecycle diagram.
2. Spring Boot concepts explained for LeetCode/OOP programmers (Annotations, IoC/DI, DTOs vs Entities).
3. Project file map.

---

## 6. Verification & Quality Plan

### 6.1 Automated Testing
1. **Service Tests (`TodoServiceTest.java`)**:
   - Unit tests covering `create`, `findById`, `update`, `toggle`, `delete`, and exception throwing when ID is missing.
2. **Controller Tests (`TodoControllerTest.java`)**:
   - MockMvc integration tests verifying HTTP status codes (`200`, `201`, `204`, `400`, `404`) and JSON response payloads.
3. **Maven Build Check**:
   - Running `./mvnw clean test` (or `mvn clean test`) to ensure all tests pass and code compiles without warnings.

### 6.2 Manual Verification
1. Start backend and verify `http://localhost:8080/api/todos` returns initial sample items.
2. Test creation with empty title and verify `400 Bad Request` with validation message.
3. Start frontend on `http://localhost:5173`, create a new item, toggle completion, edit item, and delete item.
