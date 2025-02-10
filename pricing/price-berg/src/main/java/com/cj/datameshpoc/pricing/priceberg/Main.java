package com.cj.datameshpoc.pricing.priceberg;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

public class Main {
    public static void main(String[] args) {
        // Initialize Spark session
        SparkSession spark = SparkSession.builder()
                .appName("Spark Example - Data Transformation")
                .master("local[*]")
                .getOrCreate();

        // Load a sample CSV dataset
        String filePath = "src/main/resources/people.csv";
        Dataset<Row> df = spark.read()
                .option("header", "true")
                .csv(filePath);

        // Show the loaded data
        System.out.println("Loaded Data:");
        df.show();

        // Perform some transformations: add a new column with age doubled
        Dataset<Row> transformedDF = df.withColumn("double_age", functions.col("age").multiply(2));

        // Show the transformed data
        System.out.println("Transformed Data:");
        transformedDF.show();

        // Filter the data to show only people above 30 years old
        Dataset<Row> filteredDF = transformedDF.filter(functions.col("age").gt(30));

        // Show the filtered data
        System.out.println("Filtered Data (age > 30):");
        filteredDF.show();

        // Stop the Spark session
        spark.stop();
    }
}
