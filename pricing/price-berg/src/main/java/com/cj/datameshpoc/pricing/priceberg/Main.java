package com.cj.datameshpoc.pricing.priceberg;

import org.apache.spark.sql.*;
import org.apache.spark.sql.functions;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        // Initialize Spark Session with Iceberg & S3 configs
        SparkSession spark = SparkSession.builder()
                .appName("SparkIcebergS3")
                .master("local[*]")  // Use cluster mode in production
                .config("spark.sql.catalog.pricing_catalog", "org.apache.iceberg.spark.SparkCatalog")
                .config("spark.sql.catalog.pricing_catalog.type", "hadoop")
                .config("spark.sql.catalog.pricing_catalog.warehouse", "s3a://pricing-bucket/pricing")
//                .config("spark.sql.catalog.iceberg", "org.apache.iceberg.spark.SparkCatalog") // Correct Iceberg catalog class
//                .config("spark.sql.catalog.iceberg.type", "hadoop")  // Use the 'hadoop' type for local/EMR/Hadoop cluster
//                .config("spark.sql.catalog.iceberg.warehouse", "s3a://my-iceberg-bucket/warehouse")
                .config("spark.hadoop.fs.s3a.endpoint", "http://localhost:4566")
                .config("spark.hadoop.fs.s3a.access.key", "test")
                .config("spark.hadoop.fs.s3a.secret.key", "test")
                .config("spark.hadoop.fs.s3a.connection.ssl.enabled", "false")
                .config("spark.hadoop.fs.s3a.path.style.access", "true")
                .getOrCreate();

        // PostgreSQL connection properties
        String jdbcUrl = "jdbc:postgresql://localhost:5432/pricing";
        String jdbcUn = "hello";
        String jdbcPw = "world";

        Properties connectionProps = new Properties();
        connectionProps.put("user", jdbcUn);
        connectionProps.put("password", jdbcPw);
        connectionProps.put("driver", "org.postgresql.Driver");

        // Load product table with parallel read
        Dataset<Row> productDF = spark.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", "product")
                .option("user", jdbcUn)
                .option("password", jdbcPw)
                .option("numPartitions", 8)  // Parallel read
                .option("partitionColumn", "product_id")
                .option("lowerBound", 1)
                .option("upperBound", 1000000) // Adjust dynamically
                .load();

        // Load agreement_product table with parallel read
        Dataset<Row> agreementProductDF = spark.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", "agreement_product")
                .option("user", jdbcUn)
                .option("password", jdbcPw)
                .option("numPartitions", 8)
                .option("partitionColumn", "agreement_product_id")
                .option("lowerBound", 1)
                .option("upperBound", 1000000)
                .load();

        // Load agreement table with parallel read
        Dataset<Row> agreementDF = spark.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", "agreement")
                .option("user", jdbcUn)
                .option("password", jdbcPw)
                .option("numPartitions", 4)
                .option("partitionColumn", "agreement_id")
                .option("lowerBound", 1)
                .option("upperBound", 1000000)
                .load();

        // Join agreement_product with agreement to get customer_id
        Dataset<Row> agreementProductWithCustomer = agreementProductDF
                .join(agreementDF, "agreement_id")
                .select("product_id", "customer_id", "special_price");

        // Join with products
        Dataset<Row> resultDF = productDF
                .join(agreementProductWithCustomer, "product_id", "left_outer")
                .withColumn("price", functions.coalesce(agreementProductWithCustomer.col("special_price"), productDF.col("base_price")))
                .select("product_id", "name", "customer_id", "price");

        // Create Iceberg table only if not exists
//        spark.sql("CREATE DATABASE IF NOT EXISTS pricing_catalog.pricing_db");
        spark.sql("CREATE TABLE IF NOT EXISTS pricing_catalog.price_berg " +
                "(product_id INT, name STRING, customer_id INT, price DECIMAL(10,2)) " +
                "USING iceberg PARTITIONED BY (customer_id)");

        // Append new data to Iceberg table (incremental processing)
        resultDF.write()
                .format("iceberg")
                .mode("append")
//                .option("catalog", "iceberg")  // Specify the catalog name here
                .save("price_berg.pricing");

        // Stop Spark session
        spark.stop();
    }
}
