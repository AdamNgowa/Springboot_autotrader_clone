package com.autotrader.backend.config.securityConfig;

import com.autotrader.backend.security.CustomUserDetailsService;
import com.autotrader.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration means this class contains configuration that should be executed when the application starts.
// Configuration acts like a blueprint. Without this annotation, Spring would completely ignore this class.
@Configuration
public class SecurityConfig {

    // 1. DECLARING PERMANENT SLOTS (DEPENDENCIES)
    // These 'final' fields guarantee that once these dependencies are set, they can never be changed or be null.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    // 2. CONSTRUCTOR INJECTION
    // Spring looks at this constructor, finds the instances of these classes in its memory container,
    // and automatically passes them in as temporary incoming variables.
    // 'this.permanentVar = incomingTempVar' assigns the incoming tool into our permanent class slot.
    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // 3. PASSWORD ENCODER BEAN
    // BCryptPasswordEncoder belongs to an external library, so we can't put @Component on its source code.
    // By using @Bean, we manually create it here *once*, and hand it over to Spring to manage globally.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 4. AUTHENTICATION PROVIDER BEAN
    // This sets up the 'Security Manager' sitting inside the checkpoint booth.
    // Its entire job is to process login attempts by fetching user profiles and verifying passwords.
    @Bean
    public AuthenticationProvider authenticationProvider() {

        // Create a specific type of manager (DAO Provider) that uses a database to verify credentials.
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        // Give the manager access to the "Database Records Office".
        // This tells the manager exactly which service to call to fetch a user's real profile from the database.
        provider.setUserDetailsService(userDetailsService);

        // Give the manager the "Password Verification Machine" (BCrypt).
        // Since passwords in the database are encrypted gibberish, the manager uses this to safely check if
        // the incoming password matches the secure hash in the database.
        provider.setPasswordEncoder(passwordEncoder());

        // Hand the fully equipped manager back to Spring so it can be plugged into the security filter chain.
        return provider;
    }

    // 5. THE SECURITY FILTER CHAIN (THE CHECKPOINT ARCHITECTURE)
    // Every single HTTP request coming into our server must pass through these specific rules.
    @Bean
    public SecurityFilterChain securityFilterChain(
            /*
             HttpContext/HttpSecurity is a tool provided by Spring Security that allows us
             to build our specific security rules.

             HttpSecurity.build() and related configuration methods can throw checked exceptions,
             so Java requires the method signature to declare them
             */
            HttpSecurity http) throws Exception {

        http
                // Disable Cross-Site Request Forgery (CSRF)
                /*
                 CSRF protection tracks session cookies on traditional websites. Because this is a
                 modern API using stateless JWT tokens, our API does not rely on server-side sessions for authentication., making CSRF protection unnecessary.
                 Disabling it allows us to make POST/PUT requests seamlessly.
                 */
                .csrf(csrf -> csrf.disable())

                // Make the Application Stateless
                /*
                 By default, servers love to create a "Session ID" for logged-in users and store it in server memory.
                 Setting this to STATELESS tells Spring: "Never save user sessions. Every incoming request must
                 stand on its own and bring its own credentials (the JWT token)."
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                // Configure Authorization Rules (Who can access what URLs)
                // 1. Open up the rulebook on who is allowed to see what URLs
                .authorizeHttpRequests(auth -> auth
                        /*
                         2. Public Exceptions:
                         - .requestMatchers().permitAll().any().authenticated() essentially allows anyone to access those routes
                         - Originally only had auth/register and auth/login
                         If someone wants to sign up (/auth/register) or log in (/auth/login), they obviously
                         don't have a token or account yet. This tells Spring: "Let absolutely anyone (permitAll())
                         hit these two specific URLs without requiring any security clearance."
                         */
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",

                                //OpenAPI specification endpoint
                                "/v3/api-docs/**",

                                //Swagger UI static resources
                                "/swagger-ui/**",

                                //Compatibility endpoint
                                "/swagger-ui.html",

                                "/uploads/**"
                        ).permitAll()

                        /*
                         3. Global Lockdown:
                         This closes the loop on your authorization rules. For any other request besides
                         registration and login, the user MUST be authenticated (logged in), or they get a
                         401 Unauthorized error.
                         */
                        .anyRequest().authenticated()
                )

                // Wire in the Authentication Provider Manager
                // Tells the filter chain to use the manager bean we configured above when processing logins.
                .authenticationProvider(authenticationProvider())

                // Put our Custom Bouncer at the front of the line
                /*
                 Spring Security has a standard bouncer that checks raw usernames/passwords (UsernamePasswordAuthenticationFilter).
                 We tell Spring to position our 'jwtAuthenticationFilter' BEFORE that standard check. If our filter
                 finds a valid JWT token, it authenticates the user instantly, bypassing the password check entirely.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        // Finalize the blueprint and build the functioning security rulebook object.
        return http.build();
    }
}