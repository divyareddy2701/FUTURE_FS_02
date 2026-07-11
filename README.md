# Mini CRM — Client Lead Management System

**Future Interns — Full Stack Web Development Internship — Task 2**
CIN ID: FIT/JUN26/FS18741

A CRM for managing client leads generated from website contact forms — track lead status, add follow-up notes, and search/filter through an admin dashboard.

## 🔗 Live Demo
_Add your deployed frontend + backend links here after deployment (see Deployment section)._

## Features

- Full CRUD on leads (name, email, phone, source, status)
- Lead pipeline: `NEW → CONTACTED → CONVERTED` (or `LOST`)
- Follow-up notes per lead, newest first
- Search by name/email and filter by status
- Secure admin login (HTTP Basic Auth, BCrypt-hashed password — no plaintext passwords anywhere)
- Proper HTTP status codes (`201 Created`, `404 Not Found`, `400` with field-level validation errors, etc.)
- Responsive table (collapses to cards on mobile)

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.3, Spring Data JPA, Spring Security |
| Database | MySQL |
| Frontend | HTML5, CSS3, vanilla JavaScript (fetch API) |
| Auth | HTTP Basic Auth + BCrypt password hashing |

## Folder Structure

```
FUTURE_FS_02/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/divya/minicrm/
│       ├── MiniCrmApplication.java
│       ├── config/          # Security, CORS, admin+sample data seeding
│       ├── controller/      # REST endpoints
│       ├── dto/             # Request/response objects with validation
│       ├── entity/          # JPA entities (Lead, LeadNote, AdminUser)
│       ├── enums/           # LeadStatus
│       ├── exception/       # Global error handling
│       └── repository/      # Spring Data JPA repositories
├── frontend/
│   ├── index.html
│   ├── css/style.css
│   └── js/app.js
├── README.md
├── LICENSE
└── .gitignore
```

## Database Design

**`leads`**
| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK) | auto-increment |
| name | VARCHAR(120) | not null |
| email | VARCHAR(150) | not null |
| phone | VARCHAR(20) | |
| source | VARCHAR(100) | not null |
| status | VARCHAR(20) | enum: NEW / CONTACTED / CONVERTED / LOST |
| created_at | DATETIME | set on insert |
| updated_at | DATETIME | set on every update |

**`lead_notes`**
| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK) | auto-increment |
| lead_id | BIGINT (FK → leads.id) | cascades on delete |
| content | VARCHAR(1000) | not null |
| created_at | DATETIME | set on insert |

**`admin_users`**
| Column | Type | Notes |
|---|---|---|
| id | BIGINT (PK) | auto-increment |
| username | VARCHAR(60) | unique |
| password | VARCHAR(100) | BCrypt hash |
| role | VARCHAR(20) | e.g. ROLE_ADMIN |

Relationship: one `Lead` has many `LeadNote`s (one-to-many, cascade delete — deleting a lead removes its notes).

## Installation & Running Locally

### Prerequisites
- Java 17+
- Maven
- MySQL running locally

### 1. Database
```sql
CREATE DATABASE mini_crm;
```
(Tables are auto-created by Hibernate on first run via `spring.jpa.hibernate.ddl-auto=update`.)

### 2. Configure credentials
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
app.admin.username=admin
app.admin.password=ChangeMe123!
```

### 3. Run the backend
```bash
cd backend
mvn spring-boot:run
```
Backend runs on `http://localhost:8082`. On first boot it seeds an admin account (from `application.properties`) and two sample leads so you can log in and test immediately.

### 4. Run the frontend
```bash
cd frontend
python3 -m http.server 5500
```
Visit `http://localhost:5500` and log in with the admin credentials from step 2.

> If you deploy the frontend separately from the backend, update `API_BASE` in `frontend/js/app.js` to point to your deployed backend URL.

## API Documentation

All endpoints below (except none — everything requires auth) need an `Authorization: Basic <base64(username:password)>` header.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/auth/me` | Validates credentials, returns the logged-in username |
| GET | `/api/leads` | List leads. Optional query params: `?status=NEW`, `?search=ananya` |
| POST | `/api/leads` | Create a lead |
| GET | `/api/leads/{id}` | Get one lead with its notes |
| PUT | `/api/leads/{id}` | Update a lead's details/status |
| DELETE | `/api/leads/{id}` | Delete a lead (and its notes) |
| GET | `/api/leads/{id}/notes` | List notes for a lead |
| POST | `/api/leads/{id}/notes` | Add a follow-up note |

**Create lead — request body:**
```json
{
  "name": "Ananya Rao",
  "email": "ananya.rao@example.com",
  "phone": "9876543210",
  "source": "Website Contact Form",
  "status": "NEW"
}
```

## Manual Testing Checklist

- [ ] Log in with correct credentials → dashboard loads
- [ ] Log in with wrong credentials → error message shown, no crash
- [ ] Create a lead with a missing name/invalid email → see field-level 400 error
- [ ] Create a valid lead → appears at top of table
- [ ] Edit a lead's status → pill color updates immediately
- [ ] Add a note → appears newest-first under that lead
- [ ] Delete a lead → confirmation prompt, then removed from table
- [ ] Search by partial name/email → table filters correctly
- [ ] Filter by status dropdown → only matching leads shown
- [ ] Resize browser to mobile width → table becomes stacked cards
- [ ] Refresh the page while logged in → session persists (no re-login needed) until you close the tab

## Common Errors & Fixes

| Error | Cause | Fix |
|---|---|---|
| `401 Unauthorized` on every request | Wrong admin credentials, or backend restarted with a different `app.admin.password` (won't re-seed if the user already exists) | Check `application.properties`, or manually update the password hash in the `admin_users` table |
| CORS error in browser console | Frontend origin not allowed | `SecurityConfig` currently allows all origins (`*`) for local development — tighten this before deploying |
| `Communications link failure` on startup | MySQL isn't running, or wrong port/credentials | Confirm MySQL is running on port 3306 and credentials in `application.properties` are correct |

## Deployment

- **Backend:** Render, Railway, or any host supporting a Spring Boot JAR + MySQL (e.g. PlanetScale, Railway MySQL, or Aiven for the database).
- **Frontend:** Netlify, Vercel, or GitHub Pages — just remember to update `API_BASE` in `app.js` to the deployed backend URL, and update the CORS allowed origin in `SecurityConfig.java` to match.

## Future Enhancements

- Role-based access (e.g. sales rep vs admin)
- Pagination for large lead lists
- Email notifications when a lead status changes
- Export leads to CSV

## Author

**Divya Sri Karrolla**
Final-year B.Tech, Electronics & Computer Engineering, Anurag University, Hyderabad

## License

MIT — see [LICENSE](LICENSE).
