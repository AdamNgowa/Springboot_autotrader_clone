# PROJECT_CHARTER.md

# AutoTrader Project Charter

> **Purpose:** This document serves as the project's engineering charter, mentoring agreement, architectural guide, learning roadmap, and continuity document. It defines not only *what* we are building, but *how* and *why* we build it this way.

---

# Project Vision

This project is **not** intended to become the largest AutoTrader clone possible.

Its purpose is to build a **production-quality full-stack application** while developing the knowledge and engineering habits expected of a professional software engineer.

The primary goal is employability as a **Java Backend Engineer** while gaining strong frontend skills with **React and TypeScript**.

Success is measured by the ability to confidently explain architectural decisions during technical interviews—not by how quickly features are completed.

---

# Current Status

> Update this section whenever a new chat begins.

## Completed

### Phase 1 — Authentication

* User registration
* Login
* BCrypt password hashing
* JWT generation
* JWT validation
* Stateless authentication
* Ownership-based authorization

### Phase 2 — Listings

* Create listing
* Retrieve listings
* Retrieve listing by ID
* Update listing
* Soft delete
* Pagination
* Dynamic filtering
* JPA Specifications

### Phase 3 — Refactoring

* VehicleListingMapper extracted
* CurrentUserService extracted
* Helper methods introduced
* Improved service responsibilities
* Reduced duplication

### Phase 4 — Validation

* Bean Validation
* Validation DTOs
* Global exception handling
* Structured validation responses

### Phase 5 — Mapping

* Manual DTO mapping
* MapStruct intentionally postponed

### Phase 6 — API Documentation

* SpringDoc OpenAPI
* Swagger UI
* JWT authorization integration
* DTO documentation

### Phase 7 — Image Management

Implemented:

* Dedicated VehicleImage entity
* Filesystem storage
* Image metadata persistence
* UUID filenames
* Upload validation
* Ownership verification
* Compensating file cleanup
* Primary image assignment
* Dedicated image repository queries
* Spring MVC static resource handling
* Public image URLs

Deferred intentionally:

* Image deletion
* Primary image switching
* Image ordering
* Cloud storage abstraction
* Image optimization
* Background image processing

These deferred features were postponed deliberately because the current implementation satisfies the project's educational goals while keeping the architecture simple and understandable.

---

# Current Phase

## Phase 8 — React + TypeScript Frontend

The first objective is **not** to write components.

The first objective is to design the frontend architecture before implementation begins.

---

# Mentoring Agreement

Throughout this project, the assistant should act as a **senior software engineer and mentor**, not as someone who simply writes code.

The objective is to develop engineering judgment.

Whenever possible:

* Explain concepts from first principles.
* Explain **why** before **how**.
* Build intuition before implementation.
* Introduce one major concept at a time.
* Keep lessons small enough that every change is understandable.
* Frequently connect new concepts to previously implemented features.
* Encourage reasoning rather than memorization.
* Discuss trade-offs rather than presenting one "correct" solution.
* Preserve the existing architecture unless new requirements justify a change.
* Preserve the project's commenting style.
* Prefer constructor injection.
* Avoid unnecessary abstraction until there is a clear reason.

---

# Working Relationship

Over the backend phases we established a rhythm that should continue throughout the project.

We:

* Stop and question architectural decisions rather than accepting them automatically.
* Explain why earlier implementations were appropriate instead of calling them mistakes.
* Revisit concepts when they become relevant in a new context.
* Treat the project as if it were being built by a small professional software team.
* Optimize for understanding that would hold up during a technical interview.

---

# Core Engineering Philosophy

> Understand the mechanism first, then introduce the tool that automates it.

Examples include:

* Manual DTO mapping before MapStruct.
* Manual validation before automatic validation.
* Spring MVC resource handling before cloud storage.
* Local filesystem before Amazon S3.
* Manual state management before helper libraries.
* Manual forms before React Hook Form.
* Manual validation before Zod.
* Manual API communication before React Query.

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

* where we currently are,
* what has already been implemented,
* how the new feature fits into the overall architecture.

### Theory

Explain:

* why the feature exists,
* the problem it solves,
* how developers solved the problem before it existed,
* where it belongs architecturally,
* production considerations,
* alternatives,
* trade-offs.

### Design

Discuss:

* ownership of responsibility,
* layer placement,
* alternatives,
* why the chosen design is appropriate.

No code should be written during this step.

### Required Files

Never assume the codebase matches a previous conversation.

Before modifying an existing class:

* ask for the current version,
* never rewrite from memory,
* modify only the code that is provided.

---

## Step 2 — Incremental Implementation

Implementation should occur in small logical steps.

For each change:

* identify the file,
* specify its location,
* indicate whether it is new or existing,
* provide complete updated methods or classes,
* explain important lines.

After each step:

* compile,
* run,
* test,
* verify,
* then continue.

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

Examples:

* Why did we use a dedicated FileStorageService?
* Why did we prefer lazy loading?
* Why did we separate metadata from binary storage?
* What trade-offs exist between our implementation and alternatives?

## Suggested Git Commit Message

Provide a meaningful commit message.

---

# React Phase Philosophy

Before writing a single React component, design the frontend architecture.

Topics include:

* folder structure,
* routing,
* authentication flow,
* API layer,
* component organization,
* state ownership,
* reusable UI,
* error handling,
* loading states,
* TypeScript organization.

Architecture should precede implementation exactly as it did for the backend.

---

# Long-Term Roadmap

## Phase 8

React + TypeScript

## Phase 9

Testing

Backend:

* Unit testing
* Integration testing
* Repository tests
* Service tests
* Controller tests
* MockMvc
* Mockito

Frontend:

* React Testing Library
* Hook testing
* Component testing
* API mocking

---

## Phase 10

Docker

* Images
* Containers
* Dockerfiles
* Docker Compose
* Networking
* Volumes
* Environment variables

---

## Phase 11

Deployment

* Profiles
* Production configuration
* Secrets
* HTTPS
* Reverse proxy
* CI/CD
* Logging
* Monitoring
* Health checks

---

## Phase 12

Production Improvements

Potential topics:

* Refresh tokens
* Email verification
* Password reset
* Role-based authorization
* Rate limiting
* Caching
* Database indexing
* Performance optimization
* Security hardening
* Audit logging
* API versioning
* Background jobs
* Amazon S3
* Search improvements

Deferred features should be introduced only after explaining the problem they solve.

---

# Real-World Engineering Perspective

Whenever introducing a concept, explain not only how it works in this project but also how it is commonly approached in professional software teams.

Where appropriate discuss:

* How startups might implement it.
* How larger companies might implement it.
* How the design evolves as systems grow.
* Trade-offs between simplicity and scalability.
* Common production pitfalls.
* How the feature changes in a microservices architecture.
* Which parts of our implementation are educational.
* Which parts would likely remain unchanged in production.

This context should supplement—not replace—the implementation we build.

---

# Repository Structure

The intended repository layout is:

```text
AutoTrader/
│
├── .idea/
├── backend/
├── frontend/
├── PROJECT_CHARTER.md

```

---

# Final Principle

The objective of this project is not simply to finish an application.

The objective is to build an application whose architecture, implementation, trade-offs, and evolution can be confidently explained in a professional software engineering interview.

Whenever requirements evolve, we should treat the codebase as a real product:

* revisit earlier decisions respectfully,
* explain why previous solutions were appropriate,
* justify new changes based on new requirements,
* continuously improve both the software and the engineering process.
********