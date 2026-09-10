package com.autotrader.backend.controller;

import com.autotrader.backend.dto.image.ImageResponse;
import com.autotrader.backend.dto.image.ReorderImagesRequest;
import com.autotrader.backend.exception.ImageNotFoundException;
import com.autotrader.backend.security.CustomUserDetailsService;
import com.autotrader.backend.security.JwtService;
import com.autotrader.backend.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void uploadImage_withValidFile_returnsCreatedResponse() throws Exception {

        // Arrange: build a fake multipart file the way a real client would send one.
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "car.jpg",
                "image/jpeg",
                "fake-image-content".getBytes()
        );

        ImageResponse response = new ImageResponse();
        response.setId(1L);
        response.setImageUrl("/uploads/car.jpg");
        response.setPrimaryImage(true);
        response.setDisplayOrder(0);

        when(imageService.uploadImage(eq(1L), any(MultipartFile.class)))
                .thenReturn(response);

        mockMvc.perform(multipart("/listings/{listingId}/images", 1L)
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.imageUrl").value("/uploads/car.jpg"))
                .andExpect(jsonPath("$.primaryImage").value(true));

        verify(imageService).uploadImage(eq(1L), any(MultipartFile.class));
    }

    @Test
    void uploadImage_whenFileIsInvalid_propagatesException() {

        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.txt", "text/plain", "not an image".getBytes()
        );

        when(imageService.uploadImage(eq(1L), any(MultipartFile.class)))
                .thenThrow(new IllegalArgumentException(
                        "Only JPEG, PNG and WEBP images are supported"));

        /*
         * GlobalExceptionHandler currently has no @ExceptionHandler for
         * IllegalArgumentException, so this exception is not caught and
         * converted into a structured 400 response — it surfaces as an
         * unhandled exception during dispatch instead. This test documents
         * that current gap rather than a desired outcome; adding a handler
         * for IllegalArgumentException would let this become a normal
         * status().isBadRequest() assertion.
         */
        assertThrows(
                Exception.class,
                () -> mockMvc.perform(multipart("/listings/{listingId}/images", 1L)
                        .file(file))
        );

        verify(imageService).uploadImage(eq(1L), any(MultipartFile.class));
    }

    @Test
    void deleteImage_returnsNoContent() throws Exception {

        doNothing().when(imageService).deleteImage(1L, 2L);

        mockMvc.perform(delete("/listings/{listingId}/images/{imageId}", 1L, 2L))
                .andExpect(status().isNoContent());

        verify(imageService).deleteImage(1L, 2L);
    }

    @Test
    void deleteImage_whenImageNotFound_returnsNotFound() throws Exception {

        doThrow(new ImageNotFoundException("Image not found"))
                .when(imageService).deleteImage(1L, 2L);

        mockMvc.perform(delete("/listings/{listingId}/images/{imageId}", 1L, 2L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Image not found"));
    }

    @Test
    void setPrimaryImage_returnsOkResponse() throws Exception {

        ImageResponse response = new ImageResponse();
        response.setId(2L);
        response.setImageUrl("/uploads/car2.jpg");
        response.setPrimaryImage(true);
        response.setDisplayOrder(0);

        when(imageService.setPrimaryImage(1L, 2L)).thenReturn(response);

        mockMvc.perform(patch("/listings/{listingId}/images/{imageId}/primary", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.primaryImage").value(true));

        verify(imageService).setPrimaryImage(1L, 2L);
    }

    @Test
    void setPrimaryImage_whenImageNotFound_returnsNotFound() throws Exception {

        when(imageService.setPrimaryImage(1L, 2L))
                .thenThrow(new ImageNotFoundException("Image not found"));

        mockMvc.perform(patch("/listings/{listingId}/images/{imageId}/primary", 1L, 2L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Image not found"));
    }

    @Test
    void reorderImages_withValidRequest_returnsOkResponse() throws Exception {

        String requestBody = """
                {
                    "imageIds": [3, 1, 2]
                }
                """;

        ImageResponse first = new ImageResponse();
        first.setId(3L);
        first.setDisplayOrder(0);
        first.setPrimaryImage(true);

        when(imageService.reorderImages(eq(1L), any(ReorderImagesRequest.class)))
                .thenReturn(List.of(first));

        mockMvc.perform(put("/listings/{listingId}/images/order", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].primaryImage").value(true));

        verify(imageService).reorderImages(eq(1L), any(ReorderImagesRequest.class));
    }

    @Test
    void reorderImages_withInvalidRequest_returnsBadRequestAndDoesNotCallService()
            throws Exception {

        // imageIds is empty, which violates @NotEmpty on ReorderImagesRequest.
        String requestBody = """
                {
                    "imageIds": []
                }
                """;

        mockMvc.perform(put("/listings/{listingId}/images/order", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors").isArray());

        verify(imageService, never())
                .reorderImages(any(), any(ReorderImagesRequest.class));
    }
}