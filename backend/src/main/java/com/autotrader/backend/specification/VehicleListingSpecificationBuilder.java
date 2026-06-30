package com.autotrader.backend.specification;

import com.autotrader.backend.dto.vehicleListing.VehicleListingSearchCriteria;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.data.jpa.domain.Specification;

public class VehicleListingSpecificationBuilder {
    public static Specification<VehicleListing> build(
            VehicleListingSearchCriteria filter
    ) {
        Specification<VehicleListing> spec = Specification.unrestricted();

        if (filter.getCity() != null) {
            spec = spec.and(
                    VehicleListingSpecification.hasCity(filter.getCity())
            );
        }

        if (filter.getMake() != null) {
            spec = spec.and(
                    VehicleListingSpecification.hasMake(filter.getMake())
            );
        }

        if (filter.getMaxPrice() != null) {
            spec = spec.and(
                    VehicleListingSpecification.hasMaxPrice(filter.getMaxPrice())
            );
        }

        if (filter.getMinPrice() != null) {
            spec = spec.and(
                    VehicleListingSpecification.hasMinPrice(filter.getMinPrice())
            );
        }

        return spec;
    }
}
