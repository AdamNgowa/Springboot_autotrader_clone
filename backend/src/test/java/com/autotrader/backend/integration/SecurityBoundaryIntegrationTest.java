package com.autotrader.backend.integration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.RequestBuilder;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies the security boundary end to end:
 *  - a bad token (garbage, expired, wrong key, deleted user) is treated as ANONYMOUS,
 *    so public endpoints still answer 200 and protected ones answer 401;
 *  - every protected endpoint rejects anonymous callers with 401.
 *
 * Regression tests for the Phase 12 incident where a rotated JWT secret
 * left stale tokens in browsers and public pages started failing.
 */
class SecurityBoundaryIntegrationTest extends IntegrationTestSupport {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // ==========================================
    // BAD TOKENS ON PUBLIC ENDPOINTS -> ANONYMOUS (200)
    // ==========================================

    @Test
    void shouldTreatGarbageTokenAsAnonymousOnPublicEndpoint() throws Exception {
        mockMvc.perform(get("/listings").header(AUTH, "Bearer not-a-jwt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldTreatExpiredTokenAsAnonymousOnPublicEndpoint() throws Exception {
        registerAndGetToken("expired@example.com", "Eve", "Expired");

        mockMvc.perform(get("/listings")
                        .header(AUTH, bearer(expiredToken("expired@example.com"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldTreatTokenSignedWithDifferentSecretAsAnonymousOnPublicEndpoint()
            throws Exception {

        registerAndGetToken("rotated@example.com", "Rob", "Rotated");

        // Simulates a token issued before the JWT secret was rotated.
        SecretKey otherKey = Jwts.SIG.HS256.key().build();
        String staleToken = Jwts.builder()
                .subject("rotated@example.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(otherKey)
                .compact();

        mockMvc.perform(get("/listings").header(AUTH, bearer(staleToken)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldTreatTokenOfDeletedUserAsAnonymousOnPublicEndpoint() throws Exception {
        String token = registerAndGetToken("ghost@example.com", "Gus", "Ghost");

        userRepository.deleteAll();

        mockMvc.perform(get("/listings").header(AUTH, bearer(token)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldServeSinglePublicListingWithGarbageToken() throws Exception {
        Long listingId = createListing(
                registerAndGetToken("seller@example.com", "Sam", "Seller")
        );

        mockMvc.perform(get("/listings/" + listingId)
                        .header(AUTH, "Bearer garbage.token.value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(listingId));
    }

    // ==========================================
    // BAD TOKENS ON PROTECTED ENDPOINTS -> 401
    // ==========================================

    @Test
    void shouldRejectGarbageTokenOnProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/favorites").header(AUTH, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectExpiredTokenOnProtectedEndpoint() throws Exception {
        registerAndGetToken("expired@example.com", "Eve", "Expired");

        mockMvc.perform(get("/favorites")
                        .header(AUTH, bearer(expiredToken("expired@example.com"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAcceptValidTokenOnProtectedEndpoint() throws Exception {
        String token = registerAndGetToken("valid@example.com", "Val", "Valid");

        mockMvc.perform(get("/favorites").header(AUTH, bearer(token)))
                .andExpect(status().isOk());
    }

    // ==========================================
    // ANONYMOUS ACCESS TO PROTECTED ENDPOINTS -> 401
    // ==========================================

    @ParameterizedTest(name = "{0} without a token returns 401")
    @MethodSource("protectedEndpoints")
    void shouldRejectAnonymousAccessToProtectedEndpoint(
            String description,
            RequestBuilder request
    ) throws Exception {

        mockMvc.perform(request).andExpect(status().isUnauthorized());
    }

    static Stream<Arguments> protectedEndpoints() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "car.jpg", "image/jpeg", new byte[]{1, 2, 3}
        );

        return Stream.of(
                Arguments.of("POST /listings",
                        post("/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")),
                Arguments.of("PUT /listings/1",
                        put("/listings/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")),
                Arguments.of("DELETE /listings/1", delete("/listings/1")),
                Arguments.of("POST /listings/1/images",
                        multipart("/listings/1/images").file(file)),
                Arguments.of("PATCH /listings/1/images/1/primary",
                        patch("/listings/1/images/1/primary")),
                Arguments.of("PUT /listings/1/images/order",
                        put("/listings/1/images/order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"imageIds\":[1]}")),
                Arguments.of("DELETE /listings/1/images/1",
                        delete("/listings/1/images/1")),
                Arguments.of("GET /favorites", get("/favorites")),
                Arguments.of("POST /favorites/1", post("/favorites/1")),
                Arguments.of("GET /conversations", get("/conversations")),
                Arguments.of("POST /conversations",
                        post("/conversations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"listingId\":1}")),
                Arguments.of("GET /conversations/1", get("/conversations/1")),
                Arguments.of("GET /conversations/1/messages",
                        get("/conversations/1/messages")),
                Arguments.of("POST /conversations/1/messages",
                        post("/conversations/1/messages")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"content\":\"hi\"}")),
                Arguments.of("GET /users/me", get("/users/me")),
                Arguments.of("PUT /users/me",
                        put("/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")),
                Arguments.of("PUT /users/me/password",
                        put("/users/me/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
        );
    }




    @Test
    void shouldReturn401ForListingsMeWithoutToken() throws Exception {
        mockMvc.perform(get("/listings/me")).andExpect(status().isUnauthorized());
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private String expiredToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis() - 7_200_000))
                .expiration(new Date(System.currentTimeMillis() - 3_600_000))
                .signWith(key)
                .compact();
    }
}
