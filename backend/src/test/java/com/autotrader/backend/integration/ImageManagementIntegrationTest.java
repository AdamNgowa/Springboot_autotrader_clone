package com.autotrader.backend.integration;

import com.autotrader.backend.entity.VehicleImage;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.service.FileStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full image lifecycle through the real controller, service, PostgreSQL and the
 * real FileStorageService (writing to app.upload.directory=build/test-uploads).
 *
 * The 413 oversized-upload case lives in ImageUploadLimitIntegrationTest because
 * MockMvc skips the servlet container's multipart parsing.
 */
class ImageManagementIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private FileStorageService fileStorageService;

    private String ownerToken;
    private Long listingId;

    @BeforeEach
    void setUpListing() throws Exception {
        ownerToken = registerAndGetToken("owner@example.com", "Olivia", "Owner");
        listingId = createListing(ownerToken);
    }

    // ==========================================
    // UPLOAD
    // ==========================================

    @Test
    void shouldMakeFirstUploadedImagePrimaryAndStoreFileOnDisk() throws Exception {
        JsonNode image = uploadImage(ownerToken, listingId);

        assertThat(image.get("primaryImage").asBoolean()).isTrue();
        assertThat(image.get("displayOrder").asInt()).isZero();
        assertThat(image.get("imageUrl").asText()).startsWith("/uploads/");
        assertThat(Files.exists(storedFile(image))).isTrue();

        List<VehicleImage> saved = imagesInDb(listingId);
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).isPrimaryImage()).isTrue();
    }

    @Test
    void shouldAppendSecondImageAsNonPrimary() throws Exception {
        uploadImage(ownerToken, listingId);
        JsonNode second = uploadImage(ownerToken, listingId);

        assertThat(second.get("primaryImage").asBoolean()).isFalse();
        assertThat(second.get("displayOrder").asInt()).isEqualTo(1);
    }

    @Test
    void shouldRejectUnsupportedContentType() throws Exception {
        MockMultipartFile textFile = new MockMultipartFile(
                "file", "notes.txt", "text/plain", new byte[]{1, 2, 3}
        );

        mockMvc.perform(multipart("/listings/" + listingId + "/images")
                        .file(textFile)
                        .header(AUTH, bearer(ownerToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Only JPEG, PNG and WEBP images are supported"));

        assertThat(imagesInDb(listingId)).isEmpty();
    }

    @Test
    void shouldRejectEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]
        );

        mockMvc.perform(multipart("/listings/" + listingId + "/images")
                        .file(emptyFile)
                        .header(AUTH, bearer(ownerToken)))
                .andExpect(status().isBadRequest());

        assertThat(imagesInDb(listingId)).isEmpty();
    }

    @Test
    void shouldReturn404WhenUploadingToDeletedListing() throws Exception {
        mockMvc.perform(delete("/listings/" + listingId)
                        .header(AUTH, bearer(ownerToken)))
                .andExpect(status().isNoContent());

        mockMvc.perform(multipart("/listings/" + listingId + "/images")
                        .file(imageFile())
                        .header(AUTH, bearer(ownerToken)))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // SET PRIMARY
    // ==========================================

    @Test
    void shouldMoveSelectedImageToFrontWhenSetAsPrimary() throws Exception {
        long first = uploadImage(ownerToken, listingId).get("id").asLong();
        long second = uploadImage(ownerToken, listingId).get("id").asLong();
        long third = uploadImage(ownerToken, listingId).get("id").asLong();

        mockMvc.perform(patch("/listings/" + listingId + "/images/" + third + "/primary")
                        .header(AUTH, bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(third))
                .andExpect(jsonPath("$.primaryImage").value(true))
                .andExpect(jsonPath("$.displayOrder").value(0));

        List<VehicleImage> saved = imagesInDb(listingId);

        assertThat(saved).extracting(VehicleImage::getId)
                .containsExactly(third, first, second);
        assertThat(saved).extracting(VehicleImage::getDisplayOrder)
                .containsExactly(0, 1, 2);
        assertThat(saved).extracting(VehicleImage::isPrimaryImage)
                .containsExactly(true, false, false);
    }

    // ==========================================
    // REORDER
    // ==========================================

    @Test
    void shouldReorderImagesAndMakeFirstOneInRequestPrimary() throws Exception {
        long a = uploadImage(ownerToken, listingId).get("id").asLong();
        long b = uploadImage(ownerToken, listingId).get("id").asLong();
        long c = uploadImage(ownerToken, listingId).get("id").asLong();

        mockMvc.perform(put("/listings/" + listingId + "/images/order")
                        .header(AUTH, bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reorderBody(c, a, b)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(c))
                .andExpect(jsonPath("$[0].primaryImage").value(true))
                .andExpect(jsonPath("$[1].id").value(a))
                .andExpect(jsonPath("$[2].id").value(b));

        List<VehicleImage> saved = imagesInDb(listingId);

        assertThat(saved).extracting(VehicleImage::getId)
                .containsExactly(c, a, b);
        assertThat(saved).extracting(VehicleImage::isPrimaryImage)
                .containsExactly(true, false, false);
    }

    @Test
    void shouldRejectReorderRequestMissingAnImage() throws Exception {
        long a = uploadImage(ownerToken, listingId).get("id").asLong();
        uploadImage(ownerToken, listingId);

        expectReorderBadRequest(reorderBody(a));
    }

    @Test
    void shouldRejectReorderRequestWithDuplicateIds() throws Exception {
        long a = uploadImage(ownerToken, listingId).get("id").asLong();
        uploadImage(ownerToken, listingId);

        expectReorderBadRequest(reorderBody(a, a));
    }

    @Test
    void shouldRejectReorderRequestWithForeignImageId() throws Exception {
        long a = uploadImage(ownerToken, listingId).get("id").asLong();
        uploadImage(ownerToken, listingId);

        expectReorderBadRequest(reorderBody(a, 999_999L));
    }

    @Test
    void shouldRejectEmptyReorderRequest() throws Exception {
        uploadImage(ownerToken, listingId);

        expectReorderBadRequest(reorderBody());
    }

    // ==========================================
    // DELETE
    // ==========================================

    @Test
    void shouldDeleteImageRemoveFileAndNormalizeRemainingOrder() throws Exception {
        JsonNode first = uploadImage(ownerToken, listingId);
        JsonNode second = uploadImage(ownerToken, listingId);
        JsonNode third = uploadImage(ownerToken, listingId);

        // Delete the PRIMARY image: the next one must be promoted and order must be gapless.
        mockMvc.perform(delete("/listings/" + listingId + "/images/" + first.get("id").asLong())
                        .header(AUTH, bearer(ownerToken)))
                .andExpect(status().isNoContent());

        assertThat(Files.exists(storedFile(first))).isFalse();
        assertThat(Files.exists(storedFile(second))).isTrue();
        assertThat(Files.exists(storedFile(third))).isTrue();

        List<VehicleImage> remaining = imagesInDb(listingId);

        assertThat(remaining).extracting(VehicleImage::getId)
                .containsExactly(second.get("id").asLong(), third.get("id").asLong());
        assertThat(remaining).extracting(VehicleImage::getDisplayOrder)
                .containsExactly(0, 1);
        assertThat(remaining).extracting(VehicleImage::isPrimaryImage)
                .containsExactly(true, false);
    }

    @Test
    void shouldReturn404WhenImageBelongsToAnotherListing() throws Exception {
        long imageOfFirstListing =
                uploadImage(ownerToken, listingId).get("id").asLong();

        Long secondListingId = createListing(ownerToken, "Second Listing");

        // Same owner, but the image does not belong to the listing in the URL.
        mockMvc.perform(delete("/listings/" + secondListingId + "/images/" + imageOfFirstListing)
                        .header(AUTH, bearer(ownerToken)))
                .andExpect(status().isNotFound());

        assertThat(imagesInDb(listingId)).hasSize(1);
    }

    // ==========================================
    // OWNERSHIP
    // ==========================================

    @Test
    void shouldForbidNonOwnerFromEveryImageOperation() throws Exception {
        JsonNode image = uploadImage(ownerToken, listingId);
        long imageId = image.get("id").asLong();

        String intruderToken =
                registerAndGetToken("intruder@example.com", "Ivan", "Intruder");

        mockMvc.perform(multipart("/listings/" + listingId + "/images")
                        .file(imageFile())
                        .header(AUTH, bearer(intruderToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/listings/" + listingId + "/images/" + imageId + "/primary")
                        .header(AUTH, bearer(intruderToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/listings/" + listingId + "/images/order")
                        .header(AUTH, bearer(intruderToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reorderBody(imageId)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/listings/" + listingId + "/images/" + imageId)
                        .header(AUTH, bearer(intruderToken)))
                .andExpect(status().isForbidden());

        // Nothing changed: still exactly one image, and its file is still on disk.
        assertThat(imagesInDb(listingId)).hasSize(1);
        assertThat(Files.exists(storedFile(image))).isTrue();
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private MockMultipartFile imageFile() {
        return new MockMultipartFile(
                "file", "car.jpg", "image/jpeg", new byte[]{1, 2, 3, 4, 5}
        );
    }

    private JsonNode uploadImage(String token, Long forListingId) throws Exception {
        String body = mockMvc.perform(
                        multipart("/listings/" + forListingId + "/images")
                                .file(imageFile())
                                .header(AUTH, bearer(token)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(body);
    }

    private Path storedFile(JsonNode image) {
        String filename = image.get("imageUrl").asText()
                .substring("/uploads/".length());

        return fileStorageService.getUploadPath().resolve(filename);
    }

    private List<VehicleImage> imagesInDb(Long forListingId) {
        VehicleListing listing =
                vehicleListingRepository.findById(forListingId).orElseThrow();

        return vehicleImageRepository
                .findByVehicleListingOrderByDisplayOrderAsc(listing);
    }

    private String reorderBody(Long... ids) throws Exception {
        return objectMapper.writeValueAsString(
                Map.of("imageIds", List.of(ids))
        );
    }

    private void expectReorderBadRequest(String body) throws Exception {
        mockMvc.perform(put("/listings/" + listingId + "/images/order")
                        .header(AUTH, bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
