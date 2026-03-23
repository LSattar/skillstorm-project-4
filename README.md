# skillstorm-project-4

Animal shelter management platform for SkillStorm project 4.

## Tech Stack

- Backend: Java 17, Spring Boot, Spring Security, Spring Data JPA
- Frontend: Angular
- Database: MySQL 8
- Storage: AWS S3 (animal photo metadata + URL support)

## Repository Layout

- `animal-shelter/`: Spring Boot backend API
- `frontend/`: Angular frontend
- `database/schema.sql`: database schema and seed roles

## Local Setup

### Prerequisites

- Java 17
- Node.js + npm
- MySQL 8
- (Optional) Docker Desktop for Testcontainers integration tests

### Environment Variables (Backend)

The backend uses defaults for local development but supports these overrides:

- `DB_HOST` (default `localhost`)
- `DB_PORT` (default `3306`)
- `DB_NAME` (default `animal-shelter`)
- `DB_USERNAME` (default `root`)
- `DB_PASSWORD` (default `password123`)
- `JWT_SECRET` (default dev-only value)
- `JWT_ISSUER` (default `animal-shelter-api`)
- `JWT_EXPIRATION_SECONDS` (default `86400`)
- `FRONTEND_URL` (default `http://localhost:4200`)
- `AWS_REGION` (default `us-east-1`)
- `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` (optional OAuth)

### Database Initialization

Run:

```sql
source database/schema.sql;
```

This creates all required tables and seeds `ADOPTER`, `STAFF`, and `FOSTER` roles.

## Running Locally

### Backend

From `animal-shelter/`:

```bash
./mvnw spring-boot:run
```

API base URL: `http://localhost:8080`

### Frontend

From `frontend/`:

```bash
npm install
npm run start
```

Frontend URL: `http://localhost:4200`

## Running Tests

From `animal-shelter/`:

```bash
./mvnw test
```

Notes:
- Service/controller tests run without external AWS calls.
- Integration tests use Testcontainers and will be skipped when Docker is unavailable.

## API Endpoint Overview

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/auth/link-google`

### Public Animals

- `GET /api/animals`
- `GET /api/animals/{id}`
- `GET /api/animals/{id}/photos`

### Adopter

- `GET /api/adopter/profile`
- `PUT /api/adopter/profile`
- `GET /api/adopter/questionnaire`
- `PUT /api/adopter/questionnaire`
- `POST /api/adopter/applications`
- `GET /api/adopter/applications`
- `GET /api/adopter/applications/{id}`

### Staff

- `GET/POST/PUT/DELETE /api/staff/animals...`
- `POST /api/staff/animals/{id}/move/shelter`
- `POST /api/staff/animals/{id}/move/foster`
- `POST /api/staff/animals/{id}/status`
- `GET /api/staff/applications`
- `GET /api/staff/applications/{id}`
- `POST /api/staff/applications/{id}/approve`
- `POST /api/staff/applications/{id}/deny`
- `POST /api/staff/adoptions`
- `GET /api/staff/adoptions`
- `GET /api/staff/adoptions/{id}`
- `GET/POST/PUT/PATCH /api/staff/employees...`
- `GET/POST/PUT/DELETE /api/staff/shelters...`
- `GET /api/animals/{id}/events` (staff-only)

## Entity Overview

Core tables/entities:

- `users`, `roles`, `user_roles`
- `adopter_profiles`, `adopter_questionnaires`
- `shelters`
- `animals`, `animal_photos`, `animal_events`
- `adoption_applications`
- `adoptions`

These support intake, placement, application review, and final adoption workflows with lifecycle audit events.
