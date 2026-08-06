package com.autotrader.backend.dto.vehicleListing;

import com.autotrader.backend.entity.Enums.BodyType;
import com.autotrader.backend.entity.Enums.FuelType;
import com.autotrader.backend.entity.Enums.Transmission;

import java.math.BigDecimal;

public class VehicleListingSearchCriteria {
    private String city;
    private String make;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BodyType bodyType;
    private FuelType fuelType;
    private Transmission transmission;
    private Integer year;

    public VehicleListingSearchCriteria() {
    }

    public VehicleListingSearchCriteria(String city, String make, BigDecimal minPrice, BigDecimal maxPrice) {
        this.city = city;
        this.make = make;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    // Getters and Setters
    // city
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    // make
    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }

    // min price
    public BigDecimal getMinPrice() {
        return minPrice;
    }
    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    // max price
    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    // body type
    public BodyType getBodyType() {
        return bodyType;
    }
    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    // fuel type
    public FuelType getFuelType() {
        return fuelType;
    }
    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    // transmission
    public Transmission getTransmission() {
        return transmission;
    }
    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
    }

    // year
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
}