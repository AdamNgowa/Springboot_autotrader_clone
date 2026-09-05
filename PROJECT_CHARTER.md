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

# Phase 10 — Testing

Backend:

- Unit testing
- Service tests
- Repository tests
- Controller tests
- MockMvc
- Mockito
- Integration testing

Frontend:

- React Testing Library
- Component testing
- Hook testing
- API mocking

Testing should be introduced after the relevant functionality is stable enough to test meaningfully.

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

The goal is to experience building tools that improve the development workflow, not only applications for end users.

---

# Phase 12 — Deployment

Planned:

- Environment profiles
- Production configuration
- Secrets management
- HTTPS
- Reverse proxy
- CI/CD
- Logging
- Monitoring
- Health checks
- Cloud hosting

---

# Phase 13 — Production Hardening

Potential future work:

- Refresh tokens
- Email verification
- Password reset
- Expanded role-based authorization
- Rate limiting
- Database indexing
- Caching
- Performance optimization
- Security hardening
- Audit logging
- API versioning
- Background jobs
- Cloud object storage
- Advanced search

These features should only be introduced after explaining the problem they solve and determining that the project actually requires them.

---

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