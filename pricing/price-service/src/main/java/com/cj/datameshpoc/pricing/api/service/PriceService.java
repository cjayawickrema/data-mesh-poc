package com.cj.datameshpoc.pricing.api.service;

import com.cj.datameshpoc.pricing.api.model.Product;
import com.cj.datameshpoc.pricing.api.repository.PriceDAO;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PriceService {

    private final PriceDAO priceDAO;

    public PriceService(PriceDAO productPriceDAO) {
        this.priceDAO = productPriceDAO;
    }

    public List<Product> getPricesForCustomer(Long customerId, List<Long> productIds) {
        return priceDAO.getProductPricesForCustomer(customerId, productIds);
    }
}


