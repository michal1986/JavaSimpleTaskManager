-- Create task table
CREATE TABLE IF NOT EXISTS task (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    due_date DATE,
    priority VARCHAR(20),
    assigned_to VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert dummy tasks
INSERT INTO task (title, description, status, due_date, priority, assigned_to)
VALUES
    ('Implement login', 'Create login functionality with JWT', 'IN_PROGRESS', '2024-03-20', 'HIGH', 'john_doe'),
    ('Design database', 'Design initial database schema', 'COMPLETED', '2024-03-15', 'HIGH', 'jane_doe'),
    ('Add unit tests', 'Write unit tests for core functionality', 'TODO', '2024-03-25', 'MEDIUM', 'john_doe'),
    ('Setup CI/CD', 'Configure GitHub Actions for CI/CD', 'TODO', '2024-03-30', 'LOW', 'manager');

-- Note: User creation is now handled by the DataInitializer class at application startup