package com.cj.datameshpoc.pricing.api.controller;

import com.cj.datameshpoc.pricing.api.model.PriceRequest;
import com.cj.datameshpoc.pricing.api.model.Product;
import com.cj.datameshpoc.pricing.api.service.PriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    // POST endpoint for /customer/{id}/order/prices
    @PostMapping("/{id}/order/prices")
    public ResponseEntity<List<Product>> getPrices(@PathVariable("id") Long customerId, @RequestBody PriceRequest priceRequest) {
        List<Integer> productIds = priceRequest.getProductIds(); // Get the list of product IDs

        // Fetch the products with prices using the service
        List<Product> products = priceService.getProductsWithPrices(productIds);

        // Return the list of products and their prices
        return ResponseEntity.ok(products);
    }
}