# AutoTrader — Current Status Report Generator

You are maintaining continuity for the AutoTrader project.

Generate a concise `CURRENT_STATUS.md` that describes the **actual state of the project at the end of the current conversation**.

The purpose of this document is:

> Give the next conversation enough accurate context to continue development without re-investigating completed work or accidentally implementing something that already exists.

This is an **engineering handoff**, not a conversation summary and not a copy of the project charter.

---

# 1. Source of Truth

Determine the project's current state using evidence in this order:

1. Current source files explicitly provided in this conversation
2. The latest `FOLDER_STRUCTURE.md`, if provided
3. Explicit statements from the user about functionality that was tested or confirmed
4. Earlier project documentation such as `PROJECT_CHARTER.md` or previous `CURRENT_STATUS.md`

### Rules

- Describe what **actually exists**, not what was merely discussed.
- Never assume proposed code was implemented.
- Never reconstruct files or folder structures from memory.
- If current source code conflicts with older documentation, trust the current source code.
- If the user explicitly says something was tested successfully, classify it as **Verified**.
- If code exists but its behavior has not been tested, classify it as **Implemented**, not Verified.
- If only part of a feature exists, classify it as **Partial**.
- If a feature has not been implemented, classify it as **Planned**.
- If the user intentionally postponed something, classify it as **Deferred**.
- If there is insufficient evidence to determine the state, classify it as **Unverified**.

Use these classifications naturally:

- **Implemented**
- **Verified**
- **Partial**
- **Planned**
- **Deferred**
- **Unverified**

Do not force a classification onto every sentence. Use them where they clarify the state.

---

# 2. Current Phase

State the current project phase and, where applicable, the current sub-phase.

For example:

```text
Phase 8 — React Frontend & Marketplace UX

Current sub-phase:
8.3 — Search & Filters
```

Briefly state:

- what is already implemented in the current phase
- what remains
- exactly where development should resume

If the current phase has been completed, identify the next phase instead.

Do not reproduce the entire project charter.

---

# 3. Project Summary

Write one or two short paragraphs describing what the application **currently does**.

Only describe implemented functionality.

Do not include:

- future features
- planned architecture
- hypothetical functionality
- features mentioned in the charter but not confirmed

---

# 4. Completed Phases

List completed project phases with a short summary.

Example:

```text
## Phase 1 — Authentication Backend
- User registration and login
- JWT authentication
- Spring Security authorization

## Phase 2 — Vehicle Listings Backend
- Listing CRUD
- Ownership authorization
- Pagination and filtering
```

Keep these summaries short.

Do not repeat detailed implementation explanations from `PROJECT_CHARTER.md`.

If a phase is only partially complete, do not list it as completed.

---

# 5. Current State

Describe the important functionality currently present in the application.

Group information naturally by area.

For example:

## Backend

- Authentication
- Listings
- Validation
- Image management
- Pagination/filtering

## Frontend

- React architecture
- API layer
- Routing
- Marketplace browsing
- Listing management

## Authentication

- Login
- Registration
- Session restoration
- Logout
- Protected routes

## Marketplace

- Search
- Filters
- Sorting
- Pagination
- Listing cards
- Listing details
- Seller information
- Images

Only include areas that are relevant to the current project state.

For partially implemented features, explicitly identify what exists and what does not.

---

# 6. Phase/Sub-phase Status

When the current phase contains multiple sub-phases, provide a compact status tree.

Example:

```text
Phase 8
│
├── 8.1 Baseline & Cleanup ............... Completed
├── 8.2 Authentication UX ................ Verified
├── 8.3 Search & Filters ................. Partial
├── 8.4 Sorting & Pagination ............. Partial
├── 8.5 Listing Cards .................... Partial
├── 8.6 Listing Details .................. Partial
├── 8.7 Seller Information .............. Planned
├── 8.8 Image Management ................. Partial
└── 8.9 Listing Management UX ........... Partial
```

Only include sub-phases that actually belong to the current phase.

For each sub-phase, classify its current state based on evidence.

If a sub-phase has already been intentionally excluded or postponed, mark it **Deferred** rather than unfinished.

This section is important because it allows the next conversation to immediately understand the development roadmap.

---

# 7. Remaining Work

List only unfinished work.

Separate it into:

## Immediate

Work that should be done next in the current phase.

Organize it by sub-phase when useful.

For example:

### 8.3 Search & Filters

Already implemented:

- Make filtering
- City filtering
- Price filtering
- Body type filtering

Still required:

- Reset filters
- Filter UX improvements
- Responsive filter layout
- Verification of filter combinations

Do not list functionality that is already implemented as remaining work.

### 8.4 Sorting & Pagination

Still required:

- Frontend pagination controls
- Current page state
- Page navigation
- Reset page when filters change
- Verify sorting behavior

Continue this pattern for other incomplete sub-phases.

---

## Later

List work that exists in the project roadmap but should **not be implemented yet**.

Examples:

- Deferred Phase 8 sub-phases
- Phase 9 marketplace interaction
- Phase 10 testing
- Phase 11 Docker/developer tooling
- Phase 12 deployment
- Phase 13 production hardening

Do not turn planned future work into immediate tasks.

---

# 8. Important Decisions

Record only decisions that the next conversation must remember.

Examples:

- Existing architecture should be preserved.
- Backend pagination/filtering already exists and should be consumed rather than redesigned.
- Manual DTO mapping was intentionally used before MapStruct.
- Cloud image storage is intentionally deferred.
- Advanced image processing is deferred.
- Authentication implementation is considered complete unless a concrete issue is discovered.
- A specific debug log is intentionally retained.

Keep this section short.

Do not explain general engineering principles here.

---

# 9. Important Verified Behavior

When the user has explicitly tested functionality during the conversation, record the important verified behavior.

For example:

```text
Authentication has been manually verified for:

- Login success
- Registration success
- Refresh while authenticated
- Logout
- Protected route while logged out
- Login after protected-route redirect
- Invalid login credentials
```

Do not claim tests were performed unless the user explicitly confirmed them.

If a test was proposed but not performed, do not list it as verified.

---

# 10. Files Changed

List important files that were **actually provided or confirmed as changed during the conversation**.

Do not invent filenames.

For each file, briefly state what changed if known.

Example:

```text
- `AuthContext.jsx` — session initialization now rolls back the persisted JWT when `/users/me` fails.
- `userApi.js` — current-user API request.
- `listingApi.js` — listing retrieval and filtering API calls.
```

If the exact filename or change is uncertain, mark it Unverified or omit it.

---

# 11. Next Starting Point

This section is critical.

Tell the next conversation **exactly what to do first**.

Include:

### First step

The exact sub-phase to resume.

### Before coding

Identify which current files need to be requested or inspected.

Do not assume their contents from previous conversations.

### Architecture/Theory

State whether the next step requires:

- architecture discussion first
- theory first
- direct implementation
- or simply inspection because the concept is already understood

### First implementation task

State the smallest concrete task that should be implemented first.

Example:

```text
Resume at Phase 8.3 — Search & Filters.

Before modifying anything:
1. Request the latest `FOLDER_STRUCTURE.md` if unavailable.
2. Request the current `HomePage` and `SearchFilters`.
3. Inspect the current `listingApi.getListings()` implementation.
4. Identify which 8.3 requirements are already satisfied.
5. Implement only the first missing requirement: Reset Filters.

Do not rewrite existing filtering functionality.
```

This section should make it possible for a new conversation to begin implementation immediately.

---

# 12. Continuation Notes

Include only information that prevents the next conversation from making an incorrect assumption.

Examples:

- A feature is partially implemented.
- A proposed change was never actually applied.
- A backend capability already exists and should be reused.
- A particular console log is intentionally retained.
- Authentication is already verified and should not be unnecessarily redesigned.
- A failure-path test was intentionally deferred.
- Certain sub-phases are intentionally postponed.

Keep this section concise.

---

# 13. Accuracy Rules

Before generating the report, perform an internal consistency check:

### Do not claim:

- "Implemented" without evidence.
- "Verified" without explicit testing/confirmation.
- "Completed" when only part of the feature exists.
- "Remaining" for something already implemented.
- "Changed" for a file that was never actually provided or confirmed.
- "Current" based solely on old documentation.

### Prefer:

```text
Implemented
Verified
Partial
Planned
Deferred
Unverified
```

over vague statements such as:

```text
mostly done
almost complete
should be working
probably implemented
```

When uncertain, say **Unverified**.

---

# 14. Conciseness Rules

The final document should be a **small engineering handoff**.

Do not:

- write a history of the conversation
- explain concepts at length
- reproduce the project charter
- document every implementation detail
- repeat the same information in multiple sections
- list completed work as remaining work
- include speculative future architecture

Do:

- preserve the current phase/sub-phase structure
- distinguish implemented from verified
- identify partial functionality
- identify intentionally deferred work
- identify the exact next starting point
- preserve important engineering decisions
- give the next conversation enough information to continue safely

The final `CURRENT_STATUS.md` should normally be concise enough to read in a few minutes while still being sufficient for another conversation to continue the project without losing context.
