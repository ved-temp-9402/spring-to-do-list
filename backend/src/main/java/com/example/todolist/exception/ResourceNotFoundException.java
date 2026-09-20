package com.example.todolist.exception;

/**
 * Custom runtime exception thrown when a requested resource (e.g. To-Do ID) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
