# Task Manager Final Enterprise Pack

This project contains:
- A Spring Boot backend (`task-service`) for task CRUD and search.
- A `docker-compose.yml` to run both services together.

## Tech Stack

- Backend: Spring Boot 3.2.2, Spring Web, Spring Data JPA, H2, Spring Cache
- Resilience: Resilience4j (Circuit Breaker + Rate Limiter)
- API Docs: springdoc OpenAPI + Swagger UI
- Frontend: AngularJS 1.8.2
- Container orchestration: Docker Compose

## Project Structure

- `task-service/` - Java backend service
- `frontend-ui/` - Static AngularJS UI served by Nginx
- `docker-compose.yml` - Runs backend on `8080` and UI on `4200`

## Run the Project

### Option 1: Docker Compose

```bash
docker compose up --build
```

Services:
- Backend API: `http://localhost:8080`
- Frontend UI: `http://localhost:4200`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### Option 2: Run Backend Locally

From `task-service/`:

```bash
mvn spring-boot:run
```

Then open `frontend-ui/index.html` (or serve it via any static server) and ensure backend is available at `http://localhost:8080`.

## Backend Features

- Task CRUD endpoints
- Title-based search (`Optional<Task>`)
- In-memory list tracking for created tasks (`getAllTasks` and `getByTitle` use this list)
- Persistent storage via JPA/H2 (`create`, `get`, `update`, `delete`, `list` use repository)
- Pagination support for list endpoint
- Caching on paginated list endpoint (`@Cacheable("tasks")`)
- Circuit breaker + fallback on list endpoint
- Rate limiting on list endpoint
- Async event publishing on task creation
- Audit logging on task creation
- Global exception handling with structured error payloads
- Swagger/OpenAPI integration

## Backend Configuration

File: `task-service/src/main/resources/application.yml`

- H2 datasource: `jdbc:h2:mem:taskdb`
- JPA DDL mode: `update`
- Swagger UI path: `/swagger-ui.html`

## API Endpoints

Base path: `/tasks`

1. `POST /tasks`
- Creates a task.
- Body (example):
```json
{
  "title": "Write README",
  "description": "Document project features"
}
```
- Returns: created `Task`.

2. `GET /tasks/{id}`
- Fetches task by id.
- Returns: `Task`.
- If missing: `404` with message `Task not found`.

3. `GET /tasks/getByTitle?title={title}`
- Returns first case-insensitive title match from in-memory list.
- Returns: `Optional<Task>` (JSON object when present, empty when absent).

4. `GET /tasks`
- Paginated DB-backed task listing.
- Accepts standard Spring `Pageable` query params (`page`, `size`, `sort`).
- Returns: `Page<Task>`.
- Protected with cache, circuit breaker, and rate limiter.

5. `PUT /tasks/{id}`
- Updates title and description for an existing task.
- Body: `Task` (relevant fields: `title`, `description`).
- Returns: updated `Task`.

6. `DELETE /tasks/{id}`
- Deletes an existing task.
- Returns: empty response.

7. `GET /tasks/getAllTasks`
- Returns in-memory task list maintained by service.
- Returns: `List<Task>`.

## Domain Models

### `Task`
File: `task-service/src/main/java/com/example/taskservice/domain/model/Task.java`

Fields:
- `Long id` (auto-generated)
- `String title`
- `String description`
- `LocalDateTime createdAt` (defaults to `LocalDateTime.now()`)

Methods:
- `getId()`, `setId(Long id)`
- `getTitle()`, `setTitle(String title)`
- `getDescription()`, `setDescription(String description)`
- `getCreatedAt()`, `setCreatedAt(LocalDateTime createdAt)`

### `User`
File: `task-service/src/main/java/com/example/taskservice/domain/model/User.java`

Fields:
- `transactionId`, `username`, `category`, `timeStamp`, `amount`

Methods:
- Constructor with all fields
- `toString()`
- Standard getters/setters for all fields

Note: `User` is not currently used by API/controller paths.

## Backend Class and Method Inventory

### `TaskServiceApplication`
File: `task-service/src/main/java/com/example/taskservice/TaskServiceApplication.java`

Methods:
- `main(String[] args)` - Starts Spring Boot application.

### `TaskController`
File: `task-service/src/main/java/com/example/taskservice/api/controller/TaskController.java`

Methods:
- `create(Task t)` -> `POST /tasks`
- `get(Long id)` -> `GET /tasks/{id}`
- `getByTitle(String title)` -> `GET /tasks/getByTitle`
- `list(Pageable pageable)` -> `GET /tasks`
- `update(Long id, Task t)` -> `PUT /tasks/{id}`
- `delete(Long id)` -> `DELETE /tasks/{id}`
- `getAllTask()` -> `GET /tasks/getAllTasks`

### `TaskService`
File: `task-service/src/main/java/com/example/taskservice/application/service/TaskService.java`

Methods:
- `create(Task t)` - Saves task, logs audit entry, stores in in-memory list, publishes event.
- `getTasks()` - Returns in-memory list.
- `get(Long id)` - Finds by id or throws `ResponseStatusException(404)`.
- `getByTitle(String title)` - Returns first case-insensitive in-memory match as `Optional<Task>`.
- `update(Long id, Task input)` - Updates existing task title/description in DB.
- `delete(Long id)` - Deletes existing task in DB.
- `list(Pageable pageable)` - DB pagination with cache + resilience annotations.
- `fallback(Pageable pageable, Throwable t)` - Circuit-breaker fallback, returns empty page.

### `TaskRepository`
File: `task-service/src/main/java/com/example/taskservice/domain/repository/TaskRepository.java`

Methods:
- Inherits all `JpaRepository<Task, Long>` methods (`save`, `findById`, `findAll`, `delete`, etc.).

### `AuditLogger`
File: `task-service/src/main/java/com/example/taskservice/audit/AuditLogger.java`

Methods:
- `log(String msg)` - Writes `AUDIT:` prefixed log via SLF4J.

### `TaskCreatedEvent`
File: `task-service/src/main/java/com/example/taskservice/event/TaskCreatedEvent.java`

Methods:
- Constructor `TaskCreatedEvent(Long id)`
- `getId()`

### `TaskListener`
File: `task-service/src/main/java/com/example/taskservice/listener/TaskListener.java`

Methods:
- `on(TaskCreatedEvent e)` - Async event listener, prints created task id.

### `AppConfig`
File: `task-service/src/main/java/com/example/taskservice/config/AppConfig.java`

Purpose:
- Enables async processing (`@EnableAsync`)
- Enables Spring caching (`@EnableCaching`)

### `GlobalExceptionHandler`
File: `task-service/src/main/java/com/example/taskservice/exception/GlobalExceptionHandler.java`

Methods:
- `handleValidation(MethodArgumentNotValidException, HttpServletRequest)` -> 400 + field details
- `handleBind(BindException, HttpServletRequest)` -> 400 + field details
- `handleBadRequest(Exception, HttpServletRequest)` -> 400
- `handleNotFound(NoHandlerFoundException, HttpServletRequest)` -> 404
- `handleResponseStatus(ResponseStatusException, HttpServletRequest)` -> mapped status/reason
- `handle(Exception, HttpServletRequest)` -> 500
- `error(HttpStatus, String, String, Map<String, String>)` -> builds `ErrorResponse`

Nested class:
- `ErrorResponse` with getters:
  - `getTimestamp()`
  - `getStatus()`
  - `getMessage()`
  - `getPath()`
  - `getDetails()`

## Docker Compose Setup

File: `docker-compose.yml`

Services:
- `task-service`
  - Builds from `./task-service`
  - Exposes `8080:8080`
- `ui`
  - Uses `nginx` image
  - Mounts `./frontend-ui` into nginx html root
  - Exposes `4200:80`

## Notes and Current Behavior

- `getByTitle` and `getAllTasks` rely on an in-memory list (`tasks`) populated only when `create` is called.
- `list`, `get`, `update`, and `delete` rely on database state via `TaskRepository`.
- Because these are separate sources, `getAllTasks/getByTitle` may not reflect all DB records (for example after restart or external DB writes).

