# CURRENT_STATUS.md

## Current Phase

### Phase 8 — React Frontend: **In Progress**

Phase 8 has moved from architecture planning into the first implementation stage.

Completed during this phase so far:

- React project initialized with Vite
- JavaScript selected instead of TypeScript for educational purposes
- ESLint configured
- Tailwind CSS configured
- Frontend connected successfully to the Spring Boot backend
- First end-to-end authentication request completed
- Browser persistence introduced with localStorage
- Frontend architecture discussions started

The next objective is to continue designing the API layer by introducing a dedicated `apiClient` before expanding authentication.

---

# Project Summary

AutoTrader is currently a production-oriented full-stack application consisting of a Spring Boot REST API and a React frontend.

The backend supports JWT authentication, ownership-based authorization, vehicle listings, validation, OpenAPI documentation, image management, and filesystem image storage.

The frontend has been intentionally built incrementally. Rather than immediately using helper libraries, the project has focused on understanding React fundamentals, component rendering, state management, controlled forms, API communication with `fetch`, browser storage, and frontend architecture before introducing additional abstractions.

---

# Completed Phases

## Phase 1 — Authentication

Completed.

Implemented:

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

Implemented:

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
- JWT integration
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

- Spring Boot 3.5.x
- Java 17
- Gradle
- REST API

## Security

- Spring Security
- JWT authentication
- BCrypt password hashing
- Stateless security
- CORS configured for React development

## Persistence

- PostgreSQL
- Spring Data JPA
- Hibernate

## Storage

- Local filesystem for uploaded images
- PostgreSQL for metadata

## Mapping

- Manual mapper implementation

## Validation

- Jakarta Bean Validation
- Global exception handling

## API Documentation

- Swagger UI
- OpenAPI

## Frontend

- React (JavaScript)
- Vite
- ESLint
- Tailwind CSS
- Browser Fetch API
- Controlled forms
- Browser localStorage
- Incrementally growing folder structure

---

# Important Architectural Decisions

- React will use JavaScript instead of TypeScript for this project to better understand what TypeScript adds in future projects.
- Folder structure will evolve gradually alongside implementation instead of creating every folder upfront.
- Tailwind CSS is the project's styling solution.
- Manual React concepts precede helper libraries.
- Manual forms before React Hook Form.
- Manual fetch before Axios or React Query.
- Browser localStorage introduced before React Context.
- Authentication will follow a hybrid approach:
  - localStorage provides persistence across refreshes.
  - React state/context will provide application-wide access later.
- API communication is moving toward a centralized API client rather than allowing every API module to manage authentication independently.
- Backend architectural decisions from previous phases remain unchanged.
- Deferred production features remain intentionally postponed:
  - Refresh tokens
  - HttpOnly cookie authentication
  - Email verification
  - Cloud storage
  - Production optimizations

---

# Remaining Roadmap

- Continue Phase 8 — React Frontend
  - API client abstraction
  - Authentication architecture
  - Routing
  - Global authentication state
  - Listings UI
  - Image upload UI
- Phase 9 — Testing
- Phase 10 — Docker
- Phase 11 — Deployment
- Phase 12 — Production Improvements

---

# Files and Structure Added During This Chat

## Frontend

Initialized React project using Vite.

Current frontend includes:

```text
frontend/
├── public/
├── src/
│   ├── api/
│   │   └── authApi.js
│   ├── assets/
│   ├── pages/
│   │   └── HomePage.jsx
│   ├── App.jsx
│   ├── App.css
│   ├── index.css
│   └── main.jsx
├── eslint.config.js
├── vite.config.js
├── package.json
└── index.html
```

Backend updated:

- Spring Security CORS configuration added to allow the React development server.

---

# Concepts Learned During This Chat

- React rendering cycle
- Why local variables reset every render
- React state lifecycle
- Functional state updates
- Controlled components
- Form submission in React
- JavaScript objects vs JSON
- Why `JSON.stringify()` is required
- Browser Fetch API
- Async/await request flow
- Parsing JSON responses
- Browser CORS and why Postman is unaffected
- Browser localStorage
- JWT persistence
- Difference between React memory and browser persistence
- Separation of API responsibilities
- Why centralized API communication scales better than duplicated fetch logic

---

# Interview Topics Covered

Be able to explain:

- Why React state survives re-renders but not page refreshes
- Why local variables reset on every render
- Functional updates in `useState`
- Controlled vs uncontrolled inputs
- Why `JSON.stringify()` is required before sending requests
- Difference between JavaScript objects and JSON
- What CORS is and why browsers enforce it
- Why Postman bypasses CORS
- Why JWTs are persisted in localStorage
- Why React state alone is insufficient for authentication persistence
- Benefits of a hybrid authentication architecture
- Why API communication should be centralized instead of duplicated

---

# Next Recommended Starting Point

Continue Phase 8 by designing the frontend API layer before adding additional features.

Specifically:

1. Introduce a dedicated `apiClient.js`.
2. Refactor `authApi.js` to use the API client without changing functionality.
3. Keep authentication behavior identical.
4. Afterwards, extend the API client to automatically attach JWT tokens for protected requests.
5. Only then introduce global authentication state (React Context) and routing.

Continue following the established workflow:

- Architecture discussion first.
- Request existing files before modifying them.
- Implement incrementally.
- Compile, test, and verify after every step.

---

# Notes for Continuation

The frontend is intentionally following the same educational philosophy used throughout the backend:

- understand the underlying mechanism first,
- then introduce abstractions gradually.

Current authentication flow:

React Form → authApi → Fetch API → Spring Boot → JWT → JSON Response → localStorage.

The browser successfully stores the JWT after login, and the frontend/backend integration has been verified.

An architectural discussion concluded that authentication responsibilities should not be duplicated across API modules. The agreed direction is to introduce a centralized `apiClient` responsible for common HTTP behavior, with authentication layered on afterward.

Future production improvements (refresh tokens, HttpOnly cookies, React Query, React Hook Form, Zod, etc.) remain intentionally deferred until the underlying mechanisms have been implemented and understood manually.