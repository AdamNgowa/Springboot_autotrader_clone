# CURRENT_STATUS.md

## Current Phase

### Phase 8 — React Frontend (Authentication Completed, My Listings UI In Progress)

During this chat, the frontend moved beyond authentication and into the first authenticated feature.

Completed during this phase:

- Authentication Context (`AuthContext`)
- JWT persistence using `localStorage`
- Session restoration on page refresh
- Protected routes using `ProtectedRoute`
- Authenticated routing with React Router
- `/listings/me` backend integration
- Initial implementation of the **My Listings** page
- First reusable UI component (`ListingCard`) started

Current focus:

Building the authenticated Listings Management UI incrementally, beginning with `MyListingsPage`.

---

# Project Summary

AutoTrader now consists of a production-oriented Spring Boot backend and a React frontend with a functioning authentication system.

Authenticated users can:

- log in
- remain logged in after refresh
- access protected pages
- retrieve their own listings from the backend
- display those listings in the frontend

The frontend architecture now mirrors the backend's modular design, separating routing, API communication, authentication, pages, hooks, and reusable components.

---

# Completed Phases

## Phase 1 — Authentication (Backend)

Completed.

- Registration
- Login
- BCrypt
- JWT authentication
- Ownership authorization

---

## Phase 2 — Vehicle Listings (Backend)

Completed.

- Create
- Retrieve
- Retrieve by ID
- Update
- Soft delete
- Pagination
- Dynamic filtering
- Specifications

---

## Phase 3 — Backend Refactoring

Completed.

- VehicleListingMapper extraction
- CurrentUserService extraction
- Helper methods
- Reduced duplication
- Clearer service responsibilities

---

## Phase 4 — Validation

Completed.

- Bean Validation
- Validation DTOs
- Global exception handling

---

## Phase 5 — Mapping

Completed.

- Manual DTO mapping

MapStruct intentionally postponed.

---

## Phase 6 — API Documentation

Completed.

- Swagger UI
- SpringDoc OpenAPI
- JWT integration

---

## Phase 7 — Image Management

Completed.

Implemented:

- VehicleImage entity
- Filesystem storage
- UUID filenames
- Metadata persistence
- Image retrieval
- Public image URLs

Deferred intentionally:

- Image ordering
- Image deletion
- Primary image switching
- Cloud storage

---

## Phase 8 — React Frontend (Current)

Completed so far:

### Frontend Architecture

- Vite
- React
- React Router
- Modular folder structure
- API layer
- Authentication storage
- Context API
- Protected routes

### Authentication

Implemented:

- AuthContext
- AuthProvider
- useAuth hook
- login()
- logout()
- session restoration
- current user retrieval
- JWT persistence
- loading state during session restoration
- route protection

### Listings

Implemented:

- listingApi
- getMyListings()
- MyListingsPage
- loading state
- error state
- empty state
- successful rendering of listings

Started:

- reusable ListingCard component

---

# Current Architecture

## Backend

- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Spring Data JPA
- Specifications
- DTO mapping
- Image management

## Frontend

Architecture currently consists of:

- React
- React Router
- Context API
- Fetch API
- Centralized apiClient
- authApi
- listingApi
- authStorage
- AuthContext
- useAuth hook
- ProtectedRoute
- Page-based routing
- Reusable component architecture (introduced)

---

# Important Architectural Decisions

## API Layer

All HTTP communication flows through `apiClient`.

Responsibilities:

- Authorization headers
- JSON parsing
- Error handling
- Empty response handling

Business APIs remain thin wrappers.

---

## Authentication

Authentication state belongs inside `AuthContext`.

`localStorage` is persistence only.

The authenticated user—not merely the JWT—is considered the application's source of truth.

---

## Route Protection

Protected pages are wrapped using:

```
<ProtectedRoute>
    <DashboardPage />
</ProtectedRoute>
```

The route decides access.

The page itself does not perform authentication checks.

---

## Loading During Session Restoration

A loading state was added to `AuthContext`.

Without it, protected routes redirected before the authentication check completed.

This fixed the issue where logged-in users were immediately redirected to `/login` after refreshing the page.

---

## Listings API

The backend returns paginated responses.

Example:

```
{
    content: [...],
    totalElements: ...
}
```

The frontend therefore stores:

```
data.content
```

rather than the entire response object when rendering listings.

---

## React Learning Approach

We agreed not to jump directly to finished UI.

Instead we build:

- one component
- one React concept
- one UI improvement

at a time.

Each change is explained before moving to the next.

At the end of the feature, we will have the complete polished page while understanding every part of its implementation.

---

# Remaining Roadmap

Immediate:

- Finish ListingCard
- Add listing image
- Improve typography
- Improve spacing
- Add Edit button
- Add Delete button
- Improve empty state
- Build responsive card layout

After My Listings:

- Create Listing page
- Edit Listing page
- Delete confirmation
- Image upload UI
- Public marketplace
- Listing details page
- Search and filtering UI

Later phases remain:

- Testing
- Docker
- Deployment
- Production improvements

---

# Files Added or Modified During This Chat

Frontend additions include:

- AuthContext
- useAuth
- ProtectedRoute
- listingApi
- DashboardPage
- MyListingsPage
- CreateListingPage
- ListingCard (started)

Modified:

- AppRouter
- apiClient
- authApi
- App
- main
- LoginPage

Backend additions:

- `/listings/me` endpoint
- corresponding service support for authenticated user's listings

---

# Concepts Learned During This Chat

- React Context
- Protected Routes
- React children prop
- Route composition
- Session restoration
- Loading state during authentication
- Why redirects happened before authentication completed
- API pagination
- Rendering arrays with `.map()`
- Arrow function return syntax inside JSX
- Separating reusable UI into components

---

# Interview Topics Covered

Be prepared to explain:

- Why use React Context for authentication?
- Why store both token and user?
- Why is a loading state required during session restoration?
- Why wrap routes instead of checking authentication inside pages?
- What is the `children` prop?
- Why does `.map()` require a returned JSX element?
- Why return `data.content` instead of the entire response?
- Why build reusable components such as ListingCard?

---

# Next Recommended Starting Point

Continue implementing the UI for **My Listings**.

Do not redesign the architecture.

Continue incrementally:

1. Finish the basic `ListingCard`.
2. Add the listing image.
3. Improve card layout.
4. Add Edit and Delete actions.
5. Polish spacing and typography.
6. Make the page responsive.

Only after My Listings reaches a polished state should we begin the Create Listing page.

---

# Notes for Continuation

The agreed mentoring workflow remains unchanged.

Continue implementing features in small, testable steps with explanations before code.

The current ListingCard already displays the basic listing information successfully.

The next iteration should evolve it toward a marketplace-style card:

---

[ Image ]

2019 Toyota Corolla

Toyota Corolla
Nairobi • 2019

KSh 1,850,000

## [Edit] [Delete]

Build this gradually rather than dropping in a finished component.

**Before making code changes in the next chat, ask for the latest project folder structure (`FOLDER_STRUCTURE.md`) so existing files and locations are known and no assumptions are made about the current codebase.**
