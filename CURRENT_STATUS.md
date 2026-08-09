# CURRENT_STATUS.md

## Current Phase

**Current Phase:** Authentication UX Polish

The core authentication system is complete and verified.

The next immediate phase is to finish the **Authentication UX Polish** work that has been designed but **has not yet been implemented in the codebase**.

After authentication UX is complete, development should continue with the marketplace/listing experience, followed by favorites, messaging, admin moderation, testing, and deployment.

---

## Project Summary

AutoTrader is a full-stack vehicle marketplace built with Spring Boot and React.

The backend currently provides JWT authentication, user management, vehicle listing CRUD, ownership authorization, validation, JPA Specification-based searching/filtering, pagination, DTO mapping, filesystem image storage, and secured REST APIs. The frontend provides authentication, session restoration, protected routing, listing creation/editing/deletion, image upload, listing management, and authentication-aware navigation.

The core authentication feature is complete. The remaining authentication work is UX refinement rather than architectural redesign.

---

## Completed Phases

### Phase 1 — Project Foundation

- Backend and frontend project structure established.
- Core architecture established.

### Phase 2 — Authentication Backend

- User registration implemented.
- Login implemented.
- BCrypt password hashing implemented.
- JWT generation and validation implemented.
- Spring Security configured.
- Stateless authentication implemented.
- User roles established.
- Ownership-based authorization implemented.

### Phase 3 — Vehicle Listings Backend

- Vehicle listing CRUD implemented.
- Ownership authorization implemented.
- Soft deletion implemented.
- Pagination implemented.
- Dynamic filtering implemented.
- JPA Specification pattern implemented.
- Validation implemented.
- DTO mapping introduced.

### Phase 4 — Backend Refactoring

- `VehicleListingMapper` extracted.
- `CurrentUserService` extracted.
- Helper methods introduced.
- Service responsibilities improved.
- Duplication reduced.

### Phase 5 — Validation

- Bean Validation implemented.
- Validation DTOs implemented.
- Global exception handling implemented.
- Structured validation responses implemented.

### Phase 6 — Mapping

- Manual DTO mapping implemented.
- MapStruct intentionally postponed.

### Phase 7 — API Documentation

- SpringDoc OpenAPI implemented.
- Swagger UI implemented.
- JWT authorization integration implemented.
- DTO documentation added.

### Phase 8 — Image Uploads

Implemented:

- Dedicated `VehicleImage` entity.
- Filesystem storage.
- Image metadata persistence.
- UUID filenames.
- Upload validation.
- Ownership verification.
- Compensating file cleanup.
- Primary image assignment.
- Dedicated image repository queries.
- Spring MVC static resource handling.
- Public image URLs.

Still deferred:

- Image deletion.
- Primary image switching.
- Image ordering.
- Cloud storage abstraction.
- Image optimization.
- Background image processing.

### Phase 9 — Frontend API Layer

- Generic `apiClient` created.
- Authentication API abstraction created.
- Listing API abstraction created.
- User API abstraction created.

### Phase 10 — Frontend Authentication

Completed and verified:

- `AuthContext` implemented.
- Session restoration implemented.
- Login implemented.
- Registration implemented.
- Logout implemented.
- JWT persistence implemented.
- Protected routes implemented.
- Guest-only authentication behavior established/planned.
- Authentication-aware navigation implemented.
- Automatic authentication after registration implemented.

Verified behavior:

- Existing login works.
- Session restoration works after refresh.
- Logout works.
- Navbar updates correctly after login/logout.
- Registration creates a new account.
- Registration automatically authenticates the new user.
- Newly registered users can access `/my-listings`.
- Protected routes work.

### Phase 11 — Listing Management UI

- Listing creation page completed.
- Listing editing infrastructure completed.
- Listing deletion completed.
- Client-side listing validation implemented.
- Image upload integrated.
- Reusable `ListingForm` established.

### Phase 12 — Frontend Navigation

- Navbar integrated.
- Authentication-aware navigation implemented.
- Login/Register navigation implemented.
- Logout flow implemented.
- Conditional navigation implemented.
- Protected routing implemented.
- Registration page integrated.

---

## Current Architecture

### Backend

- Spring Boot
- Spring Security
- JWT authentication
- JPA/Hibernate
- PostgreSQL
- JPA Specification pattern
- DTO layer
- Manual mapper layer
- Global exception handling
- REST API architecture
- Bean Validation
- Pagination
- Filesystem image storage

### Frontend

- React
- React Router
- Context API authentication
- Generic API client
- Feature-specific API modules
- Protected routes
- Authentication-aware navigation
- Listing forms
- Shared components
- TailwindCSS

### Security

- JWT Bearer authentication.
- `AuthContext` is the frontend authentication source of truth.
- Session restoration occurs when the application starts.
- Protected routes prevent unauthenticated access.
- Listing ownership authorization is enforced by the backend.

### Persistence

- PostgreSQL for application data.
- JPA/Hibernate for persistence.

### Image Storage

- Image metadata stored in the database.
- Image files stored on the filesystem.
- Public image URLs exposed through Spring MVC resource handling.

### API Layer

Frontend API communication is centralized through `apiClient`.

Feature-specific API modules wrap the generic client rather than making HTTP requests directly throughout components.

### Routing

React Router is used for:

- Public routes.
- Protected routes.
- Authentication routes.
- Listing management routes.

### Validation

- Backend validation uses Bean Validation.
- Frontend listing validation uses a dedicated validation utility.
- Authentication client-side validation has been designed but **has not yet been implemented**.

---

## Important Architectural Decisions

- `AuthContext` is the single source of truth for frontend authentication.
- Authentication state is based on the authenticated user rather than only JWT existence.
- Session restoration fetches the authenticated user on application startup.
- API communication is centralized through `apiClient`.
- Feature-specific API modules wrap `apiClient`.
- Listing validation exists on both client and server.
- Authentication UX validation should follow the same architectural pattern as listing validation.
- Listing forms keep saving state and validation state at the parent/page level where appropriate.
- Filesystem storage was intentionally implemented before cloud storage.
- Manual DTO mapping was intentionally implemented before MapStruct.
- JPA Specifications were chosen instead of repository-method explosion.
- Protected route logic is centralized in `ProtectedRoute`.
- Image uploads occur after successful listing creation.
- Image metadata and binary storage are intentionally separated.

### Deferred Technical Improvements

These remain intentionally postponed unless requirements make them necessary:

- Refresh tokens.
- Cloud image storage.
- Image optimization.
- Background image processing.
- Image ordering.
- Toast notification system.
- Global loading indicators.
- MapStruct.
- React Query.
- React Hook Form.
- Zod.

The project should continue following the established principle of understanding the underlying mechanism before introducing abstractions.

---

## Remaining Roadmap

### Phase A — Authentication UX Polish

**Not yet implemented.**

Planned work:

- Client-side login validation.
- Client-side registration validation.
- Validation error display.
- Loading states for login and registration.
- Disabled form controls while requests are running.
- Improved Login page UI.
- Improved Register page UI.
- Shared visual language between authentication pages.
- Login/Register cross-links.
- Redirect authenticated users away from `/login`.
- Redirect authenticated users away from `/register`.
- Preserve the originally requested protected route during login.
- Improved `ProtectedRoute` redirect behavior.
- Navbar active-link styling.
- Navbar authentication-restoration loading behavior.
- Improved authenticated-user greeting.
- Authentication-page accessibility improvements.
- Responsive authentication layouts.

### Phase B — Listing and Marketplace UX

After Authentication UX Polish is complete:

- Listing card improvements.
- Listing details improvements.
- Seller information on listing cards.
- Seller information on listing details.
- Frontend search and filtering UX.
- Frontend pagination UX.
- Sorting.
- Image management.
- Delete listing images.
- Set/change primary listing image.
- Image ordering if still required.
- Image previews.
- Upload progress.
- Improved listing error handling.
- Responsive listing interfaces.

**Note:** Backend pagination and dynamic filtering already exist. The remaining work is primarily exposing and polishing these capabilities through the frontend.

### Phase C — Marketplace Features

- Favorites / wishlist.
- Buyer and seller messaging.
- User/seller profile improvements.

### Phase D — Administration

- Admin moderation.
- Role-based administrative workflows.
- Listing moderation.
- User/content moderation as requirements become clearer.

### Phase E — Testing

Backend:

- Unit tests.
- Service tests.
- Repository tests.
- Controller tests.
- MockMvc.
- Mockito.
- Integration testing.

Frontend:

- React Testing Library.
- Component testing.
- Hook testing.
- API mocking.

### Phase F — Containerization and Production

- Docker.
- Dockerfiles.
- Docker Compose.
- PostgreSQL containerization.
- Environment variables.
- Production configuration.
- Secrets management.
- HTTPS.
- Reverse proxy.
- CI/CD.
- Logging.
- Monitoring.
- Health checks.
- Cloud hosting.

### Phase G — Production Hardening

Potential future work:

- Refresh tokens.
- Email verification.
- Password reset.
- Rate limiting.
- Database indexing.
- Caching.
- Performance optimization.
- Security hardening.
- Audit logging.
- API versioning.
- Background jobs.
- Cloud object storage.
- Advanced search.

---

## Files and Structure Added During This Chat

Important frontend files currently known to exist or have been worked on include:

- `frontend/src/main.jsx`
- `frontend/src/pages/LoginPage.jsx`
- `frontend/src/pages/RegisterPage.jsx`
- `frontend/src/components/Navbar.jsx`
- `frontend/src/components/ProtectedRoute.jsx`
- `frontend/src/context/AuthContext.jsx`
- `frontend/src/hooks/useAuth.js`
- `frontend/src/api/authApi.js`
- `frontend/src/api/apiClient.js`
- `frontend/src/components/ListingForm.jsx`
- Listing validation utility.

### Important Status

The proposed Authentication UX changes discussed during this chat have **not necessarily been applied to these files**.

In particular, do **not** assume that the following implementations are already present:

- Authentication client-side validation.
- Login loading state.
- Register loading state.
- Guest-only redirect implementation.
- Intended-destination redirect.
- Navbar active-link styling.
- Navbar auth-restoration loading behavior.
- Shared authentication-page styling.
- Improved authentication error display.

The next conversation must inspect the actual current files before modifying them.

---

## Concepts Learned During This Chat

- Authentication state versus JWT persistence.
- Session restoration.
- Context as an authentication state owner.
- Protected routes.
- Guest-only routes.
- Redirecting after authentication.
- Preserving intended navigation destinations with React Router location state.
- Loading states during asynchronous authentication.
- Client-side validation versus server-side validation.
- Reusing validation architecture across features.
- Conditional navigation based on authentication state.
- Active navigation state with React Router.
- Separating authentication concerns between API, Context, routing, and UI.
- Completing a feature before moving to the next major feature.

---

## Interview Topics Covered

Be able to explain:

- Why `AuthContext` is the frontend authentication source of truth.
- Why JWT persistence does not itself represent complete authentication state.
- How session restoration works.
- Why protected routes are necessary.
- Difference between protected routes and guest-only routes.
- How React Router can preserve the originally requested destination.
- Why authentication loading state matters during application startup.
- Why client-side validation does not replace server-side validation.
- Why listing and authentication validation can follow the same architectural pattern.
- Why API communication is centralized through `apiClient`.
- Why feature-specific API modules wrap the generic API client.
- How `NavLink` determines active navigation state.
- Why authentication UI should not flash between guest and authenticated states.
- How ownership authorization differs from frontend route protection.
- Why filesystem image storage was chosen before cloud storage.
- Why manual DTO mapping was chosen before MapStruct.
- Why JPA Specifications were chosen for dynamic filtering.

---

## Next Recommended Starting Point

At the beginning of the next chat:

### Step 1 — Establish the actual current codebase

**Do not assume any proposed changes from this status have been implemented.**

Ask for and review:

1. The latest `FOLDER_STRUCTURE.md`.
2. `LoginPage.jsx`.
3. `RegisterPage.jsx`.
4. `Navbar.jsx`.
5. `ProtectedRoute.jsx`.
6. `AuthContext.jsx`.
7. `AppRouter.jsx`.
8. `main.jsx`.
9. Any relevant authentication CSS or styling files.

The next chat should compare the actual files against the Authentication UX roadmap before changing anything.

### Step 2 — Architecture/design review

The first implementation milestone should be:

**Authentication UX Polish.**

Before coding, briefly establish how:

- authentication validation,
- loading state,
- guest-only redirects,
- intended-destination redirects,
- shared authentication UI,
- Navbar state,

fit into the existing architecture.

Do not redesign the authentication architecture unless the actual files reveal a concrete need.

### Step 3 — Implement Authentication UX Polish

Implement the remaining authentication UX improvements incrementally and verify each change.

### Step 4 — Move to Marketplace UX

Once authentication UX is verified, proceed to:

1. Seller information.
2. Listing card/details improvements.
3. Search and filtering frontend UX.
4. Pagination.
5. Sorting.
6. Image management.

The exact ordering should be reassessed after inspecting the current listing architecture.

---

## Notes for Continuation

The authentication **functionality** is complete, but the authentication **UX polish described above has not yet been implemented**.

This distinction is important. Previous assistant responses provided proposed replacement implementations for Login, Register, Navbar, and ProtectedRoute, but those proposals should **not be treated as code that already exists in the repository**.

The next conversation should therefore inspect the actual files before making changes.

The current known authentication behavior is:

- Registration works.
- Login works.
- Logout works.
- Session restoration works.
- Navbar updates after authentication changes.
- Protected routes work.
- Registration automatically authenticates the new user.
- Newly registered users can access `/my-listings`.

The following remain planned rather than confirmed implemented:

- Client-side authentication validation.
- Authentication loading states.
- Guest-only redirects.
- Intended-destination redirect after login.
- Login/Register UI polish.
- Navbar active states.
- Navbar loading behavior.
- Authentication-page accessibility/responsive improvements.

### Marketplace Direction Agreed for Future Work

After authentication UX is finished, the project should continue toward the marketplace feature set:

- Search & filtering.
- Pagination.
- Sorting.
- Image management — delete/set primary.
- Seller information.
- Favorites/wishlist.
- Buyer/seller messaging.
- Admin moderation.
- Testing.
- Docker/PostgreSQL production setup.
- Cloud hosting/deployment.

Seller information should be implemented on both **listing cards and listing details** as previously discussed.

### Folder Structure Requirement

At the beginning of every continuation chat, request the latest **`FOLDER_STRUCTURE.md`** and use it together with this document and `PROJECT_CHARTER.md`.

Never invent or assume existing file names or locations.

### Workflow Requirement

Continue using the established workflow:

1. Inspect current files.
2. Explain architecture/theory briefly.
3. Decide implementation order.
4. Modify only provided/current files.
5. Implement incrementally.
6. Verify after each meaningful step.
7. Update `CURRENT_STATUS.md` at the end of a major milestone.
