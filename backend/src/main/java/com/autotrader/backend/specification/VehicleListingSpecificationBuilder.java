package com.autotrader.backend.specification;

import com.autotrader.backend.dto.vehicleListing.VehicleListingSearchCriteria;
import com.autotrader.backend.entity.VehicleListing;
import org.springframework.data.jpa.domain.Specification;

public class VehicleListingSpecificationBuilder {
    public static Specification<VehicleListing> build(
            VehicleListingSearchCriteria filter
    ) {
        Specification<VehicleListing> spec = Specification.unrestricted();

        if (filter.getCity() != null && !filter.getCity().isBlank()) {
            spec = spec.and(
                    VehicleListingSpecification.hasCity(filter.getCity())
            );
        }

        if (filter.getMake() != null && !filter.getMake().isBlank()) {
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

        if (filter.getBodyType() != null) {
            spec = spec.and(
                    VehicleListingSpecification.hasBodyType(
                            filter.getBodyType()
                    )
            );
        }

        if (filter.getFuelType() != null) {
            spec = spec.and(
                    VehicleListingSpecification.hasFuelType(
                            filter.getFuelType()
                    )
            );
        }

        if (filter.getTransmission() != null) {
            spec = spec.and(
                    VehicleListingSpecification.hasTransmission(
                            filter.getTransmission()
                    )
            );
        }

        if (filter.getYear() != null) {
            spec = spec.and(
                    VehicleListingSpecification.hasYear(
                            filter.getYear()
                    )
            );
        }


        return spec;
    }
}
