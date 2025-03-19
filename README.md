# Task Manager API

A RESTful API for managing tasks, built with Spring Boot and Docker.

## Features

- Create, read, update, and delete tasks
- Task properties include title, description, status, due date, priority, and assignee
- User authentication with JWT
- Role-based authorization
- Automatic user initialization with BCrypt password hashing
- PostgreSQL database
- Docker support for containerization

## Prerequisites

- Java 17 or later
- Maven 3.6 or later
- Docker and Docker Compose (for containerization)

## Building and Running

### Local Development

1. Clone the repository
2. Navigate to the project directory
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the application at `http://localhost:8080`

### Docker

1. Run with Docker Compose:
   ```bash
   docker-compose up --build
   ```

## API Endpoints

The API provides the following endpoints:

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login to get a JWT token

### Tasks

- `GET /api/tasks` - Get all tasks
- `GET /api/tasks/{id}` - Get a specific task
- `GET /api/tasks/status/{status}` - Get tasks by status
- `POST /api/tasks` - Create a new task
- `PUT /api/tasks/{id}` - Update a task
- `PATCH /api/tasks/{id}/toggle` - Toggle task completion status
- `DELETE /api/tasks/{id}` - Delete a task

## API Testing with cURL

### Authentication

#### Login to get a JWT token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Tasks

Replace `YOUR_JWT_TOKEN` in the following commands with the token received from the login endpoint.

#### Get all tasks

```bash
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Get tasks by status

```bash
curl -X GET http://localhost:8080/api/tasks/status/IN_PROGRESS \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Get a specific task by ID

```bash
curl -X GET http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Create a new task

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Task",
    "description": "This is a new task",
    "status": "TODO",
    "dueDate": "2024-04-01",
    "priority": "HIGH",
    "assignedTo": "admin"
  }'
```

#### Update a task

```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Task",
    "description": "This is an updated task",
    "status": "IN_PROGRESS",
    "dueDate": "2024-04-15",
    "priority": "MEDIUM",
    "assignedTo": "john_doe"
  }'
```

#### Toggle task completion status

```bash
curl -X PATCH http://localhost:8080/api/tasks/1/toggle \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

#### Delete a task

```bash
curl -X DELETE http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

## Test Users

The application automatically creates the following default users at startup (if they don't already exist):

| Username | Password   | Role    |
|----------|------------|---------|
| admin    | admin123   | ADMIN   |
| john_doe | john1234   | USER    |
| jane_doe | jane5678   | USER    |
| manager  | manager2024| MANAGER |

This automatic initialization ensures that passwords are consistently hashed using BCrypt with the application's configured encoder.

## Development

The project uses:
- Spring Boot 3.2.3
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL
- Lombok
- Docker and Docker Compose
- Maven

## License

This project is licensed under the MIT License. 