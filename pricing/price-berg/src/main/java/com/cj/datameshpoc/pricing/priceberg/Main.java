package com.cj.datameshpoc.pricing.priceberg;

import org.apache.spark.sql.*;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
                .appName("SparkPostgresToIceberg")
                .master("local[*]")
                .config("spark.sql.extensions", "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions")
                .config("spark.sql.catalog.iceberg_catalog", "org.apache.iceberg.spark.SparkCatalog")
                .config("spark.sql.catalog.iceberg_catalog.type", "hadoop")
                .config("spark.sql.catalog.iceberg_catalog.warehouse", "s3a://price-bucket/") // S3 warehouse
                .config("spark.hadoop.fs.s3a.endpoint", "http://localhost:4566") // LocalStack S3 endpoint
                .config("spark.hadoop.fs.s3a.access.key", "test") // LocalStack access key
                .config("spark.hadoop.fs.s3a.secret.key", "test") // LocalStack secret key
                .config("spark.hadoop.fs.s3a.path.style.access", true) // Required for LocalStack
                .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
                .getOrCreate();

        String jdbcUrl = "jdbc:postgresql://localhost:5432/pricing";
        Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", "hello");
        connectionProperties.setProperty("password", "world");
        connectionProperties.setProperty("driver", "org.postgresql.Driver");

//        Dataset<?> df = spark.read()
//                .jdbc(jdbcUrl, "product", connectionProperties);

        // Example: Process data in chunks based on product_id ranges
        long startProductId = 1;
        long endProductId = 1; // fixme chunk size
        long chunkSize = 1;

        spark.sql("DROP TABLE IF EXISTS iceberg_catalog.product_iceberg");

        spark.sql("CREATE TABLE iceberg_catalog.product_iceberg (" +
                "product_id LONG, " +
                "customer_id LONG, " +
                "net_price DECIMAL(10, 2), " +
                "name STRING " +
                ") USING iceberg");
        System.out.println("Iceberg table created: iceberg_catalog.product_iceberg");

        while (startProductId <= getMaxProductId(jdbcUrl, connectionProperties)) {
            String sql = "SELECT p.product_id, p.name, COALESCE(ap.special_price, p.base_price) AS net_price, a.customer_id " +
                    "FROM product p LEFT JOIN agreement_product ap ON p.product_id = ap.product_id " +
                    "LEFT JOIN agreement a ON ap.agreement_id = a.agreement_id " +
                    "WHERE p.product_id >= " + startProductId + " AND p.product_id < " + endProductId;

            System.out.println("Fetching chunk:");
            System.out.println(sql);

            Dataset<Row> results = spark.read().jdbc(jdbcUrl, "(" + sql + ") as combined_data", connectionProperties);

            System.out.println("Found following data");
            // Process the results (e.g., write to Iceberg, perform aggregations)
            results.show();

            results.write()
                    .format("iceberg")
                    .mode("overwrite")
                    .save("iceberg_catalog.product_iceberg");

            startProductId = endProductId;
            endProductId += chunkSize;
        }

        Dataset<Row> results = spark.sql("SELECT * FROM iceberg_catalog.product_iceberg");
        results.show();

        spark.stop();
    }

    private static long getMaxProductId(String jdbcUrl, Properties connectionProperties) {
        return 5; // fixme
    }
}
