package com.autotrader.backend.repository;

import com.autotrader.backend.entity.VehicleListing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleListingRepository extends JpaRepository<VehicleListing,Long> {

}
