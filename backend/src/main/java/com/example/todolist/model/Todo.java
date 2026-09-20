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

    public Todo(Todo other) {
        this.id = other.id;
        this.title = other.title;
        this.description = other.description;
        this.completed = other.completed;
        this.createdAt = other.createdAt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return completed == todo.completed &&
                java.util.Objects.equals(id, todo.id) &&
                java.util.Objects.equals(title, todo.title) &&
                java.util.Objects.equals(description, todo.description) &&
                java.util.Objects.equals(createdAt, todo.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, title, description, completed, createdAt);
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                ", createdAt=" + createdAt +
                '}';
    }
}
