package com.cj.datameshpoc.pricing.api.service;

import com.cj.datameshpoc.pricing.api.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PriceService {

    // Simulated method to get products and their prices
    public List<Product> getProductsWithPrices(List<Integer> productIds) {
        // Simulated product data. In a real application, you would fetch this from a database or an external service
        List<Product> products = new ArrayList<>();

        for (Integer productId : productIds) {
            // Simulate getting product details. You would query your database here.
            // For example, product name and price could come from a DB or another service.
            String productName = "Product " + productId; // Example name
            Double productPrice = 19.99 * productId;    // Example price calculation

            // Create a Product object and add to the list
            Product product = new Product(productId, productName, productPrice);
            products.add(product);
        }

        return products;
    }
}
