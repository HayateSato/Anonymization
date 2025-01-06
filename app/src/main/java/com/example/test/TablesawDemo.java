package org.example;

import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.*;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.File;
import java.time.LocalDate;
import java.util.List;


public class TablesawDemo {
    public static void main(String[] args) {
        // Create a new table
        Table sales = Table.create("Sales Data");

        // Add columns
        DateColumn date = DateColumn.create("Date",
                LocalDate.parse("2024-01-01"), LocalDate.parse("2024-01-02"), LocalDate.parse("2024-01-03"));
        DoubleColumn amount = DoubleColumn.create("Amount",
                new double[] {100.50, 200.75, 150.25});
        StringColumn region = StringColumn.create("Region",
                "North", "South", "North");

        sales.addColumns(date, amount, region);

        // Basic operations
        // 1. Filter rows
        Table northRegion = sales.where(sales.doubleColumn("Amount").isGreaterThan(150.0));

        // 2. Calculate summary statistics
        Table summary = sales.summarize("Amount",
                AggregateFunctions.count,
                AggregateFunctions.sum,
                AggregateFunctions.mean,
                AggregateFunctions.stdDev,
                AggregateFunctions.min,
                AggregateFunctions.max).by("Region");

        // 3. Sort the table
        Table sorted = sales.sortOn("-Amount");

        // 4. Add a new calculated column
        DoubleColumn tax = amount.multiply(0.2);
        tax.setName("Tax");
        sales.addColumns(tax);

        // Print results
        System.out.println("Original Table:");
        System.out.println(sales.print());

        System.out.println("\nNorth Region Only:");
        System.out.println(northRegion.print());

        System.out.println("\nSummary Statistics by Region:");
        System.out.println(summary.print());

        System.out.println("\nSorted Table:");
        System.out.println(sorted.print());

        List<String> uniqueRegionsCount = sales.stringColumn("Region").unique().asList();

        System.out.println("Count of unique regions: " + uniqueRegionsCount.get(0));

        String filePath = "dataset/hierarchy/adult_hierarchy_age.csv";
        Table df = Table.read().csv(CsvReadOptions.builder(new File(filePath))
                .header(false).separator(',').build());

        System.out.println(df.structure());
        System.out.println("Age hierarchy Table:");
        System.out.println(df.inRange(0, 5).print());

        //Table.read().csv(CsvReadOptions.builder(new File(filePath)).separator(',').header(false).build());               .header(false).delimiter(',').build());
    }
}
