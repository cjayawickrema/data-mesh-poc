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

        Dataset<?> df = spark.read()
                .jdbc(jdbcUrl, "product", connectionProperties);

        df.show();

        df.write()
                .format("iceberg")
                .mode("overwrite")
                .save("iceberg_catalog.product_iceberg");

        spark.stop();
    }
}
