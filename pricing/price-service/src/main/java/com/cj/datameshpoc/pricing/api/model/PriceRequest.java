package com.cj.datameshpoc.pricing.api.model;

import java.util.List;

public class PriceRequest {

    private List<Integer> productIds;

    public List<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Integer> productIds) {
        this.productIds = productIds;
    }
}
