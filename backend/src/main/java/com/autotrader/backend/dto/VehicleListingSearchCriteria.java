package com.autotrader.backend.dto;

import java.math.BigDecimal;

public class VehicleListingSearchCriteria {
    private String city;
    private String make;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public VehicleListingSearchCriteria() {
    }

    public VehicleListingSearchCriteria(String city, String make, BigDecimal minPrice, BigDecimal maxPrice) {
        this.city = city;
        this.make = make;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

//    Getters and Setters
    //city
    public String getCity(){
        return city;
    }
    public void setCity(String city){
        this.city = city;
    }
    //make
    public String getMake(){
        return make;
    }
    public void setMake(String make){
        this.make = make;
    }

    //max price
    public BigDecimal getMaxPrice(){
        return maxPrice;
    }
    public void setMaxPrice(BigDecimal maxPrice){
        this.maxPrice = maxPrice;
    }

    //min price
    public BigDecimal getMinPrice(){
        return minPrice;
    }
    public void setMinPrice(BigDecimal minPrice){
        this.minPrice = minPrice;
    }
}
