package com.autotrader.backend.service;

import com.autotrader.backend.dto.image.ImageResponse;
import com.autotrader.backend.dto.image.ReorderImagesRequest;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleImage;
import com.autotrader.backend.entity.VehicleListing;
import com.autotrader.backend.exception.ImageNotFoundException;
import com.autotrader.backend.exception.UnauthorizedListingAccessException;
import com.autotrader.backend.mapper.ImageMapper;
import com.autotrader.backend.repository.VehicleImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    private static final Long LISTING_ID = 10L;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private VehicleListingService vehicleListingService;

    @Mock
    private VehicleImageRepository vehicleImageRepository;

    @Mock
    private LocalFileStorageService localFileStorageService;

   private ImageMapper imageMapper;

   private ImageService imageService;

    // ==========================================
    // uploadImage
    // ==========================================

    @BeforeEach
    void setUp() {
        imageMapper = new ImageMapper(localFileStorageService);

        imageService = new ImageService(
                currentUserService,
                vehicleListingService,
                vehicleImageRepository,
                localFileStorageService,
                imageMapper
        );
    }

    @Test
    void uploadImage_firstImage_becomesPrimaryWithDisplayOrderZero() {
        // Arrange
        VehicleListing listing = stubActiveListingAndUser();
        when(vehicleImageRepository.countByVehicleListing(listing)).thenReturn(0L);
        when(vehicleImageRepository.save(any(VehicleImage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(localFileStorageService.getFileUrl(anyString()))
                .thenAnswer(invocation ->
                        "/uploads/" + invocation.getArgument(0));

        // Act
        ImageResponse response =
                imageService.uploadImage(LISTING_ID, jpeg("front.jpg"));

        // Assert
        ArgumentCaptor<VehicleImage> saved = ArgumentCaptor.forClass(VehicleImage.class);
        verify(vehicleImageRepository).save(saved.capture());

        assertThat(saved.getValue().isPrimaryImage()).isTrue();
        assertThat(saved.getValue().getDisplayOrder()).isZero();
        assertThat(saved.getValue().getOriginalFilename()).isEqualTo("front.jpg");
        assertThat(saved.getValue().getStorageFilename()).endsWith(".jpg");
        assertThat(response.getImageUrl()).startsWith("/uploads/");
    }

    @Test
    void uploadImage_existingImages_appendsNonPrimaryImageAtEnd() {
        VehicleListing listing = stubActiveListingAndUser();
        when(vehicleImageRepository.countByVehicleListing(listing)).thenReturn(2L);
        when(vehicleImageRepository.save(any(VehicleImage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        imageService.uploadImage(LISTING_ID, jpeg("side.jpg"));

        ArgumentCaptor<VehicleImage> saved = ArgumentCaptor.forClass(VehicleImage.class);
        verify(vehicleImageRepository).save(saved.capture());

        assertThat(saved.getValue().isPrimaryImage()).isFalse();
        assertThat(saved.getValue().getDisplayOrder()).isEqualTo(2);
    }

    @Test
    void uploadImage_whenMetadataSaveFails_deletesStoredFileAndRethrows() {
        // Arrange: the file is written first, then the database insert fails.
        VehicleListing listing = stubActiveListingAndUser();
        when(vehicleImageRepository.countByVehicleListing(listing)).thenReturn(0L);
        when(vehicleImageRepository.save(any(VehicleImage.class)))
                .thenThrow(new RuntimeException("database unavailable"));

        // Act + Assert: the original exception reaches the caller...
        assertThatThrownBy(() ->
                imageService.uploadImage(LISTING_ID, jpeg("front.jpg")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("database unavailable");

        // ...and the SAME filename that was stored is the one cleaned up.
        ArgumentCaptor<String> storedName = ArgumentCaptor.forClass(String.class);
        verify(localFileStorageService).saveFile(any(InputStream.class), storedName.capture());
        verify(localFileStorageService).deleteFile(storedName.getValue());
    }

    @Test
    void uploadImage_whenStorageFails_doesNotSaveMetadataOrDeleteAnything() {
        stubActiveListingAndUser();

        doThrow(new RuntimeException("disk full"))
                .when(localFileStorageService)
                .saveFile(any(InputStream.class), anyString());

        assertThatThrownBy(() ->
                imageService.uploadImage(LISTING_ID, jpeg("front.jpg")))
                .hasMessage("disk full");

        verify(vehicleImageRepository, never()).save(any());
        verify(localFileStorageService, never()).deleteFile(anyString());
    }

    @Test
    void uploadImage_unsupportedContentType_isRejectedBeforeAnySideEffect() {
        MockMultipartFile textFile = new MockMultipartFile(
                "file", "notes.txt", "text/plain", new byte[]{1, 2, 3});

        assertThatThrownBy(() -> imageService.uploadImage(LISTING_ID, textFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only JPEG, PNG and WEBP images are supported");

        verifyNoInteractions(
                vehicleListingService, localFileStorageService, vehicleImageRepository);
    }

    @Test
    void uploadImage_emptyFile_isRejectedBeforeAnySideEffect() {
        MockMultipartFile empty = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> imageService.uploadImage(LISTING_ID, empty))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Image file must not be empty");

        verifyNoInteractions(
                vehicleListingService, localFileStorageService, vehicleImageRepository);
    }

    @Test
    void uploadImage_filenameWithoutExtension_isRejectedAndNothingIsStored() {
        stubActiveListingAndUser();
        MockMultipartFile noExtension = new MockMultipartFile(
                "file", "noextension", "image/jpeg", new byte[]{1, 2, 3});

        assertThatThrownBy(() -> imageService.uploadImage(LISTING_ID, noExtension))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(localFileStorageService);
        verify(vehicleImageRepository, never()).save(any());
    }

    @Test
    void uploadImage_whenUserIsNotOwner_throwsAndStoresNothing() {
        VehicleListing listing = stubActiveListingAndUser();
        User user = currentUserService.getAuthenticatedUser();
        doThrow(new UnauthorizedListingAccessException("not yours"))
                .when(vehicleListingService).verifyOwnership(listing, user);

        assertThatThrownBy(() ->
                imageService.uploadImage(LISTING_ID, jpeg("front.jpg")))
                .isInstanceOf(UnauthorizedListingAccessException.class);

        verifyNoInteractions(localFileStorageService);
        verify(vehicleImageRepository, never()).save(any());
    }

    // ==========================================
    // reorderImages
    // ==========================================

    @Test
    void reorderImages_validRequest_appliesOrderAndMakesFirstPrimary() {
        VehicleListing listing = stubOwnedListingWithImages();
        VehicleImage one = image(1L, 0, true, "one.jpg");
        VehicleImage two = image(2L, 1, false, "two.jpg");
        VehicleImage three = image(3L, 2, false, "three.jpg");
        stubImagesInDatabase(listing, one, two, three);
        when(vehicleImageRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<ImageResponse> result =
                imageService.reorderImages(LISTING_ID, reorderRequest(3L, 1L, 2L));

        assertThat(result).extracting(ImageResponse::getId)
                .containsExactly(3L, 1L, 2L);
        assertThat(result).extracting(ImageResponse::getDisplayOrder)
                .containsExactly(0, 1, 2);
        assertThat(result).extracting(ImageResponse::isPrimaryImage)
                .containsExactly(true, false, false);
    }

    @Test
    void reorderImages_requestMissingAnImage_isRejected() {
        VehicleListing listing = stubOwnedListingWithImages();
        stubImagesInDatabase(listing,
                image(1L, 0, true, "one.jpg"),
                image(2L, 1, false, "two.jpg"));

        assertThatThrownBy(() ->
                imageService.reorderImages(LISTING_ID, reorderRequest(1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The reorder request must contain all listing images");

        verify(vehicleImageRepository, never()).saveAll(anyList());
    }

    @Test
    void reorderImages_requestWithDuplicateIds_isRejected() {
        VehicleListing listing = stubOwnedListingWithImages();
        stubImagesInDatabase(listing,
                image(1L, 0, true, "one.jpg"),
                image(2L, 1, false, "two.jpg"));

        assertThatThrownBy(() ->
                imageService.reorderImages(LISTING_ID, reorderRequest(1L, 1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Image IDs must not contain duplicates");

        verify(vehicleImageRepository, never()).saveAll(anyList());
    }

    @Test
    void reorderImages_requestWithForeignId_isRejected() {
        VehicleListing listing = stubOwnedListingWithImages();
        stubImagesInDatabase(listing,
                image(1L, 0, true, "one.jpg"),
                image(2L, 1, false, "two.jpg"));

        assertThatThrownBy(() ->
                imageService.reorderImages(LISTING_ID, reorderRequest(1L, 99L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("All image IDs must belong to this listing");

        verify(vehicleImageRepository, never()).saveAll(anyList());
    }

    // ==========================================
    // setPrimaryImage
    // ==========================================

    @Test
    void setPrimaryImage_movesTargetToFrontAndShiftsOthersDown() {
        VehicleListing listing = stubOwnedListingWithImages();
        VehicleImage one = image(1L, 0, true, "one.jpg");
        VehicleImage two = image(2L, 1, false, "two.jpg");
        VehicleImage three = image(3L, 2, false, "three.jpg");
        stubImagesInDatabase(listing, one, two, three);

        ImageResponse response = imageService.setPrimaryImage(LISTING_ID, 3L);

        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.isPrimaryImage()).isTrue();

        assertThat(three.getDisplayOrder()).isZero();
        assertThat(three.isPrimaryImage()).isTrue();

        assertThat(one.getDisplayOrder()).isEqualTo(1);
        assertThat(one.isPrimaryImage()).isFalse();

        assertThat(two.getDisplayOrder()).isEqualTo(2);
        assertThat(two.isPrimaryImage()).isFalse();
    }

    @Test
    void setPrimaryImage_unknownImage_throwsImageNotFound() {
        VehicleListing listing = stubOwnedListingWithImages();
        stubImagesInDatabase(listing, image(1L, 0, true, "one.jpg"));

        assertThatThrownBy(() -> imageService.setPrimaryImage(LISTING_ID, 99L))
                .isInstanceOf(ImageNotFoundException.class);

        verify(vehicleImageRepository, never()).save(any());
    }

    // ==========================================
    // deleteImage
    // ==========================================

    @Test
    void deleteImage_removesRecordThenFileAndNormalizesRemainingImages() {
        VehicleListing listing = stubOwnedListingWithImages();
        VehicleImage first = image(1L, 0, true, "first.jpg");
        VehicleImage second = image(2L, 1, false, "second.jpg");
        when(listing.getImages()).thenReturn(List.of(first, second));
        // After the delete, the repository returns only the survivors.
        stubImagesInDatabase(listing, second);

        imageService.deleteImage(LISTING_ID, 1L);

        verify(listing).removeImage(first);

        // The row must be gone (flushed) BEFORE the file is deleted.
        InOrder order = inOrder(vehicleImageRepository, localFileStorageService);
        order.verify(vehicleImageRepository).delete(first);
        order.verify(vehicleImageRepository).flush();
        order.verify(localFileStorageService).deleteFile("first.jpg");

        // The survivor is promoted and the order has no gap.
        assertThat(second.getDisplayOrder()).isZero();
        assertThat(second.isPrimaryImage()).isTrue();
        verify(vehicleImageRepository).saveAll(anyList());
    }

    @Test
    void deleteImage_imageNotInListing_throwsAndDeletesNothing() {
        VehicleListing listing = stubOwnedListingWithImages();
        when(listing.getImages())
                .thenReturn(List.of(image(1L, 0, true, "first.jpg")));

        assertThatThrownBy(() -> imageService.deleteImage(LISTING_ID, 99L))
                .isInstanceOf(ImageNotFoundException.class);

        verify(vehicleImageRepository, never()).delete(any());
        verifyNoInteractions(localFileStorageService);
    }

    @Test
    void deleteImage_whenUserIsNotOwner_throwsAndDeletesNothing() {
        VehicleListing listing = stubOwnedListingWithImages();
        User user = currentUserService.getAuthenticatedUser();
        doThrow(new UnauthorizedListingAccessException("not yours"))
                .when(vehicleListingService).verifyOwnership(listing, user);

        assertThatThrownBy(() -> imageService.deleteImage(LISTING_ID, 1L))
                .isInstanceOf(UnauthorizedListingAccessException.class);

        verify(vehicleImageRepository, never()).delete(any());
        verifyNoInteractions(localFileStorageService);
    }

    // ==========================================
    // HELPERS
    // ==========================================

    // Stubs used by uploadImage: getActiveListing + authenticated user.
    private VehicleListing stubActiveListingAndUser() {
        VehicleListing listing = mock(VehicleListing.class);
        User user = mock(User.class);

        when(vehicleListingService.getActiveListing(LISTING_ID)).thenReturn(listing);
        when(currentUserService.getAuthenticatedUser()).thenReturn(user);

        return listing;
    }

    // Stubs used by delete/setPrimary/reorder: getActiveListingWithImages + authenticated user.
    private VehicleListing stubOwnedListingWithImages() {
        VehicleListing listing = mock(VehicleListing.class);
        User user = mock(User.class);

        when(vehicleListingService.getActiveListingWithImages(LISTING_ID))
                .thenReturn(listing);
        when(currentUserService.getAuthenticatedUser()).thenReturn(user);

        return listing;
    }

    // Returns a MUTABLE list because setPrimaryImage removes the target from it.
    private void stubImagesInDatabase(VehicleListing listing, VehicleImage... images) {
        when(vehicleImageRepository.findByVehicleListingOrderByDisplayOrderAsc(listing))
                .thenReturn(new ArrayList<>(List.of(images)));
    }

    // VehicleImage has no setId (the database assigns it), so tests set it by reflection.
    private VehicleImage image(Long id, int displayOrder, boolean primary, String storageFilename) {
        VehicleImage image = new VehicleImage();

        ReflectionTestUtils.setField(image, "id", id);
        image.setStorageFilename(storageFilename);
        image.setDisplayOrder(displayOrder);
        image.setPrimaryImage(primary);

        return image;
    }

    private ReorderImagesRequest reorderRequest(Long... ids) {
        ReorderImagesRequest request = new ReorderImagesRequest();
        request.setImageIds(List.of(ids));
        return request;
    }

    private MockMultipartFile jpeg(String filename) {
        return new MockMultipartFile(
                "file", filename, "image/jpeg", new byte[]{1, 2, 3, 4});
    }
}
