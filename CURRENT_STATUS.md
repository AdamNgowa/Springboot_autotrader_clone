# CURRENT_STATUS.md

## Current Phase

### Phase 7 — Image Management: **Completed**

Phase 7 has been completed successfully.

The backend now supports:

* Image upload
* Local filesystem storage
* Image metadata persistence
* Public image serving
* Image retrieval through listing responses

The next phase is:

> **Phase 8 — React + TypeScript Frontend**

The first objective of Phase 8 is **frontend architecture design**, not implementation.

---

# Project Summary

AutoTrader is currently a production-oriented Spring Boot REST API for vehicle marketplace listings.

The application supports secure JWT authentication, ownership-based authorization, CRUD operations for listings, validation, dynamic searching, pagination, OpenAPI documentation, and image management.

Images are stored on the local filesystem while their metadata is stored separately in PostgreSQL, establishing a clear separation between binary storage and relational data.

The backend has intentionally been built incrementally to prioritize understanding of architectural decisions before introducing automation or helper libraries.

---

# Completed Phases

## Phase 1 — Authentication

Completed.

Implemented:

* User registration
* Login
* BCrypt password hashing
* JWT generation
* JWT validation
* Stateless authentication
* Ownership-based authorization

---

## Phase 2 — Vehicle Listings

Completed.

Implemented:

* Create listing
* Retrieve listing
* Retrieve listing by ID
* Update listing
* Soft delete
* Pagination
* Dynamic filtering
* JPA Specifications

---

## Phase 3 — Refactoring

Completed.

Highlights:

* Extracted VehicleListingMapper
* Extracted CurrentUserService
* Introduced helper methods
* Improved service responsibilities
* Reduced duplication

---

## Phase 4 — Validation

Completed.

Implemented:

* Jakarta Bean Validation
* Validation DTOs
* Global exception handling
* Structured validation responses

---

## Phase 5 — Mapping

Completed.

Implemented:

* Manual DTO mapping

MapStruct intentionally postponed.

---

## Phase 6 — API Documentation

Completed.

Implemented:

* OpenAPI
* Swagger UI
* JWT integration
* DTO documentation

---

## Phase 7 — Image Management

Completed.

Implemented:

* VehicleImage entity
* OneToMany / ManyToOne relationship
* Local filesystem storage
* FileStorageService
* ImageService
* UUID filenames
* MIME type validation
* Ownership verification
* Metadata persistence
* Compensating cleanup for failed persistence
* Automatic primary image assignment
* Spring MVC static resource configuration
* Public image URLs
* Image retrieval within listing responses
* Specialized repository query for fetching listings with images

---

# Current Architecture

## Backend

Spring Boot 3.5.x

Java 17

Gradle

REST API

---

## Security

* Spring Security
* JWT authentication
* BCrypt password hashing
* Stateless security

---

## Persistence

* PostgreSQL
* Spring Data JPA
* Hibernate

---

## Storage

Filesystem storage

Uploads directory managed through FileStorageService.

Metadata stored separately inside PostgreSQL.

---

## Mapping

Manual mapper implementation.

VehicleListingMapper now maps image metadata into API responses.

MapStruct intentionally postponed.

---

## Validation

Jakarta Bean Validation

Global exception handling.

---

## API Documentation

Swagger UI

OpenAPI

Bearer authentication support.

---

## Frontend

Not yet started.

---

# Important Architectural Decisions

* Metadata and binary files are stored separately.
* Local filesystem storage was intentionally implemented before cloud storage.
* FileStorageService owns filesystem operations.
* ImageService orchestrates image upload workflows.
* VehicleImage is its own entity rather than embedding image information into VehicleListing.
* UUID filenames prevent collisions and avoid exposing original filenames publicly.
* Lazy loading remains the default relationship strategy.
* Specialized repository queries are preferred over globally eager relationships.
* Validation occurs before side effects.
* Authorization occurs before resource modification.
* Manual implementations precede helper libraries to reinforce understanding.
* Service responsibilities remain intentionally focused.
* Constructor injection is used throughout the application.

Deferred intentionally:

* Image deletion
* Primary image switching
* Image ordering
* Cloud storage abstraction
* Refresh tokens
* Email verification
* Production optimizations

These deferred features may become a future production-improvement phase rather than extending Phase 7.

---

# Remaining Roadmap

* Phase 8 — React + TypeScript Frontend
* Phase 9 — Testing
* Phase 10 — Docker & Containers
* Phase 11 — Deployment
* Phase 12 — Production Improvements

---

# Files and Structure Added During This Chat

## Repository

Repository structure standardized to:

```text
AutoTrader/
│
├── .idea/
├── backend/
├── frontend/
├── PROJECT_CHARTER.md
├── CURRENT_STATUS.md
└── README.md
```

## Documentation

Created:

* PROJECT_CHARTER.md

Planned:

* STATUS_REPORT_PROMPT.md

Discussed long-term documentation strategy using:

* PROJECT_CHARTER.md
* CURRENT_STATUS.md

---

# Concepts Learned During This Chat

* Why specialized repository methods are preferable to globally eager loading.
* Why different use cases require different fetch strategies.
* How Spring MVC serves static resources outside the classpath.
* Why backend architecture should evolve with new requirements rather than replacing earlier decisions.
* Why project documentation should evolve alongside the codebase.
* How maintaining a project charter improves long-term continuity.
* Why architecture design should precede frontend implementation.

---

# Interview Topics Covered

Be able to explain:

* Why FileStorageService exists separately from ImageService.
* Why filesystem storage was implemented before cloud storage.
* Why metadata is stored separately from image files.
* Why UUID filenames are preferable to original filenames.
* Why lazy loading remains the default strategy.
* Why fetch joins should be targeted instead of replacing lazy loading globally.
* Why architecture evolves as requirements evolve.
* Why backend APIs should influence frontend architecture.
* Why documentation should be treated as part of the codebase.

---

# Next Recommended Starting Point

Begin Phase 8.

Do **not** begin by creating React components.

Instead:

1. Design the frontend architecture.
2. Design the folder structure.
3. Design routing.
4. Design authentication flow.
5. Design API communication.
6. Design state ownership.
7. Design reusable component organization.

Only after the architecture is understood should implementation begin.

---

# Notes for Continuation

The mentoring workflow has been refined.

Implementation should now follow a consistent two-step process:

### Step 1

* Recap the current state.
* Explain the theory.
* Explain architectural decisions.
* Discuss alternatives and trade-offs.
* Request any existing files before suggesting changes.

### Step 2

* Guide the implementation incrementally.
* Specify file names and locations.
* Explain important code.
* Compile and test before continuing.
* Conclude with:

    * Files changed
    * Why they changed
    * Concepts learned
    * Production considerations
    * Interview Notes
    * Suggested Git commit message

An additional documentation workflow has also been established.

Future chats should begin by providing:

1. PROJECT_CHARTER.md
2. CURRENT_STATUS.md

This combination provides sufficient context to continue the project without reconstructing prior discussions.
