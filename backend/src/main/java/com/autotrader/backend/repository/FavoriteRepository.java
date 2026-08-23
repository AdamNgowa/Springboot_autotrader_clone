package com.autotrader.backend.repository;

import com.autotrader.backend.entity.Favorite;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserAndVehicleListing(
            User user,
            VehicleListing vehicleListing
    );

    boolean existsByUserAndVehicleListing(
            User user,
            VehicleListing vehicleListing
    );

    void deleteByUserAndVehicleListing(
            User user,
            VehicleListing vehicleListing
    );

    List<Favorite> findByUser(User user);
}