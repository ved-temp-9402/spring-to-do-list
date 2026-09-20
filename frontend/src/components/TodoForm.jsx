import React, { useState } from 'react';

export default function TodoForm({ onAddTodo }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [validationError, setValidationError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!title.trim()) {
      setValidationError('Title cannot be empty');
      return;
    }

    setValidationError('');
    setSubmitting(true);
    try {
      await onAddTodo(title.trim(), description.trim());
      setTitle('');
      setDescription('');
    } catch {
      // Keep form inputs on failure
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="todo-form" onSubmit={handleSubmit}>
      <h3>Add a New Task</h3>
      {validationError && <div className="form-error">{validationError}</div>}
      <div className="form-group">
        <input
          type="text"
          className="form-input"
          placeholder="Task title (e.g. Learn Dependency Injection)..."
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          disabled={submitting}
          maxLength={100}
        />
      </div>
      <div className="form-group">
        <textarea
          className="form-textarea"
          placeholder="Description (optional)..."
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          disabled={submitting}
          rows={2}
          maxLength={500}
        />
      </div>
      <button type="submit" className="submit-btn" disabled={submitting || !title.trim()}>
        {submitting ? 'Adding...' : 'Add Task'}
      </button>
    </form>
  );
}
