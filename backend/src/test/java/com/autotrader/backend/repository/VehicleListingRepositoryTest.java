package com.autotrader.backend.repository;

import com.autotrader.backend.entity.Enums.*;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleImage;
import com.autotrader.backend.entity.VehicleListing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VehicleListingRepositoryTest {

    @Autowired
    private VehicleListingRepository vehicleListingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleImageRepository vehicleImageRepository;

    @Test
    void shouldFindListingWithImages() {

        // Arrange
        User seller = createSeller("seller@example.com");

        VehicleListing listing = createListing(
                seller,
                "Toyota Corolla",
                ListingStatus.ACTIVE
        );

        vehicleListingRepository.save(listing);

        VehicleImage firstImage = createImage(
                listing,
                "corolla-front.jpg",
                "stored-front.jpg",
                0,
                true
        );

        VehicleImage secondImage = createImage(
                listing,
                "corolla-side.jpg",
                "stored-side.jpg",
                1,
                false
        );

        /*
         * Keep both sides of the relationship synchronized.
         */
        listing.addImage(firstImage);
        listing.addImage(secondImage);

        vehicleImageRepository.saveAll(
                List.of(firstImage, secondImage)
        );

        // Act
        Optional<VehicleListing> result =
                vehicleListingRepository.findByIdWithImages(listing.getId());

        // Assert
        assertThat(result).isPresent();

        VehicleListing foundListing = result.get();

        assertThat(foundListing.getId())
                .isEqualTo(listing.getId());

        assertThat(foundListing.getTitle())
                .isEqualTo("Toyota Corolla");

        assertThat(foundListing.getImages())
                .hasSize(2);

        assertThat(foundListing.getImages())
                .extracting(VehicleImage::getOriginalFilename)
                .containsExactlyInAnyOrder(
                        "corolla-front.jpg",
                        "corolla-side.jpg"
                );
    }

    @Test
    void shouldReturnEmptyWhenListingDoesNotExist() {

        Optional<VehicleListing> result =
                vehicleListingRepository.findByIdWithImages(999999L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindListingsBySellerAndStatusWithPagination() {

        User seller = createSeller("seller@example.com");

        VehicleListing activeListingOne = createListing(
                seller,
                "Toyota Corolla",
                ListingStatus.ACTIVE
        );

        VehicleListing activeListingTwo = createListing(
                seller,
                "Honda Civic",
                ListingStatus.ACTIVE
        );

        VehicleListing soldListing = createListing(
                seller,
                "Mazda CX-5",
                ListingStatus.SOLD
        );

        vehicleListingRepository.saveAll(
                List.of(
                        activeListingOne,
                        activeListingTwo,
                        soldListing
                )
        );

        PageRequest pageable = PageRequest.of(0, 2);

        Page<VehicleListing> result =
                vehicleListingRepository.findBySellerAndStatus(
                        seller,
                        ListingStatus.ACTIVE,
                        pageable
                );

        assertThat(result.getContent())
                .hasSize(2);

        assertThat(result.getTotalElements())
                .isEqualTo(2);

        assertThat(result.getTotalPages())
                .isEqualTo(1);

        assertThat(result.getContent())
                .extracting(VehicleListing::getStatus)
                .containsOnly(ListingStatus.ACTIVE);

        assertThat(result.getContent())
                .extracting(VehicleListing::getTitle)
                .containsExactlyInAnyOrder(
                        "Toyota Corolla",
                        "Honda Civic"
                );
    }

    @Test
    void shouldNotReturnListingsFromAnotherSeller() {

        User firstSeller = createSeller("first@example.com");
        User secondSeller = createSeller("second@example.com");

        VehicleListing firstSellerListing = createListing(
                firstSeller,
                "Toyota Corolla",
                ListingStatus.ACTIVE
        );

        VehicleListing secondSellerListing = createListing(
                secondSeller,
                "Honda Civic",
                ListingStatus.ACTIVE
        );

        vehicleListingRepository.saveAll(
                List.of(
                        firstSellerListing,
                        secondSellerListing
                )
        );

        PageRequest pageable = PageRequest.of(0, 10);

        Page<VehicleListing> result =
                vehicleListingRepository.findBySellerAndStatus(
                        firstSeller,
                        ListingStatus.ACTIVE,
                        pageable
                );

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(result.getContent().get(0).getTitle())
                .isEqualTo("Toyota Corolla");

        assertThat(result.getContent().get(0).getSeller().getId())
                .isEqualTo(firstSeller.getId());
    }

    // ==========================================
    // TEST HELPERS
    // ==========================================

    private User createSeller(String email) {

        User seller = new User();

        seller.setFirstName("Test");
        seller.setLastName("Seller");
        seller.setEmail(email);
        seller.setPassword("encoded-password");
        seller.setPhoneNumber("0712345678");
        seller.setRole(UserRole.ADMIN);

        return userRepository.save(seller);
    }

    private VehicleListing createListing(
            User seller,
            String title,
            ListingStatus status
    ) {

        VehicleListing listing = new VehicleListing();

        listing.setTitle(title);
        listing.setDescription("Test vehicle description");
        listing.setPrice(new BigDecimal("15000.00"));

        // Java property remains "year".
        // Database column is "vehicle_year".
        listing.setYear(2022);

        listing.setMake("Toyota");
        listing.setModel("Corolla");
        listing.setMileage(25000);
        listing.setFuelType(FuelType.PETROL);
        listing.setTransmission(Transmission.AUTOMATIC);
        listing.setBodyType(BodyType.SEDAN);
        listing.setCity("Nairobi");
        listing.setStatus(status);
        listing.setSeller(seller);

        return listing;
    }

    private VehicleImage createImage(
            VehicleListing listing,
            String originalFilename,
            String storageFilename,
            int displayOrder,
            boolean primaryImage
    ) {

        VehicleImage image = new VehicleImage();

        image.setOriginalFilename(originalFilename);
        image.setStorageFilename(storageFilename);
        image.setContentType("image/jpeg");
        image.setFileSize(1024L);
        image.setDisplayOrder(displayOrder);
        image.setPrimaryImage(primaryImage);

        return image;
    }
}