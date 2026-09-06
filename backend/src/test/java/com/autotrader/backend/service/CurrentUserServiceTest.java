package com.autotrader.backend.service;

import com.autotrader.backend.entity.User;
import com.autotrader.backend.exception.AuthenticatedUserNotFoundException;
import com.autotrader.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/*
 * Unit tests for CurrentUserService.
 *
 * The service depends on two external sources of state:
 *
 * 1. Spring Security's SecurityContextHolder, which tells us who is
 *    currently authenticated.
 *
 * 2. UserRepository, which retrieves the corresponding User entity
 *    from the database.
 *
 * In a unit test we do not start the Spring application or connect to
 * the database. Instead, we control the SecurityContext manually and
 * replace UserRepository with a Mockito mock.
 */
@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    /*
     * Mockito creates a fake UserRepository for each test.
     *
     * This prevents the unit test from making real database calls.
     * Individual tests can define exactly what the repository should
     * return using Mockito's when(...).thenReturn(...) syntax.
     */
    @Mock
    private UserRepository userRepository;

    /*
     * Mockito creates the real CurrentUserService and injects the
     * mocked UserRepository into its constructor.
     *
     * This is important: we are testing the actual service logic,
     * not a mocked CurrentUserService.
     */
    @InjectMocks
    private CurrentUserService currentUserService;

    /*
     * SecurityContextHolder maintains authentication state.
     *
     * Because that state can survive between tests, every test must
     * leave the security context clean so that one test cannot
     * accidentally influence another test.
     */
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /*
     * Verifies the normal successful path:
     *
     * authenticated user
     *       ↓
     * email extracted from Authentication
     *       ↓
     * repository finds User
     *       ↓
     * User is returned
     */
    @Test
    void shouldReturnAuthenticatedUser() {
        User expectedUser = mock(User.class);

        // Simulate Spring Security having authenticated this email.
        setAuthenticatedUser("user@example.com");

        /*
         * Stub the repository.
         *
         * We are telling Mockito:
         * "If CurrentUserService asks for this email, pretend the
         * database returned expectedUser."
         */
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(expectedUser));

        // Execute the real production service method.
        User actualUser = currentUserService.getAuthenticatedUser();

        /*
         * assertSame checks object identity.
         *
         * The service should return the exact User object supplied by
         * the repository rather than creating or replacing it.
         */
        assertSame(expectedUser, actualUser);

        /*
         * Verify that the service actually looked up the authenticated
         * user's email in the repository.
         */
        verify(userRepository).findByEmail("user@example.com");
    }

    /*
     * Verifies the case where there is no Authentication object at all.
     *
     * Expected flow:
     *
     * SecurityContext
     *       ↓
     * no Authentication
     *       ↓
     * AuthenticatedUserNotFoundException
     *
     * The repository should never be accessed because there is no
     * authenticated identity to look up.
     */
    @Test
    void shouldThrowExceptionWhenAuthenticationIsMissing() {
        SecurityContextHolder.clearContext();

        assertThrows(
                AuthenticatedUserNotFoundException.class,
                () -> currentUserService.getAuthenticatedUser()
        );

        // Authentication failed before a repository lookup was necessary.
        verifyNoInteractions(userRepository);
    }

    /*
     * Verifies the case where an Authentication object exists but
     * Spring Security considers it unauthenticated.
     *
     * This is different from the previous test:
     *
     * Test 1: Authentication == null
     * Test 2: Authentication exists, but isAuthenticated() == false
     */
    @Test
    void shouldThrowExceptionWhenAuthenticationIsNotAuthenticated() {

        /*
         * The two-argument UsernamePasswordAuthenticationToken
         * constructor creates an unauthenticated token.
         */
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "user@example.com",
                        null
                );

        // Place our simulated authentication into Spring Security's context.
        setAuthentication(authentication);

        assertThrows(
                AuthenticatedUserNotFoundException.class,
                () -> currentUserService.getAuthenticatedUser()
        );

        // The service should reject the request before querying the database.
        verifyNoInteractions(userRepository);
    }

    /*
     * Verifies the case where authentication itself is valid, but the
     * authenticated email cannot be found in our application database.
     *
     * This protects against a mismatch between Spring Security's
     * authentication state and our application's User records.
     */
    @Test
    void shouldThrowExceptionWhenAuthenticatedUserDoesNotExistInDatabase() {

        // Simulate a valid authentication for an email not present in the DB.
        setAuthenticatedUser("missing@example.com");

        /*
         * Optional.empty() represents a repository lookup where no
         * matching User exists.
         */
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                AuthenticatedUserNotFoundException.class,
                () -> currentUserService.getAuthenticatedUser()
        );

        /*
         * Unlike the earlier failure cases, the repository SHOULD have
         * been called because authentication was valid.
         */
        verify(userRepository).findByEmail("missing@example.com");
    }

    /*
     * Convenience method for creating an authenticated
     * UsernamePasswordAuthenticationToken.
     *
     * The three-argument constructor is used because supplying the
     * authorities collection represents an authenticated token.
     *
     * We are not testing authorization roles in this unit test, so an
     * empty authority list is sufficient.
     */
    private void setAuthenticatedUser(String email) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of()
                );

        setAuthentication(authentication);
    }

    /*
     * Installs the supplied Authentication into the current
     * SecurityContext.
     *
     * This manually simulates the state that Spring Security normally
     * establishes after successful authentication during an HTTP request.
     */
    private void setAuthentication(
            UsernamePasswordAuthenticationToken authentication) {

        // Create a fresh context rather than relying on previous test state.
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Associate our simulated Authentication with that context.
        context.setAuthentication(authentication);

        // Make this context the current security context for the test thread.
        SecurityContextHolder.setContext(context);
    }
}