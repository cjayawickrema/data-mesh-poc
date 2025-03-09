package com.cj.datameshpoc.pricing.priceberg;

import org.apache.iceberg.CatalogProperties;
import org.apache.iceberg.Schema;
import org.apache.iceberg.Table;
import org.apache.iceberg.aws.glue.GlueCatalog;
import org.apache.iceberg.catalog.TableIdentifier;
import org.apache.iceberg.types.Types;

import java.util.*;

public class IcebergTableCreation {

    public static void main(String[] args) {
        Map<String, String> properties = new HashMap<>();
        properties.put(CatalogProperties.CATALOG_IMPL, "org.apache.iceberg.aws.glue.GlueCatalog");
        properties.put(CatalogProperties.WAREHOUSE_LOCATION, "s3://my-bucket/iceberg/"); // Replace with your S3 bucket and path.
        properties.put("glue.endpoint", "http://localhost:4566"); // LocalStack Glue endpoint
        properties.put("s3.endpoint", "http://localhost:4566"); // LocalStack S3 endpoint
        properties.put("s3.path-style-access", "true"); // Required for LocalStack
        properties.put("s3.region", "us-east-1"); // Localstack region.
        properties.put("glue.region", "us-east-1");

        GlueCatalog catalog = new GlueCatalog();
        catalog.initialize("iceberg_catalog", properties);

        Schema schema = new Schema(
                Types.NestedField.required(1, "id", Types.LongType.get()),
                Types.NestedField.required(2, "product_name", Types.StringType.get())
        );

        Table table = catalog.createTable(TableIdentifier.of("default.product_iceberg"), schema);
        System.out.println("Iceberg table created: " + table.name());
    }
}
