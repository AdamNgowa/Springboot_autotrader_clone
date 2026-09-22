# Phase 11 — Docker & Developer Tooling

├── 11.1 Docker Fundamentals & Containerization Strategy — COMPLETE
├── 11.2 Dockerizing PostgreSQL — COMPLETE
├── 11.3 Dockerizing Spring Boot Backend — COMPLETE
├── 11.4 Dockerizing React Frontend — COMPLETE
├── 11.5 Docker Compose & Multi-Container Networking — PENDING
├── 11.6 Persistent Volumes & Upload Storage — PENDING
├── 11.7 Developer Tooling — PENDING
└── 11.8 Phase 11 Review — PENDING

---

this is where we left of :
Excellent. **Phase 11.4 — Dockerizing the React frontend is now complete.**

You have now verified all the important pieces:

* React production build is generated inside a Node build container.
* Nginx serves the resulting static files.
* Host port `5173` maps to Nginx's container port `80`.
* The frontend communicates with the backend through `localhost:8080`.
* React Router works with direct URL navigation and browser refreshes because of the Nginx SPA fallback.
* We did not need to modify your existing Compose configuration.

### 11.4 Reflection

**Files Changed**

* `frontend/Dockerfile`

    * Added the multi-stage production build.
    * Added the Nginx configuration.
* `frontend/.dockerignore`

    * Prevents unnecessary development files from entering the Docker build context.
* `frontend/nginx.conf`

    * Added SPA fallback using `try_files`.

**Why They Changed**

* Node is required to **build** React, but not to **run** the production application.
* Nginx is responsible for serving the compiled frontend.
* Nginx needs the SPA fallback because React Router owns application routes such as `/favorites` and `/my-listings`.

**Concepts Learned**

* Multi-stage Docker builds.
* Build-time vs runtime dependencies.
* Docker host ports vs container ports.
* Static asset serving.
* SPA routing and server-side fallback.
* Why development navigation can work while production deep links fail.
* Why a container can serve a React application without Node running in the final image.

**Production Considerations**

* Nginx can later act as a reverse proxy for the backend.
* HTTPS/TLS will eventually belong at the deployment/reverse-proxy layer.
* Frontend environment configuration needs to be handled at build time with Vite.
* We have not yet solved persistent image uploads across container recreation; that's specifically what **11.6** will address.
* We have intentionally left your current Compose setup unchanged.

**Interview Notes**

A common question:

> **Why use a multi-stage Docker build for a React application?**

A strong answer:

> React needs Node.js and npm during the build process, but the resulting application is just static HTML, CSS, JavaScript, and assets. A multi-stage build uses Node only in the build stage and copies the generated `dist` directory into a lightweight Nginx image for runtime. This reduces the final image size and attack surface and keeps build tooling out of production.

Another good one:

> **Why does React Router require an Nginx fallback?**

Because browser navigation to `/favorites` causes the web server to receive `/favorites`. Since that isn't a physical file, Nginx would normally return `404`. `try_files ... /index.html` sends the SPA entry point instead, allowing React Router to interpret the URL.

**Suggested Git commit:**

```text
feat(frontend): containerize React app with Nginx SPA routing
```

---

# Phase 11.5 — Docker Compose & Multi-Container Networking

Now we're moving from **"each application can run in a container"** to:

> **"The entire application can be operated as a multi-container system."**

Your current architecture is already very close:

```text
                    Docker / Host
                         │
          ┌──────────────┼──────────────┐
          │              │              │
          ▼              ▼              ▼
     PostgreSQL       Backend        Frontend
     :5432            :8080          :80
          ▲              ▲              │
          │              │              │
          └──────────────┘              │
                                         │
                              Browser → :5173
```

But there's an important distinction we'll learn next:

```text
Browser → localhost:8080
```

is **not** the same networking mechanism as:

```text
Backend → postgres:5432
```

The first goes through the host's published port.

The second uses **Docker's internal network and DNS**.

That's the key concept for 11.5.

### Before we change anything

Since you've explicitly decided **not to change the current Compose file**, we won't.

We'll first inspect and understand your existing Compose setup as-is, particularly:

1. Which containers Compose manages.
2. Which ports are published to the host.
3. Which ports are only internal.
4. How Docker Compose creates the network.
5. How the backend resolves `postgres`.
6. Why the browser must continue using `localhost`.
7. Why a browser cannot use Docker's internal hostname `postgres` or `backend`.

Then we'll make the **smallest justified change** to bring the frontend into the Compose-managed application, rather than redesigning your setup.

The next thing I want to establish is the actual current Compose file and its location, because we previously encountered the `backend/docker-compose.yml` vs project-root discrepancy. Send me the current contents of the Compose file you're actually running, and we'll start 11.5 from there.

