package com.autotrader.backend.repository;

import com.autotrader.backend.entity.Enums.ListingStatus;
import com.autotrader.backend.entity.User;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VehicleListingRepository
        extends JpaRepository<VehicleListing, Long>, JpaSpecificationExecutor<VehicleListing> {

    @Query("""
            SELECT l
            FROM VehicleListing l
            LEFT JOIN FETCH l.images
            WHERE l.id = :id
            """)
    Optional<VehicleListing> findByIdWithImages(@Param("id") Long id);

    Page<VehicleListing> findBySellerAndStatus(
            User seller,
            ListingStatus status,
            Pageable pageable);
}
