package com.autotrader.backend.specification;

import com.autotrader.backend.entity.Enums.ListingStatus;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class VehicleListingSpecification {
    public static Specification<VehicleListing> hasCity(String city) {

        return (root, query, cb) ->
                cb.equal(root.get("city"), city);
    }

    public static Specification<VehicleListing> hasMake(String make) {

        return (root, query, cb) ->
                cb.equal(root.get("make"), make);
    }

    public static Specification<VehicleListing> hasStatus(ListingStatus status) {

        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<VehicleListing> hasMaxPrice(BigDecimal maxPrice) {
        return ((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("price"), maxPrice));
    }

    public static Specification<VehicleListing> hasMinPrice(BigDecimal minPrice) {

        return ((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("price"), minPrice));
    }
}

