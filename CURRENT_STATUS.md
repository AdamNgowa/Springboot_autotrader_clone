# AutoTrader — Current Status

## Current Position

Phase 8 — React Frontend & Marketplace UX

Authentication UX (8.1/8.2) is intentionally excluded from the current work because the implemented authentication behavior has already been manually verified.

Current focus:

- 8.3 Search & Filters
- 8.4 Sorting & Pagination
- 8.5 Listing Cards
- 8.6 Listing Details
- 8.7 Seller Information
- 8.8 Image Management
- 8.9 Listing Management UX

Deferred until later:

- 8.10 Global Loading/Error/Empty States
- 8.11 Responsive & Accessibility
- 8.12 Integration & Regression Testing

The current resume point is Phase 8.6 — Listing Details.

---

## Phase 8 Progress

### 8.3 Search & Filters

Implemented and working:

- Make, city, price, body type, fuel type, and transmission filters
- Sorting selection
- Backend filtering integration
- Explicit Apply Filters button
- Explicit Reset Filters button
- Separate editable filter state and applied filter state
- Pagination reset when filters are applied or reset
- Loading and empty-result handling

Current behavior:

- Editing filters does not automatically send requests.
- Apply Filters copies the editable filter state into the active query state.
- Reset Filters restores the initial filters and default sort order.
- The backend filtering system should not be redesigned unless a concrete problem is discovered.

Remaining:

- Verify error behavior
- Remove unnecessary debug logging while preserving the intentional generated listing URL log in `listingApi.js -> getListings()`
- Make further UX changes only when justified by a concrete issue

Note: `apiClient.js` was recently inspected and still contains temporary response logging that should be removed separately. The intentional listing URL `console.log()` in `getListings()` must remain.

---

### 8.4 Sorting & Pagination

Completed and manually verified.

Implemented:

- Backend pagination data consumption
- `currentPage` state
- `totalPages` state
- Previous and Next controls
- Numbered page controls
- Disabled Previous on the first page
- Disabled Next on the last page
- Active page indication
- Preservation of active filters and sorting when changing pages
- Pagination reset when filters are applied or reset

Current backend page size:

```text
5
```

Manual verification confirmed correct backend pagination metadata and page navigation with the current dataset.

No further changes are currently justified unless a concrete issue is discovered.

---

### 8.5 Listing Cards

Completed for the current scope and manually verified.

Implemented and refined:

- Vehicle identity and location display
- Price display
- Primary image lookup
- Missing-image fallback
- Full-width image presentation across the upper card area
- Listing details navigation
- Owner Edit action
- Owner Delete action
- Improved spacing and visual hierarchy
- Semantic React Router `Link` navigation
- Keyboard-accessible listing navigation
- Independent owner actions outside the listing link

Current design decisions:

- The primary image occupies the complete upper portion of the card.
- The card container controls rounded image corners using `overflow-hidden`.
- The fallback states `No image available`.
- Listing navigation uses a real link rather than a clickable non-interactive `<article>`.
- Edit and Delete do not depend on event propagation handling.

Seller information remains outside the current Listing Card implementation and will be handled after inspecting actual backend seller data in Phase 8.7.

Broader responsive and accessibility review remains deferred to 8.11.

No further Listing Card changes are currently justified unless a concrete issue is discovered.

---

### 8.6 Listing Details

Next implementation area.

Currently implemented:

- Listing retrieval by ID
- Loading state
- Error state
- Not-found state
- Image gallery integration
- Primary image selection
- Image selection
- Title
- Price
- City
- Description
- Vehicle specifications
- Enum formatting
- Seller placeholder section

Current components:

- `ListingDetailsPage`
- `ImageGallery`
- `SpecificationCard`

Before making changes:

1. Request and inspect the current `frontend/src/pages/ListingDetailsPage.jsx`.
2. Do not reconstruct the file from memory.
3. Inspect the current data flow and existing component responsibilities.
4. Identify the next concrete UX issue before changing code.

Likely review areas:

- Image gallery UX
- Image fallback behavior
- Seller placeholder replacement after backend data inspection
- Listing information presentation
- Existing loading/error/not-found behavior
- Appropriate marketplace actions only if justified

Do not implement seller information blindly. First inspect the actual listing response, DTOs, and existing API data.

Do not perform deferred 8.10, 8.11, or 8.12 work while reviewing this phase.

---

### 8.7 Seller Information

Not implemented yet.

The current listing details page contains placeholder seller content.

Before implementation:

- Inspect the actual listing response.
- Inspect `VehicleListingResponse`.
- Inspect relevant user/listing DTO relationships.
- Determine exactly what seller information is already exposed.
- Use existing backend data where possible.
- Avoid backend changes unless the existing response is genuinely insufficient.
- If a backend change is required, choose the smallest appropriate change.

After the available data is understood, decide whether seller information should appear on:

- Listing details
- Listing cards

Do not invent a seller-data contract before inspecting the actual backend implementation.

---

### 8.8 Image Management

Current functionality:

- Image upload integration
- Image gallery
- Primary image display
- Listing image rendering

Remaining possibilities:

- Delete listing images
- Set/change primary image
- Image management UI
- Image previews
- Image ordering only if product requirements justify it
- Upload progress only if useful

Before implementing any operation:

1. Inspect the current image API.
2. Inspect the backend controller and service.
3. Inspect the existing frontend image integration.
4. Confirm which endpoints already exist.
5. Do not assume delete or primary-image switching endpoints are available.

Do not introduce:

- Cloud storage
- Advanced image processing
- Background image processing

unless requirements change.

---

### 8.9 Listing Management UX

Existing functionality:

- Listing creation
- Listing editing
- Listing deletion
- Reusable `ListingForm`
- Client-side listing validation
- Owner-specific listing actions
- My Listings functionality
- Image upload integration

Before making changes, inspect the current files and actual behavior.

Review:

- Create submission/loading behavior
- Edit submission/loading behavior
- Delete confirmation behavior
- Error presentation
- Success feedback where justified
- Owner action behavior
- Image handling during create/edit
- Navigation consistency after create/edit/delete

Preserve the existing architecture unless a concrete problem justifies a change.

---

# Important Existing Authentication Code

Authentication behavior is currently considered implemented and manually verified.

`AuthContext` currently handles:

- JWT persistence
- Session restoration
- `/users/me` verification
- Token rollback if session initialization fails
- Login
- Registration
- Logout
- `isAuthenticated` derived from `user`

The important session initialization behavior is:

1. Save token.
2. Set token in React state.
3. Request `/users/me`.
4. Set the authenticated user if successful.
5. If `/users/me` fails:
   - remove the persisted token
   - clear the React token
   - clear the user
   - rethrow the error

Do not change this behavior unless a concrete problem is discovered.

One optional `/users/me` failure test remains deferred.

---

# Important Debug Logging Decision

One intentional frontend log must remain:

`listingApi.js -> getListings()`

It logs the generated `/listings?...` URL.

This log is intentionally retained.

Temporary response-inspection and development logs elsewhere should be removed unless deliberately required.

The current `apiClient.js` should be reviewed separately because temporary `console.log("data", data)` logging was observed during the latest inspection.

---

# Working Rules

Before modifying an existing file:

1. Request the current file if it has not been provided.
2. Do not reconstruct files from memory.
3. Preserve the existing architecture.
4. Identify a concrete reason for the change.
5. Make one logical change at a time unless several changes are tightly coupled within the same reviewed component.
6. Explain why the change is needed.
7. After implementation, run, compile, test, and manually verify before continuing.
8. Do not implement deferred 8.10, 8.11, or 8.12 work.
9. Preserve the intentional listing URL log in `listingApi.js -> getListings()`.

The latest `FOLDER_STRUCTURE.md` should be requested at the beginning of a continuation chat when it is not already available.

---

# Where To Resume

## Phase 8.6 — Listing Details

Start by requesting:

```text
frontend/src/pages/ListingDetailsPage.jsx
```

Then inspect the actual current implementation before proposing changes.

Current high-level data flow:

```text
Route
  ↓
ListingDetailsPage
  ↓
getListing(id)
  ↓
Listing response
  ├── vehicle information
  ├── images → ImageGallery
  └── specifications → SpecificationCard
```

The immediate objective is not to redesign the page. It is to inspect what already exists, identify the next justified improvement, and preserve the current architecture.

Seller information should remain placeholder work until the actual backend response and DTO structure have been inspected.

---

# Repository Structure

The repository uses a two-application structure:

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

The exact structure must always be taken from the latest `FOLDER_STRUCTURE.md` rather than assumed from this summary.
