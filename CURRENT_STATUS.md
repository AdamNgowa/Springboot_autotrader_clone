# Phase 12 — Deployment
**Status: IN PROGRESS** (last updated 2026-10-01)

Target architecture: React on Vercel → Nginx + Spring Boot (Docker) on OCI VM → Neon PostgreSQL

- 12.1 Deployment Architecture & Production Environment
  - 12.1.1 Production topology - COMPLETE
  - 12.1.2 Managed vs self-managed infrastructure - COMPLETE
  - 12.1.3 Network boundaries - COMPLETE
  - 12.1.4 Environment separation - COMPLETE
  - 12.1.5 Deployment flow - COMPLETE
    **Status: COMPLETE**

- 12.2 Neon PostgreSQL
  - 12.2.1 Neon project & database - COMPLETE
  - 12.2.2 Pooled PostgreSQL endpoint - COMPLETE
  - 12.2.3 Environment-driven database configuration - COMPLETE
  - 12.2.4 PostgreSQL JDBC driver (42.7.13) - COMPLETE
  - 12.2.5 TLS & channel binding - COMPLETE
  - 12.2.6 Spring Boot → Neon connectivity - COMPLETE
  - 12.2.7 Hibernate schema verification - COMPLETE
  - 12.2.8 Neon persistence verification - COMPLETE
    **Status: COMPLETE**

- 12.3 Production Configuration
  - 12.3.1 Spring profiles & environment separation - COMPLETE (`application-prod.properties`: show-sql off, `ddl-auto=validate`, `open-in-view=false`, logging levels)
  - 12.3.2 Environment variables - COMPLETE
  - 12.3.3 Production secrets - COMPLETE (policy defined; local JWT secret rotated; production JWT secret and strong Neon password are generated on the VM in 12.4.4)
  - 12.3.4 JWT secret management - COMPLETE (new local secret validated as 32 bytes of Base64; `JwtAuthenticationFilter` treats invalid/expired tokens as anonymous; verified: garbage Bearer token on a public endpoint returns 200)
  - 12.3.5 Production database configuration - COMPLETE
  - 12.3.6 Production upload configuration - COMPLETE (5MB/6MB limits in base `application.properties`; JSON 413 handler verified with a >5MB upload; nginx `client_max_body_size 10M`)
  - 12.3.7 Production logging configuration - COMPLETE (stdout only, INFO app, WARN Hibernate/Security)
  - 12.3.8 Production startup verification - COMPLETE (prod profile run in Docker against local Postgres: listings load anonymous and logged in; `LazyInitializationException` fixed with `@Transactional`). Prod profile + Neon together is verified on the VM in 12.4.7.
  - 12.3.9 Transaction boundaries & error visibility - COMPLETE (`@Transactional` on listing, conversation and message services; `/error` permitted; no `LazyInitializationException` in backend logs)
  - 12.3.10 Production-parity testing - COMPLETE (tests run on PostgreSQL 17 via Testcontainers, H2 removed; `open-in-view=false` in test properties; regression test for paginated `GET /listings`; `./gradlew test` passes)
    **Status: COMPLETE** (production DB password and production JWT secret are created on the VM in 12.4.4)

- 12.4 Production Docker Deployment
  - 12.4.1 Production backend image - COMPLETE
  - 12.4.2 OCI VM preparation - PENDING
  - 12.4.3 Docker runtime configuration - COMPLETE
  - 12.4.4 Environment & secrets - PENDING
  - 12.4.5 Persistent application storage - COMPLETE
  - 12.4.6 Container restart policy - PENDING
  - 12.4.7 Container verification - PENDING
    **Status: IN PROGRESS**

- 12.5 Reverse Proxy
  - 12.5.1 Nginx installation (on VM) - PENDING
  - 12.5.2 Reverse proxy configuration - COMPLETE (local container; adapt for VM)
  - 12.5.3 Request forwarding - COMPLETE (local container; adapt for VM)
  - 12.5.4 HTTP headers - PENDING (set in nginx; Spring `forward-headers-strategy` not set)
  - 12.5.5 Access logging - PENDING
    **Status: IN PROGRESS**

- 12.6 HTTPS & DNS
  - 12.6.1 Domain configuration - PENDING
  - 12.6.2 DNS records - PENDING
  - 12.6.3 TLS certificate - PENDING
  - 12.6.4 HTTPS configuration - PENDING
  - 12.6.5 Secure API verification - PENDING
    **Status: NOT STARTED**

- 12.7 Frontend Production Deployment
  - 12.7.1 Production API configuration - PENDING (local `/api` version done; redo for Vercel)
  - 12.7.2 React production build - COMPLETE
  - 12.7.3 Managed frontend hosting (Vercel) - PENDING
  - 12.7.4 SPA routing - PENDING (local nginx version done; needs `vercel.json`)
  - 12.7.5 CORS verification - PENDING
    **Status: IN PROGRESS**

- 12.8 Health Checks & Observability
  - 12.8.1 Application health endpoint - PENDING
  - 12.8.2 Container health - PENDING
  - 12.8.3 Server health - PENDING
  - 12.8.4 Application logging - PENDING
  - 12.8.5 Nginx logging - PENDING
  - 12.8.6 Basic diagnostics - PENDING
    **Status: NOT STARTED**

- 12.9 CI/CD
  - 12.9.1 GitHub Actions - PENDING
  - 12.9.2 Backend tests - PENDING
  - 12.9.3 Frontend tests - PENDING
  - 12.9.4 Frontend build - PENDING
  - 12.9.5 Backend build - PENDING
  - 12.9.6 Deployment automation - PENDING
  - 12.9.7 Production verification - PENDING
    **Status: NOT STARTED**

- 12.10 Production Database Migration Strategy
  - 12.10.1 Hibernate schema management review - PENDING
  - 12.10.2 Flyway evaluation - PENDING
  - 12.10.3 Initial migration - PENDING
  - 12.10.4 Production migration workflow - PENDING
  - 12.10.5 Migration verification - PENDING
    **Status: NOT STARTED**

---

## Done outside the original plan (local Docker only, not production)
- Nginx inside the frontend container proxies `/api/*` to `backend:8080` (prefix stripped) and serves the SPA
- Backend and Postgres no longer publish host ports; only the frontend publishes `5173:80`
- `VITE_API_URL=/api` and the backend `build:` section are set in `docker-compose.yml`
- Hardcoded `localhost:8080` removed from `imageApi.js`
- Image upload, favorites, listings and deep-link refresh verified through the local proxy
- Testcontainers (PostgreSQL 17) replaces H2 for all repository and integration tests via a shared `TestcontainersConfiguration`; tests keep `ddl-auto=create-drop` until Flyway owns the schema (12.10)
- `VehicleListingService`, `ConversationService` and `MessageService` annotated with `@Transactional` / `@Transactional(readOnly = true)` (found by running with `open-in-view=false`); `FavoriteService` and the image write methods already had them; `ImageService.uploadImage` is intentionally not transactional (file cleanup relies on the save committing inside its try/catch)

## Lessons from 12.3
- **`open-in-view` was hiding a missing transaction boundary.** Spring Boot defaults it to `true`, so lazy loading worked anywhere in a request. With the prod profile (`false`), `VehicleListingMapper` hit a detached `User` proxy: `LazyInitializationException ... no session`.
- **A server error can show up as a 401.** The exception forwards the request to `/error`, which Spring Security protects, so the client sees 401 and the real cause is only in the logs. Always read `docker compose logs backend` before theorising.
- **H2 tests with open-in-view on could not catch this class of bug.** Resolved in 12.3.10: tests now run on real PostgreSQL via Testcontainers with `open-in-view=false`. H2 is not Postgres (the `vehicle_year` column name exists only because `year` is reserved in H2; renaming it needs a migration, so it stays for now). Removing H2 from the classpath matters: otherwise Spring Boot silently falls back to an embedded H2 when a test class misses the container.
- **Secrets:** never paste `.env` or `docker compose config` output. The JWT secret was rotated after exposure. `.env` was confirmed never committed.
- **`bootRun` hang at 80% against Neon:** not investigated. Docker is the verified run path. Running from IntelliJ needs `JWT_SECRET`, `DB_USERNAME`, `DB_PASSWORD` set in the run configuration (a missing variable fails fast, so it does not explain a hang).

## 12.3 closing checklist (all done)
- [x] Click through every feature under the prod profile: listing details, create/edit/delete listing, favorites, messaging inbox and conversation, seller profile, image upload / set primary / reorder / delete. Check `docker compose logs backend | Select-String "LazyInitialization"` prints nothing
- [x] Add `@Transactional` to any other service that maps entities to DTOs or does multi-step writes (candidates: `FavoriteService`, `ConversationService`, `MessageService`, `ImageService`, `UserService`)
- [x] Add `/error` to the `permitAll` list in `SecurityConfig` so failures return 500 instead of a misleading 401
- [x] Add `spring.jpa.open-in-view=false` to `src/test/resources/application.properties` (tests pass)
- [x] Testcontainers with PostgreSQL replacing H2 (pulled forward from Phase 13)
- [x] Apply the three pending edits: move multipart limits to base `application.properties` (12.3.6), `MaxUploadSizeExceededException` 413 handler (12.3.6), try/catch in `JwtAuthenticationFilter` (12.3.4)

## To-do by sub-phase

### 12.3 Production Configuration
- [x] Add `application-prod.properties` (`show-sql=false`, `ddl-auto=validate`)
- [x] Confirm `.env` is in `.gitignore` (ignored, never tracked, never in history)
- [x] Update `.env.example` with `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_SSL_MODE`, `DB_CHANNEL_BINDING`, `JWT_EXPIRATION`, `APP_UPLOAD_DIRECTORY`
- [x] Generate a new local `JWT_SECRET` (production one is generated on the VM in 12.4.4)
- [ ] Use a strong DB password for production (reset the Neon role password in 12.4.4; store only in the VM `.env`)
- [x] Set `spring.servlet.multipart.max-file-size` and `max-request-size` in base `application.properties`
- [x] Set production logging levels
- [x] Set `spring.jpa.open-in-view=false`
- [x] `bootRun` hang: closed without investigation (Docker is the supported path)
- [x] Run the backend with the prod profile and confirm startup plus API calls (local Postgres container)

### 12.4 Production Docker Deployment
- [ ] Create the OCI account and VM
- [ ] Configure SSH, firewall/security list (ports 22, 80, 443), and `ufw`
- [ ] Install Docker and Docker Compose on the VM
- [ ] Create a production compose file: no local Postgres service, Neon values via env, backend only, `SPRING_PROFILES_ACTIVE=prod`
- [ ] Put production secrets in a `.env` on the VM (permissions 600), not in Git; generate JWT secret on the VM with `openssl rand -base64 32`
- [ ] Reset the Neon role password and store it only in the VM `.env`
- [ ] Add `restart: unless-stopped` to each service
- [ ] Add Docker log rotation (`json-file` `max-size` / `max-file`) so logs cannot fill the disk
- [ ] Add PostgreSQL/Neon readiness handling on startup
- [ ] Deploy the backend container on the VM and verify it connects to Neon (prod profile + Neon together)
- [ ] Verify uploads persist across container restart and recreate
- [ ] Change `dev.ps1` `up` to `docker compose up -d --build`

### 12.5 Reverse Proxy
- [ ] Decide what to do with the frontend container's nginx (dev only, or remove)
- [ ] Install Nginx on the VM
- [ ] Write an API-only nginx config for the VM (`proxy_pass` to `localhost:8080`, drop the SPA block)
- [ ] Set `client_max_body_size` for image uploads (must stay larger than Spring's 6MB request limit)
- [ ] Keep the forwarded headers (`Host`, `X-Real-IP`, `X-Forwarded-For`, `X-Forwarded-Proto`)
- [ ] Add `server.forward-headers-strategy=framework` in Spring
- [ ] Configure access and error logging
- [ ] Verify `/uploads/*` is served through Nginx

### 12.6 HTTPS & DNS
- [ ] Decide on a domain (or a free subdomain)
- [ ] Create DNS records (API → OCI VM public IP)
- [ ] Issue a TLS certificate (Let's Encrypt / certbot)
- [ ] Configure HTTPS and HTTP→HTTPS redirect in Nginx
- [ ] Set up certificate auto-renewal
- [ ] Verify the API over HTTPS with `curl`

### 12.7 Frontend Production Deployment
- [ ] Decide: Vercel rewrite of `/api` to the VM (same-origin, `VITE_API_URL=/api`) or absolute API URL with CORS
- [ ] Set production `VITE_API_URL` accordingly
- [ ] Make the CORS allowed origin an env variable (currently hardcoded `http://localhost:5173` in `SecurityConfig`)
- [ ] Allow the Vercel production domain in CORS (if not using the rewrite)
- [ ] Create the Vercel project and connect the GitHub repo
- [ ] Set `VITE_API_URL` in Vercel environment variables
- [ ] Add `vercel.json` (SPA rewrite for deep links, plus `/api` rewrite if chosen)
- [ ] Verify register, login, listings, image upload, favorites, messaging from the Vercel URL
- [ ] Verify images load from the API domain (`getImageUrl`)
- [ ] Verify the frontend shows the 413 message when a file over 5MB is uploaded

### 12.8 Health Checks & Observability
- [ ] Add Spring Boot Actuator and expose `/actuator/health` only
- [ ] Permit the health endpoint in `SecurityConfig`
- [ ] Add a Docker `HEALTHCHECK` for the backend
- [ ] Server health checks (`df`, `free`, `top`, `docker stats`)
- [ ] Application logging format and levels
- [ ] Nginx access/error log review
- [ ] Write a short diagnostics checklist

### 12.9 CI/CD
- [ ] GitHub Actions workflow file (Docker is available on ubuntu runners, so Testcontainers tests can run in CI)
- [ ] Backend tests job
- [ ] Frontend tests job
- [ ] Frontend build job
- [ ] Backend build job
- [ ] Deployment automation to OCI (SSH or registry pull)
- [ ] Store secrets in GitHub Actions secrets
- [ ] Post-deploy health check

### 12.10 Production Database Migration Strategy
- [ ] Review `ddl-auto=update` risks for production
- [ ] Evaluate Flyway
- [ ] Create the initial migration from the current schema
- [ ] Switch `ddl-auto` to `validate` or `none`
- [ ] Define the production migration workflow
- [ ] Verify migrations against Neon
- [ ] Confirm the Neon backup/restore options and test a restore

### Housekeeping
- [ ] Update the charter's Phase 12 section to point to this plan
- [ ] Regenerate `FOLDER_STRUCTURE.md` (stale: missing `JwtAuthenticationEntryPoint`, `application-prod.properties`, and the real `src/main/resources` location)
- [ ] Delete the duplicate test listings in the local database
- [ ] Update the stale comment in `VehicleListingIntegrationTest.setUp` (it still says the shared context uses an in-memory H2 database)

### Logged for Phase 13 (mirrored in the charter's Phase 13 "Gaps identified during Phase 12")
- [ ] `GET /listings/me` and `/users/me` are matched by `permitAll` wildcards; declare them `authenticated()` first
- [ ] Disable submit buttons while a request is in flight (duplicate listings)
- [ ] Frontend: clear the session and redirect to login on 401 / expired token
- [ ] Non-root containers, container resource limits, image size optimization
- [ ] N+1 query review on paginated listings (fetch join / `@EntityGraph` / DTO projection; beware in-memory pagination with collection fetch joins)
- [ ] Application-wide `@Transactional` policy review; connection pool tuning for Neon
- [ ] Structured (JSON) logging; `INFO`/`WARN` logging for auth failures (never log tokens or passwords)
- [ ] Fail fast at startup if `JWT_SECRET` is not valid Base64 or is shorter than 32 bytes; JWT secret rotation procedure
- [ ] Secrets manager evaluation; secret scanning (git hook and CI)
- [ ] Backup strategy for the uploads volume
- [ ] Tests: no class-level `@Transactional` hiding lazy-loading bugs; invalid Bearer token on public endpoint; 413 on oversized upload; run Testcontainers tests in CI
- [ ] Versioned image tags / registry instead of `latest`; nginx upstream re-resolution after backend recreation
- [ ] Revisit folder structure generator; document IDE run configuration; investigate `bootRun` hang
- Testcontainers with PostgreSQL: moved into Phase 12.3.10

**NEXT STEP:** 12.4.2 (OCI VM preparation), then 12.4.4 (production secrets on the VM) and the production compose file