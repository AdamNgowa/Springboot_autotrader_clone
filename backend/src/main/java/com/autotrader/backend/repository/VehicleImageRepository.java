package com.autotrader.backend.repository;

import com.autotrader.backend.entity.VehicleImage;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
     extends JpaRepository<VehicleImage, Long> tells spring that this repository,
     is responsible for persisting vehicleImage entities with database
 */

public interface VehicleImageRepository
        extends JpaRepository<VehicleImage, Long> {

    /**
     * Checks whether at least one VehicleImage exists associated with the specified VehicleListing.
     * Generates a "SELECT CASE WHEN COUNT(...) > 0" query using the listing ID filter.
     *
     * @param listing The VehicleListing entity to filter by
     * @return true if at least one image exists for the given listing, false otherwise
     */
    boolean existsByVehicleListing(VehicleListing listing);

    /**
     * Retrieves all VehicleImage entities associated with a specific VehicleListing,
     * sorted in ascending order by their display order sequence.
     * Generates a "SELECT ... WHERE vehicle_listing_id = ? ORDER BY display_order ASC" query.
     *
     * @param listing The VehicleListing entity whose images are being retrieved
     * @return A list of VehicleImage entities ordered by displayOrder (1, 2, 3...)
     */
    List<VehicleImage> findByVehicleListingOrderByDisplayOrderAsc(VehicleListing listing);

    /**
     * Counts the total number of VehicleImage entities associated with a specific VehicleListing.
     * Generates a "SELECT COUNT(...) WHERE vehicle_listing_id = ?" query.
     *
     * @param listing The VehicleListing entity to count images for
     * @return The total number of images linked to the given listing
     */
    long countByVehicleListing(VehicleListing listing);
}