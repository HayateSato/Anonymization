package com.example.test;

import tech.tablesaw.api.Table;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Mondrian {

    // Existing methods remain unchanged...

    /**
     * Runs the entire anonymization process with specified inputs.
     * This replaces the main method for better reusability.
     *
     * @param quasiIdentifiers List of quasi-identifier column names.
     * @param sensitiveAttributes List of sensitive column names (optional).
     * @param dataFilePath Path to the CSV file containing the dataset.
     * @param hierarchyFileDir Path to the directory containing hierarchy files.
     * @param anonymizedFileDir Path to the directory where anonymized files will be saved.
     * @param k The k-anonymity parameter.
     * @return The anonymized Table.
     * @throws IOException If any file operation fails.
     */


    public static Table runAnonymize(List<String> quasiIdentifiers, String dataFilePath,
                                     String hierarchyFileDir, int k) throws IOException {
        // Your existing anonymization logic here, for example:
        Table df = Table.read().csv(dataFilePath);

        // Additional anonymization logic as required...

        return df; // Return the anonymized table
    }



    public static Table runFullAnonymization(
            List<String> quasiIdentifiers,
            List<String> sensitiveAttributes,
            String dataFilePath,
            String hierarchyFileDir,
            String anonymizedFileDir,
            int k
    ) throws IOException {
        // Validate the input files and directories
        validateFileExists(dataFilePath);
        validateDirectoryExists(hierarchyFileDir);
        ensureDirectoryExists(anonymizedFileDir);

        // Run the anonymization
        Table anonymizedDf = runAnonymize(quasiIdentifiers, dataFilePath, hierarchyFileDir, k);

        // Save the anonymized data to the specified directory
        String outputFilePath = anonymizedFileDir + "k_" + k + "_anonymized_dataset.csv";
        anonymizedDf.write().csv(outputFilePath);

        System.out.println("Anonymized data saved to: " + outputFilePath);

        return anonymizedDf;
    }


    /**
     * Validates if a file exists at the given path.
     * @param filePath The file path to validate.
     * @throws IOException If the file does not exist.
     */
    private static void validateFileExists(String filePath) throws IOException {
        if (!new File(filePath).exists()) {
            throw new IOException("File not found: " + filePath);
        }
    }

    /**
     * Validates if a directory exists at the given path.
     * @param dirPath The directory path to validate.
     * @throws IOException If the directory does not exist.
     */
    private static void validateDirectoryExists(String dirPath) throws IOException {
        if (!new File(dirPath).exists()) {
            throw new IOException("Directory not found: " + dirPath);
        }
    }

    /**
     * Ensures that a directory exists, creating it if necessary.
     * @param dirPath The directory path to validate or create.
     */
    private static void ensureDirectoryExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
