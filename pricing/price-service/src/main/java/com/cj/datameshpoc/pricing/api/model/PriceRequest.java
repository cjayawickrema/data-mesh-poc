package com.cj.datameshpoc.pricing.api.model;

import java.util.List;

public class PriceRequest {

    private List<Long> productIds;

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }
}
