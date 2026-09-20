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
