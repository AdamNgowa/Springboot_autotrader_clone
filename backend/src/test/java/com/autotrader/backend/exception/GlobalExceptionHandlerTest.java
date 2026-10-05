package com.autotrader.backend.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the exception -> HTTP response CONTRACT of GlobalExceptionHandler in isolation.
 *
 * A tiny controller throws each exception on demand, so no database, security or
 * network is involved and the result is deterministic.
 *
 * Why the 413 case lives here and not in an integration test: the real trigger is
 * Tomcat aborting a multipart upload mid-stream. The server answers and closes the
 * socket while the client is still sending, so the client sees a connection reset
 * instead of the response (worse on Windows). That is a property of the transport,
 * not of our handler, so we test the handler directly. The end-to-end behaviour
 * (browser -> Vercel/Render -> Spring) is verified manually in Phase 12.7.
 */
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void maxUploadSizeExceeded_returns413WithJsonBody() throws Exception {
        mockMvc.perform(get("/test/upload-too-large"))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.status").value(413))
                .andExpect(jsonPath("$.message")
                        .value("The uploaded file is too large. Images must be under 5MB."))
                .andExpect(jsonPath("$.path").value("/test/upload-too-large"));
    }

    @Test
    void unauthorizedConversationAccess_returns403() throws Exception {
        mockMvc.perform(get("/test/conversation-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message")
                        .value("You are not allowed to access this conversation"));
    }

    @Test
    void illegalArgument_returns400WithMessage() throws Exception {
        mockMvc.perform(get("/test/bad-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Image file must not be empty"));
    }

    @RestController
    static class ThrowingController {

        @GetMapping("/test/upload-too-large")
        void uploadTooLarge() {
            throw new MaxUploadSizeExceededException(5L * 1024 * 1024);
        }

        @GetMapping("/test/conversation-denied")
        void conversationDenied() {
            throw new UnauthorizedConversationAccessException(
                    "You are not allowed to access this conversation");
        }

        @GetMapping("/test/bad-argument")
        void badArgument() {
            throw new IllegalArgumentException("Image file must not be empty");
        }
    }
}