# Comprehensive Running Guide: In-Memory Spring Boot & React To-Do Application

Welcome to the comprehensive running and developer operations guide for the **Spring Boot + React To-Do List Application**. This runbook contains end-to-end instructions for launching, testing, debugging, and troubleshooting both backend and frontend services.

---

## Table of Contents

1. [Prerequisites & System Verification](#1-prerequisites--system-verification)
2. [Running the Spring Boot Backend (Terminal)](#2-running-the-spring-boot-backend-terminal)
3. [Running the Spring Boot Backend (VS Code)](#3-running-the-spring-boot-backend-vs-code)
4. [Running the React Frontend](#4-running-the-react-frontend)
5. [Testing the REST API via `curl`](#5-testing-the-rest-api-via-curl)
6. [Troubleshooting & Common Pitfalls](#6-troubleshooting--common-pitfalls)

---

## 1. Prerequisites & System Verification

Before launching the services, verify that the required runtimes and tools are installed on your workstation.

### Required Software

| Component | Minimum Version | Recommended Version | Verification Command |
| :--- | :--- | :--- | :--- |
| **Java JDK** | 21+ | OpenJDK 21 LTS | `java -version` |
| **Node.js** | 18+ | Node.js 20 LTS | `node -v` |
| **npm** | 9+ | npm 10+ | `npm -v` |
| **Apache Maven** | 3.8+ (Optional; wrapper included) | Maven 3.9+ | `mvn -version` or `./mvnw -version` |

### Step-by-Step System Check

Open your terminal and run the following checks:

```bash
# 1. Verify Java version (must be Java 21 or higher)
java -version
# Expected output snippet:
# openjdk version "21.0.x" ...

# 2. Verify Node.js version
node -v
# Expected output snippet:
# v18.x.x, v20.x.x, or v22.x.x

# 3. Verify npm version
npm -v
# Expected output snippet:
# 9.x.x or 10.x.x

# 4. Verify Maven (or use the repository's bundled Maven wrapper)
cd backend && ./mvnw -v && cd ..
```

> **Installation Notes:**
> - **Java 21:** If Java is not installed, install OpenJDK 21 via your package manager (e.g. `sudo apt install openjdk-21-jdk` on Ubuntu/Debian, `brew install openjdk@21` on macOS, or via [SDKMAN!](https://sdkman.io/): `sdk install java 21.0.2-tem`).
> - **Node.js:** If Node is missing, install via [nvm](https://github.com/nvm-sh/nvm) (`nvm install 20`) or package manager (`sudo apt install nodejs npm`).

---

## 2. Running the Spring Boot Backend (Terminal)

The backend is built with Spring Boot 3.3.x and Java 21, packaged with an embedded Tomcat web server.

### Option A: Using the Maven Wrapper (Recommended)

The Maven wrapper (`mvnw` on Linux/macOS, `mvnw.cmd` on Windows) ensures the exact compatible Maven version is used without requiring a global Maven installation.

1. Navigate to the `backend/` directory:
   ```bash
   cd backend
   ```

2. Start the Spring Boot application:
   ```bash
   # On Linux / macOS:
   ./mvnw spring-boot:run

   # On Windows (Command Prompt / PowerShell):
   mvnw.cmd spring-boot:run
   ```

### Option B: Using Global Maven

If you have Apache Maven installed globally on your PATH:
```bash
cd backend
mvn spring-boot:run
```

### Expected Terminal Output

When the application boots successfully, you will observe the Spring Boot ASCII banner followed by startup logs:

```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.3.3)

2026-09-21T03:40:11.329+05:30  INFO 90 --- [todo-list-backend] [           main] c.e.t.TodoListApplication                : Starting TodoListApplication using Java 21.0.12...
2026-09-21T03:40:11.944+05:30  INFO 90 --- [todo-list-backend] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2026-09-21T03:40:11.965+05:30  INFO 90 --- [todo-list-backend] [           main] c.e.t.TodoListApplication                : Started TodoListApplication in 1.45 seconds (process running for 1.89)
```

### Verifying the Backend is Live

Open a separate terminal window and ping the REST endpoint:
```bash
curl -i http://localhost:8080/api/todos
```
You should receive an `HTTP/1.1 200 OK` response with a JSON array containing the 3 initial sample to-do items.

### Running Backend Unit & Integration Tests

To run the automated test suite (Controller tests with MockMvc and Service unit tests):
```bash
cd backend
./mvnw clean test
# or: mvn clean test
```
All 13 tests should pass with `BUILD SUCCESS`.

### Stopping the Backend

In the terminal where Spring Boot is running, press:
```text
Ctrl + C
```

---

## 3. Running the Spring Boot Backend (VS Code)

Visual Studio Code provides a first-class developer experience for Java and Spring Boot.

### Recommended VS Code Extensions

For the best experience, ensure the following extensions are installed:
1. **Extension Pack for Java** (`vscjava.vscode-java-pack`) — provides Java Language Support, Debugger, Maven project explorer, and test runner.
2. **Spring Boot Extension Pack** (`vmware.vscode-spring-boot`) — provides Spring Boot Tools, Spring Initializr, and the Spring Boot Dashboard.

### Method 1: CodeLens "Run" / "Debug" (Direct in Editor)

1. Open the project root directory in VS Code:
   ```bash
   code /home/vedansh/Developer/Vedansh/spring_to_do_list
   ```
2. In the VS Code File Explorer (left sidebar), navigate to:
   `backend/src/main/java/com/example/todolist/TodoListApplication.java`
3. Open `TodoListApplication.java`.
4. Locate the `main` method:
   ```java
   public static void main(String[] args) {
       SpringApplication.run(TodoListApplication.class, args);
   }
   ```
5. Directly above the `public static void main` line, VS Code renders two clickable CodeLens actions: **Run** and **Debug**.
6. Click **Run** to start the application, or **Debug** to launch with breakpoints enabled.
7. The integrated "Debug Console" or "Terminal" panel will display the Spring Boot startup logs.

### Method 2: Using the Spring Boot Dashboard Extension

1. In the VS Code Activity Bar (far left sidebar), click on the **Spring Boot Leaf Icon** (Spring Boot Dashboard).
2. Under the **Apps** panel, you will see `todo-list-backend` listed.
3. Hover over `todo-list-backend`:
   - Click the **Play Icon** (`▶`) to start.
   - Click the **Bug Icon** to start in debug mode.
   - Click the **Stop Icon** (`■`) to terminate the application.
4. Active beans and live request mapping endpoints can also be inspected directly from this panel.

### Method 3: Using the Integrated VS Code Terminal

1. Open a new integrated terminal in VS Code: `Ctrl + \`` (or menu: `Terminal` -> `New Terminal`).
2. Run:
   ```bash
   cd backend && ./mvnw spring-boot:run
   ```

### Debugging with Breakpoints in VS Code

1. Open `backend/src/main/java/com/example/todolist/controller/TodoController.java` or `backend/src/main/java/com/example/todolist/service/TodoService.java`.
2. Click in the left margin (gutter) next to any line number (e.g. inside `createTodo` or `toggleTodoStatus`) to set a red breakpoint dot.
3. Start the application using **Debug** mode (Method 1 or Method 2).
4. Trigger the endpoint via `curl` or from the React frontend in your browser.
5. VS Code will pause execution at the breakpoint, allowing you to:
   - Inspect local variables, request DTOs, and internal state in the **Variables** pane.
   - Step Over (`F10`), Step Into (`F11`), or Continue (`F5`).
   - Evaluate arbitrary Java expressions in the **Debug Console**.

---

## 4. Running the React Frontend

The frontend is built with **React 18** and **Vite**, offering lightning-fast Hot Module Replacement (HMR).

### Step 1: Navigate to the Frontend Directory

```bash
cd frontend
```

### Step 2: Install Node Dependencies

If this is your first time running the project, install the dependencies:
```bash
npm install
```
This installs `react`, `react-dom`, `vite`, and `@vitejs/plugin-react`.

### Step 3: Start the Vite Development Server

```bash
npm run dev
```

### Expected Output

```text
  VITE v5.4.2  ready in 180 ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
  ➜  press h + enter to show help
```

### Step 4: Access the Application

1. Open your web browser and navigate to:
   **`http://localhost:5173`**
2. You will see the To-Do List application loaded with the 3 default tasks fetched from the backend.
3. Try creating a new to-do, editing title/description, clicking the checkbox to toggle completion, filtering items (*All*, *Active*, *Completed*), and deleting an item.

### Production Build & Preview

To test the production build of the frontend:
```bash
cd frontend
npm run build    # Compiles assets into dist/
npm run preview  # Serves the production build locally
```

---

## 5. Testing the REST API via `curl`

You can test every backend endpoint directly from the command line using `curl`.

> **Tip:** The `-i` flag prints the HTTP response headers and status codes, allowing you to inspect status codes like `200 OK`, `201 Created`, `204 No Content`, and `400/404` errors.

### Endpoint Quick Reference

| Method | URL | Description | Expected Status |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/todos` | List all todos (newest first) | `200 OK` |
| `GET` | `/api/todos/{id}` | Retrieve a single todo by ID | `200 OK` or `404 Not Found` |
| `POST` | `/api/todos` | Create a new todo | `201 Created` or `400 Bad Request` |
| `PUT` | `/api/todos/{id}` | Replace title, description, completion | `200 OK` or `404 Not Found` |
| `PATCH` | `/api/todos/{id}/toggle` | Toggle `completed` status | `200 OK` or `404 Not Found` |
| `DELETE`| `/api/todos/{id}` | Delete todo by ID | `204 No Content` or `404 Not Found` |

---

### 1. List All Todos (`GET /api/todos`)

```bash
curl -i -X GET http://localhost:8080/api/todos
```

**Expected Response (`200 OK`):**
```http
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 3,
    "title": "Connect React Frontend",
    "description": "Test full-stack CRUD functionality through REST API",
    "completed": false,
    "createdAt": "2026-09-21T03:40:00"
  },
  {
    "id": 2,
    "title": "Explore Dependency Injection",
    "description": "See how Spring wires beans together without manual 'new' keywords",
    "completed": false,
    "createdAt": "2026-09-21T02:40:00"
  },
  {
    "id": 1,
    "title": "Learn Spring Boot Architecture",
    "description": "Understand Controller, Service, and Repository layers",
    "completed": false,
    "createdAt": "2026-09-21T01:40:00"
  }
]
```

---

### 2. Get Single Todo by ID (`GET /api/todos/{id}`)

#### Success Case:
```bash
curl -i -X GET http://localhost:8080/api/todos/1
```

**Expected Response (`200 OK`):**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "title": "Learn Spring Boot Architecture",
  "description": "Understand Controller, Service, and Repository layers",
  "completed": false,
  "createdAt": "2026-09-21T01:40:00"
}
```

#### Not Found Case:
```bash
curl -i -X GET http://localhost:8080/api/todos/999
```

**Expected Response (`404 Not Found`):**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "Todo not found with id: 999",
  "details": []
}
```

---

### 3. Create a New Todo (`POST /api/todos`)

#### Success Case:
```bash
curl -i -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Deploy to Staging",
    "description": "Package JAR and deploy to containerized environment"
  }'
```

**Expected Response (`201 Created`):**
```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 4,
  "title": "Deploy to Staging",
  "description": "Package JAR and deploy to containerized environment",
  "completed": false,
  "createdAt": "2026-09-21T03:45:12.123"
}
```

#### Validation Error Case (Blank Title):
```bash
curl -i -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "",
    "description": "This should fail because title is blank"
  }'
```

**Expected Response (`400 Bad Request`):**
```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for request body",
  "details": [
    "title: Title is required"
  ]
}
```

---

### 4. Update an Existing Todo (`PUT /api/todos/{id}`)

#### Success Case:
```bash
curl -i -X PUT http://localhost:8080/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Mastered Spring Boot Architecture",
    "description": "Controller, Service, Repository layers fully understood and tested",
    "completed": true
  }'
```

**Expected Response (`200 OK`):**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "title": "Mastered Spring Boot Architecture",
  "description": "Controller, Service, Repository layers fully understood and tested",
  "completed": true,
  "createdAt": "2026-09-21T01:40:00"
}
```

#### Not Found Case:
```bash
curl -i -X PUT http://localhost:8080/api/todos/999 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Missing Item",
    "description": "Does not exist",
    "completed": false
  }'
```

**Expected Response (`404 Not Found`):**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "Todo not found with id: 999",
  "details": []
}
```

---

### 5. Toggle Todo Completion Status (`PATCH /api/todos/{id}/toggle`)

```bash
curl -i -X PATCH http://localhost:8080/api/todos/2/toggle
```

**Expected Response (`200 OK`):**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 2,
  "title": "Explore Dependency Injection",
  "description": "See how Spring wires beans together without manual 'new' keywords",
  "completed": true,
  "createdAt": "2026-09-21T02:40:00"
}
```

---

### 6. Delete a Todo (`DELETE /api/todos/{id}`)

#### Success Case:
```bash
curl -i -X DELETE http://localhost:8080/api/todos/3
```

**Expected Response (`204 No Content`):**
```http
HTTP/1.1 204 No Content
```
*(No body is returned for 204 responses)*

#### Not Found Case (Deleting Already-Deleted or Non-Existent ID):
```bash
curl -i -X DELETE http://localhost:8080/api/todos/999
```

**Expected Response (`404 Not Found`):**
```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "Todo not found with id: 999",
  "details": []
}
```

---

## 6. Troubleshooting & Common Pitfalls

### Issue 1: Port Already In Use (`Port 8080` or `Port 5173`)

#### Symptoms:
- **Backend fails to start** with error:
  `Web server failed to start. Port 8080 was already in use. Identify and stop the process that's listening on port 8080 or configure this application to listen on another port.`
- **Frontend fails or starts on port 5174** (`Port 5173 is in use, trying another one...`).

#### Diagnosis & Resolution:

##### On Linux / macOS:
1. Identify the process ID occupying the port:
   ```bash
   # For backend (port 8080):
   lsof -i :8080
   # or:
   netstat -tulnp | grep 8080

   # For frontend (port 5173):
   lsof -i :5173
   ```
2. Kill the conflicting process:
   ```bash
   kill -9 <PID>

   # Quick one-liner to free port 8080:
   fuser -k 8080/tcp
   ```

##### On Windows:
1. Identify the process ID:
   ```cmd
   netstat -ano | findstr :8080
   ```
2. Terminate the process:
   ```cmd
   taskkill /F /PID <PID>
   ```

##### Alternative: Changing Ports
If you cannot stop the conflicting process:
- **Change backend port:** Edit `backend/src/main/resources/application.properties` and set `server.port=8081`. Then update `API_BASE_URL` in `frontend/src/services/api.js` to `http://localhost:8081/api/todos`.
- **Change frontend port:** In `frontend/package.json`, modify the dev script: `"dev": "vite --port 5174"`, and ensure `CorsConfig.java` in the backend allows `http://localhost:5174`.

---

### Issue 2: CORS Errors (`Cross-Origin Request Blocked`)

#### Symptoms:
The frontend UI shows an error badge: `Failed to fetch` or browser DevTools Console displays:
```text
Access to fetch at 'http://localhost:8080/api/todos' from origin 'http://localhost:5173'
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

#### Explanation:
Cross-Origin Resource Sharing (CORS) is a browser security measure that blocks JavaScript running on one origin (`http://localhost:5173`) from reading HTTP responses from a different origin (`http://localhost:8080`), unless the receiving server sends explicit CORS headers (`Access-Control-Allow-Origin`).

#### Common Causes & Fixes:
1. **Backend Is Not Running (Most Common):**
   When the Spring Boot backend is shut down or crashed, the browser cannot connect to `localhost:8080`. In modern browsers, connection refusals during a cross-origin fetch are reported generically as CORS errors!
   - *Fix:* Ensure the Spring Boot backend is actively running (`mvn spring-boot:run`) and listening on port 8080 before interacting with the frontend.
2. **Accessing Frontend on IP Instead of Hostname:**
   If you open `http://127.0.0.1:5173` instead of `http://localhost:5173`, some browsers treat this as a different origin.
   - *Fix:* `CorsConfig.java` is already configured to permit both `http://localhost:5173` and `http://127.0.0.1:5173`. Make sure you use one of these two origins.
3. **Reviewing CORS Configuration:**
   CORS settings are defined in `backend/src/main/java/com/example/todolist/config/CorsConfig.java`:
   ```java
   registry.addMapping("/api/**")
           .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
           .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
           .allowedHeaders("*")
           .allowCredentials(true);
   ```

---

### Issue 3: In-Memory Data Reset Behavior

#### Symptoms:
Any newly created or edited to-do items disappear after you stop and restart the backend server, reverting back to the 3 default items.

#### Explanation:
- This application uses `InMemoryTodoRepository`, which maintains records in a `ConcurrentHashMap` stored inside JVM heap memory.
- It does **not** connect to a persistent external database (e.g. MySQL, PostgreSQL, or disk file).
- When the JVM process terminates (`Ctrl + C` or restarts triggered by `spring-boot-devtools`), all in-memory heap structures are discarded.
- Upon booting again, the constructor of `InMemoryTodoRepository` executes and initializes the 3 default tasks.

#### Why In-Memory?
- **Zero Configuration:** Eliminates the need to install, configure, or migrate an external database engine.
- **Pure Architectural Focus:** Allows you to master Spring Boot's Controller-Service-Repository layers and Dependency Injection without database driver friction.
- **Enterprise Extension:** In a production application, you would replace `InMemoryTodoRepository` with a Spring Data JPA interface extending `JpaRepository<Todo, Long>` connected to PostgreSQL or H2, without changing the `TodoService` or `TodoController` contracts!
