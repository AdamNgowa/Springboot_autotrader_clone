package com.autotrader.backend.repository;

import com.autotrader.backend.entity.VehicleImage;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.data.jpa.repository.JpaRepository;

/*
     extends JpaRepository<VehicleImage, Long> tells spring that this repository,
     is responsible for persisting vehicleImage entities with database
 */

public interface VehicleImageRepository
        extends JpaRepository<VehicleImage, Long> {

    boolean existsByVehicleListing(VehicleListing listing);
}
