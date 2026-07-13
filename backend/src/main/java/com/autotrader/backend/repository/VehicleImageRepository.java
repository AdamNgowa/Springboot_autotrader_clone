package com.autotrader.backend.repository;

import com.autotrader.backend.entity.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleImageRepository
        extends JpaRepository<VehicleImage, Long> {
}
