package com.autotrader.backend.repository;

import com.autotrader.backend.entity.Enums.*;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleImage;
import com.autotrader.backend.entity.VehicleListing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VehicleImageRepositoryTest {

    @Autowired
    private VehicleImageRepository vehicleImageRepository;

    @Autowired
    private VehicleListingRepository vehicleListingRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCheckWhetherListingHasImages() {
        // Arrange
        User seller = createUser("seller@example.com");

        VehicleListing listing =
                createListing(seller, "Toyota Corolla");

        // Before adding an image, none should exist.
        assertThat(
                vehicleImageRepository.existsByVehicleListing(listing)
        ).isFalse();

        VehicleImage image =
                createImage(
                        listing,
                        "corolla-front.jpg",
                        "stored-front.jpg",
                        0,
                        true
                );

        vehicleImageRepository.save(image);

        // Act
        boolean exists =
                vehicleImageRepository.existsByVehicleListing(listing);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnImagesOrderedByDisplayOrderAscending() {
        // Arrange
        User seller = createUser("seller@example.com");

        VehicleListing listing =
                createListing(seller, "Toyota Corolla");

        VehicleImage thirdImage =
                createImage(
                        listing,
                        "corolla-rear.jpg",
                        "stored-rear.jpg",
                        2,
                        false
                );

        VehicleImage firstImage =
                createImage(
                        listing,
                        "corolla-front.jpg",
                        "stored-front.jpg",
                        0,
                        true
                );

        VehicleImage secondImage =
                createImage(
                        listing,
                        "corolla-side.jpg",
                        "stored-side.jpg",
                        1,
                        false
                );

        // Deliberately save in a non-display-order sequence.
        vehicleImageRepository.saveAll(
                List.of(
                        thirdImage,
                        firstImage,
                        secondImage
                )
        );

        // Act
        List<VehicleImage> result =
                vehicleImageRepository
                        .findByVehicleListingOrderByDisplayOrderAsc(
                                listing
                        );

        // Assert
        assertThat(result)
                .hasSize(3);

        assertThat(result)
                .extracting(VehicleImage::getDisplayOrder)
                .containsExactly(0, 1, 2);

        assertThat(result)
                .extracting(VehicleImage::getOriginalFilename)
                .containsExactly(
                        "corolla-front.jpg",
                        "corolla-side.jpg",
                        "corolla-rear.jpg"
                );
    }

    @Test
    void shouldCountImagesForListing() {
        // Arrange
        User seller = createUser("seller@example.com");

        VehicleListing listing =
                createListing(seller, "Toyota Corolla");

        VehicleImage firstImage =
                createImage(
                        listing,
                        "front.jpg",
                        "stored-front.jpg",
                        0,
                        true
                );

        VehicleImage secondImage =
                createImage(
                        listing,
                        "side.jpg",
                        "stored-side.jpg",
                        1,
                        false
                );

        VehicleImage thirdImage =
                createImage(
                        listing,
                        "rear.jpg",
                        "stored-rear.jpg",
                        2,
                        false
                );

        vehicleImageRepository.saveAll(
                List.of(
                        firstImage,
                        secondImage,
                        thirdImage
                )
        );

        // Act
        long count =
                vehicleImageRepository.countByVehicleListing(listing);

        // Assert
        assertThat(count).isEqualTo(3);
    }

    @Test
    void shouldNotCountImagesBelongingToAnotherListing() {
        // Arrange
        User seller = createUser("seller@example.com");

        VehicleListing firstListing =
                createListing(seller, "Toyota Corolla");

        VehicleListing secondListing =
                createListing(seller, "Honda Civic");

        VehicleImage firstListingImage =
                createImage(
                        firstListing,
                        "corolla.jpg",
                        "stored-corolla.jpg",
                        0,
                        true
                );

        VehicleImage secondListingImage =
                createImage(
                        secondListing,
                        "civic.jpg",
                        "stored-civic.jpg",
                        0,
                        true
                );

        vehicleImageRepository.saveAll(
                List.of(
                        firstListingImage,
                        secondListingImage
                )
        );

        // Act
        long firstListingCount =
                vehicleImageRepository.countByVehicleListing(
                        firstListing
                );

        // Assert
        assertThat(firstListingCount)
                .isEqualTo(1);
    }

    private User createUser(String email) {
        User user = new User();

        user.setFirstName("Test");
        user.setLastName("Seller");
        user.setEmail(email);
        user.setPassword("encoded-password");
        user.setPhoneNumber("0712345678");
        user.setRole(UserRole.ADMIN);

        return userRepository.save(user);
    }

    private VehicleListing createListing(
            User seller,
            String title
    ) {
        VehicleListing listing = new VehicleListing();

        listing.setTitle(title);
        listing.setDescription("Test description");
        listing.setPrice(new BigDecimal("15000.00"));
        listing.setYear(2022);
        listing.setMake("Toyota");
        listing.setModel("Corolla");
        listing.setMileage(25000);
        listing.setFuelType(FuelType.PETROL);
        listing.setTransmission(Transmission.AUTOMATIC);
        listing.setBodyType(BodyType.SEDAN);
        listing.setCity("Nairobi");
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setSeller(seller);

        return vehicleListingRepository.save(listing);
    }

    private VehicleImage createImage(
            VehicleListing listing,
            String originalFilename,
            String storageFilename,
            int displayOrder,
            boolean primaryImage
    ) {
        VehicleImage image = new VehicleImage();

        image.setVehicleListing(listing);
        image.setOriginalFilename(originalFilename);
        image.setStorageFilename(storageFilename);
        image.setContentType("image/jpeg");
        image.setFileSize(1024L);
        image.setDisplayOrder(displayOrder);
        image.setPrimaryImage(primaryImage);

        return image;
    }
}