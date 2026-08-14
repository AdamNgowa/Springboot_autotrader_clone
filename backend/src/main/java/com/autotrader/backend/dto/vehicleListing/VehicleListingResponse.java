package com.autotrader.backend.dto.vehicleListing;

import com.autotrader.backend.dto.image.ImageResponse;
import com.autotrader.backend.dto.user.SellerResponse;
import com.autotrader.backend.entity.Enums.BodyType;
import com.autotrader.backend.entity.Enums.FuelType;
import com.autotrader.backend.entity.Enums.Transmission;

import java.math.BigDecimal;
import java.util.List;

public class VehicleListingResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;

    private String make;
    private String model;
    private Integer year;
    private Integer mileage;

    private FuelType fuelType;
    private Transmission transmission;
    private BodyType bodyType;

    private String city;

    private List<ImageResponse> images;

    private SellerResponse seller;

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
            String city
    ) {

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

    //Description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //Mileage
    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    //Fuel type
    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    //Transmission
    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
    }

    //Body type
    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
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

    //Images
    public List<ImageResponse> getImages() {
        return images;
    }

    public void setImages(List<ImageResponse> images) {
        this.images = images;
    }

    // Seller
    public SellerResponse getSeller() {
        return seller;
    }

    public void setSeller(SellerResponse seller) {
        this.seller = seller;
    }
}