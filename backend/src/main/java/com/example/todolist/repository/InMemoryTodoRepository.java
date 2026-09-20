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
