# Phase 12 — Deployment

**Status: IN PROGRESS**

* 12.1 Deployment Architecture & Production Environment - COMPLETE
* 12.2 Neon PostgreSQL - COMPLETE
* 12.3 Production Configuration - COMPLETE
* 12.4 Production Docker Deployment - COMPLETE
* 12.5 Reverse Proxy - COMPLETE
* 12.6 HTTPS & DNS - PENDING
* 12.7 Frontend Production Deployment - PENDING
* 12.8 Health Checks & Observability - PENDING
* 12.9 CI/CD - PENDING
* 12.10 Production Database Migration Strategy - PENDING

## Current Context

**12.5 Reverse Proxy is complete.**

* Nginx (frontend container) is the only public entry point, published at `5173:80`.
* All API traffic goes through `/api/*`. Nginx strips the `/api` prefix and forwards to `backend:8080`.
* Everything else falls back to `index.html`, so React routes like `/favorites` and `/listings/:id` survive a refresh.
* Backend and PostgreSQL no longer publish host ports. They are reachable only on the internal Docker network.
* `VITE_API_URL=/api` (relative, build-time). The frontend image must be rebuilt when it changes.
* The backend service in `docker-compose.yml` now has a `build:` section, so images are always built from `./backend` and `./frontend`.
* `imageApi.js` no longer hardcodes `http://localhost:8080`. It uses `VITE_API_URL`.
* Verified: register, login, create listing, favorites, and refresh on deep links all work through Nginx. `curl http://localhost:5173/api/listings` returns 200.

**Rules for this setup:**

* Do not run `docker build -t` manually. Use `docker compose up -d --build`.
* After changing `VITE_API_URL`, run `docker compose build --no-cache frontend`.
* After recreating only the backend, run `docker compose restart frontend`.
* Use `docker compose down`, not `down -v`, unless wiping data is intended.

**Known items to handle later:**

* Image upload works, but large files still need checking against Spring's multipart limit (`spring.servlet.multipart.max-file-size`).
* The create listing button is not disabled while submitting, which allows duplicate listings.
* Generate a fresh `JWT_SECRET` and a strong DB password for real deployment.
* Production database migration strategy is deferred to 12.10.

**NEXT STEP:** 12.6 HTTPS & DNS