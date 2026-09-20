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
