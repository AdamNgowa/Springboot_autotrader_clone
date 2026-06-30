package com.autotrader.backend.dto.vehicleListing;

import java.math.BigDecimal;

public class VehicleListingResponse {

    private Long id;
    private String title;
    private BigDecimal price;
    private String make;
    private String model;
    private Integer year;
    private String city;

    // Default Constructor
    public VehicleListingResponse() {
    }

    // Parameterized Constructor
    public VehicleListingResponse(
            Long id,
            String title,
            BigDecimal price,
            String make,
            String model,
            Integer year,
            String city) {

        this.id = id;
        this.title = title;
        this.price = price;
        this.make = make;
        this.model = model;
        this.year = year;
        this.city = city;
    }

    // --- Getters and Setters ---

    // Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Price
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // Make
    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    // Model
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    // Year
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    // City
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}