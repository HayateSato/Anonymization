//package com.example.test;
//
//import org.opencv.core.*;
//import org.opencv.imgcodecs.Imgcodecs;
//
//import java.time.LocalDate;
//import java.util.*;
//
//public class OpenCVDemo {
//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//    }
//
//    public static void main(String[] args) {
//        // Create a new matrix (table equivalent)
//        Mat sales = new Mat(3, 4, CvType.CV_64FC1);
//
//        // Add data (equivalent to columns)
//        double[][] data = {
//                {2024, 1, 100.50, 0}, // Date: 2024-01-01, Amount: 100.50, Region: 0 (North)
//                {2024, 2, 200.75, 1}, // Date: 2024-01-02, Amount: 200.75, Region: 1 (South)
//                {2024, 3, 150.25, 0}  // Date: 2024-01-03, Amount: 150.25, Region: 0 (North)
//        };
//
//        for (int i = 0; i < data.length; i++) {
//            sales.put(i, 0, data[i]);
//        }
//
//        // Basic operations
//        // 1. Filter rows (equivalent to northRegion)
//        Mat northRegion = new Mat();
//        Core.compare(sales.col(2), new Scalar(150.0), northRegion, Core.CMP_GT);
//        List<Double> northAmounts = new ArrayList<>();
//        for (int i = 0; i < northRegion.rows(); i++) {
//            if (northRegion.get(i, 0)[0] != 0) {
//                northAmounts.add(sales.get(i, 2)[0]);
//            }
//        }
//
//        // 2. Calculate summary statistics
//        Mat amounts = sales.col(2);
//        Scalar sum = Core.sumElems(amounts);
//        double mean = Core.mean(amounts).val[0];
//        MatOfDouble stdDev = new MatOfDouble();
//        MatOfDouble meanMat = new MatOfDouble();
//        Core.meanStdDev(amounts, meanMat, stdDev);
//        double min = Core.minMaxLoc(amounts).minVal;
//        double max = Core.minMaxLoc(amounts).maxVal;
//
//        // 3. Sort the data (equivalent to sorted table)
//        Mat sortedIndices = new Mat();
//        Core.sortIdx(amounts, sortedIndices, Core.SORT_DESCENDING);
//
//        // 4. Add a new calculated column (tax)
//        Mat tax = new Mat();
//        Core.multiply(amounts, new Scalar(0.2), tax);
//
//        // Print results
//        System.out.println("Original Data:");
//        System.out.println(sales.dump());
//
//        System.out.println("\nNorth Region Amounts:");
//        System.out.println(northAmounts);
//
//        System.out.println("\nSummary Statistics:");
//        System.out.println("Count: " + amounts.rows());
//        System.out.println("Sum: " + sum.val[0]);
//        System.out.println("Mean: " + mean);
//        System.out.println("StdDev: " + stdDev.get(0, 0)[0]);
//        System.out.println("Min: " + min);
//        System.out.println("Max: " + max);
//
//        System.out.println("\nSorted Indices:");
//        System.out.println(sortedIndices.dump());
//
//        System.out.println("\nTax Column:");
//        System.out.println(tax.dump());
//
//        // Reading CSV file (simplified, as OpenCV doesn't have built-in CSV reading)
//        String filePath = "dataset/hierarchy/adult_hierarchy_age.csv";
//        Mat csvData = Imgcodecs.imread(filePath, Imgcodecs.IMREAD_ANYDEPTH);
//        if (csvData.empty()) {
//            System.out.println("Failed to read CSV file. OpenCV's imread is not ideal for CSV files.");
//        } else {
//            System.out.println("\nCSV Data (first 5 rows):");
//            System.out.println(csvData.submat(0, Math.min(5, csvData.rows()), 0, csvData.cols()).dump());
//        }
//    }
//}