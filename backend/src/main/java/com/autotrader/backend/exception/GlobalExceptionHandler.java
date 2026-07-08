package com.autotrader.backend.exception;

import com.autotrader.backend.dto.error.ErrorResponse;
import com.autotrader.backend.dto.error.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response; // Unused Tomcat engine class; safe to remove in production cleanup
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

// @RestControllerAdvice tells Spring Boot that this class is a global "safety net" catcher intercepting all controllers.
// It combines @ControllerAdvice (global scoping) and @ResponseBody (forces all returned objects to convert automatically into JSON strings).
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==========================================
    // 1. BUSINESS RULE HANDLER: DUPLICATE REGISTRATIONS
    // ==========================================

    // Intercepts EmailAlreadyExistsException thrown by AuthService when someone signs up with a taken email
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex,     // The exception object instance containing the message text
            HttpServletRequest request) {       // Provides raw metadata about the network call from the client

        // Build a fresh instance of our custom standardized Error DTO packet
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),                  // Stamp the precise date and time the error occurred
                HttpStatus.CONFLICT.value(),          // Inject numeric HTTP status code 409
                HttpStatus.CONFLICT.getReasonPhrase(),// Inject standard HTTP description string ("Conflict")
                ex.getMessage(),                      // Pull out the string custom message passed inside the Service layer throw statement
                request.getRequestURI()               // Capture the exact API endpoint path URL visited (e.g., "/api/auth/register")
        );

        // Package the Error DTO into a ResponseEntity configuration, enforce HTTP 409 status on the response header, and ship it out
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    // ==========================================
    // 2. BUSINESS RULE HANDLER: BAD LOGINS
    // ==========================================

    // Intercepts InvalidCredentialsException thrown when passwords don't match or email isn't registered
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        // Compile standard error body payload tracking the failed authentication metadata
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),          // Inject numeric HTTP status code 401
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),// Inject standard HTTP description string ("Unauthorized")
                ex.getMessage(),                          // Collects user-friendly text (e.g., "Invalid email or password")
                request.getRequestURI()
        );

        // Deliver network package back with a strict 401 Unauthorized block signature
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    // ==========================================
    // 3. DATABASE RULE HANDLER: MISSING ITEMS
    // ==========================================

    // Intercepts ListingNotFoundException triggered when searching for missing or soft-deleted inventory listings
    @ExceptionHandler(ListingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleListingNotFound(
            ListingNotFoundException ex,
            HttpServletRequest request) {

        // Construct standard error layout identifying resource layout gaps
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),          // Inject numeric HTTP status code 404
                HttpStatus.NOT_FOUND.getReasonPhrase(),// Inject standard HTTP description string ("Not Found")
                ex.getMessage(),                       // Message stating "Listing not found"
                request.getRequestURI()
        );

        // Dispatch back to client with standard 404 resource missing code signature
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    // ==========================================
    // 4. SECURITY RULE HANDLER: ILLEGAL ACCESS REJECTIONS
    // ==========================================

    // Intercepts UnauthorizedListingAccessException triggered when users try to update/delete listings owned by other people
    @ExceptionHandler(UnauthorizedListingAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedListingAccess(
            UnauthorizedListingAccessException ex,
            HttpServletRequest request
    ){
        // Compile standard forbidden notification blocking data access corruption attempts
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),          // Inject numeric HTTP status code 403
                HttpStatus.FORBIDDEN.getReasonPhrase(),// Inject standard HTTP description string ("Forbidden")
                ex.getMessage(),                       // Rejection text alerting user they don't own this data item
                request.getRequestURI()
        );

        // Block processing transmission stream routing with 403 Forbidden header stamp
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }

    // ==========================================
    // 5. SECURITY CONTEXT HANDLER: GHOST USER DETECTION
    // ==========================================

    // Intercepts AuthenticatedUserNotFoundException if a client presents a signed valid JWT token but their account was deleted from the database
    @ExceptionHandler(AuthenticatedUserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticatedUserNotFound(
            AuthenticatedUserNotFoundException ex,
            HttpServletRequest request) {

        // Construct error state clearing out invalid ghost credentials footprint tracking
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),          // Inject numeric HTTP status code 401
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),// Inject standard HTTP description string ("Unauthorized")
                ex.getMessage(),
                request.getRequestURI()
        );

        // Push data response with a clean 401 configuration blocking system interaction mapping access
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    // ==========================================
    // 6. CORE JSR-380 ENGINE HANDLER: DTO VALIDATION CRIMES
    // ==========================================

    // Intercepts MethodArgumentNotValidException which triggers automatically when incoming controller DTO criteria validation checks break down
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        // Step 1: Open the exception container and isolate Spring's core validation report card mapping state (BindingResult)
        List<ValidationError> validationErrors =
                ex.getBindingResult()
                        // Step 2: Grab the internal list of all raw individual FieldError violations compiled by Hibernate Validator
                        .getFieldErrors()
                        // Step 3: Turn the Java List into a sequential Stream process pipeline to map elements
                        .stream()
                        // Step 4: Run a translation process on every single individual Spring FieldError encountered
                        .map(fieldError -> new ValidationError(
                                fieldError.getField(),          // Pull out the invalid DTO field location string name (e.g., "password")
                                fieldError.getDefaultMessage()  // Pull out your custom annotated constraint validation message text (e.g., "Password is required")
                        ))
                        // Step 5: Shut down the translation stream processing pipeline and gather all our clean custom DTO models into a clean immutable Java List
                        .toList();

        // Step 6: Create our final uniform, comprehensive master ErrorResponse instance package
        ErrorResponse error =
                new ErrorResponse(
                        LocalDateTime.now(),                       // Establish timing marker trace metrics
                        HttpStatus.BAD_REQUEST.value(),            // Inject numeric HTTP status code 400
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),  // Inject standard HTTP description string ("Bad Request")
                        "Validation failed",                       // Set explicit root cause notification identifier text
                        request.getRequestURI(),                   // Note down the incoming client route location path
                        validationErrors                           // Feed our custom mapped array containing sub-level field-by-field error reports
                );

        // Step 7: Output final structured JSON model wrapper inside a hard coded HTTP 400 status envelope
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}