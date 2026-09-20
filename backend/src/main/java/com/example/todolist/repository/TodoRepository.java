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
