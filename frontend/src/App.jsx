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
      throw err;
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
      throw err;
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
