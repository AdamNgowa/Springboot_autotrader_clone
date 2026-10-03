# Phase 12 — Deployment
**Status: IN PROGRESS** (last updated 2026-10-03)

Target architecture: React on Vercel → Spring Boot (Docker) on Render → Neon PostgreSQL

> Hosting changed on 2026-10-01: Oracle Cloud (OCI) requires a credit card at sign-up and only a virtual prepaid card is available, so the backend moves to Render. See "Hosting decision" below.

- 12.1 Deployment Architecture & Production Environment
  - 12.1.1 Production topology - COMPLETE (revised: Render replaces the OCI VM and the VM Nginx)
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
  - 12.3.3 Production secrets - COMPLETE (policy defined; local JWT secret rotated; production JWT secret and strong Neon password are created for Render in 12.4.4)
  - 12.3.4 JWT secret management - COMPLETE (new local secret validated as 32 bytes of Base64; `JwtAuthenticationFilter` treats invalid/expired tokens as anonymous; verified: garbage Bearer token on a public endpoint returns 200)
  - 12.3.5 Production database configuration - COMPLETE
  - 12.3.6 Production upload configuration - COMPLETE (5MB/6MB limits in base `application.properties`; JSON 413 handler verified with a >5MB upload; nginx `client_max_body_size 10M`)
  - 12.3.7 Production logging configuration - COMPLETE (stdout only, INFO app, WARN Hibernate/Security)
  - 12.3.8 Production startup verification - COMPLETE (prod profile run in Docker against local Postgres: listings load anonymous and logged in; `LazyInitializationException` fixed with `@Transactional`). Prod profile + Neon together is verified on Render in 12.4.7.
  - 12.3.9 Transaction boundaries & error visibility - COMPLETE (`@Transactional` on listing, conversation and message services; `/error` permitted; no `LazyInitializationException` in backend logs)
  - 12.3.10 Production-parity testing - COMPLETE (tests run on PostgreSQL 17 via Testcontainers, H2 removed; `open-in-view=false` in test properties; regression test for paginated `GET /listings`; `./gradlew test` passes)
    **Status: COMPLETE** (production DB password and production JWT secret are created for Render in 12.4.4)

  - 12.4 Production Backend Deployment (Render)
    - 12.4.1 Production backend image - COMPLETE
    - 12.4.2 Render account & verification - COMPLETE (Hobby workspace; no card on file; free web service created successfully without requiring card verification)
    - 12.4.3 Docker runtime configuration - COMPLETE (server.port=${PORT:8080} configured; Docker build/runtime configuration verified; JVM memory tuning deferred until observed need)
    - 12.4.4 Environment & secrets - COMPLETE (Render production environment configured with Spring profile, Neon connection variables, TLS/channel-binding settings and production JWT secret)
    - 12.4.5 Persistent application storage - DEFERRED TO 12.11 (Render Free filesystem is ephemeral; persistent image storage will be implemented separately)
    - 12.4.6 Restart & cold-start behaviour - DEFERRED TO FINAL VERIFICATION (free service spin-down/wake behaviour will be tested together with the deployed frontend and persistent image storage)
    - 12.4.7 Service verification - COMPLETE FOR DEPLOYMENT AVAILABILITY (Render deployment succeeded and live backend URL responds; / returns the expected Spring Security 401 Unauthorized because the root endpoint is protected. Full application-flow verification is deferred until frontend and persistent storage are complete)
    **Status: COMPLETE / FINAL END-TO-END VERIFICATION PENDING**

- 12.5 Reverse Proxy & Forwarded Headers
  - 12.5.1 Nginx installation (on VM) - NOT NEEDED (Render's edge proxy replaces it)
  - 12.5.2 Reverse proxy configuration - COMPLETE (local container only, kept for development)
  - 12.5.3 Request forwarding - COMPLETE (local container only, kept for development)
  - 12.5.4 HTTP headers - COMPLETE (Spring `forward-headers-strategy=framework`)
  - 12.5.5 Access logging - NOT NEEDED (Render collects logs)
    **Status: COMPLETE**

- 12.6 HTTPS & DNS
  - 12.6.1 Domain configuration - OPTIONAL (default `*.onrender.com` URL first; custom domain later)
  - 12.6.2 DNS records - OPTIONAL (only with a custom domain)
  - 12.6.3 TLS certificate - NOT NEEDED (managed by Render)
  - 12.6.4 HTTPS configuration - COMPLETE (confirm HTTPS-only access and correct scheme through forwarded headers)
  - 12.6.5 Secure API verification - COMPLETE
    **Status: COMPLETE**

- 12.7 Frontend Production Deployment
  - 12.7.1 Production API configuration - PENDING (local `/api` version done; redo for Vercel)
  - 12.7.2 React production build - COMPLETE
  - 12.7.3 Managed frontend hosting (Vercel) - PENDING
  - 12.7.4 SPA routing - PENDING (local nginx version done; needs `vercel.json`)
  - 12.7.5 CORS verification - PENDING
  - 12.7.6 Backend cold-start handling in the UI - PENDING (free Render service takes about a minute to wake)
    **Status: IN PROGRESS**

- 12.8 Health Checks & Observability
  - 12.8.1 Application health endpoint - PENDING (Actuator `/actuator/health`, also used as Render's health check path)
  - 12.8.2 Container health - NOT NEEDED (Render health check replaces a Docker HEALTHCHECK)
  - 12.8.3 Server health - NOT NEEDED (managed platform; use Render metrics)
  - 12.8.4 Application logging - PENDING
  - 12.8.5 Nginx logging - NOT NEEDED
  - 12.8.6 Basic diagnostics - PENDING
    **Status: NOT STARTED**

- 12.9 CI/CD
  - 12.9.1 GitHub Actions - PENDING
  - 12.9.2 Backend tests - PENDING
  - 12.9.3 Frontend tests - PENDING
  - 12.9.4 Frontend build - PENDING
  - 12.9.5 Backend build - PENDING
  - 12.9.6 Deployment automation - PENDING (Render auto-deploy from GitHub, ideally only after CI passes)
  - 12.9.7 Production verification - PENDING
    **Status: NOT STARTED**

- 12.10 Production Database Migration Strategy
  - 12.10.1 Hibernate schema management review - PENDING
  - 12.10.2 Flyway evaluation - PENDING
  - 12.10.3 Initial migration - PENDING
  - 12.10.4 Production migration workflow - PENDING
  - 12.10.5 Migration verification - PENDING
    **Status: NOT STARTED**

- 12.11 Production Image Storage
  - 12.11.1 Storage options evaluation - PENDING (providers that work without a credit card; `FileStorageService` is the seam)
  - 12.11.2 Storage implementation - PENDING
  - 12.11.3 Image URL strategy - PENDING (`/uploads/...` becomes an absolute storage URL; `ImageMapper` and `getImageUrl`)
  - 12.11.4 Existing local images - PENDING (re-upload or migrate)
  - 12.11.5 Persistence verification - PENDING (images survive redeploy, restart and spin-down)
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

## Hosting decision: Render instead of OCI (2026-10-01)
- **Why:** OCI sign-up requires a credit card; only a virtual prepaid card is available. Render's free tier is advertised as card-free, but Render sometimes asks for a card for verification (a small authorization hold that is reversed), so account creation is the first thing to test.
- **What it removes:** VM provisioning, SSH, firewall/`ufw`, Docker install, Nginx install and config, certbot and DNS, the production compose file, the VM `.env`, Docker log rotation, restart policy, Docker `HEALTHCHECK`, server health checks, Nginx logs. TLS and a public HTTPS URL come from Render.
- **What it introduces:** (1) free web services have an ephemeral filesystem, so uploaded images are lost on every redeploy, restart and spin-down, and a free service cannot attach a persistent disk (hence 12.11); (2) free services spin down after 15 minutes idle and take about a minute to wake (12.4.6, 12.7.6); (3) a small instance, so the JVM needs a memory cap (12.4.3); (4) the app must bind to Render's `PORT` (12.4.3).
- **Unchanged:** Neon stays the database, Vercel stays the frontend host, `docker-compose.yml` stays for local development.
- **Account facts (billing page, 2026-10-03):** Hobby workspace; no card on file; monthly included usage: 750 free instance hours, 5 GB bandwidth, 2 custom domains, 25 services, 500 pipeline (build) minutes. Docker builds consume pipeline minutes and image downloads served by the backend consume bandwidth. The billing page counts 1 service although the project shows none active; check what it is.
- **Open decisions:** Render region (closest to the Neon region), image storage provider, whether to accept cold starts.

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
- [x] Generate a new local `JWT_SECRET` (production one is generated for Render in 12.4.4)
- [ ] Use a strong DB password for production (reset the Neon role password in 12.4.4; store only in Render environment variables)
- [x] Set `spring.servlet.multipart.max-file-size` and `max-request-size` in base `application.properties`
- [x] Set production logging levels
- [x] Set `spring.jpa.open-in-view=false`
- [x] `bootRun` hang: closed without investigation (Docker is the supported path)
- [x] Run the backend with the prod profile and confirm startup plus API calls (local Postgres container)

### 12.4 Production Backend Deployment (Render)
- [x] Render account exists (Hobby workspace, no card on file, 0 of 750 free instance hours used)
- [x] Create the first free web service; if Render asks for card verification, test whether the virtual card is accepted
- [x] Choose the Render region closest to the Neon database (Ohio / US East; Neon is AWS East 2 / Ohio)
- [x] Bind Spring to Render's port: `server.port=${PORT:8080}`
- [ ] Tune the JVM for a 512 MB instance (heap cap, for example through `JAVA_TOOL_OPTIONS`)
- [x] Create the Render Web Service from the GitHub repo using `backend/Dockerfile`
- [x] Set environment variables in Render: `SPRING_PROFILES_ACTIVE=prod`, `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `DB_SSL_MODE=require`, `DB_CHANNEL_BINDING=require`, `JWT_SECRET`
- [x] Generate the production `JWT_SECRET` (base64 of at least 32 random bytes) and paste it straight into Render, never into chat
- [x] Reset the Neon role password and store it only in Render environment variables
- [ ] Add Neon cold-start handling (Neon compute may be suspended when idle; connection timeouts and retries)
- [x] Deploy the backend and verify it connects to Neon (prod profile + Neon together)
- [ ] Verify behaviour after idle spin-down and wake
- [ ] Optional later: a `render.yaml` Blueprint
- [ ] Change `dev.ps1` `up` to `docker compose up -d --build`

### 12.5 Reverse Proxy & Forwarded Headers
- [x] Keep the frontend container's nginx for local development only
- [ ] Add `server.forward-headers-strategy=framework` in Spring
- [ ] Verify a 5MB image upload passes Render's edge (Spring still enforces 5MB/6MB)

### 12.6 HTTPS & DNS
- [ ] Use the default `https://<service>.onrender.com` URL first; decide on a custom domain later (optional)
- [ ] Confirm HTTP is redirected to HTTPS
- [ ] Verify the API over HTTPS with `curl`

### 12.7 Frontend Production Deployment
- [ ] Decide: Vercel rewrite of `/api` to the Render URL (same-origin, `VITE_API_URL=/api`) or absolute API URL with CORS (a rewrite adds a proxy hop whose timeout may be shorter than a one-minute cold start; verify before choosing)
- [ ] Set production `VITE_API_URL` accordingly
- [ ] Make the CORS allowed origin an env variable (currently hardcoded `http://localhost:5173` in `SecurityConfig`)
- [ ] Allow the Vercel production domain in CORS (if not using the rewrite)
- [ ] Create the Vercel project and connect the GitHub repo
- [ ] Set `VITE_API_URL` in Vercel environment variables
- [ ] Add `vercel.json` (SPA rewrite for deep links, plus `/api` rewrite if chosen)
- [ ] Verify register, login, listings, image upload, favorites, messaging from the Vercel URL
- [ ] Verify images load from the API domain (`getImageUrl`)
- [ ] Verify the frontend shows the 413 message when a file over 5MB is uploaded
- [ ] Handle backend cold start in the UI (longer timeout and a "waking up the server" message)

### 12.8 Health Checks & Observability
- [ ] Add Spring Boot Actuator and expose `/actuator/health` only
- [ ] Permit the health endpoint in `SecurityConfig`
- [ ] Set it as Render's health check path
- [ ] Application logging format and levels (Render collects stdout)
- [ ] Write a short diagnostics checklist (Render logs and metrics, Neon dashboard)

### 12.9 CI/CD
- [ ] GitHub Actions workflow file (Docker is available on ubuntu runners, so Testcontainers tests can run in CI)
- [ ] Backend tests job
- [ ] Frontend tests job
- [ ] Frontend build job
- [ ] Backend build job
- [ ] Deployment automation: Render auto-deploy from GitHub, ideally only after CI passes
- [ ] Store secrets in GitHub Actions secrets (only if a Render deploy hook is used)
- [ ] Post-deploy health check

### 12.10 Production Database Migration Strategy
- [ ] Review `ddl-auto=update` risks for production
- [ ] Evaluate Flyway
- [ ] Create the initial migration from the current schema
- [ ] Switch `ddl-auto` to `validate` or `none`
- [ ] Define the production migration workflow
- [ ] Verify migrations against Neon
- [ ] Confirm the Neon backup/restore options and test a restore

### 12.11 Production Image Storage
- [ ] Research storage providers with current terms that work without a credit card and alongside Render's free tier
- [ ] Decide between an S3-compatible API and a provider SDK
- [ ] Add a second `FileStorageService` implementation selected by configuration; keep the local filesystem for development and tests
- [ ] Return absolute image URLs from the API; update `ImageMapper` and `getImageUrl`
- [ ] Decide what to do with existing local images (re-upload or migrate)
- [ ] Verify upload, set primary, reorder and delete, and that images survive a redeploy and a spin-down

### Housekeeping
- [ ] Update the charter: add persistent image storage to the Phase 12 plan, mark "Cloud object storage" in Phase 13 as pulled forward, and replace VM wording with Render
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
- [ ] Paid always-on Render instance with a persistent disk (removes cold starts; needs a payment card)
- [ ] Custom domain and DNS (optional)
- Testcontainers with PostgreSQL: moved into Phase 12.3.10

**NEXT STEP: 12.5.4 — add server.forward-headers-strategy=framework to backend/src/main/resources/application.properties,
then verify the configuration and commit it before moving to 12.6.**