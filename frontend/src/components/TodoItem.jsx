import React, { useState } from 'react';

export default function TodoItem({ todo, onToggle, onUpdate, onDelete }) {
  const [isEditing, setIsEditing] = useState(false);
  const [editTitle, setEditTitle] = useState(todo.title);
  const [editDescription, setEditDescription] = useState(todo.description || '');

  const handleSaveEdit = async (e) => {
    e.preventDefault();
    if (!editTitle.trim()) return;
    try {
      await onUpdate(todo.id, editTitle.trim(), editDescription.trim(), todo.completed);
      setIsEditing(false);
    } catch {
      // Keep edit mode active on failure
    }
  };

  const formattedDate = todo.createdAt
    ? new Date(todo.createdAt).toLocaleString(undefined, {
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      })
    : '';

  if (isEditing) {
    return (
      <li className="todo-item editing">
        <form onSubmit={handleSaveEdit} className="edit-form">
          <input
            type="text"
            className="form-input"
            value={editTitle}
            onChange={(e) => setEditTitle(e.target.value)}
            required
            maxLength={100}
          />
          <textarea
            className="form-textarea"
            value={editDescription}
            onChange={(e) => setEditDescription(e.target.value)}
            rows={2}
            maxLength={500}
          />
          <div className="edit-actions">
            <button type="submit" className="btn-save">Save</button>
            <button type="button" className="btn-cancel" onClick={() => setIsEditing(false)}>Cancel</button>
          </div>
        </form>
      </li>
    );
  }

  return (
    <li className={`todo-item ${todo.completed ? 'completed' : ''}`}>
      <div className="todo-main">
        <label className="checkbox-container">
          <input
            type="checkbox"
            checked={todo.completed}
            onChange={() => onToggle(todo.id)}
          />
          <span className="checkmark"></span>
        </label>
        <div className="todo-content">
          <h4 className="todo-title">{todo.title}</h4>
          {todo.description && <p className="todo-desc">{todo.description}</p>}
          <div className="todo-meta">
            <span className={`status-badge ${todo.completed ? 'badge-completed' : 'badge-pending'}`}>
              {todo.completed ? 'Completed' : 'Pending'}
            </span>
            {formattedDate && <span className="todo-date">{formattedDate}</span>}
          </div>
        </div>
      </div>
      <div className="item-actions">
        <button
          type="button"
          className="action-btn edit-btn"
          title="Edit Task"
          onClick={() => setIsEditing(true)}
        >
          ✏️
        </button>
        <button
          type="button"
          className="action-btn delete-btn"
          title="Delete Task"
          onClick={() => onDelete(todo.id)}
        >
          🗑️
        </button>
      </div>
    </li>
  );
}
