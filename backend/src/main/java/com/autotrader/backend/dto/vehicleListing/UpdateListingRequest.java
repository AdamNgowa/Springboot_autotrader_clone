package com.autotrader.backend.dto.vehicleListing;

import com.autotrader.backend.entity.Enums.BodyType;
import com.autotrader.backend.entity.Enums.FuelType;
import com.autotrader.backend.entity.Enums.Transmission;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class UpdateListingRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Schema(
            description = "Short title describing the vehicle listing",
            example = "2019 Toyota Corolla 1.8 Hybrid"
    )
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Schema(
            description = "Detailed description of the vehicle",
            example = "Well maintained, accident free, full service history."
    )
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    @Schema(
            description = "Asking price of the vehicle",
            example = "1850000"
    )
    private BigDecimal price;

    @NotNull(message = "Year is required")
    @Min(value = 1886, message = "Year must be 1886 or later")
    @Max(value = 2100, message = "Year must not exceed 2100")
    @Schema(
            description = "Manufacturing year of the vehicle",
            example = "2019"
    )
    private Integer year;

    @NotBlank(message = "Make is required")
    @Size(max = 50, message = "Make must not exceed 50 characters")
    @Schema(
            description = "Vehicle manufacturer",
            example = "Toyota"
    )
    private String make;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    @Schema(
            description = "Vehicle model",
            example = "Corolla"
    )
    private String model;

    @NotNull(message = "Mileage is required")
    @PositiveOrZero(message = "Mileage cannot be negative")
    @Schema(
            description = "Current mileage in kilometres",
            example = "65000"
    )
    private Integer mileage;

    @NotNull(message = "Fuel type is required")
    @Schema(
            description = "Fuel type of the vehicle",
            example = "PETROL"
    )
    private FuelType fuelType;

    @NotNull(message = "Transmission is required")
    @Schema(
            description = "Transmission type of the vehicle",
            example = "AUTOMATIC"
    )
    private Transmission transmission;

    @NotNull(message = "Body type is required")
    @Schema(
            description = "Body type of the vehicle",
            example = "SEDAN"
    )
    private BodyType bodyType;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Schema(
            description = "City where the vehicle is located",
            example = "Cape Town"
    )
    private String city;

    public UpdateListingRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}