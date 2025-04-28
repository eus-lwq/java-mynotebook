# Personal Notebook Management System

## Overview
A feature-rich notebook management system built with Spring Boot, MySQL, and JavaFX. The system allows users to create and manage notebooks, pages, tables, and graphs with a secure and intuitive interface.

## Technology Stack
- Backend: Spring Boot
- Database: MySQL
- Frontend: JavaFX
- Dependencies: Spring Data JPA, Spring Security, Flexmark (Markdown), Lombok

## API Endpoints

### Authentication
```
POST /api/auth/register
- Register a new user
- Body: { username: string, password: string }
- Returns: { id: long, username: string, token: string }

POST /api/auth/login
- Login existing user
- Body: { username: string, password: string }
- Returns: { token: string }
```

### Notebooks
```
GET /api/notebooks
- Get all notebooks for current user
- Returns: [{ id: long, title: string, createdAt: date }]

POST /api/notebooks
- Create new notebook
- Body: { title: string }
- Returns: { id: long, title: string, createdAt: date }

GET /api/notebooks/{id}
- Get specific notebook
- Returns: { id: long, title: string, createdAt: date, pages: [...] }

PUT /api/notebooks/{id}
- Update notebook
- Body: { title: string }
- Returns: { id: long, title: string, createdAt: date }

DELETE /api/notebooks/{id}
- Delete notebook
```

### Pages
```
GET /api/notebooks/{notebookId}/pages
- Get all pages in notebook
- Returns: [{ id: long, title: string, content: string, createdAt: date }]

POST /api/notebooks/{notebookId}/pages
- Create new page
- Body: { title: string, content: string }
- Returns: { id: long, title: string, content: string, createdAt: date }

GET /api/pages/{id}
- Get specific page
- Returns: { id: long, title: string, content: string, createdAt: date, tables: [...], graphs: [...] }

PUT /api/pages/{id}
- Update page
- Body: { title: string, content: string }
- Returns: { id: long, title: string, content: string, createdAt: date }

DELETE /api/pages/{id}
- Delete page
```

### Tables
```
POST /api/pages/{pageId}/tables
- Create new table
- Body: { data: string[][] }
- Returns: { id: long, data: string[][], createdAt: date }

GET /api/tables/{id}
- Get specific table
- Returns: { id: long, data: string[][], createdAt: date }

PUT /api/tables/{id}
- Update table
- Body: { data: string[][] }
- Returns: { id: long, data: string[][], createdAt: date }

DELETE /api/tables/{id}
- Delete table
```

### Graphs
```
POST /api/pages/{pageId}/graphs
- Create new graph
- Body: { type: string, tableId: long, config: object }
- Returns: { id: long, type: string, tableId: long, config: object }

GET /api/graphs/{id}
- Get specific graph
- Returns: { id: long, type: string, tableId: long, config: object }

PUT /api/graphs/{id}
- Update graph
- Body: { type: string, tableId: long, config: object }
- Returns: { id: long, type: string, tableId: long, config: object }

DELETE /api/graphs/{id}
- Delete graph
```

## Database Schema

### Users
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Notebooks
```sql
CREATE TABLE notebooks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Pages
```sql
CREATE TABLE pages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notebook_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (notebook_id) REFERENCES notebooks(id)
);
```

### Tables
```sql
CREATE TABLE data_tables (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    page_id BIGINT NOT NULL,
    data JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (page_id) REFERENCES pages(id)
);
```

### Graphs
```sql
CREATE TABLE graphs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    page_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    config JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (page_id) REFERENCES pages(id),
    FOREIGN KEY (table_id) REFERENCES data_tables(id)
);
```
Functionalities
Backend: Java Spring Boot
- Backend developed using Spring Boot framework.
- Expose RESTful APIs to manage authentication, notebooks, notes, tables, graphs, etc.
- Uses multithreading to handle concurrent user requests for fast, responsive experience.
- Employ Service Layer and Repository Pattern for clean architecture and testability.
Database: MySQL
- Use MySQL for persistent storage.
- Entity relationships:
  - Users → Notebooks → Pages
  - Pages → Markdown content, Tables, Graph data
- Tables include:
  - users (id, username, password_hash, salt, role)
  - notebooks (id, user_id, title, created_at)
  - pages (id, notebook_id, title, content, created_at)
  - tables (id, page_id, table_data)
  - graphs (id, page_id, type, source_table_id, config)
Frontend: JavaFX / Swing
- Intuitive GUI connected to backend via REST API.
- Login/Registration interface.
- Main interface includes:
  - Sidebar: List of notebooks and pages.
  - Editor pane: Markdown editor + preview.
  - Tool panels: Table manager, Graph generator, Export options.
Common (All Users)
- Login Page:
  - Secure login via hashed password + salt + pepper.
  - Backend validates credentials, issues session token.
- Registration Page:
  - Register new account with basic validations.
  - Optional role assignment (admin/user).
User Features
Notebook & Page Management
- Create/delete/edit notebooks and pages.
- Pages contain Markdown-formatted text.
- Rich text editor with Markdown shortcuts and preview panel.
Table Management
- Insert/edit/delete tables in any page.
- Table operations:
  - Add/delete rows and columns
  - Edit individual cells
  - Import/export table as CSV
Graph Generation
- Choose table data to create:
  - Line Graphs
  - Bar Charts
  - Pie Charts
- Graphs embedded within notebook pages.
- Use Java libraries like JFreeChart for rendering.
Export Options
- Export page or full notebook as:
  - Markdown .md file
  - PDF (with rendered graphs and tables)
Search and Tags
- Search notes by keyword, tags, or creation date.
- Tag pages for better organization.
Advanced Features (optional, if time permits)
- Dark/Light mode UI themes
- Password-protected notebooks
- Auto-save with version history
- Spell checker
- Notebook sharing with other users (read-only or collaborative mode)
- Real-time sync using WebSockets or periodic polling
Multithreading and Performance
- Spring Boot backend supports multi-threaded request handling.
- Asynchronous processing for:
  - Auto-save
  - Export operations
  - Large table or graph rendering
- Thread pool management to scale with user load.
Security
- Passwords stored with salted and peppered hashes.
- Role-based access control (user/admin).
- All API endpoints secured with token-based authentication (JWT or session token).
- Input validation and sanitation to prevent SQL injection / XSS.

