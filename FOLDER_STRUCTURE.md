autotrader
├── .idea/
├── backend [com.autotrader.backend]/
│ ├── .gradle/
│ ├── build/
│ ├── gradle/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/
│ │ │ │ └── com.autotrader.backend/
│ │ │ │ ├── config/
│ │ │ │ │ ├── openapi/
│ │ │ │ │ │ └── OpenApiConfig.java
│ │ │ │ │ ├── securityConfig/
│ │ │ │ │ │ └── SecurityConfig.java
│ │ │ │ │ └── webconfig/
│ │ │ │ │ └── WebConfig.java
│ │ │ │ ├── controller/
│ │ │ │ │ ├── AuthController.java
│ │ │ │ │ ├── ImageController.java
│ │ │ │ │ └── VehicleListingController.java
│ │ │ │ ├── dto/
│ │ │ │ │ ├── auth/
│ │ │ │ │ │ ├── AuthResponse.java
│ │ │ │ │ │ ├── LoginRequest.java
│ │ │ │ │ │ └── RegisterRequest.java
│ │ │ │ │ ├── error/
│ │ │ │ │ │ ├── ErrorResponse.java
│ │ │ │ │ │ └── ValidationError.java
│ │ │ │ │ ├── image/
│ │ │ │ │ │ └── ImageResponse.java
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
│ │ │ │ │ ├── User.java
│ │ │ │ │ ├── VehicleImage.java
│ │ │ │ │ └── VehicleListing.java
│ │ │ │ ├── exception/
│ │ │ │ │ ├── AuthenticatedUserNotFoundException.java
│ │ │ │ │ ├── EmailAlreadyExistsException.java
│ │ │ │ │ ├── GlobalExceptionHandler.java
│ │ │ │ │ ├── InvalidCredentialsException.java
│ │ │ │ │ ├── ListingNotFoundException.java
│ │ │ │ │ └── UnauthorizedListingAccessException.java
│ │ │ │ ├── mapper/
│ │ │ │ │ └── VehicleListingMapper.java
│ │ │ │ ├── repository/
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
│ │ │ │ │ ├── FileStorageService.java
│ │ │ │ │ ├── ImageService.java
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
│ ├── node_modules/
│ ├── public/
│ ├── src/
│ │ ├── api/
│ │ │ ├── apiClient.js
│ │ │ └── authApi.js
│ │ ├── assets/
│ │ ├── auth/
│ │ │ └── authStorage.js
│ │ ├── context/
│ │ │ └── AuthContext.jsx
│ │ ├── hooks/
│ │ │ └── useAuth.js
│ │ ├── pages/
│ │ │ ├── HomePage.jsx
│ │ │ └── LoginPage.jsx
│ │ ├── routes/
│ │ │ └── AppRouter.jsx
│ │ ├── App.css
│ │ ├── App.jsx
│ │ ├── index.css
│ │ └── main.jsx
│ ├── .gitignore
│ ├── eslint.config.js
│ ├── index.html
│ ├── package.json
│ ├── package-lock.json
│ ├── README.md
│ └── vite.config.js
├── uploads/
├── CURRENT_STATUS.md
├── FOLDER_STRUCTURE.md
├── PROJECT_CHARTER.md
└── STATUS_REPORT_PROMPT.md
