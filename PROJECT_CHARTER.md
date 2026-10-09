# AutoTrader Project Charter

> **Purpose:** This document serves as the project's engineering charter, mentoring agreement, architectural guide, learning roadmap, and continuity document. It defines not only _what_ we are building, but _how_ and _why_ we build it.

---

# Project Vision

This project is **not** intended to become the largest AutoTrader clone possible.

Its purpose is to build a **production-quality full-stack application** while developing the knowledge and engineering habits expected of a professional software engineer.

The primary goal is employability as a **Java Backend Engineer** while gaining strong frontend skills with React and TypeScript.

Success is measured by the ability to confidently explain architectural decisions during technical interviews—not by how quickly features are completed.

---

# Completed Backend Phases

## Phase 1 — Authentication Backend

Implemented:

- User registration
- Login
- BCrypt password hashing
- JWT generation
- JWT validation
- Stateless authentication
- Spring Security
- User roles
- Ownership-based authorization

---

## Phase 2 — Vehicle Listings Backend

Implemented:

- Create listing
- Retrieve listings
- Retrieve listing by ID
- Update listing
- Soft deletion
- Ownership authorization
- Pagination
- Dynamic filtering
- JPA Specifications

---

## Phase 3 — Backend Refactoring

Implemented:

- `VehicleListingMapper`
- `CurrentUserService`
- Helper methods
- Improved service responsibilities
- Reduced duplication

---

## Phase 4 — Backend Validation

Implemented:

- Bean Validation
- Validation DTOs
- Global exception handling
- Structured validation responses

---

## Phase 5 — Backend Mapping

Implemented:

- Manual DTO mapping
- Dedicated mapper layer

Intentionally postponed:

- MapStruct

The manual implementation was chosen first so the mapping mechanism could be understood before introducing code-generation tooling.

---

## Phase 6 — API Documentation

Implemented:

- SpringDoc OpenAPI
- Swagger UI
- JWT authorization integration
- DTO documentation

---

## Phase 7 — Image Management Backend

**Updated 2026-08-19:** A direct inspection of `ImageController.java`, `ImageService.java`, `VehicleImageRepository.java`, and `VehicleImage.java` confirmed that several items previously recorded as "intentionally deferred" are in fact implemented. This section has been corrected accordingly.

**Updated 2026-10-05:** Integration tests written in Phase 12 showed that image deletion did not actually delete the database row: the deleted image was still in `VehicleListing.images`, and `cascade = ALL` re-persisted it at flush time while the physical file was still removed. `ImageService.deleteImage` now calls `listing.removeImage(image)` before deleting. See the Phase 13 data access and testing notes.

Implemented:

- Dedicated `VehicleImage` entity (includes `displayOrder` and `primaryImage` fields)
- Filesystem storage
- Image metadata persistence
- UUID filenames
- Upload validation (JPEG/PNG/WEBP content-type check)
- Ownership verification
- Compensating file cleanup (storage rollback if metadata persistence fails)
- Primary image assignment (first uploaded image, and explicit switching)
- Dedicated image repository queries
- Spring MVC static resource handling
- Public image URLs
- **Image deletion** — `DELETE /listings/{listingId}/images/{imageId}`, removes the DB record and physical file, then re-normalizes remaining images' display order
- **Primary image switching** — `PATCH /listings/{listingId}/images/{imageId}/primary`, moves target to display order 0 and re-indexes the rest
- **Image ordering** — `PUT /listings/{listingId}/images/order`, bulk reorder with validation that the request contains exactly the listing's current image IDs with no duplicates

Still intentionally deferred:

- Cloud storage abstraction
- Image optimization
- Background image processing

These remain deferred until the product requirements justify the additional complexity.

---

# Phase 8 — React Frontend & Marketplace UX

## Completed / Implemented Areas

The frontend currently includes:

### Frontend Foundation

- React application
- React Router
- Application routing
- Shared components
- Feature-oriented frontend structure
- TailwindCSS styling

### API Layer

- Generic `apiClient`
- Authentication API module
- Listing API module
- User API module
- Image API module
- Centralized HTTP communication

### Authentication

Implemented:

- `AuthContext`
- `useAuth`
- JWT persistence
- Session restoration
- Login
- Registration
- Logout
- Authentication-aware navigation
- Protected routes
- Guest-only route infrastructure
- Automatic authentication after registration

### Listing Management

Implemented:

- Listing creation
- Listing editing
- Listing deletion
- Reusable `ListingForm`
- Client-side listing validation
- Owner-specific listing actions
- My Listings functionality
- Image Integration

Cloud storage and advanced image processing remain deferred.

### Marketplace Browsing

Implemented:

- Listing cards
- Listing details page
- Vehicle specifications
- Search/filter controls
- Make filtering
- City filtering
- Price range filtering
- Body type filtering
- Fuel type filtering
- Transmission filtering
- Sorting controls
- Debounced filter requests
- Backend pagination data consumption
- Loading states
- Error states
- Empty-result states

The backend provides pagination and dynamic filtering through the listings API.

### Seller Information

Seller info is displayed on `ListingDetailsPage.jsx`

---

## Marketplace Browsing UX

Complete:

- Frontend pagination controls
- Current page state
- Page navigation
- Page size handling where appropriate
- Better filter UX
- Reset filters
- Search/filter loading behavior
- Improved empty states
- Improved error handling
- Responsive search/filter layout

---

## Listing Card UX - Implemented

## Listing Details UX - Implemented

---

## Image Management UX

Image management is done through `ImageManager.jsx`

Do not introduce cloud storage or an advanced image-processing pipeline during this phase unless requirements change.

---

# Phase 8 — React Frontend & Marketplace UX

The frontend application and core marketplace browsing experience are established.

Implemented:

- React frontend architecture
- Application routing
- Centralized API layer
- Authentication and protected routes
- Listing management
- Client-side validation
- Image upload (with progress) and gallery integration
- Image delete/set-primary/reorder API functions and backend endpoints
- Listing cards and listing details
- Search, filtering, and sorting
- Backend pagination integration
- Loading, error, and empty states
- Frontend pagination
- Image management UI wiring/verification

---

# Phase 9 — Marketplace Features & User Interaction

Marketplace interaction features building on top of listings, users, and auth are established.

## Phase 9.1 — Favorites / Wishlist

Implemented:

- Favoriting an active listing
- Duplicate-favorite prevention
- Removing a favorite
- Querying favorite status
- Retrieving a user's own favorites
- Ownership scoping (users can only see and manage their own favorites)
- Rejection of favorites on inactive or nonexistent listings
- Authentication enforcement on favorite actions
- Listing-card favorite toggle
- Dedicated favorites page
- Favorite-related loading and error handling

**Status: COMPLETE**

## Phase 9.2 — Seller Profile Improvements

Implemented:

- Public seller profile (viewable without authentication)
- Seller's active listings, paginated
- Seller profile page
- Seller information surfaced on listing details
- Navigation from listing details to the seller's profile
- Reuse of existing listing card presentation for a seller's listings
- Loading, error, and empty states for seller profile and seller listings

**Status: COMPLETE**

## Phase 9.3 — Buyer/Seller Messaging

Implemented:

- Conversations between a buyer and seller, scoped to a vehicle listing
- Sending and retrieving messages within a conversation
- Conversation list / inbox
- Starting a conversation directly from a listing
- Participant-only access (a user cannot view or act on a conversation they're not part of, including by manipulating a URL/ID)
- Authentication enforcement on all messaging actions
- Reuse of the existing authentication and current-user identification mechanism to identify the authenticated participant
- Messaging UI, including loading, error, and empty states

**Status: COMPLETE**

## Phase 9.4 — Additional Marketplace Interactions

Not yet scoped. Held open for future marketplace interaction features as requirements become clear.

---

**Phase 9 overall status: COMPLETE (for now)**

**Next step: Phase 10 — Testing**

---

# Phase 10 — Testing & Quality Assurance

Testing and quality assurance were established across the backend, with the frontend testing infrastructure also established.

Implemented:

* Testing architecture and strategy
* Backend unit and service testing
* Repository / JPA integration testing with H2
* Controller and API testing
* Backend integration testing
* Authentication integration testing
* Vehicle listing integration testing
* Favorites integration testing
* Frontend testing infrastructure using Vitest
* jsdom browser-like test environment
* React Testing Library setup
* `@testing-library/jest-dom` setup
* Initial frontend test coverage for `AuthContext`
* Verification that the frontend test suite executes successfully

Backend testing verified:

* Service behavior
* Repository persistence and queries
* HTTP status codes
* Request validation
* Authentication and authorization boundaries
* Core listing, favorite, image, conversation, message, and user APIs
* Deterministic message ordering
* Core authentication, listing, and favorites integration flows

Frontend testing setup verified:

* Vitest configuration
* jsdom environment
* React Testing Library integration
* Test setup configuration
* Initial authentication context behavior

The remaining frontend testing expansion, security and cross-feature testing, regression/coverage review,
and additional integration testing are intentionally deferred to Phase 13.

**Updated 2026-10-05:** Part of that deferred work was pulled forward into Phase 12, before CI/CD. Repository and integration tests now run on PostgreSQL 17 through Testcontainers instead of H2 (12.3.10). The backend suite also gained integration tests for the security boundary, image management, messaging and seller profiles, unit tests for `ImageService` and `UserService`, and a handler-level `GlobalExceptionHandlerTest`. Frontend testing expansion is still open.

**Phase 10 overall status: COMPLETE**

# Phase 11 — Docker & Developer Tooling

## Docker

Planned:

- Docker images
- Containers
- Dockerfiles
- Docker Compose
- PostgreSQL containerization
- Container networking
- Volumes
- Environment variables

## Developer Tooling

Planned:

- Project document generator
- Folder structure generator
- Current status generator
- Git hooks
- Formatting and linting
- Development scripts
- Environment validation
- Dependency auditing
- Project health reports

### Developer Tooling Status

The following developer tooling has been implemented:

- Development scripts

The following items remain optional or deferred because they primarily provide developer convenience rather than significant architectural or production value:

- Project document generator
- Folder structure generator
- Current status generator
- Git hooks
- Project health reports

The following items have greater relevance to production workflows and may be addressed in later phases where appropriate:

- Formatting and linting
- Environment validation
- Dependency auditing

Developer tooling should be introduced when it solves a real development, maintenance, or operational problem. Convenience tooling should not take priority over more valuable application, deployment, security, testing, or production engineering work.


** PHASE 11 STATUS - Docker setup complete ,remaining developer tooling will be done during phase 13: Production hardening later **

# Phase 12 — Deployment

Planned:

- Environment profiles
- Production configuration
- Centralized environment variables
- Frontend API configuration
- Secrets management
- Database migration strategy
- Multi-stage backend Docker build
- HTTPS
- Reverse proxy
- CI/CD
- Logging
- Monitoring
- Health checks
- PostgreSQL readiness handling
- Cloud hosting
- Managed PostgreSQL (Neon) connectivity
- Production upload configuration
- Transaction boundaries under production settings
- Production-parity testing with PostgreSQL (Testcontainers)
- Pre-CI test expansion (security boundary, image management, messaging and seller profile integration tests)
- Domain and DNS
- Frontend hosting and CORS configuration


# Phase 13 — Production Hardening

Potential future work, grouped by topic. Every item should be introduced based on an identified need rather than simply because it is a common production technology. Items marked *(found in Phase 12)* came out of real problems met while deploying.

### Security & Authentication

Authentication works end to end, but deployment exposed how much of its safety depends on secret handling and rule ordering.

- Refresh tokens
- Email verification
- Password reset
- Expanded role-based authorization
- Rate limiting
- Security hardening
- Audit logging
- Explicit `authenticated()` rules for `/listings/me` and `/users/me`, declared before the wildcard `permitAll` matchers *(found in Phase 12: these routes are currently matched by the public wildcards, so their protection depends on the service layer instead of the filter chain; `SecurityBoundaryIntegrationTest` pins today's 401 behaviour so the change can be made safely)*
- Startup validation of `JWT_SECRET` (valid Base64, at least 32 bytes) *(found in Phase 12: a weak secret only fails on the first login)*
- JWT secret rotation procedure *(found in Phase 12: the secret was exposed and had to be replaced, which invalidates every issued token)*
- Secrets manager evaluation to replace the `.env` file on the VM
- Secret scanning (git hook and CI) *(found in Phase 12: secrets were pasted into chats and printed in output)*

### Performance & Data Access

Running with `open-in-view=false` made lazy loading visible. The listing endpoints now work, but they issue more queries than they should.

- Database indexing
- Caching
- Performance optimization
- N+1 query review on paginated listings (fetch join, `@EntityGraph` or DTO projections; fetch-joining a collection with pagination makes Hibernate paginate in memory)
- Application-wide `@Transactional` policy review (every service that maps entities or does multi-step writes)
- Connection pool tuning for Neon's pooled endpoint
- Review `cascade = ALL` and `orphanRemoval = true` on `VehicleListing.images`, and decide whether images should be removed only through the aggregate *(found in Phase 12: deleting an image through the repository while it was still in the listing's collection let the cascade cancel the delete, so the row survived and only the file was removed; fixed with `removeImage` in `ImageService.deleteImage`)*

### Product & API Evolution

Capabilities that change what the product does or how clients consume it. They stay deferred until requirements justify them.

- API versioning
- Background jobs
- Cloud object storage
- Advanced search

### Frontend Robustness

Small gaps in how the UI behaves when the network or the session misbehaves.

- Disable submit buttons while a request is in flight *(found in Phase 12: duplicate listings were created)*
- Frontend handling of expired or rejected tokens (clear the session and redirect to login on 401) *(found in Phase 12: a rotated secret left stale tokens in browsers)*

### Reliability & Observability

Once the app runs on a server, the questions become "is it healthy?" and "what happened?".

- Reliability and resilience improvements
- Production observability improvements
- Advanced monitoring and alerting
- Structured (JSON) logging
- Logging of authentication failures, never tokens or passwords *(found in Phase 12: the app logs nothing about auth events)*
- Backup strategy for the uploads volume *(found in Phase 12: images live in a Docker volume on a single VM)*
- Replace the broad `IllegalArgumentException` handler with dedicated exceptions such as `InvalidImageException` *(found in Phase 12: missing handlers made bad uploads return 500; the quick fix maps every `IllegalArgumentException` to 400, which would also report a future programming error as a client error)*
- retryable cleanup/outbox processing for image deletion

### Container & Image Hardening

The following items directly strengthen the application's security, reliability, resilience, or operational safety at the container level:

- Container security
- Non-root containers
- Image size and runtime image optimization
- Container resource limits

### Testing & Quality

Testing and quality improvements deferred from Phase 10. Phase 12 added several items because the test suite ran with `open-in-view` on and on H2, so it missed a lazy-loading bug that production settings exposed. A second round of tests written before CI found more real defects and added the lessons below.

- Expanded frontend testing with Vitest, jsdom, and React Testing Library
- Security & cross-feature testing
- Test review, regression testing, and coverage analysis
- Additional messaging integration tests (pulled forward into Phase 12, kept here as a record)
- Additional image management integration tests (pulled forward into Phase 12, kept here as a record)
- Seller/user integration tests (pulled forward into Phase 12, kept here as a record)
- Docker Testcontainers with PostgreSQL for repository/JPA integration testing instead of H2 (pulled forward into Phase 12, kept here as a record)
- Review tests for class-level or method-level `@Transactional`, which keeps one session open and hides lazy-loading bugs
- Test that an invalid or expired Bearer token on a public endpoint is treated as anonymous (pulled forward into Phase 12, kept here as a record)
- Test that an oversized upload returns 413 with the JSON error body (pulled forward into Phase 12 as a handler-level test in `GlobalExceptionHandlerTest`; the end-to-end path is verified manually)
- Run the Testcontainers-based tests in CI
- Cover every flow that deletes, cascades or depends on flush order with at least one test on real PostgreSQL *(found in Phase 12: `ImageServiceTest` with mocked repositories passed while image deletion was broken; only the Testcontainers integration test exposed it, because a mock cannot reproduce Hibernate's cascade behaviour)*
- Review tests that assert a failure as the expected outcome, such as `propagatesException` *(found in Phase 12: two controller tests had locked in missing 403 and 400 handlers; they passed for months and had to be rewritten once the handlers existed. Search for such tests whenever a handler is added)*
- Give every custom exception a handler and a test that asserts its status code and JSON body *(found in Phase 12: `UnauthorizedConversationAccessException` and `IllegalArgumentException` returned 500 instead of 403 and 400)*
- Keep `src/test/resources/application.properties` complete *(found in Phase 12: the test file shadows the main one on the classpath, so every property the application requires, such as the CORS origin and multipart limits, must be repeated there)*
- Test transport-level behaviour at the handler, not through a client *(found in Phase 12: Tomcat aborts an oversized multipart upload mid-stream and closes the socket, so MockMvc cannot trigger it and a real HTTP client sees a connection reset instead of the 413. Assert the handler's contract in isolation and verify the end-to-end path manually or in a staging environment)*
- Make every MockMvc integration test extend `IntegrationTestSupport` *(found in Phase 12: one shared annotation signature lets Spring cache a single context and a single PostgreSQL container, and the base class clears tables child-first so foreign keys never break a cleanup. A test with a different signature, such as a random-port server, starts its own context and container)*

### Developer Tooling & Code Quality

Deferred from Phase 11. A few of these became more relevant because documents and local runs drifted from reality.

- Dependency auditing
- Formatting and linting
- Environment validation
- Folder structure generator, revisited *(found in Phase 12: `FOLDER_STRUCTURE.md` drifted from the real codebase)*
- Document the local IDE run configuration (`JWT_SECRET`, `DB_USERNAME`, `DB_PASSWORD`) and investigate the unexplained `bootRun` hang at 80% against Neon
- Remaining optional Phase 11 tooling (project document generator, current status generator, Git hooks, project health reports)

### Infrastructure & Deployment

Additional infrastructure improvements that may be introduced after the initial deployment when their value is established:

- More advanced CI/CD workflows
- Automated rollback strategies
- Infrastructure-as-code
- Blue/green or canary deployments
- Distributed caching
- Additional scalability improvements
- Versioned image tags and a container registry instead of the mutable `latest` tag *(found in Phase 12: a manually built image silently replaced the backend image)*
- Nginx upstream re-resolution after the backend container is recreated *(found in Phase 12: nginx caches the backend IP at startup)*

These features should only be introduced after explaining the problem they solve and determining that the project actually requires them.

### Deferred Production Architecture

Some features may require larger architectural changes and should remain deferred until there is a demonstrated need:

- Cloud object storage for uploaded images
- Background job processing
- Advanced search infrastructure
- Distributed caching
- More sophisticated deployment strategies
- Additional scalability improvements

The project should prioritize understanding the problem and the underlying engineering
concepts before introducing additional infrastructure, abstractions, or distributed-system components.

# Mentoring Agreement

Throughout this project, the assistant should act as a **senior software engineer and mentor**, not as someone who simply writes code.

The objective is to develop engineering judgment.

Whenever possible:

- Explain concepts from first principles.
- Explain **why** before **how**.
- Build intuition before implementation.
- Introduce one major concept at a time.
- Keep lessons small enough that every change is understandable.
- Frequently connect new concepts to previously implemented features.
- Encourage reasoning rather than memorization.
- Discuss trade-offs rather than presenting one "correct" solution.
- Preserve the existing architecture unless new requirements justify a change.
- Preserve the project's commenting style.
- Prefer constructor injection.
- Avoid unnecessary abstraction until there is a clear reason.

---

# Working Relationship

Over the backend phases we established a rhythm that should continue throughout the project.

We:

- Stop and question architectural decisions rather than accepting them automatically.
- Explain why earlier implementations were appropriate instead of calling them mistakes.
- Revisit concepts when they become relevant in a new context.
- Treat the project as if it were being built by a small professional software team.
- Optimize for understanding that would hold up during a technical interview.
- **Verify documentation against actual source files rather than trusting prior status summaries**, since this project has already shown that status documents can drift from the real codebase.

---

# Core Engineering Philosophy

> Understand the mechanism first, then introduce the tool that automates it.

Examples include:

- Manual DTO mapping before MapStruct.
- Manual validation before automatic validation.
- Spring MVC resource handling before cloud storage.
- Local filesystem before Amazon S3.
- Manual state management before helper libraries.
- Manual forms before React Hook Form.
- Manual validation before Zod.
- Manual API communication before React Query.

For every abstraction:

1. Understand the underlying mechanism.
2. Implement it manually.
3. Introduce tooling later.
4. Compare both approaches.

---

# Feature Workflow

Every significant feature should follow the same process.

## Step 1 — Architecture & Theory

Before implementation:

### Current Project Recap

Explain:

- where we currently are,
- what has already been implemented,
- how the new feature fits into the overall architecture.

### Theory

Explain:

- why the feature exists,
- the problem it solves,
- how developers solved the problem before it existed,
- where it belongs architecturally,
- production considerations,
- alternatives,
- trade-offs.

### Design

Discuss:

- ownership of responsibility,
- layer placement,
- alternatives,
- why the chosen design is appropriate.

No code should be written during this step.

### Required Files

Never assume the codebase matches a previous conversation.

Before modifying an existing class:

- ask for the current version,
- never rewrite from memory,
- modify only the code that is provided.

The latest `FOLDER_STRUCTURE.md` should also be requested when beginning a continuation chat.

---

## Step 2 — Incremental Implementation

Implementation should occur in small logical steps.

For each change:

- identify the file,
- specify its location,
- indicate whether it is new or existing,
- provide complete updated methods or classes,
- explain important lines.

After each step:

- compile,
- run,
- test,
- verify,
- then continue.

---

# Reflection Template

Every completed implementation should conclude with:

## Files Changed

Explain which files changed.

## Why They Changed

Explain why each modification was necessary.

## Concepts Learned

Summarize the engineering concepts introduced.

## Production Considerations

Discuss how the implementation might evolve in larger systems.

## Interview Notes

Include at least one interview-style discussion question.

## Suggested Git Commit Message

Provide a meaningful commit message.

---

# Real-World Engineering Perspective

Whenever introducing a concept, explain not only how it works in this project but also how it is commonly approached in professional software teams.

Where appropriate discuss:

- How startups might implement it.
- How larger companies might implement it.
- How the design evolves as systems grow.
- Trade-offs between simplicity and scalability.
- Common production pitfalls.
- How the feature changes in a microservices architecture.
- Which parts of our implementation are educational.
- Which parts would likely remain unchanged in production.

This context should supplement—not replace—the implementation we build.

---

# Repository Structure

The repository currently follows a two-application structure:

```text
AutoTrader/
├── backend/
├── frontend/
├── uploads/
├── CURRENT_STATUS.md
├── FOLDER_STRUCTURE.md
├── PROJECT_CHARTER.md
└── STATUS_REPORT_PROMPT.md
```

The exact file structure must always be taken from the latest `FOLDER_STRUCTURE.md` rather than assumed from this document.

---

# Final Principle

The objective of this project is not simply to finish an application.

The objective is to build an application whose architecture, implementation, trade-offs, and evolution can be confidently explained in a professional software engineering interview.

Whenever requirements evolve, we should treat the codebase as a real product:

- revisit earlier decisions respectfully,
- explain why previous solutions were appropriate,
- justify new changes based on new requirements,
- continuously improve both the software and the engineering process.