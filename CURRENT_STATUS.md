## Phase 11 — Docker & Developer Tooling
**Status: COMPLETE**

Completed:

- 11.1 Docker fundamentals & containerization strategy
- 11.2 PostgreSQL containerization
- 11.3 Spring Boot backend containerization
- 11.4 React frontend containerization
- 11.5 Docker Compose and multi-container networking
- 11.6 Persistent volumes and upload storage
- 11.7 Developer tooling
- 11.8 Phase 11 review

Developer tooling was completed and verified.

---

# Current Architecture

## Development

```text
React/Vite
    |
    v
Spring Boot
    |
    v
PostgreSQL
```

Docker Compose currently provides:

```text
frontend container
       |
backend container
       |
postgres container

Persistent volumes:
- postgres-data
- uploads-data
```

The backend currently runs on Java 17 and uses Gradle.

## Current Docker Architecture

### PostgreSQL

- PostgreSQL 17
- Docker container
- Persistent `postgres-data` volume

### Spring Boot

- Eclipse Temurin 17 JRE
- JAR-based runtime image
- Port 8080
- `uploads-data` mounted at `/app/uploads`

### React

- Node 22 Alpine build stage
- Nginx Alpine runtime stage
- Port 80 inside container
- Vite production build

---

# Phase 12 — Deployment

**Status: IN PROGRESS**

## Deployment Goal

Deploy AutoTrader as a realistic production-style system while maintaining a $0 target cost.

Target architecture:

```text
                    Internet
                       |
                       v
              Managed React Hosting
                  (Vercel)
                       |
                     HTTPS
                       |
                       v
                Production API
                       |
                       v
                OCI Linux VM
                 +---------+
                 |  Nginx |
                 +----+----+
                      |
                      v
               Spring Boot
                  Docker
                      |
                      v
              Managed PostgreSQL
                    Neon
```

Initial production responsibilities:

- React deployment handled by a managed static/frontend platform
- Spring Boot infrastructure operated on an OCI Always Free VM
- PostgreSQL operated as a managed Neon database
- Nginx used as the backend reverse proxy
- Docker used for backend deployment
- Persistent filesystem storage remains under review because current image storage is local to the backend environment
- GitHub Actions will eventually provide CI/CD
- HTTPS, DNS, health checks, logging, and production verification are part of this phase

---

# Phase 12 Objectives

## 12.1 Deployment Architecture & Production Environment
**Status: STARTING**

Understand and document:

- Production topology
- Managed vs self-managed responsibilities
- Network boundaries
- Environment separation
- Deployment flow
- Production configuration requirements

## 12.2 Neon PostgreSQL
**Status: NOT STARTED**

Planned:

- Create Neon PostgreSQL project/database
- Understand connection details
- Configure Spring Boot for external PostgreSQL
- Store credentials outside source code
- Verify database connectivity
- Verify application persistence

## 12.3 Production Configuration
**Status: NOT STARTED**

Planned:

- Separate development/test/production configuration
- Environment variables
- Production secrets
- JWT secret management
- Database configuration
- Upload directory configuration
- Disable development-only SQL logging

## 12.4 Production Docker Deployment
**Status: NOT STARTED**

Planned:

- Build production backend image
- Run backend on OCI
- Configure environment
- Configure restart behavior
- Configure persistent application storage where required
- Verify container health

## 12.5 Reverse Proxy
**Status: NOT STARTED**

Planned:

- Nginx
- Backend proxying
- Request forwarding
- HTTP headers
- Production access logging

## 12.6 HTTPS & DNS
**Status: NOT STARTED**

Planned:

- DNS configuration
- TLS/HTTPS
- Certificate management
- Secure API access

## 12.7 Frontend Production Deployment
**Status: NOT STARTED**

Planned:

- Production API URL configuration
- React production build
- Managed static hosting
- SPA routing configuration
- CORS verification

## 12.8 Health Checks & Observability
**Status: NOT STARTED**

Planned:

- Application health endpoint
- Container health
- Server health
- Application logs
- Reverse-proxy logs
- Basic operational diagnostics

## 12.9 CI/CD
**Status: NOT STARTED**

Planned:

```text
Git push
   |
   v
GitHub Actions
   |
   +--> backend tests
   +--> frontend tests
   +--> frontend build
   +--> backend build
   |
   v
Docker image / deployment
   |
   v
Production verification
```

## 12.10 Production Database Migration Strategy
**Status: NOT STARTED**

The current backend uses Hibernate schema auto-update during development.

A controlled migration mechanism such as Flyway will be evaluated before production schema changes are treated as operationally safe.

---

# Verified Production Readiness Gaps

The following observations are based on the configuration supplied for the current repository.

### 1. Hibernate schema management

Current:

```properties
spring.jpa.hibernate.ddl-auto=update
```

This is suitable for development convenience but should not be treated as the final production migration strategy.

### 2. SQL logging

Current:

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

These settings are useful while learning/debugging but should be reviewed and normally disabled for production.

### 3. Development database defaults

Current configuration falls back to:

```text
localhost
5432
autotrader
postgres
postgres
```

Those defaults are appropriate for local Docker/development use but should not be silently available as production credentials.

### 4. JWT secret

A JWT secret is currently present directly in `application.properties`.

The production deployment must use a separately managed secret supplied through the deployment environment.

The production secret should not be committed to source control.

### 5. Filesystem image storage

The application currently stores uploaded images on the filesystem.

This is compatible with the current Docker development architecture because `uploads-data` is persistent.

For the first deployment, we will deliberately evaluate whether persistent VM storage is sufficient or whether object storage should be introduced. Cloud object storage remains a Phase 13 candidate unless deployment requirements make it necessary earlier.

### 6. Frontend API configuration

No frontend `.env` files currently exist.

Before deploying the React application separately from the backend, the frontend API base URL mechanism must be verified and adapted for production.

### 7. Test database

Tests currently use H2 with `create-drop`.

This remains appropriate for the existing test suite while PostgreSQL/Testcontainers remains a planned Phase 13 improvement.

---

# Phase 12 Engineering Principle

The objective is not merely to make the application accessible over the internet.

The objective is to understand and practice:

- Production configuration
- Linux deployment
- Docker operations
- Managed database connectivity
- Reverse proxies
- HTTPS
- DNS
- Secrets
- Health checks
- Logging
- CI/CD
- Deployment verification
- Failure diagnosis

Infrastructure should be introduced when it teaches or solves a real problem rather than because it is fashionable.

---

