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
