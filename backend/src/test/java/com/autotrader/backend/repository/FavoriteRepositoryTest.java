package com.autotrader.backend.repository;

import com.autotrader.backend.entity.Enums.*;
import com.autotrader.backend.entity.Favorite;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FavoriteRepositoryTest {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleListingRepository vehicleListingRepository;

    @Test
    void shouldFindFavoriteByUserAndVehicleListing() {
        // Arrange
        User user = createUser("buyer@example.com");
        VehicleListing listing = createListing(
                user,
                "Toyota Corolla"
        );

        Favorite favorite = createFavorite(user, listing);

        favoriteRepository.save(favorite);

        // Act
        Optional<Favorite> result =
                favoriteRepository.findByUserAndVehicleListing(
                        user,
                        listing
                );

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId())
                .isEqualTo(user.getId());
        assertThat(result.get().getVehicleListing().getId())
                .isEqualTo(listing.getId());
    }

    @Test
    void shouldReturnEmptyWhenFavoriteDoesNotExist() {
        // Arrange
        User user = createUser("buyer@example.com");
        VehicleListing listing = createListing(
                user,
                "Toyota Corolla"
        );

        // Act
        Optional<Favorite> result =
                favoriteRepository.findByUserAndVehicleListing(
                        user,
                        listing
                );

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldCheckWhetherFavoriteExists() {
        // Arrange
        User user = createUser("buyer@example.com");
        VehicleListing listing = createListing(
                user,
                "Toyota Corolla"
        );

        Favorite favorite = createFavorite(user, listing);

        favoriteRepository.save(favorite);

        // Act
        boolean exists =
                favoriteRepository.existsByUserAndVehicleListing(
                        user,
                        listing
                );

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenFavoriteDoesNotExist() {
        // Arrange
        User user = createUser("buyer@example.com");
        VehicleListing listing = createListing(
                user,
                "Toyota Corolla"
        );

        // Act
        boolean exists =
                favoriteRepository.existsByUserAndVehicleListing(
                        user,
                        listing
                );

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void shouldDeleteFavoriteByUserAndVehicleListing() {
        // Arrange
        User user = createUser("buyer@example.com");
        VehicleListing listing = createListing(
                user,
                "Toyota Corolla"
        );

        Favorite favorite = createFavorite(user, listing);

        favoriteRepository.save(favorite);

        // Act
        favoriteRepository.deleteByUserAndVehicleListing(
                user,
                listing
        );

        // Assert
        assertThat(
                favoriteRepository.existsByUserAndVehicleListing(
                        user,
                        listing
                )
        ).isFalse();
    }

    @Test
    void shouldFindAllFavoritesForUser() {
        // Arrange
        User user = createUser("buyer@example.com");

        VehicleListing firstListing =
                createListing(user, "Toyota Corolla");

        VehicleListing secondListing =
                createListing(user, "Honda Civic");

        Favorite firstFavorite =
                createFavorite(user, firstListing);

        Favorite secondFavorite =
                createFavorite(user, secondListing);

        favoriteRepository.saveAll(
                List.of(firstFavorite, secondFavorite)
        );

        // Act
        List<Favorite> result =
                favoriteRepository.findByUser(user);

        // Assert
        assertThat(result)
                .hasSize(2);

        assertThat(result)
                .extracting(favorite ->
                        favorite.getVehicleListing().getTitle()
                )
                .containsExactlyInAnyOrder(
                        "Toyota Corolla",
                        "Honda Civic"
                );
    }

    private User createUser(String email) {
        User user = new User();

        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPassword("encoded-password");
        user.setPhoneNumber("0712345678");
        user.setRole(UserRole.USER);

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

    private Favorite createFavorite(
            User user,
            VehicleListing listing
    ) {
        Favorite favorite = new Favorite();

        favorite.setUser(user);
        favorite.setVehicleListing(listing);

        return favorite;
    }
}