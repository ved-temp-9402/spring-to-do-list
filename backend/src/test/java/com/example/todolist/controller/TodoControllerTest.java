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

    @Test
    void shouldGetTodoById() throws Exception {
        Todo saved = repository.save(new Todo(null, "Get by ID", "Desc", false, null));

        mockMvc.perform(get("/api/todos/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Get by ID")));
    }

    @Test
    void shouldUpdateTodo() throws Exception {
        Todo saved = repository.save(new Todo(null, "Original", "Original Desc", false, null));
        UpdateTodoRequest request = new UpdateTodoRequest("Updated", "Updated Desc", true);

        mockMvc.perform(put("/api/todos/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ malformed json "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Malformed JSON request body")));
    }
}
