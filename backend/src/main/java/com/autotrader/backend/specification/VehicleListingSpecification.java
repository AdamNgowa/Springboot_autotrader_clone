package com.autotrader.backend.specification;

import com.autotrader.backend.entity.Enums.BodyType;
import com.autotrader.backend.entity.Enums.FuelType;
import com.autotrader.backend.entity.Enums.ListingStatus;
import com.autotrader.backend.entity.Enums.Transmission;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

//VehicleListingSpecification: Houses individual,
// reusable query predicate factories (e.g., hasMake(), hasMaxPrice(), hasStatus()).
public class VehicleListingSpecification {
    public static Specification<VehicleListing> hasCity(String city) {

        return (root, query, cb) ->
                cb.like(
                        //Converts users input to lowercase
                        cb.lower(root.get("city")),
                        "%" + city.toLowerCase() + "%");
    }

    public static Specification<VehicleListing> hasMake(String make) {

        return (root, query, cb) ->
                cb.like(

                        cb.lower(root.get("make")),
                        "%" + make.toLowerCase() + "%"
                );
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

    public static Specification<VehicleListing> hasBodyType(
            BodyType bodyType) {

        return (root, query, cb) ->
                cb.equal(root.get("bodyType"), bodyType);
    }

    public static Specification<VehicleListing> hasFuelType(
            FuelType fuelType) {

        return (root, query, cb) ->
                cb.equal(root.get("fuelType"), fuelType);
    }

    public static Specification<VehicleListing> hasTransmission(
            Transmission transmission) {

        return (root, query, cb) ->
                cb.equal(root.get("transmission"), transmission);
    }

    public static Specification<VehicleListing> hasYear(
            Integer year) {

        return (root, query, cb) ->
                cb.equal(root.get("year"), year);
    }
}

