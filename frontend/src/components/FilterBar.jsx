import React from 'react';

export default function FilterBar({ currentFilter, setFilter, counts }) {
  const filters = [
    { key: 'all', label: 'All', count: counts.all },
    { key: 'active', label: 'Active', count: counts.active },
    { key: 'completed', label: 'Completed', count: counts.completed },
  ];

  return (
    <div className="filter-bar">
      {filters.map((f) => (
        <button
          key={f.key}
          type="button"
          className={`filter-btn ${currentFilter === f.key ? 'active' : ''}`}
          onClick={() => setFilter(f.key)}
        >
          {f.label}
          <span className="count-badge">{f.count}</span>
        </button>
      ))}
    </div>
  );
}
