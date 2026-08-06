# CURRENT_STATUS.md

## Current Phase

### Phase 8 — React Frontend (Listing Details Completed, Public Listings Starting)

During this chat, the Listing Details feature was completed and integrated into the React frontend.

Completed during this phase:

- Listing Details page
- Public listing route (`/listings/:id`)
- Individual listing API integration
- Loading, error, and "listing not found" states
- Image gallery with thumbnail selection
- Reusable `SpecificationCard` component
- Vehicle specifications section
- Seller information placeholder
- Improved listing presentation

The next feature is **Public Listings (Home Page)**, where visitors will browse all active listings.

---

# Project Summary

AutoTrader now consists of a production-oriented Spring Boot backend and a React frontend supporting authenticated listing management together with public listing viewing.

Authenticated users can:

- register and log in
- remain authenticated across refreshes
- create listings
- edit their own listings
- delete their own listings
- upload images during listing creation
- view and manage their own listings

Any visitor can:

- open an individual listing
- browse listing images
- view vehicle specifications

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

Backend:

- Bean Validation
- Validation DTOs
- Global exception handling

Frontend:

- Shared validation utility
- Client-side validation
- Shared validation between Create and Edit pages

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

## Phase 7 — Image Management (Backend)

Completed.

Implemented:

- VehicleImage entity
- Filesystem storage
- UUID filenames
- Metadata persistence
- Public image URLs
- Image upload endpoints

Deferred intentionally:

- Image deletion
- Image ordering
- Primary image switching
- Cloud storage

---

## Phase 8 — React Frontend (Current)

Completed so far:

### Frontend Architecture

- React
- Vite
- React Router
- Context API
- Centralized API layer
- Protected routes
- Modular folder structure

### Authentication

Implemented:

- AuthContext
- JWT persistence
- Session restoration
- ProtectedRoute

### Listings Management

Implemented:

- My Listings page
- ListingCard component
- Create Listing
- Edit Listing
- Delete Listing
- Shared validation
- Success messages
- Loading and saving states

### Image Upload

Implemented:

- Multiple image upload during listing creation
- Backend image integration
- Primary image display in cards

### Listing Details

Implemented:

- Listing Details page
- Public route
- Thumbnail gallery
- Image switching
- Vehicle specifications
- Reusable SpecificationCard component
- Seller placeholder section
- Loading/error handling

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
- Filesystem image storage
- Dedicated Image API

## Frontend

Current architecture includes:

- React
- React Router
- Context API
- Fetch API
- Centralized `apiClient`
- `authApi`
- `listingApi`
- `AuthContext`
- `ProtectedRoute`
- `ListingForm`
- `ListingCard`
- `SpecificationCard`
- Shared validation utility
- Listing Details page

---

# Important Architectural Decisions

- All HTTP communication continues through `apiClient`.
- Business-specific API files remain thin wrappers around the API client.
- Authentication state remains owned by `AuthContext`.
- Validation logic is centralized in `validateListing`.
- Listing forms are shared between Create and Edit pages.
- Images are uploaded after listing creation because a listing ID is required.
- Listing Details uses local component state to manage the selected gallery image instead of additional libraries.
- Image deletion, image ordering, primary image management, cloud storage, and React Query remain intentionally postponed to preserve the educational progression.

---

# Remaining Roadmap

Immediate:

- Build Public Listings (Home Page)
- Pagination
- Search
- Filtering
- Sorting
- Responsive polish

Later:

- Testing
- Docker
- Deployment
- Production improvements

---

# Files and Structure Added During This Chat

### Added

- `ListingDetailsPage.jsx`
- `SpecificationCard.jsx`

### Modified

- `AppRouter.jsx`
- `ListingCard.jsx`
- `listingApi.js`
- `CURRENT_STATUS.md` (this document)

---

# Concepts Learned During This Chat

- Separating collection views from detail views.
- Using route parameters with `useParams`.
- Fetching a single resource independently from a list.
- Managing loading, error, and empty states.
- Image gallery state management using React state.
- Building reusable presentational components.
- Defensive rendering while asynchronous data loads.
- Why local UI state is appropriate for image selection.

---

# Interview Topics Covered

Be prepared to explain:

- Why have separate endpoints for a collection and a single resource?
- Why use route parameters?
- Why initialize state with `null`?
- Why did accessing `listing.price` initially throw an error?
- Why is the selected image stored in component state?
- Why extract `SpecificationCard` into its own component?
- Why is the listing list stale immediately after editing?

---

# Next Recommended Starting Point

Before making code changes:

1. Provide the current `FOLDER_STRUCTURE.md`.
2. Review the current `HomePage.jsx`.
3. Design the Public Listings page architecture before implementation.
4. Build the public listings page incrementally using the existing `ListingCard` component where appropriate.

---

# Notes for Continuation

The Listing Details feature is considered complete for the current scope.

One known improvement remains:

- After editing a listing, the My Listings page still displays stale data until it is reloaded because the local React state is not synchronized after the update. This should be addressed when improving the listings management experience.

The next feature is the Public Listings page, which will introduce public browsing, followed by pagination, search, filtering, and sorting.

At the beginning of the next chat, please provide:

- `PROJECT_CHARTER.md`
- `CURRENT_STATUS.md`
- the latest `FOLDER_STRUCTURE.md`

This ensures the next conversation knows the current project structure, avoids assuming file names, and requests existing files before suggesting modifications.
