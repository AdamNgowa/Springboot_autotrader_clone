# CURRENT_STATUS.md

## Current Phase

### Phase 8 — React Frontend (Listings Management Completed, Image Upload Starting)

During this chat, the frontend listings management workflow was completed and validated.

Completed during this phase:

- Create Listing page
- Edit Listing page
- Client-side validation shared between Create and Edit pages
- Reusable `validateListing` utility
- Validation error clearing as users edit fields
- Delete Listing functionality
- Delete confirmation flow
- My Listings management UI completed

The next feature is **Image Upload**, which will be implemented by integrating the existing backend image management API.

---

# Project Summary

AutoTrader currently consists of a production-oriented Spring Boot backend and a React frontend with authenticated listing management.

Authenticated users can:

- register and log in
- remain authenticated across refreshes
- create listings
- edit their own listings
- delete their own listings
- view all of their listings
- perform client-side validation before API requests

The backend already supports image management through dedicated image endpoints and filesystem storage. The frontend is now beginning integration with those APIs.

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

- Shared `validateListing` utility
- Client-side validation for Create Listing
- Client-side validation for Edit Listing
- Shared validation logic between both pages

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

- Image ordering
- Image deletion
- Primary image switching
- Cloud storage

---

## Phase 8 — React Frontend (Current)

Completed so far:

### Frontend Architecture

- React
- Vite
- React Router
- API layer
- Context API
- Protected routes
- Modular folder structure

### Authentication

Implemented:

- AuthContext
- JWT persistence
- Session restoration
- ProtectedRoute
- Loading state during authentication restoration

### Listings

Implemented:

- My Listings page
- ListingCard component
- Create Listing page
- Edit Listing page
- Delete Listing
- Shared client-side validation
- Validation error display
- Validation error clearing
- Success messages
- Loading and saving states

Next:

- Image upload integration

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
- Centralized apiClient
- authApi
- listingApi
- authStorage
- AuthContext
- useAuth hook
- ProtectedRoute
- Reusable ListingCard
- Shared validation utility

---

# Important Architectural Decisions

## Shared Client Validation

Validation now lives in a dedicated utility:

```
utils/validateListing.js
```

Both Create and Edit pages use the same validation rules to avoid duplication and ensure consistency.

---

## API Layer

All HTTP communication continues to flow through `apiClient`.

Business-specific API files remain thin wrappers around it.

---

## Authentication

Authentication state remains owned by `AuthContext`.

`localStorage` is persistence only.

---

## Image Upload Strategy

The agreed implementation sequence is:

1. Inspect backend image endpoints
2. Create `imageApi.js`
3. Add image picker to ListingForm
4. Upload images after listing creation
5. Show upload progress/loading
6. Display uploaded images
7. Delete uploaded images
8. Select primary image

Images will be uploaded **after** a listing is successfully created, since they require an existing listing ID.

---

# Remaining Roadmap

Immediate:

- Integrate backend image upload API
- Upload images from Create Listing
- Display uploaded images
- Delete uploaded images
- Primary image selection

After image upload:

- Public Home page
- Listing Details page
- Search
- Filtering
- Pagination
- Sorting
- Responsive UI polish

Later phases remain:

- Testing
- Docker
- Deployment
- Production improvements

---

# Files and Structure Added During This Chat

### Added

- `frontend/src/utils/validateListing.js`

### Modified

- `CreateListingPage.jsx`
- `EditListingPage.jsx`

Delete functionality was completed and validation logic was refactored to use the shared utility.

---

# Concepts Learned During This Chat

- Sharing validation logic between pages
- Avoiding duplicated business rules
- Client-side validation before API requests
- Clearing validation errors as users edit fields
- Early returns for validation failures
- Debugging runtime errors caused by passing incorrect variables (`updateListing` vs `updatedListing`)

---

# Interview Topics Covered

Be prepared to explain:

- Why extract validation into a shared utility?
- Why validate on the client if the backend also validates?
- Why should validation remain centralized?
- What caused the `Cannot read properties of undefined (reading 'trim')` error?
- Why was passing `updateListing` instead of `updatedListing` incorrect?
- Why upload images after creating a listing instead of including them in the initial request?

---

# Next Recommended Starting Point

Before making code changes:

1. Provide the latest versions of:
   - `ImageController.java`
   - `ImageService.java`

2. Confirm the current `FOLDER_STRUCTURE.md` so existing frontend files are known and no file names or locations are assumed.

Then begin Step 1 of the image upload implementation by verifying the backend API before writing frontend code.

---

# Notes for Continuation

Image upload will be implemented incrementally using the agreed eight-step plan.

The backend implementation already exists, so the remaining work is frontend integration.

The mentoring workflow remains unchanged:

- inspect existing files before modifying them
- explain architecture before implementation
- build one logical step at a time
- compile, run, and test after each step before continuing

The next conversation should begin by reviewing:

- `PROJECT_CHARTER.md`
- `CURRENT_STATUS.md`
- `FOLDER_STRUCTURE.md`

and then request the current contents of:

- `ImageController.java`
- `ImageService.java`

before implementing the frontend image upload feature.
