# FOLDER_STRUCTURE.md

autotrader/
│
├── .github/
│   └── workflows/
│       ├── ci.yml
│       └── qodana_code_quality.yml
│
├── .idea/
│
├── backend/
│   ├── .gradle/
│   ├── build/
│   ├── gradle/
│   │
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── autotrader/
│   │   │   │           └── backend/
│   │   │   │               │
│   │   │   │               ├── config/
│   │   │   │               │   ├── cloudinary/
│   │   │   │               │   │   └── CloudinaryConfig.java
│   │   │   │               │   │
│   │   │   │               │   ├── openapi/
│   │   │   │               │   │   └── OpenApiConfig.java
│   │   │   │               │   │
│   │   │   │               │   ├── securityConfig/
│   │   │   │               │   │   └── SecurityConfig.java
│   │   │   │               │   │
│   │   │   │               │   └── webconfig/
│   │   │   │               │       └── WebConfig.java
│   │   │   │               │
│   │   │   │               ├── controller/
│   │   │   │               │   ├── AuthController.java
│   │   │   │               │   ├── ConversationController.java
│   │   │   │               │   ├── FavoriteController.java
│   │   │   │               │   ├── ImageController.java
│   │   │   │               │   ├── MessageController.java
│   │   │   │               │   ├── UserController.java
│   │   │   │               │   └── VehicleListingController.java
│   │   │   │               │
│   │   │   │               ├── dto/
│   │   │   │               │   ├── auth/
│   │   │   │               │   ├── error/
│   │   │   │               │   ├── favorite/
│   │   │   │               │   ├── image/
│   │   │   │               │   ├── messaging/
│   │   │   │               │   ├── user/
│   │   │   │               │   └── vehicleListing/
│   │   │   │               │
│   │   │   │               ├── entity/
│   │   │   │               │   ├── Enums/
│   │   │   │               │   │   ├── BodyType.java
│   │   │   │               │   │   ├── FuelType.java
│   │   │   │               │   │   ├── ListingStatus.java
│   │   │   │               │   │   ├── Transmission.java
│   │   │   │               │   │   └── UserRole.java
│   │   │   │               │   │
│   │   │   │               │   ├── Conversation.java
│   │   │   │               │   ├── Favorite.java
│   │   │   │               │   ├── Message.java
│   │   │   │               │   ├── User.java
│   │   │   │               │   ├── VehicleImage.java
│   │   │   │               │   └── VehicleListing.java
│   │   │   │               │
│   │   │   │               ├── exception/
│   │   │   │               │   ├── AuthenticatedUserNotFoundException.java
│   │   │   │               │   ├── EmailAlreadyExistsException.java
│   │   │   │               │   ├── GlobalExceptionHandler.java
│   │   │   │               │   ├── ImageNotFoundException.java
│   │   │   │               │   ├── InvalidCredentialsException.java
│   │   │   │               │   ├── ListingNotFoundException.java
│   │   │   │               │   ├── UnauthorizedConversationAccessException.java
│   │   │   │               │   ├── UnauthorizedListingAccessException.java
│   │   │   │               │   └── UserNotFoundException.java
│   │   │   │               │
│   │   │   │               ├── mapper/
│   │   │   │               │   ├── ConversationMapper.java
│   │   │   │               │   ├── FavoriteMapper.java
│   │   │   │               │   ├── ImageMapper.java
│   │   │   │               │   ├── MessageMapper.java
│   │   │   │               │   ├── UserMapper.java
│   │   │   │               │   └── VehicleListingMapper.java
│   │   │   │               │
│   │   │   │               ├── repository/
│   │   │   │               │   ├── ConversationRepository.java
│   │   │   │               │   ├── FavoriteRepository.java
│   │   │   │               │   ├── MessageRepository.java
│   │   │   │               │   ├── UserRepository.java
│   │   │   │               │   ├── VehicleImageRepository.java
│   │   │   │               │   └── VehicleListingRepository.java
│   │   │   │               │
│   │   │   │               ├── security/
│   │   │   │               │   ├── CustomUserDetailsService.java
│   │   │   │               │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │               │   ├── JwtAuthenticationFilter.java
│   │   │   │               │   └── JwtService.java
│   │   │   │               │
│   │   │   │               ├── service/
│   │   │   │               │   ├── AuthService.java
│   │   │   │               │   ├── ConversationService.java
│   │   │   │               │   ├── CurrentUserService.java
│   │   │   │               │   ├── FavoriteService.java
│   │   │   │               │   ├── FileStorageService.java
│   │   │   │               │   ├── LocalFileStorageService.java
│   │   │   │               │   ├── CloudinaryFileStorageService.java
│   │   │   │               │   ├── ImageService.java
│   │   │   │               │   ├── MessageService.java
│   │   │   │               │   ├── UserService.java
│   │   │   │               │   └── VehicleListingService.java
│   │   │   │               │
│   │   │   │               ├── specification/
│   │   │   │               │   ├── VehicleListingSpecification.java
│   │   │   │               │   └── VehicleListingSpecificationBuilder.java
│   │   │   │               │
│   │   │   │               └── BackendApplication.java
│   │   │
│   │   │   ├── resources/
│   │   │   │   ├── db/
│   │   │   │   │   └── migration/
│   │   │   │   │       └── V1__initial_schema.sql
│   │   │   │   │
│   │   │   │   ├── application.properties
│   │   │   │   └── application-prod.properties
│   │   │
│   │   └── test/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── autotrader/
│   │       │           └── backend/
│   │       │               │
│   │       │               ├── controller/
│   │       │               │   ├── AuthControllerTest.java
│   │       │               │   ├── ConversationControllerTest.java
│   │       │               │   ├── FavoriteControllerTest.java
│   │       │               │   ├── ImageControllerTest.java
│   │       │               │   ├── MessageControllerTest.java
│   │       │               │   ├── UserControllerTest.java
│   │       │               │   └── VehicleListingControllerTest.java
│   │       │               │
│   │       │               ├── exception/
│   │       │               │   └── GlobalExceptionHandlerTest.java
│   │       │               │
│   │       │               ├── integration/
│   │       │               │   ├── AuthenticationIntegrationTest.java
│   │       │               │   ├── FavoritesIntegrationTest.java
│   │       │               │   ├── ImageManagementIntegrationTest.java
│   │       │               │   ├── IntegrationTestSupport.java
│   │       │               │   ├── MessagingIntegrationTest.java
│   │       │               │   ├── SecurityBoundaryIntegrationTest.java
│   │       │               │   ├── SellerProfileIntegrationTest.java
│   │       │               │   └── VehicleListingIntegrationTest.java
│   │       │               │
│   │       │               ├── mapper/
│   │       │               │   └── ImageMapperTest.java
│   │       │               │
│   │       │               ├── repository/
│   │       │               │   ├── ConversationRepositoryTest.java
│   │       │               │   ├── FavoriteRepositoryTest.java
│   │       │               │   ├── MessageRepositoryTest.java
│   │       │               │   ├── UserRepositoryTest.java
│   │       │               │   ├── VehicleImageRepositoryTest.java
│   │       │               │   └── VehicleListingRepositoryTest.java
│   │       │               │
│   │       │               ├── service/
│   │       │               │   ├── AuthServiceTest.java
│   │       │               │   ├── ConversationServiceTest.java
│   │       │               │   ├── CurrentUserServiceTest.java
│   │       │               │   ├── FavoriteServiceTest.java
│   │       │               │   ├── ImageServiceTest.java
│   │       │               │   ├── MessageServiceTest.java
│   │       │               │   ├── UserServiceTest.java
│   │       │               │   └── VehicleListingServiceTest.java
│   │       │               │
│   │       │               ├── BackendApplicationTests.java
│   │       │               └── TestcontainersConfiguration.java
│   │       │
│   │       └── resources/
│   │           └── application.properties
│   │
│   ├── uploads/
│   ├── .dockerignore
│   ├── .gitattributes
│   ├── .gitignore
│   ├── Dockerfile
│   ├── build.gradle
│   ├── gradlew
│   ├── gradlew.bat
│   └── settings.gradle
│
├── frontend/
│   ├── dist/
│   ├── node_modules/
│   ├── public/
│   │
│   ├── src/
│   │   ├── api/
│   │   │   ├── apiClient.js
│   │   │   ├── authApi.js
│   │   │   ├── favoriteApi.js
│   │   │   ├── imageApi.js
│   │   │   ├── listingApi.js
│   │   │   ├── messagingApi.js
│   │   │   ├── sellerApi.js
│   │   │   └── userApi.js
│   │   │
│   │   ├── assets/
│   │   │
│   │   ├── auth/
│   │   │   └── authStorage.js
│   │   │
│   │   ├── components/
│   │   │   ├── AuthCard.jsx
│   │   │   ├── FloatingMessagesButton.jsx
│   │   │   ├── FormField.jsx
│   │   │   ├── GalleryArrow.jsx
│   │   │   ├── GuestOnlyRoute.jsx
│   │   │   ├── ImageGallery.jsx
│   │   │   ├── ImageLightbox.jsx
│   │   │   ├── ImageManager.jsx
│   │   │   ├── ListingCard.jsx
│   │   │   ├── ListingForm.jsx
│   │   │   ├── ListingGrid.jsx
│   │   │   ├── ListingGridSkeleton.jsx
│   │   │   ├── ListingManagement.jsx
│   │   │   ├── ManagedImageCard.jsx
│   │   │   ├── MessageBubble.jsx
│   │   │   ├── MessageComposer.jsx
│   │   │   ├── Navbar.jsx
│   │   │   ├── Notice.jsx
│   │   │   ├── Pagination.jsx
│   │   │   ├── PendingImageCard.jsx
│   │   │   ├── ProtectedRoute.jsx
│   │   │   ├── SearchFilters.jsx
│   │   │   ├── SellerCard.jsx
│   │   │   └── SpecificationCard.jsx
│   │   │
│   │   ├── constants/
│   │   │   └── listingEnums.js
│   │   │
│   │   ├── context/
│   │   │   └── AuthContext.jsx
│   │   │
│   │   ├── hooks/
│   │   │   └── useAuth.js
│   │   │
│   │   ├── pages/
│   │   │   ├── ConversationPage.jsx
│   │   │   ├── ConversationsPage.jsx
│   │   │   ├── CreateListingPage.jsx
│   │   │   ├── DashboardPage.jsx
│   │   │   ├── EditListingPage.jsx
│   │   │   ├── FavoritesPage.jsx
│   │   │   ├── HomePage.jsx
│   │   │   ├── ListingDetailsPage.jsx
│   │   │   ├── LoginPage.jsx
│   │   │   ├── MyListingsPage.jsx
│   │   │   ├── RegisterPage.jsx
│   │   │   └── SellerProfilePage.jsx
│   │   │
│   │   ├── routes/
│   │   │   └── AppRouter.jsx
│   │   │
│   │   ├── utils/
│   │   │   ├── getImageUrl.js
│   │   │   ├── validateAuth.js
│   │   │   └── validateListing.js
│   │   │
│   │   ├── App.css
│   │   ├── App.jsx
│   │   ├── index.css
│   │   └── main.jsx
│   │
│   ├── tests/
│   │   ├── AuthContext.test.jsx
│   │   ├── getImageUrl.test.js
│   │   ├── GuestOnlyRoute.test.jsx
│   │   ├── LoginPage.test.jsx
│   │   ├── ProtectedRoute.test.jsx
│   │   ├── setup.js
│   │   ├── setup.test.js
│   │   ├── validateAuth.test.js
│   │   └── validateListing.test.js
│   │
│   ├── .dockerignore
│   ├── .gitignore
│   ├── eslint.config.js
│   ├── Dockerfile
│   ├── index.html
│   ├── nginx.conf
│   ├── package-lock.json
│   ├── package.json
│   ├── README.md
│   ├── vercel.json
│   └── vite.config.js
│
├── uploads/
│
├── .env
├── .env.example
├── .gitignore
├── CURRENT_STATUS.md
├── PRODUCTION_DIAGNOSTICS.md
├── dev.ps1
├── docker-compose.yml
├── FOLDER_STRUCTURE.md
├── PROJECT_CHARTER.md
└── STATUS_REPORT_PROMPT.md