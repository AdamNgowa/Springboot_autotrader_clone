# Phase 12 — Deployment
**Status: IN PROGRESS**

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
    - 12.3.1 Spring profiles & environment separation - PENDING
    - 12.3.2 Environment variables - COMPLETE
    - 12.3.3 Production secrets - PENDING
    - 12.3.4 JWT secret management - PENDING
    - 12.3.5 Production database configuration - COMPLETE
    - 12.3.6 Production upload configuration - PENDING
    - 12.3.7 Production logging configuration - PENDING
    - 12.3.8 Production startup verification - PENDING
      **Status: IN PROGRESS**

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

## To-do by sub-phase

### 12.3 Production Configuration
- [ ] Add `application-prod.properties` (`show-sql=false`, `ddl-auto` review)
- [ ] Confirm `.env` is in `.gitignore`
- [ ] Update `.env.example` with `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_SSL_MODE`, `DB_CHANNEL_BINDING`, `JWT_EXPIRATION`, `APP_UPLOAD_DIRECTORY`
- [ ] Generate a new `JWT_SECRET` (the old ones were pasted in chat and printed in logs)
- [ ] Use a strong DB password for production
- [ ] Set `spring.servlet.multipart.max-file-size` and `max-request-size`
- [ ] Set production logging levels
- [ ] Set `spring.jpa.open-in-view=false`
- [ ] Investigate `bootRun` hanging at 80% against Neon
- [ ] Run the backend against Neon and confirm a full startup plus one API call

### 12.4 Production Docker Deployment
- [ ] Create the OCI account and VM
- [ ] Configure SSH, firewall/security list (ports 22, 80, 443), and `ufw`
- [ ] Install Docker and Docker Compose on the VM
- [ ] Create a production compose file: no local Postgres service, Neon values via env, backend only
- [ ] Put production secrets in a `.env` on the VM, not in Git
- [ ] Add `restart: unless-stopped` to each service
- [ ] Add PostgreSQL/Neon readiness handling on startup
- [ ] Deploy the backend container on the VM and verify it connects to Neon
- [ ] Verify uploads persist across container restart and recreate
- [ ] Change `dev.ps1` `up` to `docker compose up -d --build`

### 12.5 Reverse Proxy
- [ ] Decide what to do with the frontend container's nginx (dev only, or remove)
- [ ] Install Nginx on the VM
- [ ] Write an API-only nginx config for the VM (`proxy_pass` to `localhost:8080`, drop the SPA block)
- [ ] Set `client_max_body_size` for image uploads
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

### 12.8 Health Checks & Observability
- [ ] Add Spring Boot Actuator and expose `/actuator/health` only
- [ ] Permit the health endpoint in `SecurityConfig`
- [ ] Add a Docker `HEALTHCHECK` for the backend
- [ ] Server health checks (`df`, `free`, `top`, `docker stats`)
- [ ] Application logging format and levels
- [ ] Nginx access/error log review
- [ ] Write a short diagnostics checklist

### 12.9 CI/CD
- [ ] GitHub Actions workflow file
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
- [ ] Regenerate `FOLDER_STRUCTURE.md`

### Logged for Phase 13
- [ ] `GET /listings/me` and `/users/me` are matched by `permitAll` wildcards; declare them `authenticated()` first
- [ ] Disable the create-listing button while submitting (duplicate listings)
- [ ] Non-root containers, resource limits, image size optimization
- [ ] Testcontainers with PostgreSQL instead of H2

**NEXT STEP:** 12.3 (profiles, secrets, logging, multipart limits), then 12.4.2 (OCI VM)