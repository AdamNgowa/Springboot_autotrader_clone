# CURRENT_STATUS.md

## Current Phase

### Phase 8 — React Frontend: Authentication Architecture (In Progress)

Phase 8 continued beyond the initial API layer and entered the authentication architecture stage.

Completed during this chat:

- Centralized API communication through `apiClient`
- Added configurable authentication behavior via `requiresAuth`
- Added robust HTTP error handling
- Added safe handling for responses without JSON bodies
- Introduced an authentication storage abstraction (`authStorage`)
- Began implementing global authentication state using React Context
- Introduced `AuthProvider` as the application's authentication state owner
- Decided on a hybrid authentication model using both React Context and `localStorage`

The next objective is to complete the integration between `AuthProvider` and the authentication flow before introducing protected routing and authenticated user retrieval.

---

# Project Summary

AutoTrader currently consists of a production-oriented Spring Boot REST API and a React frontend that now has the foundations of a scalable authentication architecture.

The backend already supports JWT authentication, authorization, listings, validation, OpenAPI documentation, image management, and structured error handling. The frontend now includes a reusable HTTP client, centralized authentication persistence, and the beginnings of application-wide authentication state through React Context.

---

# Completed Phases

## Phase 1 — Authentication

Completed.

- User registration
- Login
- BCrypt password hashing
- JWT generation
- JWT validation
- Stateless authentication
- Ownership-based authorization

---

## Phase 2 — Vehicle Listings

Completed.

- CRUD operations
- Pagination
- Dynamic filtering
- JPA Specifications

---

## Phase 3 — Refactoring

Completed.

Highlights:

- VehicleListingMapper extraction
- CurrentUserService extraction
- Reduced duplication
- Improved service responsibilities

---

## Phase 4 — Validation

Completed.

Implemented:

- Jakarta Bean Validation
- Validation DTOs
- Global exception handling
- Structured validation responses

---

## Phase 5 — Mapping

Completed.

Implemented:

- Manual DTO mapping

MapStruct intentionally postponed.

---

## Phase 6 — API Documentation

Completed.

Implemented:

- OpenAPI
- Swagger UI
- JWT authorization support
- DTO documentation

---

## Phase 7 — Image Management

Completed.

Implemented:

- VehicleImage entity
- Filesystem storage
- Metadata persistence
- UUID filenames
- Upload validation
- Ownership verification
- Public image serving
- Listing image retrieval

Deferred intentionally:

- Image deletion
- Image ordering
- Primary image switching
- Cloud storage abstraction

---

# Current Architecture

## Backend

- Spring Boot
- Java 17
- Gradle
- REST API

## Security

- Spring Security
- JWT authentication
- BCrypt password hashing
- Stateless authentication
- CORS configured for React

## Persistence

- PostgreSQL
- Spring Data JPA
- Hibernate

## Storage

- Local filesystem for images
- PostgreSQL for metadata
- Browser `localStorage` for JWT persistence

## Validation

- Jakarta Bean Validation
- Global exception handling
- Structured error DTOs

## Mapping

- Manual DTO mapping

## API Documentation

- SpringDoc OpenAPI
- Swagger UI

## Frontend

Current frontend architecture includes:

- React (JavaScript)
- Vite
- Tailwind CSS
- ESLint
- React Router
- Fetch API
- Centralized `apiClient`
- API modules (`authApi`)
- Authentication storage abstraction (`authStorage`)
- React Context (`AuthContext`)
- Controlled forms
- Local component state
- Global authentication state (partially implemented)

---

# Important Architectural Decisions

### Centralized API Client

All HTTP communication now flows through a reusable `apiClient`.

Responsibilities include:

- base URL management
- attaching Authorization headers
- HTTP error handling
- JSON parsing
- handling empty response bodies

Individual API modules should focus only on business endpoints.

---

### Authentication Storage

JWT persistence has been extracted from API modules into dedicated storage helper functions.

This separates browser persistence from HTTP communication.

---

### Explicit Authentication Requests

`apiClient` now accepts:

```javascript
requiresAuth: true;
```

instead of automatically attaching JWTs to every request.

This prevents accidental Authorization headers on public endpoints such as login and registration.

---

### Hybrid Authentication Model

An important architectural decision was made:

React Context supplements `localStorage`; it does not replace it.

Responsibilities are intentionally separated.

`localStorage`

- survives browser refreshes
- acts as persistent storage

React Context

- stores the current authentication state
- triggers UI updates
- provides application-wide access
- eliminates repeated `localStorage` reads

---

### Authentication Ownership

Another architectural decision made during this chat:

The `AuthProvider` should eventually own the authenticated user/session, not just the JWT.

The JWT exists primarily for backend communication.

Application components should consume authentication information from Context rather than directly interacting with browser storage.

---

### Error Handling

HTTP concerns remain inside `apiClient`.

Components receive normal JavaScript errors without knowing HTTP implementation details.

---

# Remaining Roadmap

Continue Phase 8.

Remaining authentication work:

- Finish integrating `AuthProvider` with login
- Synchronize Context after successful authentication
- Implement logout
- Add authenticated user loading (`/users/me` or equivalent endpoint)
- Store authenticated user inside Context
- Introduce protected routes
- Add route guards
- Build authenticated layouts/navigation

After authentication:

- Listings UI
- Listing CRUD
- Image upload UI
- Reusable UI components
- Loading states
- Global error handling

Future phases remain:

- Testing
- Docker
- Deployment
- Production improvements

---

# Files and Structure Added During This Chat

## New

```
src/context/
    AuthContext.jsx

src/auth/
    authStorage.js
```

---

## Modified

```
src/api/
    apiClient.js
    authApi.js

src/pages/
    HomePage.jsx

src/App.jsx

src/main.jsx
```

---

# Concepts Learned During This Chat

- Separation of HTTP concerns from business logic
- Why authentication headers should be opt-in
- Safe parsing of HTTP responses
- Why APIs may legitimately return empty bodies
- Centralized API client architecture
- Browser persistence vs application state
- React Context responsibilities
- Why Context complements rather than replaces `localStorage`
- Ownership of authentication state
- Designing authentication for future scalability

---

# Interview Topics Covered

Be able to explain:

- Why use a centralized API client?
- Why shouldn't every request automatically attach a JWT?
- What problem does `requiresAuth` solve?
- Why separate `authStorage` from `authApi`?
- Why should HTTP error handling stay inside `apiClient`?
- Why can `response.json()` fail?
- Why should React Context not replace `localStorage`?
- Why should `AuthProvider` own authentication state?
- Why is storing only the JWT in Context insufficient?
- What responsibilities belong to an API layer versus a UI component?

---

# Next Recommended Starting Point

Begin by completing the authentication architecture before building additional application features.

Implementation order:

1. Finish integrating `AuthProvider` with the login flow.
2. Ensure successful login updates both:
   - `localStorage`
   - React Context
3. Design a `/me` endpoint for retrieving the authenticated user's profile.
4. Expand `AuthContext` to store the authenticated user alongside the token.
5. Implement logout.
6. Introduce protected routes and route guards.

Continue following the established workflow:

- discuss architecture first
- request current files before modifying existing code
- implement incrementally
- compile and test after every step

---

# Notes for Continuation

Several important architectural conclusions were reached during this conversation and should be preserved:

- React Context supplements browser persistence instead of replacing it.
- Authentication should eventually revolve around the authenticated user rather than the JWT itself.
- The JWT is an implementation detail used for backend communication.
- UI components should primarily consume authentication state from Context.
- HTTP concerns (headers, parsing, error handling) belong inside `apiClient`.
- Browser persistence belongs inside `authStorage`.
- Business operations belong inside API modules such as `authApi`.
- UI components should remain focused on rendering and user interaction.

At the end of this conversation, `AuthContext` has been introduced but is not yet fully integrated with the login flow. The next chat should complete that integration before implementing protected routes or additional authenticated features.
