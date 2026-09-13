# CURRENT STATUS

## Phase 10

├── 10.1 Testing Architecture & Strategy
├── 10.2 Backend Unit & Service Testing
│   ├── 10.2.1 CurrentUserService Unit Testing
│   │   ├── Authentication-state behavior tested
│   │   ├── Authenticated user lookup tested
│   │   ├── Missing database user tested
│   │   ├── Mockito repository interaction verification
│   │   └── SecurityContext cleanup between tests
│   ├── 10.2.2 AuthService Unit Testing
│   │   ├── Successful registration tested
│   │   ├── Duplicate email registration tested
│   │   ├── Successful login tested
│   │   ├── Missing user login tested
│   │   └── Incorrect password login tested
│   ├── 10.2.3 VehicleListingService Unit Testing
│   │   ├── Listing creation tested
│   │   ├── Active listing retrieval tested
│   │   ├── Current user listings tested
│   │   ├── Seller listings tested
│   │   ├── Listing ownership during update tested
│   │   ├── Unauthorized update tested
│   │   ├── Soft deletion tested
│   │   ├── Missing listing behavior tested
│   │   └── Deleted listing behavior tested
│   ├── 10.2.4 FavoriteService Unit Testing
│   │   ├── Favorite creation tested
│   │   ├── Duplicate favorite prevention tested
│   │   ├── Favorite removal tested
│   │   ├── Favorite status lookup tested
│   │   └── Current user favorites retrieval tested
│   ├── 10.2.5 ConversationService Unit Testing
│   │   ├── Conversation creation tested
│   │   ├── Existing conversation reuse tested
│   │   ├── Self-conversation prevention tested
│   │   ├── Current user conversation retrieval tested
│   │   ├── Participant conversation access tested
│   │   └── Unauthorized conversation access tested
│   └── 10.2.6 MessageService Unit Testing
│       ├── Message sending tested
│       ├── Message retrieval tested
│       └── Conversation authorization delegation tested
├── 10.3 Backend Repository / JPA Integration Testing
│   ├── UserRepository testing completed
│   │   ├── Lookup by email tested
│   │   └── Missing-user (not-found) behavior tested
│   ├── MessageRepository testing completed
│   │   ├── Conversation message retrieval tested
│   │   ├── Message ordering tested
│   │   ├── Conversation isolation tested
│   │   └── Pagination tested
│   ├── ConversationRepository testing completed
│   │   ├── Lookup by buyer/seller/listing tested
│   │   ├── Missing-conversation behavior tested
│   │   ├── Buyer-or-seller participant retrieval tested (paginated)
│   │   ├── Lookup by id scoped to participant tested
│   │   └── Non-participant access denial tested
│   ├── FavoriteRepository testing completed
│   │   ├── Lookup by user and listing tested
│   │   ├── Missing-favorite behavior tested
│   │   ├── Existence check tested (true/false cases)
│   │   ├── Deletion by user and listing tested
│   │   └── All-favorites-for-user retrieval tested
│   ├── VehicleImageRepository testing completed
│   │   ├── Image-existence check for listing tested
│   │   ├── Display-order ascending retrieval tested
│   │   ├── Image count per listing tested
│   │   └── Listing data isolation (no cross-listing leakage) tested
│   ├── VehicleListingRepository testing completed
│   │   ├── Listing retrieval with images tested
│   │   ├── VehicleListing ↔ VehicleImage relationship tested
│   │   ├── Seller and status filtering tested
│   │   ├── Pagination tested
│   │   ├── Seller data isolation tested
│   │   └── Missing listing behavior tested
│   ├── Deterministic message ordering hardened in production
│   │   └── createdAt ASC with id ASC tie-breaker
│   └── Full test suite regression verified successfully
├── 10.4 Backend Controller & API Testing
│   ├── AuthControllerTest completed
│   │   ├── Valid registration tested (201, delegates to service)
│   │   └── Invalid registration tested (400, service not called)
│   │   ├── Valid login tested (200, delegates to service)
│   │   └── Invalid login tested (400, service not called)
│   ├── VehicleListingControllerTest completed
│   │   ├── Listing creation tested (201, Location header, delegates to service)
│   │   ├── Invalid creation tested (400, service not called)
│   │   ├── Paginated listing retrieval tested
│   │   ├── Current user's listings tested
│   │   ├── Listing by id tested (found / not found)
│   │   ├── Listing update tested (valid, invalid, not-owner-forbidden)
│   │   └── Listing deletion tested (success, not found)
│   ├── FavoriteControllerTest completed
│   │   ├── Add favorite tested (success, listing not found)
│   │   ├── Remove favorite tested
│   │   └── Favorite status and current-user favorites list tested
│   ├── ImageControllerTest completed
│   │   ├── Image upload tested (success, unhandled invalid-file gap documented)
│   │   ├── Image deletion tested (success, not found)
│   │   ├── Primary image assignment tested (success, not found)
│   │   └── Image reorder tested (valid, invalid)
│   ├── ConversationControllerTest completed
│   │   ├── Conversation creation tested (valid, invalid)
│   │   ├── Current user's conversations tested (paginated)
│   │   ├── Conversation retrieval tested (participant access)
│   │   └── Unauthorized access gap documented (no handler yet for 403 conversion)
│   ├── MessageControllerTest completed
│   │   ├── Paginated message retrieval tested
│   │   └── Message sending tested (valid, invalid)
│   └── UserControllerTest completed
│       ├── Current authenticated user profile tested
│       ├── Public seller profile tested (found, not found)
│       └── Seller's active listings tested (paginated)
├── 10.5 Backend Integration Testing (IN PROGRESS)
│   ├── AuthenticationIntegrationTest completed
│   ├── VehicleListingIntegrationTest completed
│   │   └── Missing AuthenticationEntryPoint fixed — unauthenticated
│   │       requests were returning 403 instead of 401
│   ├── FavoritesIntegrationTest completed
│   │   ├── Add/remove favorite tested
│   │   ├── Idempotent favoriting tested
│   │   ├── Per-user scoping tested
│   │   ├── Favorites list retrieval tested
│   │   └── 404/401 rejection paths tested
│   ├── Shared ApplicationContext cleanup ordering fixed
│   │   └── Child tables (favorites/messages/conversations/images)
│   │       now cleared before parent tables (listings/users) in
│   │       @BeforeEach, since integration test classes with identical
│   │       @SpringBootTest config share one H2 database
│   └── DEFERRED for now (see below): messaging, image, and
│       seller/user integration tests
├── 10.6 Frontend Testing
├── 10.7 Security & Cross-Feature Testing
└── 10.8 Test Review, Regression & Coverage

---

### Issues Resolved During Testing

* **H2 reserved keyword conflict:** `year` was mapped to the database column `vehicle_year` while preserving the Java/API property name `year`.
* **Missing test configuration:** Added test-specific `application.properties` with H2 and JWT configuration so the Spring application context could load during tests.
* **JPA vehicle image relationship:** Corrected bidirectional `VehicleListing` ↔ `VehicleImage` relationship handling with synchronized collection/owning-side updates.
* **Nondeterministic message ordering:** Message pagination originally ordered only by `createdAt`, allowing equal timestamps to produce inconsistent results. Production ordering was hardened to `createdAt ASC, id ASC`.
* **Boxed/primitive mismatch in listing DTOs:** `VehicleListingResponse.getMileage()`, and the `mileage`/`year` setters on `CreateListingRequest` and `UpdateListingRequest`, declared primitive `int`/`Integer` inconsistently against their boxed `Integer` fields. A `null` mileage (e.g. any DTO built without it) triggered an unboxing `NullPointerException` during JSON serialization, surfacing as an unexplained 500 in `VehicleListingControllerTest` and `UserControllerTest`. Fixed by making field, getter, and setter consistently `Integer` across all three DTOs.
* **Missing AuthenticationEntryPoint:** No custom `AuthenticationEntryPoint` was registered in `SecurityConfig`, so Spring Security's default `Http403ForbiddenEntryPoint` returned 403 for unauthenticated requests instead of 401. Added `JwtAuthenticationEntryPoint` and wired it via `.exceptionHandling(...)`.
* **Regression verification:** Full `clean test` execution now completes successfully.

### Verification

* **10.2 Backend Unit & Service Testing:** COMPLETE
* **10.2.1 CurrentUserService Unit Testing:** COMPLETE
* **10.2.2 AuthService Unit Testing:** COMPLETE
* **10.2.3 VehicleListingService Unit Testing:** COMPLETE
* **10.2.4 FavoriteService Unit Testing:** COMPLETE
* **10.2.5 ConversationService Unit Testing:** COMPLETE
* **10.2.6 MessageService Unit Testing:** COMPLETE
* **10.3 Backend Repository / JPA Integration Testing:** COMPLETE
* **UserRepository testing:** COMPLETE
* **MessageRepository testing:** COMPLETE
* **ConversationRepository testing:** COMPLETE
* **FavoriteRepository testing:** COMPLETE
* **VehicleImageRepository testing:** COMPLETE
* **VehicleListingRepository testing:** COMPLETE
* **10.4 Backend Controller & API Testing:** COMPLETE
* **AuthControllerTest:** COMPLETE
* **VehicleListingControllerTest:** COMPLETE
* **FavoriteControllerTest:** COMPLETE
* **ImageControllerTest:** COMPLETE
* **ConversationControllerTest:** COMPLETE
* **MessageControllerTest:** COMPLETE
* **UserControllerTest:** COMPLETE
* **10.5 Backend Integration Testing:** IN PROGRESS
* **AuthenticationIntegrationTest:** COMPLETE
* **VehicleListingIntegrationTest:** COMPLETE
* **FavoritesIntegrationTest:** COMPLETE
* **MessagingIntegrationTest, ImageIntegrationTest, Seller/UserIntegrationTest:** DEFERRED — tracked in `PROJECT_CHARTER.md` under Phase 10.5, to be picked up after 10.6/10.7
* JUnit 5 and Mockito test dependencies resolved correctly.
* Test sources placed under `src/test/java`.
* H2 test database configuration verified.
* Full test suite verified with `.\gradlew.bat clean test`.
* Current result: **BUILD SUCCESSFUL; all tests passing.**
* Two documented gaps carried forward (not regressions, tracked for 10.7):
  * `IllegalArgumentException` on invalid image upload has no dedicated `@ExceptionHandler` — currently surfaces as an unhandled exception rather than a structured 400.
  * `UnauthorizedConversationAccessException` has no dedicated `@ExceptionHandler` — currently surfaces as an unhandled exception rather than a structured 403.
* Remaining compiler warnings (deprecated API usage in `SecurityConfig`, unchecked/unsafe operations in `VehicleListingServiceTest`) do not currently cause test failures.

## NEXT STEP

* Pause remaining 10.5 integration tests (messaging, image, seller/user — deferred, see `PROJECT_CHARTER.md`)
* Begin **10.6 Frontend Testing**
* After finishing 10.6 proceed to **10.7 Security & Cross-Feature Testing**