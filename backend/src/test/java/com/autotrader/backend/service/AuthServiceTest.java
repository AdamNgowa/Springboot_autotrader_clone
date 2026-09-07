package com.autotrader.backend.service;

import com.autotrader.backend.dto.auth.AuthResponse;
import com.autotrader.backend.dto.auth.LoginRequest;
import com.autotrader.backend.dto.auth.RegisterRequest;
import com.autotrader.backend.entity.Enums.UserRole;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.exception.EmailAlreadyExistsException;
import com.autotrader.backend.exception.InvalidCredentialsException;
import com.autotrader.backend.repository.UserRepository;
import com.autotrader.backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    /*
     * The repository is mocked because this is a unit test.
     * We want to test AuthService's decisions without connecting
     * to the real database.
     */
    @Mock
    private UserRepository userRepository;

    /*
     * PasswordEncoder is mocked so the test does not depend on the
     * BCrypt implementation. We control exactly what encoded password
     * the service receives.
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /*
     * JwtService is mocked because JWT creation is not what this
     * unit test is responsible for verifying.
     */
    @Mock
    private JwtService jwtService;

    /*
     * Mockito creates the real AuthService and injects the three
     * mocked dependencies into its constructor.
     */
    @InjectMocks
    private AuthService authService;

    /*
     * Successful registration:
     *
     * - email does not already exist
     * - password is encoded
     * - User is saved
     * - USER role is assigned
     * - JWT is generated
     * - AuthResponse containing the token is returned
     */
    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequest request = new RegisterRequest();

        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPhoneNumber("0712345678");
        request.setPassword("password123");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(jwtService.generateToken("john@example.com"))
                .thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        /*
            - Usually verify(userRepository).save() only tells us that userRepository's save method was called.
            - However, userCaptor.capture() tells Mockito to capture the actual argument passed into save()
         */
        verify(userRepository).save(userCaptor.capture());
        /*
            - Then userCaptor.getValue() retrieves the actual captured object.
         */
        User savedUser = userCaptor.getValue();
        /*
            - Now we can inspect it using assertEquals()
            - assertEquals(expectedValue, testingValue) quite literally checks if the two values passed into it are equal.
            - Test passes when the two values are equivalent.
            - This is possible because of ArgumentCaptor<T>
         */

        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("0712345678", savedUser.getPhoneNumber());
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals(UserRole.USER, savedUser.getRole());

        assertEquals("jwt-token", response.getToken());

        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).encode("password123");
        verify(jwtService).generateToken("john@example.com");
    }

    /*
     * Registration must be rejected when the email is already
     * associated with an existing account.
     *
     * No new User should be saved, no password should be encoded,
     * and no JWT should be generated.
     */
    @Test
    void shouldThrowExceptionWhenRegisteringExistingEmail() {

        //Create a new register request and populate it with setters
        RegisterRequest request = new RegisterRequest();

        request.setEmail("existing@example.com");
        request.setPassword("password123");

        //Create a new User and populate it with setter
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");

        //Mock execution of userRepository.findByEmail and pretend to return existingUser
        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(existingUser));

        //authService.register(request) must throw EmailAlreadyExistsException.
        //It's a fail if no exception is thrown, or if a different exception type is thrown.
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );

        //Verify that userRepository's findByEmail was executed
        verify(userRepository).findByEmail("existing@example.com");

        /*
            - verify that no other interactions with userRepository happened
            - verify that passwordEncoder and jwtService were never executed.
         */
        verifyNoMoreInteractions(userRepository);

        verifyNoInteractions(passwordEncoder, jwtService);
    }

    /*
     * Successful login:
     *
     * - user exists
     * - password matches
     * - JWT is generated
     * - token is returned
     */
    @Test
    void shouldLoginUserSuccessfully() {

        //Create a new login request and populate it with setters
        LoginRequest request = new LoginRequest();

        request.setEmail("john@example.com");
        request.setPassword("password123");

        //Create a User that simulates what would be stored in the database,
        //with an already-encoded password (this is what findByEmail would return)
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encoded-password");

        //Mock execution of userRepository.findByEmail and pretend the user was found
        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        //Mock passwordEncoder.matches so it pretends the raw password
        //matches the stored encoded password, without running real BCrypt logic
        when(passwordEncoder.matches(
                "password123",
                "encoded-password"
        )).thenReturn(true);

        //Mock jwtService so it returns a fixed token instead of generating a real JWT
        when(jwtService.generateToken("john@example.com"))
                .thenReturn("jwt-token");

        //Run the actual method under test
        AuthResponse response = authService.login(request);

        //Check that the token returned in the response is the one jwtService produced
        assertEquals("jwt-token", response.getToken());

        //Verify each mocked dependency was actually called with the expected arguments
        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches(
                "password123",
                "encoded-password"
        );
        verify(jwtService).generateToken("john@example.com");
    }

    /*
     * Login must fail when no User exists for the supplied email.
     *
     * Because authentication fails during the repository lookup,
     * password verification and JWT generation must never occur.
     */
    @Test
    void shouldThrowExceptionWhenLoginUserDoesNotExist() {

        //Create a new login request and populate it with setters
        LoginRequest request = new LoginRequest();

        request.setEmail("missing@example.com");
        request.setPassword("password123");

        //Mock execution of userRepository.findByEmail and pretend no user was found
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        //authService.login(request) must throw InvalidCredentialsException.
        //It's a fail if no exception is thrown, or if a different exception type is thrown.
        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        //Verify that userRepository's findByEmail was executed
        verify(userRepository).findByEmail("missing@example.com");

        //Verify that passwordEncoder and jwtService were never executed,
        //since there's no point checking a password or generating a token
        //for a user that doesn't exist
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    /*
     * Login must fail when the User exists but the supplied password
     * does not match the stored encoded password.
     *
     * JWT generation must not occur because authentication failed.
     */
    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {

        //Create a new login request and populate it with setters
        LoginRequest request = new LoginRequest();

        request.setEmail("john@example.com");
        request.setPassword("wrong-password");

        //Create a User that simulates what would be stored in the database
        User user = new User();

        user.setEmail("john@example.com");
        user.setPassword("encoded-password");

        //Mock execution of userRepository.findByEmail and pretend the user was found
        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        //Mock passwordEncoder.matches so it pretends the raw password
        //does NOT match the stored encoded password
        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-password"
        )).thenReturn(false);

        //authService.login(request) must throw InvalidCredentialsException.
        //It's a fail if no exception is thrown, or if a different exception type is thrown.
        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        //Verify that findByEmail and matches were both actually called
        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches(
                "wrong-password",
                "encoded-password"
        );

        //Verify jwtService was never called, since authentication failed
        //before a token could be generated
        verifyNoInteractions(jwtService);
    }
}