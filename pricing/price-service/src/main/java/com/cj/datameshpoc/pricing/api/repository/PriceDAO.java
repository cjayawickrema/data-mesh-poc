package com.cj.datameshpoc.pricing.api.repository;

import com.cj.datameshpoc.pricing.api.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceDAO {

    private final JdbcTemplate jdbcTemplate;

    public PriceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Product> getProductPricesForCustomer(Long customerId, List<Long> productIds) {
        if (productIds.isEmpty()) return List.of(); // Return empty if no products provided

        // Generate dynamic placeholders for product IDs
        String inSql = String.join(",", productIds.stream().map(id -> "?").toArray(String[]::new));

        String sql = """            
            select
                p.product_id, 
                p.name, 
                COALESCE(ap.special_price, p.base_price) AS price 
            from product p
            left outer join agreement_product ap on ap.product_id = p.product_id
            left outer join agreement a on a.agreement_id = ap.agreement_id and customer_id = ?
            where p.product_id in (%s)
        """.formatted(inSql);

        // Prepare query parameters (customerId + productIds)
        Object[] params = new Object[productIds.size() + 1];
        params[0] = customerId;
        for (int i = 0; i < productIds.size(); i++) {
            params[i + 1] = productIds.get(i);
        }

        return jdbcTemplate.query(sql, params, (rs, rowNum) ->
                new Product(
                        rs.getLong("product_id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price").doubleValue()
                )
        );
    }
}
