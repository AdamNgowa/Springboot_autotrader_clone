package com.autotrader.backend.config;

import com.autotrader.backend.entity.*;
import com.autotrader.backend.entity.Enums.BodyType;
import com.autotrader.backend.entity.Enums.FuelType;
import com.autotrader.backend.entity.Enums.ListingStatus;
import com.autotrader.backend.entity.Enums.Transmission;
import com.autotrader.backend.repository.VehicleListingRepository;
import com.autotrader.backend.repository.UserRepository;
import com.autotrader.backend.service.VehicleListingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ListingDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VehicleListingRepository vehicleListingRepository;
    private final VehicleListingService vehicleListingService;

    public ListingDataInitializer(UserRepository userRepository,
                                  VehicleListingService vehicleListingService,
                                  VehicleListingRepository vehicleListingRepository) {
        this.userRepository = userRepository;
        this.vehicleListingRepository = vehicleListingRepository;
        this.vehicleListingService = vehicleListingService;

    }

    @Override
    public void run(String... args) {

        User user = userRepository.findByEmail("john@example.com")
                .orElseThrow(() -> new RuntimeException("User not found"));

        VehicleListing listing = new VehicleListing();

        listing.setTitle("Toyota Prado TX 2018");
        listing.setDescription("Well maintained, single owner vehicle");
        listing.setPrice(new BigDecimal("4500000"));
        listing.setYear(2018);
        listing.setMake("Toyota");
        listing.setModel("Prado");
        listing.setMileage(85000);
        listing.setFuelType(FuelType.DIESEL);
        listing.setTransmission(Transmission.AUTOMATIC);
        listing.setBodyType(BodyType.SUV);
        listing.setCity("Nairobi");
        listing.setStatus(ListingStatus.ACTIVE);


         listing.setSeller(user);
//         vehicleListingRepository.save(listing);
         System.out.println("Vehicle Listing Saved!");


    }
}