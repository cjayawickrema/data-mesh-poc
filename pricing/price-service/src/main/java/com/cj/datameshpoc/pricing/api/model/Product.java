package com.cj.datameshpoc.pricing.api.model;

public class Product {

    private Integer productId;
    private String name;
    private Double price;

    // Constructor, Getters, Setters
    public Product(Integer productId, String name, Double price) {
        this.productId = productId;
        this.name = name;
        this.price = price;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
