package com.autotrader.backend.dto.vehicleListing;

import com.autotrader.backend.entity.Enums.BodyType;
import com.autotrader.backend.entity.Enums.FuelType;
import com.autotrader.backend.entity.Enums.Transmission;

import java.math.BigDecimal;

public class CreateListingRequest {

    private String title;
    private String description;
    private BigDecimal price;
    private Integer year;
    private String make;
    private String model;
    private Integer mileage;
    private FuelType fuelType;
    private Transmission transmission;
    private BodyType bodyType;
    private String city;


    public CreateListingRequest() {
    }

    public CreateListingRequest(
            String title,
            String description,
            BigDecimal price,
            int year,
            String make,
            String model,
            int mileage,
            FuelType fuelType,
            Transmission transmission,
            BodyType bodyType,
            String city
            ) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.year = year;
        this.make = make;
        this.model = model;
        this.mileage = mileage;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.bodyType = bodyType;
        this.city = city;

    }

    // Getters and Setters

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

    public int getYear() {
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

    public int getMileage() {
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