import React from 'react';
import TodoItem from './TodoItem';

export default function TodoList({ todos, onToggle, onUpdate, onDelete }) {
  if (todos.length === 0) {
    return (
      <div className="empty-state">
        <p className="empty-icon">📝</p>
        <h3>No tasks found</h3>
        <p className="empty-subtitle">Add a task above to get started.</p>
      </div>
    );
  }

  return (
    <ul className="todo-list">
      {todos.map((todo) => (
        <TodoItem
          key={todo.id}
          todo={todo}
          onToggle={onToggle}
          onUpdate={onUpdate}
          onDelete={onDelete}
        />
      ))}
    </ul>
  );
}
