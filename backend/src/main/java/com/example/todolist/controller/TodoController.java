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
