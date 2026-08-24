autotrader/
├── backend/
│ ├── .gradle/
│ ├── bin/
│ ├── build/
│ ├── gradle/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/autotrader/backend/
│ │ │ │ ├── config/
│ │ │ │ │ ├── openapi/
│ │ │ │ │ │ └── OpenApiConfig.java
│ │ │ │ │ ├── securityConfig/
│ │ │ │ │ │ └── SecurityConfig.java
│ │ │ │ │ └── webconfig/
│ │ │ │ │ └── WebConfig.java
│ │ │ │ ├── controller/
│ │ │ │ │ ├── AuthController.java
│ │ │ │ │ ├── FavoriteController.java
│ │ │ │ │ ├── ImageController.java
│ │ │ │ │ ├── UserController.java
│ │ │ │ │ └── VehicleListingController.java
│ │ │ │ ├── dto/
│ │ │ │ │ ├── auth/
│ │ │ │ │ │ ├── AuthResponse.java
│ │ │ │ │ │ ├── LoginRequest.java
│ │ │ │ │ │ └── RegisterRequest.java
│ │ │ │ │ ├── error/
│ │ │ │ │ │ ├── ErrorResponse.java
│ │ │ │ │ │ └── ValidationError.java
│ │ │ │ │ ├── favorite/
│ │ │ │ │ │ ├── FavoriteResponse.java
│ │ │ │ │ │ └── FavoriteStatusResponse.java
│ │ │ │ │ ├── image/
│ │ │ │ │ │ ├── ImageResponse.java
│ │ │ │ │ │ └── ReorderImagesRequest.java
│ │ │ │ │ ├── user/
│ │ │ │ │ │ ├── SellerResponse.java
│ │ │ │ │ │ └── UserResponse.java
│ │ │ │ │ └── vehicleListing/
│ │ │ │ │ ├── CreateListingRequest.java
│ │ │ │ │ ├── UpdateListingRequest.java
│ │ │ │ │ ├── VehicleListingResponse.java
│ │ │ │ │ └── VehicleListingSearchCriteria.java
│ │ │ │ ├── entity/
│ │ │ │ │ ├── Enums/
│ │ │ │ │ │ ├── BodyType.java
│ │ │ │ │ │ ├── FuelType.java
│ │ │ │ │ │ ├── ListingStatus.java
│ │ │ │ │ │ ├── Transmission.java
│ │ │ │ │ │ └── UserRole.java
│ │ │ │ │ ├── Favorite.java
│ │ │ │ │ ├── User.java
│ │ │ │ │ ├── VehicleImage.java
│ │ │ │ │ └── VehicleListing.java
│ │ │ │ ├── exception/
│ │ │ │ │ ├── AuthenticatedUserNotFoundException.java
│ │ │ │ │ ├── EmailAlreadyExistsException.java
│ │ │ │ │ ├── GlobalExceptionHandler.java
│ │ │ │ │ ├── ImageNotFoundException.java
│ │ │ │ │ ├── InvalidCredentialsException.java
│ │ │ │ │ ├── ListingNotFoundException.java
│ │ │ │ │ ├── UnauthorizedListingAccessException.java
│ │ │ │ │ └── UserNotFoundException.java
│ │ │ │ ├── mapper/
│ │ │ │ │ ├── FavoriteMapper.java
│ │ │ │ │ ├── ImageMapper.java
│ │ │ │ │ ├── UserMapper.java
│ │ │ │ │ └── VehicleListingMapper.java
│ │ │ │ ├── repository/
│ │ │ │ │ ├── FavoriteRepository.java
│ │ │ │ │ ├── UserRepository.java
│ │ │ │ │ ├── VehicleImageRepository.java
│ │ │ │ │ └── VehicleListingRepository.java
│ │ │ │ ├── security/
│ │ │ │ │ ├── CustomUserDetailsService.java
│ │ │ │ │ ├── JwtAuthenticationFilter.java
│ │ │ │ │ └── JwtService.java
│ │ │ │ ├── service/
│ │ │ │ │ ├── AuthService.java
│ │ │ │ │ ├── CurrentUserService.java
│ │ │ │ │ ├── FavoriteService.java
│ │ │ │ │ ├── FileStorageService.java
│ │ │ │ │ ├── ImageService.java
│ │ │ │ │ ├── UserService.java
│ │ │ │ │ └── VehicleListingService.java
│ │ │ │ ├── specification/
│ │ │ │ │ ├── VehicleListingSpecification.java
│ │ │ │ │ └── VehicleListingSpecificationBuilder.java
│ │ │ │ └── BackendApplication.java
│ │ │ └── resources/
│ │ └── test/
│ ├── .gitattributes
│ ├── .gitignore
│ ├── build.gradle
│ ├── gradlew
│ ├── gradlew.bat
│ ├── HELP.md
│ └── settings.gradle
├── frontend/
│ ├── dist/
│ ├── node_modules/
│ ├── public/
│ ├── src/
│ │ ├── api/
│ │ │ ├── apiClient.js
│ │ │ ├── authApi.js
│ │ │ ├── favoriteApi.js
│ │ │ ├── imageApi.js
│ │ │ ├── listingApi.js
│ │ │ └── userApi.js
│ │ ├── assets/
│ │ ├── auth/
│ │ │ └── authStorage.js
│ │ ├── components/
│ │ │ ├── GuestOnlyRoute.jsx
│ │ │ ├── ImageGallery.jsx
│ │ │ ├── ImageManager.jsx
│ │ │ ├── ListingCard.jsx
│ │ │ ├── ListingForm.jsx
│ │ │ ├── Navbar.jsx
│ │ │ ├── ProtectedRoute.jsx
│ │ │ ├── SearchFilters.jsx
│ │ │ └── SpecificationCard.jsx
│ │ ├── constants/
│ │ │ └── listingEnums.js
│ │ ├── context/
│ │ │ └── AuthContext.jsx
│ │ ├── hooks/
│ │ │ └── useAuth.js
│ │ ├── pages/
│ │ │ ├── CreateListingPage.jsx
│ │ │ ├── DashboardPage.jsx
│ │ │ ├── EditListingPage.jsx
│ │ │ ├── FavoritesPage.jsx
│ │ │ ├── HomePage.jsx
│ │ │ ├── ListingDetailsPage.jsx
│ │ │ ├── LoginPage.jsx
│ │ │ ├── MyListingsPage.jsx
│ │ │ └── RegisterPage.jsx
│ │ ├── routes/
│ │ │ └── AppRouter.jsx
│ │ ├── utils/
│ │ │ ├── getImageUrl.js
│ │ │ ├── validateAuth.js
│ │ │ └── validateListing.js
│ │ ├── App.css
│ │ ├── App.jsx
│ │ ├── index.css
│ │ └── main.jsx
│ ├── .gitignore
│ ├── eslint.config.js
│ ├── index.html
│ ├── package-lock.json
│ ├── package.json
│ ├── README.md
│ └── vite.config.js
├── uploads/
├── .gitignore
├── CURRENT_STATUS.md
├── FOLDER_STRUCTURE.md
├── PROJECT_CHARTER.md
└── STATUS_REPORT_PROMPT.md
