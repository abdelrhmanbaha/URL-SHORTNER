# URL Shortener — REST API

A production-ready URL shortening service built with Java and Spring Boot.
Send a long URL, get a short one back. Links expire automatically after 10 minutes.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 25 + Spring Boot 4 | Core backend framework |
| PostgreSQL 18 | Permanent link storage |
| Valkey (Redis-compatible) | TTL-based link expiry |
| Docker + Docker Compose | Infrastructure management |
| Flyway | Database migrations |
| Lombok | Boilerplate reduction |

## API Endpoints

| Method | Endpoint | Description | Response |
|---|---|---|---|
| POST | `/shorten` | Shorten a URL | 201 Created |
| GET | `/links/{code}` | Get full URL from short code | 200 OK |
| GET | `/{code}` | Redirect browser to full URL | 302 Found |

## How It Works

1. User sends a full URL to `POST /shorten`
2. SHA-256 hash is generated from the URL — first 8 characters become the short code
3. Same URL always produces the same code (idempotency)
4. Link is saved to PostgreSQL with a 10-minute expiry timestamp
5. Short code is stored in Valkey with 10-minute TTL — auto-deleted after expiry
6. User visits the short URL — server checks Valkey, redirects if valid, returns 410 Gone if expired

## Architecture
### Layer responsibilities
- **Controller** — receives HTTP requests, returns responses
- **Service** — all business logic (hashing, idempotency, expiry checks)
- **Repository** — database access via Spring Data JPA
- **Entity** — maps to PostgreSQL links table

## Running Locally

Prerequisites: Java 25, Docker Desktop

```bash
# 1. Start infrastructure
docker-compose up -d

# 2. Run the application
./mvnw spring-boot:run
```

App runs at `http://localhost:8080`

## Testing the API

Shorten a URL:
```bash
curl -X POST http://localhost:8080/shorten \
  -H "Content-Type: application/json" \
  -d '"https://www.example.com"'
```

Get full URL:
```bash
curl http://localhost:8080/links/4d2f17ab
```

Redirect: open `http://localhost:8080/4d2f17ab` in your browser.

## Error Handling

| Status | Meaning |
|---|---|
| 404 Not Found | Short code does not exist |
| 410 Gone | Link has expired |
| 500 Internal Server Error | Unexpected error — logged and returned as JSON |

## Database Schema

```sql
CREATE TABLE links (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    full_url    TEXT        NOT NULL,
    short_code  VARCHAR(8)  NOT NULL UNIQUE,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    expires_at  TIMESTAMP   NOT NULL
);
```   