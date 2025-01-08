package org.example;

import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Mondrian {

    private static Table summarized(Table partition, String dim, List<String> qiList) {
        for (String qi : qiList) {
            partition = partition.sortAscendingOn(qi);
            Column<?> col = partition.column(qi);
            if (!col.get(0).equals(col.get(col.size() - 1))) {
                String range = col.get(0) + "-" + col.get(col.size() - 1);
                partition.replaceColumn(qi, StringColumn.create(qi,
                        Collections.nCopies(partition.rowCount(), range)));
            }
        }
        return partition;
    }

    /**
     * Recursively anonymizes a given data partition by splitting it based on the most significant quasi-identifier
     * and ensuring that each resulting partition satisfies the k-anonymity requirement.
     *
     * @param partition The data partition to be anonymized, represented as a Table.
     * @param ranks A list of quasi-identifiers with their respective ranks, sorted in descending order of significance.
     * @param k The minimum number of records required in each partition to satisfy k-anonymity.
     * @param qiList A list of quasi-identifiers used for anonymization.
     * @return A Table representing the anonymized data partition.
     */
    private static Table anonymize(Table partition, List<Map.Entry<String, Integer>> ranks,
                                   int k, List<String> qiList) {
        String dim = ranks.get(0).getKey();

        partition = partition.sortAscendingOn(dim);
        int size = partition.rowCount();
        int mid = size / 2;

        Table leftPartition = partition.first(mid);
        Table rightPartition = partition.last(size - mid);

        if (leftPartition.rowCount() >= k && rightPartition.rowCount() >= k) {
            Table leftAnonymized = anonymize(leftPartition, ranks, k, qiList);
            Table rightAnonymized = anonymize(rightPartition, ranks, k, qiList);

            // Verify that both tables have the same structure
            if (!leftAnonymized.columnNames().equals(rightAnonymized.columnNames())) {
                throw new IllegalStateException("Column structures don't match during anonymization");
            }

            // Append tables while maintaining the column structure
            return leftAnonymized.append(rightAnonymized);
        }
        return summarized(partition, dim, qiList);
    }

    /**
     * Performs the Mondrian k-anonymity algorithm on the given data partition.
     * This method calculates the ranks of quasi-identifiers based on the number of unique values,
     * sorts them, and then calls the anonymize method to apply the k-anonymity process.
     *
     * @param partition The data partition to be anonymized, represented as a Table.
     * @param qiList A list of quasi-identifiers used for anonymization.
     * @param k The minimum number of records required in each partition to satisfy k-anonymity.
     * @return A Table representing the anonymized data partition.
     */
    public static Table mondrian(Table partition, List<String> qiList, int k) {
        // Calculate ranks (number of unique values for each QI)
        Map<String, Integer> ranks = new HashMap<>();
        for (String qi : qiList) {
            ranks.put(qi, partition.column(qi).unique().size());
        }

        // Sort ranks by value in descending order
        List<Map.Entry<String, Integer>> sortedRanks = ranks.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        return anonymize(partition, sortedRanks, k, qiList);
    }


    /**
     * Maps text values in the specified quasi-identifier columns of a table to numeric identifiers
     * using the provided hierarchy trees. This transformation is necessary for the anonymization process.
     *
     * @param df The table containing the data to be transformed.
     * @param qiList A list of quasi-identifier column names that need to be mapped.
     * @param hierarchyTreeDict A map where each key is a column name and the value is the corresponding
     *                          hierarchy tree used for mapping text values to numeric identifiers.
     * @return The transformed table with text values in the specified columns replaced by numeric identifiers.
     * @throws IllegalArgumentException if a specified column is not found in the table or if a hierarchy tree
     *                                  is not found for a column.
     * @throws IllegalStateException if a numeric value in the hierarchy tree is invalid.
     */
    private static Table mapTextToNum(Table df, List<String> qiList,
                                      Map<String, HierarchyTree> hierarchyTreeDict) {
        for (String column : qiList) {
            if (!df.containsColumn(column)) {
                throw new IllegalArgumentException("Column " + column + " not found in the table");
            }

            HierarchyTree hierarchyTree = hierarchyTreeDict.get(column);
            if (hierarchyTree == null) {
                throw new IllegalArgumentException("Hierarchy tree not found for column " + column);
            }

            Map<String, String> mapping = new HashMap<>();
            Column<?> col = df.column(column);

            if (col.isEmpty()) {
                continue;
            }

            // Get first non-null value to determine type
            Object firstValue = col.get(0);

            // Build mapping from hierarchy tree
            if (firstValue instanceof String) {
                // For string values
                hierarchyTree.getLeafIdDict().forEach((leafId, leaf) ->
                        mapping.put(leaf.getValue(), leafId));

                StringColumn stringCol = df.stringColumn(column);
                StringColumn newCol = StringColumn.create(column);

                for (String value : stringCol) {
                    newCol.append(mapping.getOrDefault(value, value));
                }
                df.replaceColumn(column, newCol);

            } else if (firstValue instanceof Integer || firstValue instanceof Double) {
                // For numeric values
                hierarchyTree.getLeafIdDict().forEach((leafId, leaf) -> {
                    try {
                        mapping.put(leaf.getValue(), leafId);
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException("Invalid numeric value in hierarchy tree: " + leaf.getValue());
                    }
                });

                StringColumn newCol = StringColumn.create(column);

                for (int i = 0; i < col.size(); i++) {
                    String value = String.valueOf(col.get(i));
                    newCol.append(mapping.getOrDefault(value, value));
                }
                df.replaceColumn(column, newCol);
            } else {
                throw new IllegalArgumentException("Unsupported column type for " + column + ": " +
                        firstValue.getClass().getSimpleName());
            }
        }
        return df;
    }



    /**
     * Converts numeric identifiers back to their original text values in the specified quasi-identifier columns
     * of a table using the provided hierarchy trees. This transformation is necessary to interpret the anonymized data.
     *
     * @param df The table containing the data to be transformed.
     * @param qiList A list of quasi-identifier column names that need to be mapped back to text.
     * @param hierarchyTreeDict A map where each key is a column name and the value is the corresponding
     *                          hierarchy tree used for mapping numeric identifiers back to text values.
     * @return The transformed table with numeric identifiers in the specified columns replaced by their original text values.
     * @throws IllegalArgumentException if a specified column is not found in the table or if a hierarchy tree
     *                                  is not found for a column.
     */
    private static Table mapNumToText(Table df, List<String> qiList,
                                      Map<String, HierarchyTree> hierarchyTreeDict) {
        for (String column : qiList) {
            HierarchyTree hierarchyTree = hierarchyTreeDict.get(column);
            StringColumn col = df.stringColumn(column);

            List<String> newValues = new ArrayList<>();
            for (String value : col) {
                if (value.matches("\\d+")) {
                    // Single number
                    HierarchyTreeNode leaf = hierarchyTree.getLeafIdDict().get(value);
                    newValues.add(leaf.getValue());
                } else if (value.contains("-")) {
                    // Interval
                    String[] parts = value.split("-");
                    HierarchyTreeNode commonAncestor =
                            hierarchyTree.findCommonAncestor(parts[0], parts[1]);
                    newValues.add(commonAncestor.getValue());
                } else {
                    newValues.add(value);
                }
            }

            df.replaceColumn(column, StringColumn.create(column, newValues));
        }
        return df;
    }


    /**
     * Checks if the given data table satisfies the k-anonymity requirement.
     * This is done by grouping the data based on the specified quasi-identifiers
     * and ensuring that each group contains at least k records.
     *
     * @param df The data table to be checked, represented as a Table.
     * @param qiList A list of quasi-identifier column names used for grouping the data.
     * @param k The minimum number of records required in each group to satisfy k-anonymity.
     * @return A boolean value indicating whether the data satisfies k-anonymity.
     *         Returns true if all groups have at least k records, false otherwise.
     */
    private static boolean checkKAnonymity(Table df, List<String> qiList, int k) {
        // Group by all quasi-identifiers and count occurrences
        Map<List<String>, Integer> groups = new HashMap<>();

        // For each row
        for (Row row : df) {
            // Get values for all quasi-identifiers
            List<String> qiValues = new ArrayList<>();
            for (String qi : qiList) {
                qiValues.add(row.getString(qi));
            }

            // Count occurrences
            groups.merge(qiValues, 1, Integer::sum);
        }

        // Check if any group has fewer than k records
        return groups.values().stream().allMatch(count -> count >= k);
    }

    /**
     * Runs the anonymization process on a dataset. This includes mapping textual data to numerical identifiers,
     * applying the Mondrian k-anonymity algorithm, verifying the k-anonymity of the result, and finally mapping
     * the numerical identifiers back to textual data.
     *
     * @param qiList The list of quasi-identifiers to be used in the anonymization process.
     * @param dataFile The path to the CSV file containing the dataset.
     * @param hierarchyFileDir The directory containing the CSV files defining the hierarchy trees for each
     *                         quasi-identifier.
     * @param k The minimum number of records required in each partition to satisfy k-anonymity.
     * @return A Table object containing the anonymized dataset.
     * @throws IOException If there is an error reading the CSV files.
     */
    public static Table runAnonymize(List<String> qiList, String dataFile,
                                     String hierarchyFileDir, int k) throws IOException {
        // Read the dataset
        Table df = Table.read().csv(dataFile);

        // Build hierarchy trees
        Map<String, HierarchyTree> hierarchyTreeDict = new HashMap<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(hierarchyFileDir), "*.csv")) {
            for (Path path : stream) {
                String hierarchyType = path.getFileName().toString().split("_")[2].split("\\.")[0];
                hierarchyTreeDict.put(hierarchyType, new HierarchyTree(path.toString()));
            }
        }

        // Map text to numbers
        df = mapTextToNum(df, qiList, hierarchyTreeDict);

        // Anonymize
        df = mondrian(df, qiList, k);

        // Verify k-anonymity
        if (!checkKAnonymity(df, qiList, k)) {
            throw new RuntimeException("Not all partitions are k-anonymous");
        }

        // Map numbers back to text
        df = mapNumToText(df, qiList, hierarchyTreeDict);

        return df;
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter the value of k (default is 5): ");
            int k = scanner.hasNextInt() ? scanner.nextInt() : 5;
            scanner.nextLine(); // consume the newline

            System.out.print("Enter the data file path (default is 'dataset/dataset.csv'): ");
            String dataFilePath = scanner.nextLine().trim();
            if (dataFilePath.isEmpty()) {
                dataFilePath = "dataset/dataset.csv";
            }

            System.out.print("Enter the anonymized file directory path (default is 'dataset/anonymized/'): ");
            String anonymizedFileDirPath = scanner.nextLine().trim();
            if (anonymizedFileDirPath.isEmpty()) {
                anonymizedFileDirPath = "dataset/anonymized/";
            }

            System.out.print("Enter the hierarchy file directory path (default is 'dataset/hierarchy/'): ");
            String hierarchyFileDirPath = scanner.nextLine().trim();
            if (hierarchyFileDirPath.isEmpty()) {
                hierarchyFileDirPath = "dataset/hierarchy/";
            }

            // Check if the data file exists
            if (!new File(dataFilePath).exists()) {
                throw new FileNotFoundException("Data file not found: " + dataFilePath);
            }

            // Check if the hierarchy file directory exists
            if (!new File(hierarchyFileDirPath).exists()) {
                throw new FileNotFoundException("Hierarchy file directory not found: " + hierarchyFileDirPath);
            }

            // Ensure the anonymized file directory exists
            File anonymizedDir = new File(anonymizedFileDirPath);
            if (!anonymizedDir.exists()) {
                anonymizedDir.mkdirs();
            }

            List<String> quasiIdentifiers = Arrays.asList(
                    "sex", "age", "race", "marital-status", "education",
                    "native-country", "workclass", "occupation"
            );
            List<String> sensitiveAttributes = Collections.singletonList("salary-class");
            List<String> identifier = Arrays.asList("ID", "soc_sec_id", "given_name", "surname");

            Table anonymizedDf = runAnonymize(quasiIdentifiers, dataFilePath, hierarchyFileDirPath, k);

            // Save the anonymized DataFrame
            String outputFilePath = anonymizedFileDirPath + "k_" + k + "_anonymized_dataset.csv";
            anonymizedDf.write().csv(outputFilePath);
            System.out.println("Anonymized data saved to: " + outputFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}