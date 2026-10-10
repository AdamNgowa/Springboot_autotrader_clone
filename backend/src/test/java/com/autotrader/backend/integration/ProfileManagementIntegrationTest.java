package com.autotrader.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Profile management end to end: update name/phone, change password.
 * Runs against real PostgreSQL, so the "reload the row by id" behaviour of
 * UserService is exercised for real instead of through mocks.
 */
class ProfileManagementIntegrationTest extends IntegrationTestSupport {

    // The password registerAndGetToken() gives every user it creates.
    private static final String PASSWORD = "SecurePassword123!";
    private static final String NEW_PASSWORD = "BrandNewPassword456!";

    // ==========================================
    // PUT /users/me
    // ==========================================

    @Test
    void shouldUpdateProfileAndPersistChanges() throws Exception {
        String token = registerAndGetToken("profile@example.com", "John", "Doe");
        String hashBefore = userRepository.findByEmail("profile@example.com")
                .orElseThrow().getPassword();

        mockMvc.perform(put("/users/me")
                        .header(AUTH, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"  Jane ","lastName":"Smith","phoneNumber":"+254799999999"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.phoneNumber").value("+254799999999"))
                .andExpect(jsonPath("$.email").value("profile@example.com"));

        // The change must be visible to a fresh request, i.e. really persisted.
        mockMvc.perform(get("/users/me").header(AUTH, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.phoneNumber").value("+254799999999"));

        // Updating the profile must never touch the password hash.
        assertThat(userRepository.findByEmail("profile@example.com")
                .orElseThrow().getPassword()).isEqualTo(hashBefore);
    }

    @Test
    void shouldIgnoreEmailAndRoleSentInProfileUpdate() throws Exception {
        String token = registerAndGetToken("mass@example.com", "Mia", "Assign");

        // Mass-assignment attempt: the request DTO has no email or role field.
        mockMvc.perform(put("/users/me")
                        .header(AUTH, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Mia","lastName":"Assign","phoneNumber":"+254700000000",
                                 "email":"hacker@example.com","role":"ADMIN"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mass@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        assertThat(userRepository.findByEmail("hacker@example.com")).isEmpty();
    }

    @Test
    void shouldRejectBlankFirstNameWithFieldError() throws Exception {
        String token = registerAndGetToken("blank@example.com", "Bo", "Blank");

        mockMvc.perform(put("/users/me")
                        .header(AUTH, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"   ","lastName":"Blank","phoneNumber":"+254700000000"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors[0].field").value("firstName"));

        mockMvc.perform(get("/users/me").header(AUTH, bearer(token)))
                .andExpect(jsonPath("$.firstName").value("Bo"));
    }

    // ==========================================
    // PUT /users/me/password
    // ==========================================

    @Test
    void shouldChangePasswordAndAcceptOnlyTheNewOneAtLogin() throws Exception {
        String token = registerAndGetToken("change@example.com", "Cy", "Change");

        mockMvc.perform(put("/users/me/password")
                        .header(AUTH, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordBody(PASSWORD, NEW_PASSWORD)))
                .andExpect(status().isNoContent());

        // The hash is stored, never the plain text.
        assertThat(userRepository.findByEmail("change@example.com")
                .orElseThrow().getPassword()).isNotEqualTo(NEW_PASSWORD);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("change@example.com", NEW_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("change@example.com", PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectWrongCurrentPasswordWith400AndKeepOldPassword() throws Exception {
        String token = registerAndGetToken("wrong@example.com", "Wes", "Wrong");

        mockMvc.perform(put("/users/me/password")
                        .header(AUTH, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordBody("NotMyPassword1!", NEW_PASSWORD)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current password is incorrect"))
                .andExpect(jsonPath("$.validationErrors[0].field").value("currentPassword"));

        // Nothing changed: the old password still logs in.
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("wrong@example.com", PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectTooShortNewPassword() throws Exception {
        String token = registerAndGetToken("short@example.com", "Sal", "Short");

        mockMvc.perform(put("/users/me/password")
                        .header(AUTH, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordBody(PASSWORD, "short")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors[0].field").value("newPassword"));
    }

    /*
     * Characterization test for a known Phase 13 gap: JWTs are stateless, so a token
     * issued before a password change keeps working until it expires. If tokens are
     * ever invalidated on password change, this test must be flipped to expect 401.
     */
    @Test
    void shouldKeepExistingTokenValidAfterPasswordChange() throws Exception {
        String token = registerAndGetToken("stale@example.com", "Stu", "Stale");

        mockMvc.perform(put("/users/me/password")
                        .header(AUTH, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordBody(PASSWORD, NEW_PASSWORD)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/me").header(AUTH, bearer(token)))
                .andExpect(status().isOk());
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private String passwordBody(String current, String next) {
        return """
                {"currentPassword":"%s","newPassword":"%s"}
                """.formatted(current, next);
    }

    private String loginBody(String email, String password) {
        return """
                {"email":"%s","password":"%s"}
                """.formatted(email, password);
    }
}