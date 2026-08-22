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

// VehicleListingRepository: Extends JpaSpecificationExecutor<VehicleListing>,
// granting access to spring methods like findAll(Specification, Pageable).
public interface VehicleListingRepository
        extends JpaRepository<VehicleListing, Long>, JpaSpecificationExecutor<VehicleListing> {

    /**
     * Fetches a VehicleListing by its primary key along with all associated images in a single SQL query.
     * Uses JPQL "LEFT JOIN FETCH" to eagerly load the images collection, preventing N+1 query problems.
     *
     * @param id The primary key ID of the VehicleListing
     * @return An Optional containing the VehicleListing with its images populated, or Optional.empty() if not found
     */
    @Query("""
            SELECT l
            FROM VehicleListing l
            LEFT JOIN FETCH l.images
            WHERE l.id = :id
            """)
    Optional<VehicleListing> findByIdWithImages(@Param("id") Long id);

    /**
     * Retrieves a paginated list of vehicle listings submitted by a specific seller matching a given listing status.
     * Generates a "SELECT ... WHERE seller_id = ? AND status = ? LIMIT ? OFFSET ?" query.
     *
     * @param seller The User entity who owns the listings
     * @param status The current status filter (e.g., ACTIVE, PENDING, SOLD)
     * @param pageable Pagination and sorting information (page number, size, sort directions)
     * @return A Page object containing matching VehicleListing items and pagination metadata
     */
    Page<VehicleListing> findBySellerAndStatus(
            User seller,
            ListingStatus status,
            Pageable pageable);
}