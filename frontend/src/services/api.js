const API_BASE_URL = 'http://localhost:8080/api/todos';

/**
 * Helper to handle fetch responses and parse error messages
 */
async function handleResponse(response) {
  if (!response.ok) {
    let errorMessage = `Request failed with status ${response.status}`;
    try {
      const errorBody = await response.json();
      if (errorBody.details && errorBody.details.length > 0) {
        errorMessage = errorBody.details.join(', ');
      } else if (errorBody.message) {
        errorMessage = errorBody.message;
      }
    } catch {
      // response wasn't JSON
    }
    throw new Error(errorMessage);
  }
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

export const todoApi = {
  async getAll() {
    const response = await fetch(API_BASE_URL);
    return handleResponse(response);
  },

  async create(title, description) {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, description }),
    });
    return handleResponse(response);
  },

  async update(id, title, description, completed) {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, description, completed }),
    });
    return handleResponse(response);
  },

  async toggleStatus(id) {
    const response = await fetch(`${API_BASE_URL}/${id}/toggle`, {
      method: 'PATCH',
    });
    return handleResponse(response);
  },

  async delete(id) {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'DELETE',
    });
    return handleResponse(response);
  },
};
