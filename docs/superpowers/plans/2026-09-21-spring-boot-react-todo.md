# In-Memory Spring Boot & React To-Do List Application Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a full-stack in-memory CRUD To-Do List web application with a Spring Boot 3 (Java 21) REST API backend and a clean, modern React (Vite) frontend, accompanied by a comprehensive running guide and educational architecture documentation.

**Architecture:** The backend follows standard enterprise layered architecture (Controller -> Service -> Repository -> Model/DTO), using thread-safe `ConcurrentHashMap` and `AtomicLong` for in-memory persistence and Jakarta Validation for incoming payloads. The frontend is built with React and Vite, using modular components and a centralized API service layer with clean, responsive vanilla CSS.

**Tech Stack:** Java 21, Spring Boot 3.3.3, Maven, JUnit 5, MockMvc, React 18+, Vite, modern CSS.

**Spec:** [docs/superpowers/specs/2026-09-21-spring-boot-react-todo-design.md](file:///home/vedansh/Developer/Vedansh/spring_to_do_list/docs/superpowers/specs/2026-09-21-spring-boot-react-todo-design.md)

## Global Constraints

- Backend must use Java 21 and Spring Boot 3.3.x.
- Storage must be completely in-memory using thread-safe collections (`ConcurrentHashMap`, `AtomicLong`); no external database.
- Backend must adhere to layered architecture: Controller -> Service -> Repository.
- DTOs must be validated using Jakarta Validation annotations (`@NotBlank`, `@Size`).
- Centralized exception handling via `@RestControllerAdvice` returning structured JSON error bodies.
- CORS must allow requests from `http://localhost:5173` and `http://127.0.0.1:5173`.
- Detailed educational comments must be added to all Java classes explaining Spring annotations and architecture.
- Both `RUNNING_GUIDE.md` and `README.md` must be created with exhaustive step-by-step instructions.

---

### Task 1: Backend Scaffolding & Maven Setup

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/resources/application.properties`
- Create: `backend/src/main/java/com/example/todolist/TodoListApplication.java`

**Interfaces:**
- Consumes: None
- Produces: Runnable Spring Boot base application on port `8080`.

- [ ] **Step 1: Create directory structure for backend**

```bash
mkdir -p backend/src/main/java/com/example/todolist
mkdir -p backend/src/main/resources
mkdir -p backend/src/test/java/com/example/todolist
```

- [ ] **Step 2: Create `backend/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.3</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>todo-list-backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>todo-list-backend</name>
    <description>In-Memory Spring Boot To-Do List Application</description>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web: Embedded Tomcat and REST API support -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Jakarta Bean Validation: Validating request payloads -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- DevTools: Automatic application restart during development -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Testing: JUnit 5, AssertJ, Spring Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 3: Create `backend/src/main/resources/application.properties`**

```properties
# Server port
server.port=8080

# Application info
spring.application.name=todo-list-backend

# Logging levels
logging.level.com.example.todolist=DEBUG
```

- [ ] **Step 4: Create `backend/src/main/java/com/example/todolist/TodoListApplication.java`**

```java
package com.example.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point of our Spring Boot application.
 *
 * Notice the @SpringBootApplication annotation:
 * In LeetCode/basic Java, you manually instantiate objects and manage dependencies.
 * @SpringBootApplication activates three core features:
 * 1. @Configuration: Tags the class as a source of bean definitions for the application context.
 * 2. @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings.
 * 3. @ComponentScan: Tells Spring to look for other components, configurations, and services
 *    in the 'com.example.todolist' package and its sub-packages.
 */
@SpringBootApplication
public class TodoListApplication {

    public static void main(String[] args) {
        // Launches the embedded Tomcat web server and initializes the Spring IoC container
        SpringApplication.run(TodoListApplication.class, args);
    }
}
```

- [ ] **Step 5: Verify build with Maven**

Run: `mvn clean compile -f backend/pom.xml`
Expected: `BUILD SUCCESS`

- [ ] **Step 6: Commit**

```bash
git add backend/
git commit -m "feat(backend): scaffold Spring Boot 3 application with Maven"
```

---

### Task 2: Domain Model & DTOs

**Files:**
- Create: `backend/src/main/java/com/example/todolist/model/Todo.java`
- Create: `backend/src/main/java/com/example/todolist/dto/CreateTodoRequest.java`
- Create: `backend/src/main/java/com/example/todolist/dto/UpdateTodoRequest.java`
- Create: `backend/src/main/java/com/example/todolist/dto/ErrorResponse.java`

**Interfaces:**
- Consumes: Jakarta Validation annotations (`@NotBlank`, `@Size`)
- Produces: `Todo` model, `CreateTodoRequest`, `UpdateTodoRequest`, `ErrorResponse`

- [ ] **Step 1: Create `backend/src/main/java/com/example/todolist/model/Todo.java`**

```java
package com.example.todolist.model;

import java.time.LocalDateTime;

/**
 * Domain entity representing a To-Do item.
 * In backend development, domain models represent the core data structure
 * maintained by the application.
 */
public class Todo {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    public Todo() {
        this.createdAt = LocalDateTime.now();
    }

    public Todo(Long id, String title, String description, boolean completed, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
```

- [ ] **Step 2: Create `backend/src/main/java/com/example/todolist/dto/CreateTodoRequest.java`**

```java
package com.example.todolist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for creating a new To-Do item.
 *
 * Why DTOs instead of passing the Todo model directly in HTTP requests?
 * 1. Security: Prevents clients from supplying malicious fields like an existing 'id'.
 * 2. Separation of concerns: API contract is decoupled from internal storage representation.
 * 3. Validation: Jakarta annotations validate client inputs before hitting business logic.
 */
public class CreateTodoRequest {

    @NotBlank(message = "Title is required and cannot be blank")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    public CreateTodoRequest() {
    }

    public CreateTodoRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
```

- [ ] **Step 3: Create `backend/src/main/java/com/example/todolist/dto/UpdateTodoRequest.java`**

```java
package com.example.todolist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing To-Do item.
 */
public class UpdateTodoRequest {

    @NotBlank(message = "Title is required and cannot be blank")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private boolean completed;

    public UpdateTodoRequest() {
    }

    public UpdateTodoRequest(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
```

- [ ] **Step 4: Create `backend/src/main/java/com/example/todolist/dto/ErrorResponse.java`**

```java
package com.example.todolist.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard structured error response payload sent back to clients.
 */
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<String> details;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, List<String> details) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
```

- [ ] **Step 5: Verify build**

Run: `mvn compile -f backend/pom.xml`
Expected: `BUILD SUCCESS`

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/example/todolist/model/ backend/src/main/java/com/example/todolist/dto/
git commit -m "feat(backend): add Todo model and request/response DTOs"
```

---

### Task 3: In-Memory Repository Layer

**Files:**
- Create: `backend/src/main/java/com/example/todolist/repository/TodoRepository.java`
- Create: `backend/src/main/java/com/example/todolist/repository/InMemoryTodoRepository.java`

**Interfaces:**
- Consumes: `Todo` model
- Produces: `TodoRepository` interface & `InMemoryTodoRepository` Spring bean (`@Repository`)

- [ ] **Step 1: Create `backend/src/main/java/com/example/todolist/repository/TodoRepository.java`**

```java
package com.example.todolist.repository;

import com.example.todolist.model.Todo;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface following the Data Access Object (DAO) pattern.
 *
 * Why an interface?
 * Dependency Inversion Principle: High-level modules (Service) depend on abstractions (Interface),
 * not concrete implementations. If we later switch to a PostgreSQL or MongoDB database,
 * the Service code does not change at all!
 */
public interface TodoRepository {
    List<Todo> findAll();
    Optional<Todo> findById(Long id);
    Todo save(Todo todo);
    boolean deleteById(Long id);
    void clear();
}
```

- [ ] **Step 2: Create `backend/src/main/java/com/example/todolist/repository/InMemoryTodoRepository.java`**

```java
package com.example.todolist.repository;

import com.example.todolist.model.Todo;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe in-memory implementation of TodoRepository.
 *
 * Notice the @Repository annotation:
 * This informs Spring's IoC container that this class is a managed Bean providing
 * data persistence. Spring instantiates it as a singleton and injects it wherever needed.
 *
 * Why ConcurrentHashMap and AtomicLong?
 * Web servers (like Tomcat) handle incoming HTTP requests on multiple concurrent threads.
 * Standard HashMap or primitive `long` could lead to race conditions or data corruption.
 */
@Repository
public class InMemoryTodoRepository implements TodoRepository {

    private final ConcurrentHashMap<Long, Todo> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public InMemoryTodoRepository() {
        // Pre-populate with initial starter todos so the application is immediately interactive
        save(new Todo(null, "Learn Spring Boot Architecture", "Understand Controller, Service, and Repository layers", false, LocalDateTime.now().minusHours(2)));
        save(new Todo(null, "Explore Dependency Injection", "See how Spring wires beans together without manual 'new' keywords", false, LocalDateTime.now().minusHours(1)));
        save(new Todo(null, "Connect React Frontend", "Test full-stack CRUD functionality through REST API", false, LocalDateTime.now()));
    }

    @Override
    public List<Todo> findAll() {
        List<Todo> todos = new ArrayList<>(store.values());
        // Sort newest first
        todos.sort(Comparator.comparing(Todo::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return todos;
    }

    @Override
    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            long newId = idSequence.getAndIncrement();
            todo.setId(newId);
        }
        if (todo.getCreatedAt() == null) {
            todo.setCreatedAt(LocalDateTime.now());
        }
        store.put(todo.getId(), todo);
        return todo;
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public void clear() {
        store.clear();
        idSequence.set(1);
    }
}
```

- [ ] **Step 3: Verify build**

Run: `mvn compile -f backend/pom.xml`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/example/todolist/repository/
git commit -m "feat(backend): add thread-safe in-memory repository"
```

---

### Task 4: Service Layer & Business Logic (TDD)

**Files:**
- Create: `backend/src/main/java/com/example/todolist/exception/ResourceNotFoundException.java`
- Create: `backend/src/main/java/com/example/todolist/service/TodoService.java`
- Test: `backend/src/test/java/com/example/todolist/service/TodoServiceTest.java`

**Interfaces:**
- Consumes: `TodoRepository`, `CreateTodoRequest`, `UpdateTodoRequest`
- Produces: `TodoService` Spring bean (`@Service`)

- [ ] **Step 1: Create `backend/src/main/java/com/example/todolist/exception/ResourceNotFoundException.java`**

```java
package com.example.todolist.exception;

/**
 * Custom runtime exception thrown when a requested resource (e.g. To-Do ID) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

- [ ] **Step 2: Write failing unit test for `TodoService`**

Create `backend/src/test/java/com/example/todolist/service/TodoServiceTest.java`:
```java
package com.example.todolist.service;

import com.example.todolist.dto.CreateTodoRequest;
import com.example.todolist.dto.UpdateTodoRequest;
import com.example.todolist.exception.ResourceNotFoundException;
import com.example.todolist.model.Todo;
import com.example.todolist.repository.InMemoryTodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TodoServiceTest {

    private InMemoryTodoRepository repository;
    private TodoService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTodoRepository();
        repository.clear(); // clean state for each test
        service = new TodoService(repository);
    }

    @Test
    void shouldCreateTodo() {
        CreateTodoRequest request = new CreateTodoRequest("Buy milk", "2% organic milk");
        Todo created = service.createTodo(request);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Buy milk");
        assertThat(created.getDescription()).isEqualTo("2% organic milk");
        assertThat(created.isCompleted()).isFalse();
        assertThat(created.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldGetAllTodos() {
        service.createTodo(new CreateTodoRequest("Task 1", "Desc 1"));
        service.createTodo(new CreateTodoRequest("Task 2", "Desc 2"));

        List<Todo> all = service.getAllTodos();
        assertThat(all).hasSize(2);
    }

    @Test
    void shouldGetTodoById() {
        Todo created = service.createTodo(new CreateTodoRequest("Task 1", "Desc 1"));
        Todo found = service.getTodoById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getTitle()).isEqualTo("Task 1");
    }

    @Test
    void shouldThrowWhenTodoNotFound() {
        assertThatThrownBy(() -> service.getTodoById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Todo not found with id: 999");
    }

    @Test
    void shouldUpdateTodo() {
        Todo created = service.createTodo(new CreateTodoRequest("Initial Title", "Initial Desc"));

        UpdateTodoRequest updateRequest = new UpdateTodoRequest("Updated Title", "Updated Desc", true);
        Todo updated = service.updateTodo(created.getId(), updateRequest);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getDescription()).isEqualTo("Updated Desc");
        assertThat(updated.isCompleted()).isTrue();
    }

    @Test
    void shouldToggleTodoCompletion() {
        Todo created = service.createTodo(new CreateTodoRequest("Toggle Task", "Desc"));
        assertThat(created.isCompleted()).isFalse();

        Todo toggled = service.toggleTodoStatus(created.getId());
        assertThat(toggled.isCompleted()).isTrue();

        Todo toggledBack = service.toggleTodoStatus(created.getId());
        assertThat(toggledBack.isCompleted()).isFalse();
    }

    @Test
    void shouldDeleteTodo() {
        Todo created = service.createTodo(new CreateTodoRequest("Delete Task", "Desc"));
        service.deleteTodo(created.getId());

        assertThatThrownBy(() -> service.getTodoById(created.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
```

- [ ] **Step 3: Run test to verify it fails (Service not implemented yet)**

Run: `mvn test -Dtest=TodoServiceTest -f backend/pom.xml`
Expected: Compilation failure or FAIL because `TodoService` does not exist yet.

- [ ] **Step 4: Implement `backend/src/main/java/com/example/todolist/service/TodoService.java`**

```java
package com.example.todolist.service;

import com.example.todolist.dto.CreateTodoRequest;
import com.example.todolist.dto.UpdateTodoRequest;
import com.example.todolist.exception.ResourceNotFoundException;
import com.example.todolist.model.Todo;
import com.example.todolist.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer containing the core business logic.
 *
 * Notice the @Service annotation:
 * This registers the class as a Spring Bean within the Service layer.
 *
 * Notice Constructor Injection:
 * Spring automatically supplies the 'TodoRepository' bean to this constructor.
 * This is the heart of Dependency Injection (DI) in Spring Boot:
 * We don't call `new InMemoryTodoRepository()` here. Instead, Spring gives it to us.
 */
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Todo getTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
    }

    public Todo createTodo(CreateTodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle().trim());
        todo.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        todo.setCompleted(false);
        todo.setCreatedAt(LocalDateTime.now());
        return todoRepository.save(todo);
    }

    public Todo updateTodo(Long id, UpdateTodoRequest request) {
        Todo existing = getTodoById(id);
        existing.setTitle(request.getTitle().trim());
        existing.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        existing.setCompleted(request.isCompleted());
        return todoRepository.save(existing);
    }

    public Todo toggleTodoStatus(Long id) {
        Todo existing = getTodoById(id);
        existing.setCompleted(!existing.isCompleted());
        return todoRepository.save(existing);
    }

    public void deleteTodo(Long id) {
        boolean deleted = todoRepository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Todo not found with id: " + id);
        }
    }
}
```

- [ ] **Step 5: Run unit test to verify it passes**

Run: `mvn test -Dtest=TodoServiceTest -f backend/pom.xml`
Expected: `BUILD SUCCESS`, 7 tests passed.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/example/todolist/service/ backend/src/main/java/com/example/todolist/exception/ backend/src/test/java/com/example/todolist/service/
git commit -m "feat(backend): implement TodoService business logic with unit tests"
```

---

### Task 5: REST Controller, CORS & Centralized Exception Handling

**Files:**
- Create: `backend/src/main/java/com/example/todolist/config/CorsConfig.java`
- Create: `backend/src/main/java/com/example/todolist/exception/GlobalExceptionHandler.java`
- Create: `backend/src/main/java/com/example/todolist/controller/TodoController.java`
- Test: `backend/src/test/java/com/example/todolist/controller/TodoControllerTest.java`

**Interfaces:**
- Consumes: `TodoService`, `CreateTodoRequest`, `UpdateTodoRequest`
- Produces: REST endpoints at `/api/todos` (GET, POST, PUT, PATCH, DELETE)

- [ ] **Step 1: Create `backend/src/main/java/com/example/todolist/config/CorsConfig.java`**

```java
package com.example.todolist.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cross-Origin Resource Sharing (CORS) Configuration.
 *
 * Browsers block frontend web apps (running on port 5173) from making AJAX requests
 * to a backend on another port (8080) unless explicitly allowed via CORS headers.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

- [ ] **Step 2: Create `backend/src/main/java/com/example/todolist/exception/GlobalExceptionHandler.java`**

```java
package com.example.todolist.exception;

import com.example.todolist.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralized exception interceptor.
 *
 * Notice @RestControllerAdvice:
 * Without this, if an unhandled exception occurs, Spring returns a generic HTML error page
 * with a stack trace. With @RestControllerAdvice, any exception thrown inside any Controller
 * is caught here and converted into clean, standard JSON error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for request body",
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

- [ ] **Step 3: Create `backend/src/main/java/com/example/todolist/controller/TodoController.java`**

```java
package com.example.todolist.controller;

import com.example.todolist.dto.CreateTodoRequest;
import com.example.todolist.dto.UpdateTodoRequest;
import com.example.todolist.model.Todo;
import com.example.todolist.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing HTTP CRUD endpoints for To-Do items.
 *
 * Key Spring Boot Annotations explained:
 * - @RestController: Marks this class as a request handler where every method returns
 *   a domain object directly serialized into JSON (combines @Controller and @ResponseBody).
 * - @RequestMapping("/api/todos"): Sets the base URL path prefix for all endpoints here.
 * - @GetMapping, @PostMapping, @PutMapping, @PatchMapping, @DeleteMapping: HTTP method routing.
 * - @PathVariable: Extracts parameters from URL path (e.g. /api/todos/{id}).
 * - @RequestBody: Deserializes the incoming JSON body into a Java DTO object.
 * - @Valid: Triggers Jakarta Bean Validation on the request object.
 */
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody CreateTodoRequest request) {
        Todo created = todoService.createTodo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @Valid @RequestBody UpdateTodoRequest request) {
        Todo updated = todoService.updateTodo(id, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Todo> toggleTodoStatus(@PathVariable Long id) {
        Todo toggled = todoService.toggleTodoStatus(id);
        return ResponseEntity.ok(toggled);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
```

- [ ] **Step 4: Create integration test `backend/src/test/java/com/example/todolist/controller/TodoControllerTest.java`**

```java
package com.example.todolist.controller;

import com.example.todolist.dto.CreateTodoRequest;
import com.example.todolist.dto.UpdateTodoRequest;
import com.example.todolist.model.Todo;
import com.example.todolist.repository.InMemoryTodoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InMemoryTodoRepository repository;

    @BeforeEach
    void cleanState() {
        repository.clear();
    }

    @Test
    void shouldCreateAndReturnTodo() throws Exception {
        CreateTodoRequest request = new CreateTodoRequest("Study REST APIs", "Learn GET, POST, PUT, DELETE");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Study REST APIs")))
                .andExpect(jsonPath("$.completed", is(false)));
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {
        CreateTodoRequest request = new CreateTodoRequest("", "Description only");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.details", not(empty())));
    }

    @Test
    void shouldGetAllTodos() throws Exception {
        repository.save(new Todo(null, "Task A", "Desc A", false, null));
        repository.save(new Todo(null, "Task B", "Desc B", true, null));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldReturn404ForNonexistentTodo() throws Exception {
        mockMvc.perform(get("/api/todos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Todo not found with id: 999")));
    }

    @Test
    void shouldToggleTodoStatus() throws Exception {
        Todo saved = repository.save(new Todo(null, "Toggle Test", "Desc", false, null));

        mockMvc.perform(patch("/api/todos/" + saved.getId() + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void shouldDeleteTodo() throws Exception {
        Todo saved = repository.save(new Todo(null, "Delete Test", "Desc", false, null));

        mockMvc.perform(delete("/api/todos/" + saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/todos/" + saved.getId()))
                .andExpect(status().isNotFound());
    }
}
```

- [ ] **Step 5: Run integration tests to verify all endpoints pass**

Run: `mvn test -Dtest=TodoControllerTest -f backend/pom.xml`
Expected: `BUILD SUCCESS`, all tests passed.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/example/todolist/controller/ backend/src/main/java/com/example/todolist/config/ backend/src/main/java/com/example/todolist/exception/ backend/src/test/java/com/example/todolist/controller/
git commit -m "feat(backend): implement REST controller, CORS, exception handling and tests"
```

---

### Task 6: Frontend Scaffolding (React + Vite)

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.js`
- Create: `frontend/index.html`
- Create: `frontend/src/main.jsx`
- Create: `frontend/src/index.css`

**Interfaces:**
- Consumes: Node.js & npm runtime
- Produces: Runnable Vite React dev environment on port `5173`

- [ ] **Step 1: Create directory structure for frontend**

```bash
mkdir -p frontend/src/components frontend/src/services
```

- [ ] **Step 2: Create `frontend/package.json`**

```json
{
  "name": "todo-list-frontend",
  "private": true,
  "version": "0.1.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.3.1",
    "vite": "^5.4.2"
  }
}
```

- [ ] **Step 3: Create `frontend/vite.config.js`**

```javascript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    open: false
  }
});
```

- [ ] **Step 4: Create `frontend/index.html`**

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Spring Boot & React To-Do List</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.jsx"></script>
  </body>
</html>
```

- [ ] **Step 5: Create base styles `frontend/src/index.css`**

```css
:root {
  --primary: #4f46e5;
  --primary-hover: #4338ca;
  --primary-light: #eef2ff;
  --success: #10b981;
  --success-light: #d1fae5;
  --danger: #ef4444;
  --danger-hover: #dc2626;
  --danger-light: #fee2e2;
  --gray-50: #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-300: #d1d5db;
  --gray-400: #9ca3af;
  --gray-500: #6b7280;
  --gray-600: #4b5563;
  --gray-700: #374151;
  --gray-800: #1f2937;
  --gray-900: #111827;
  --shadow-sm: 0 1px 2px 0 rgb(0 0 0 / 0.05);
  --shadow-md: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  --radius: 10px;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background-color: #f3f4f6;
  color: var(--gray-800);
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
}
```

- [ ] **Step 6: Create `frontend/src/main.jsx`**

```jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

- [ ] **Step 7: Install npm dependencies**

Run: `npm install --prefix frontend`
Expected: `added ... packages in ...s`

- [ ] **Step 8: Commit**

```bash
git add frontend/package.json frontend/vite.config.js frontend/index.html frontend/src/main.jsx frontend/src/index.css frontend/package-lock.json
git commit -m "feat(frontend): scaffold React Vite application"
```

---

### Task 7: Frontend API Client & Components

**Files:**
- Create: `frontend/src/services/api.js`
- Create: `frontend/src/components/FilterBar.jsx`
- Create: `frontend/src/components/TodoForm.jsx`
- Create: `frontend/src/components/TodoItem.jsx`
- Create: `frontend/src/components/TodoList.jsx`
- Create: `frontend/src/App.jsx`
- Create: `frontend/src/App.css`

**Interfaces:**
- Consumes: REST endpoints exposed by backend (`/api/todos`)
- Produces: Complete interactive To-Do List UI

- [ ] **Step 1: Create `frontend/src/services/api.js`**

```javascript
const API_BASE_URL = 'http://localhost:8080/api/todos';

/**
 * Helper to handle fetch responses and parse error messages
 */
async function handleResponse(response) {
  if (!response.ok) {
    let errorMessage = `Request failed with status ${response.status}`;
    try {
      const errorBody = await response.json();
      if (errorBody.details && errorBody.details.length > 0) {
        errorMessage = errorBody.details.join(', ');
      } else if (errorBody.message) {
        errorMessage = errorBody.message;
      }
    } catch {
      // response wasn't JSON
    }
    throw new Error(errorMessage);
  }
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

export const todoApi = {
  async getAll() {
    const response = await fetch(API_BASE_URL);
    return handleResponse(response);
  },

  async create(title, description) {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, description }),
    });
    return handleResponse(response);
  },

  async update(id, title, description, completed) {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, description, completed }),
    });
    return handleResponse(response);
  },

  async toggleStatus(id) {
    const response = await fetch(`${API_BASE_URL}/${id}/toggle`, {
      method: 'PATCH',
    });
    return handleResponse(response);
  },

  async delete(id) {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'DELETE',
    });
    return handleResponse(response);
  },
};
```

- [ ] **Step 2: Create `frontend/src/components/FilterBar.jsx`**

```jsx
import React from 'react';

export default function FilterBar({ currentFilter, setFilter, counts }) {
  const filters = [
    { key: 'all', label: 'All', count: counts.all },
    { key: 'active', label: 'Active', count: counts.active },
    { key: 'completed', label: 'Completed', count: counts.completed },
  ];

  return (
    <div className="filter-bar">
      {filters.map((f) => (
        <button
          key={f.key}
          type="button"
          className={`filter-btn ${currentFilter === f.key ? 'active' : ''}`}
          onClick={() => setFilter(f.key)}
        >
          {f.label}
          <span className="count-badge">{f.count}</span>
        </button>
      ))}
    </div>
  );
}
```

- [ ] **Step 3: Create `frontend/src/components/TodoForm.jsx`**

```jsx
import React, { useState } from 'react';

export default function TodoForm({ onAddTodo }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [validationError, setValidationError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!title.trim()) {
      setValidationError('Title cannot be empty');
      return;
    }

    setValidationError('');
    setSubmitting(true);
    try {
      await onAddTodo(title.trim(), description.trim());
      setTitle('');
      setDescription('');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="todo-form" onSubmit={handleSubmit}>
      <h3>Add a New Task</h3>
      {validationError && <div className="form-error">{validationError}</div>}
      <div className="form-group">
        <input
          type="text"
          className="form-input"
          placeholder="Task title (e.g. Learn Dependency Injection)..."
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          disabled={submitting}
          maxLength={100}
        />
      </div>
      <div className="form-group">
        <textarea
          className="form-textarea"
          placeholder="Description (optional)..."
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          disabled={submitting}
          rows={2}
          maxLength={500}
        />
      </div>
      <button type="submit" className="submit-btn" disabled={submitting || !title.trim()}>
        {submitting ? 'Adding...' : 'Add Task'}
      </button>
    </form>
  );
}
```

- [ ] **Step 4: Create `frontend/src/components/TodoItem.jsx`**

```jsx
import React, { useState } from 'react';

export default function TodoItem({ todo, onToggle, onUpdate, onDelete }) {
  const [isEditing, setIsEditing] = useState(false);
  const [editTitle, setEditTitle] = useState(todo.title);
  const [editDescription, setEditDescription] = useState(todo.description || '');

  const handleSaveEdit = async (e) => {
    e.preventDefault();
    if (!editTitle.trim()) return;
    await onUpdate(todo.id, editTitle.trim(), editDescription.trim(), todo.completed);
    setIsEditing(false);
  };

  const formattedDate = todo.createdAt
    ? new Date(todo.createdAt).toLocaleString(undefined, {
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      })
    : '';

  if (isEditing) {
    return (
      <li className="todo-item editing">
        <form onSubmit={handleSaveEdit} className="edit-form">
          <input
            type="text"
            className="form-input"
            value={editTitle}
            onChange={(e) => setEditTitle(e.target.value)}
            required
            maxLength={100}
          />
          <textarea
            className="form-textarea"
            value={editDescription}
            onChange={(e) => setEditDescription(e.target.value)}
            rows={2}
            maxLength={500}
          />
          <div className="edit-actions">
            <button type="submit" className="btn-save">Save</button>
            <button type="button" className="btn-cancel" onClick={() => setIsEditing(false)}>Cancel</button>
          </div>
        </form>
      </li>
    );
  }

  return (
    <li className={`todo-item ${todo.completed ? 'completed' : ''}`}>
      <div className="todo-main">
        <label className="checkbox-container">
          <input
            type="checkbox"
            checked={todo.completed}
            onChange={() => onToggle(todo.id)}
          />
          <span className="checkmark"></span>
        </label>
        <div className="todo-content">
          <h4 className="todo-title">{todo.title}</h4>
          {todo.description && <p className="todo-desc">{todo.description}</p>}
          <div className="todo-meta">
            <span className={`status-badge ${todo.completed ? 'badge-completed' : 'badge-pending'}`}>
              {todo.completed ? 'Completed' : 'Pending'}
            </span>
            {formattedDate && <span className="todo-date">{formattedDate}</span>}
          </div>
        </div>
      </div>
      <div className="item-actions">
        <button
          type="button"
          className="action-btn edit-btn"
          title="Edit Task"
          onClick={() => setIsEditing(true)}
        >
          ✏️
        </button>
        <button
          type="button"
          className="action-btn delete-btn"
          title="Delete Task"
          onClick={() => onDelete(todo.id)}
        >
          🗑️
        </button>
      </div>
    </li>
  );
}
```

- [ ] **Step 5: Create `frontend/src/components/TodoList.jsx`**

```jsx
import React from 'react';
import TodoItem from './TodoItem';

export default function TodoList({ todos, onToggle, onUpdate, onDelete }) {
  if (todos.length === 0) {
    return (
      <div className="empty-state">
        <p className="empty-icon">📝</p>
        <h3>No tasks found</h3>
        <p className="empty-subtitle">Add a task above to get started.</p>
      </div>
    );
  }

  return (
    <ul className="todo-list">
      {todos.map((todo) => (
        <TodoItem
          key={todo.id}
          todo={todo}
          onToggle={onToggle}
          onUpdate={onUpdate}
          onDelete={onDelete}
        />
      ))}
    </ul>
  );
}
```

- [ ] **Step 6: Create `frontend/src/App.css`**

```css
.app-container {
  max-width: 680px;
  margin: 40px auto;
  padding: 0 16px;
}

.app-header {
  text-align: center;
  margin-bottom: 28px;
}

.app-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: var(--gray-900);
}

.app-subtitle {
  color: var(--gray-500);
  font-size: 0.95rem;
  margin-top: 4px;
}

.alert-error {
  background-color: var(--danger-light);
  color: var(--danger);
  padding: 12px 16px;
  border-radius: var(--radius);
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
}

.alert-dismiss {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: var(--danger);
}

.card {
  background: #ffffff;
  border-radius: var(--radius);
  box-shadow: var(--shadow-md);
  padding: 24px;
  margin-bottom: 24px;
}

.todo-form h3 {
  font-size: 1.1rem;
  margin-bottom: 14px;
  color: var(--gray-800);
}

.form-group {
  margin-bottom: 12px;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--gray-300);
  border-radius: 6px;
  font-size: 0.95rem;
  font-family: inherit;
  transition: border-color 0.15s ease;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.form-error {
  color: var(--danger);
  font-size: 0.85rem;
  margin-bottom: 8px;
}

.submit-btn {
  background-color: var(--primary);
  color: #fff;
  border: none;
  padding: 10px 20px;
  font-size: 0.95rem;
  font-weight: 600;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.15s ease;
}

.submit-btn:hover:not(:disabled) {
  background-color: var(--primary-hover);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.filter-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.filter-btn {
  background: #ffffff;
  border: 1px solid var(--gray-300);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--gray-600);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.15s ease;
}

.filter-btn.active {
  background-color: var(--primary);
  color: #ffffff;
  border-color: var(--primary);
}

.count-badge {
  background: rgba(0, 0, 0, 0.08);
  padding: 1px 6px;
  border-radius: 10px;
  font-size: 0.75rem;
}

.filter-btn.active .count-badge {
  background: rgba(255, 255, 255, 0.25);
}

.todo-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item {
  background: #ffffff;
  border-radius: var(--radius);
  box-shadow: var(--shadow-sm);
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  transition: box-shadow 0.15s ease, border-color 0.15s ease;
  border: 1px solid var(--gray-200);
}

.todo-item:hover {
  box-shadow: var(--shadow-md);
}

.todo-item.completed {
  opacity: 0.75;
  background-color: var(--gray-50);
}

.todo-item.completed .todo-title {
  text-decoration: line-through;
  color: var(--gray-400);
}

.todo-main {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  flex: 1;
}

.checkbox-container {
  display: block;
  position: relative;
  cursor: pointer;
  margin-top: 3px;
}

.checkbox-container input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--primary);
}

.todo-content {
  flex: 1;
}

.todo-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--gray-900);
  word-break: break-word;
}

.todo-desc {
  font-size: 0.88rem;
  color: var(--gray-600);
  margin-top: 4px;
  word-break: break-word;
}

.todo-meta {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-top: 8px;
}

.status-badge {
  font-size: 0.75rem;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 12px;
  text-transform: uppercase;
}

.badge-completed {
  background-color: var(--success-light);
  color: var(--success);
}

.badge-pending {
  background-color: var(--primary-light);
  color: var(--primary);
}

.todo-date {
  font-size: 0.75rem;
  color: var(--gray-400);
}

.item-actions {
  display: flex;
  gap: 6px;
  margin-left: 12px;
}

.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  border-radius: 4px;
  font-size: 1rem;
  transition: background-color 0.15s ease;
}

.action-btn:hover {
  background-color: var(--gray-100);
}

.edit-actions {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.btn-save {
  background: var(--primary);
  color: #fff;
  border: none;
  padding: 6px 14px;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
}

.btn-cancel {
  background: var(--gray-200);
  color: var(--gray-700);
  border: none;
  padding: 6px 14px;
  border-radius: 4px;
  cursor: pointer;
}

.empty-state {
  text-align: center;
  padding: 48px 16px;
  background: #ffffff;
  border-radius: var(--radius);
  border: 2px dashed var(--gray-300);
}

.empty-icon {
  font-size: 2.5rem;
  margin-bottom: 8px;
}

.empty-state h3 {
  font-size: 1.1rem;
  color: var(--gray-700);
}

.empty-subtitle {
  color: var(--gray-400);
  font-size: 0.9rem;
  margin-top: 4px;
}

.loading-indicator {
  text-align: center;
  padding: 24px;
  color: var(--gray-500);
  font-size: 0.95rem;
}
```

- [ ] **Step 7: Create `frontend/src/App.jsx`**

```jsx
import React, { useState, useEffect } from 'react';
import { todoApi } from './services/api';
import TodoForm from './components/TodoForm';
import FilterBar from './components/FilterBar';
import TodoList from './components/TodoList';
import './App.css';

export default function App() {
  const [todos, setTodos] = useState([]);
  const [filter, setFilter] = useState('all');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchTodos = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await todoApi.getAll();
      setTodos(data);
    } catch (err) {
      setError('Could not connect to Spring Boot backend: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTodos();
  }, []);

  const handleAddTodo = async (title, description) => {
    try {
      const newTodo = await todoApi.create(title, description);
      setTodos((prev) => [newTodo, ...prev]);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleToggle = async (id) => {
    try {
      const updated = await todoApi.toggleStatus(id);
      setTodos((prev) => prev.map((t) => (t.id === id ? updated : t)));
    } catch (err) {
      setError(err.message);
    }
  };

  const handleUpdate = async (id, title, description, completed) => {
    try {
      const updated = await todoApi.update(id, title, description, completed);
      setTodos((prev) => prev.map((t) => (t.id === id ? updated : t)));
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (id) => {
    try {
      await todoApi.delete(id);
      setTodos((prev) => prev.filter((t) => t.id !== id));
    } catch (err) {
      setError(err.message);
    }
  };

  const filteredTodos = todos.filter((todo) => {
    if (filter === 'active') return !todo.completed;
    if (filter === 'completed') return todo.completed;
    return true;
  });

  const counts = {
    all: todos.length,
    active: todos.filter((t) => !t.completed).length,
    completed: todos.filter((t) => t.completed).length,
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <h1>Task Manager</h1>
        <p className="app-subtitle">Spring Boot 3 + React In-Memory CRUD</p>
      </header>

      {error && (
        <div className="alert-error">
          <span>{error}</span>
          <button className="alert-dismiss" onClick={() => setError('')}>&times;</button>
        </div>
      )}

      <div className="card">
        <TodoForm onAddTodo={handleAddTodo} />
      </div>

      <FilterBar currentFilter={filter} setFilter={setFilter} counts={counts} />

      {loading ? (
        <div className="loading-indicator">Loading tasks from backend...</div>
      ) : (
        <TodoList
          todos={filteredTodos}
          onToggle={handleToggle}
          onUpdate={handleUpdate}
          onDelete={handleDelete}
        />
      )}
    </div>
  );
}
```

- [ ] **Step 8: Verify build with Vite**

Run: `npm run build --prefix frontend`
Expected: `vite build` completed successfully, dist folder generated.

- [ ] **Step 9: Commit**

```bash
git add frontend/src/
git commit -m "feat(frontend): implement UI components and API client service"
```

---

### Task 8: Comprehensive Running Guide & Educational Documentation

**Files:**
- Create: `RUNNING_GUIDE.md`
- Create: `README.md`

**Interfaces:**
- Consumes: Complete project structure
- Produces: Exhaustive developer manual & conceptual guide for VS Code and terminal

- [ ] **Step 1: Create `RUNNING_GUIDE.md`**

Create `RUNNING_GUIDE.md` covering:
1. System check (`java -version`, `node -v`).
2. Running the Spring Boot backend via terminal (`mvn spring-boot:run` from `backend/`).
3. Running the Spring Boot backend via VS Code (`code-sandbox`):
   - Using the Spring Boot Dashboard extension.
   - Or opening `backend/src/main/java/com/example/todolist/TodoListApplication.java` and clicking **Run** above `main()`.
4. Running the React frontend (`cd frontend && npm run dev`).
5. Testing each API endpoint using complete copy-paste `curl` commands (GET, POST, PUT, PATCH, DELETE).
6. Troubleshooting guide:
   - What to do if port 8080 or 5173 is already in use (`lsof -i :8080`, `kill -9`).
   - CORS error explanation and resolution.
   - In-memory data reset behavior on backend restart.

- [ ] **Step 2: Create `README.md`**

Create `README.md` covering:
1. Architecture diagrams and overview.
2. Conceptual guide for LeetCode Java developers:
   - Inversion of Control (IoC) & Beans.
   - Dependency Injection (DI) without `new`.
   - Controller vs Service vs Repository separation.
   - Annotations cheat sheet (`@SpringBootApplication`, `@RestController`, `@Service`, `@Repository`, `@Valid`, `@RequestBody`, `@PathVariable`).
3. Project directory map.

- [ ] **Step 3: Commit documentation**

```bash
git add RUNNING_GUIDE.md README.md
git commit -m "docs: add comprehensive running guide and Spring Boot concepts README"
```

---

### Task 9: Full Verification & Sanity Check

**Files:**
- Verification only

- [ ] **Step 1: Run all backend tests**

Run: `mvn clean test -f backend/pom.xml`
Expected: `BUILD SUCCESS`, all unit and integration tests pass.

- [ ] **Step 2: Verify frontend builds without errors**

Run: `npm run build --prefix frontend`
Expected: Vite build succeeds with 0 errors.

- [ ] **Step 3: Verify git status is clean**

Run: `git status`
Expected: Clean working tree.
