//package com.example.test;
//
//import java.io.*;
//import java.util.*;
//import java.util.stream.Collectors;
//
// class Mondrian {
//
//    private static List<List<String>> summarized(List<List<String>> partition, int[] qiIndices) {
//        for (int qiIndex : qiIndices) {
//            List<String> column = partition.stream().map(row -> row.get(qiIndex)).collect(Collectors.toList());
//            String min = Collections.min(column);
//            String max = Collections.max(column);
//            if (!min.equals(max)) {
//                for (List<String> row : partition) {
//                    row.set(qiIndex, min + "-" + max);
//                }
//            }
//        }
//        return partition;
//    }
//
//    private static List<List<String>> anonymize(List<List<String>> partition, List<Integer> ranks, int k, int[] qiIndices) {
//        int dim = ranks.get(0);
//
//        // Sort based on the most significant QI
//        partition.sort(Comparator.comparing(row -> row.get(dim)));
//
//        int size = partition.size();
//        int mid = size / 2;
//
//        List<List<String>> leftPartition = partition.subList(0, mid);
//        List<List<String>> rightPartition = partition.subList(mid, size);
//
//        if (leftPartition.size() >= k && rightPartition.size() >= k) {
//            List<List<String>> leftAnonymized = anonymize(leftPartition, ranks, k, qiIndices);
//            List<List<String>> rightAnonymized = anonymize(rightPartition, ranks, k, qiIndices);
//
//            List<List<String>> combined = new ArrayList<>(leftAnonymized);
//            combined.addAll(rightAnonymized);
//            return combined;
//        }
//        return summarized(partition, qiIndices);
//    }
//
//    public static List<List<String>> mondrian(List<List<String>> partition, int[] qiIndices, int k) {
//        // Calculate ranks (number of unique values for each QI)
//        Map<Integer, Integer> ranks = new HashMap<>();
//        for (int qiIndex : qiIndices) {
//            ranks.put(qiIndex, (int) partition.stream().map(row -> row.get(qiIndex)).distinct().count());
//        }
//
//        // Sort ranks by value in descending order
//        List<Integer> sortedRanks = ranks.entrySet()
//                .stream()
//                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//
//        return anonymize(partition, sortedRanks, k, qiIndices);
//    }
//
//    private static boolean checkKAnonymity(List<List<String>> df, int[] qiIndices, int k) {
//        Map<List<String>, Integer> groups = new HashMap<>();
//        for (List<String> row : df) {
//            List<String> qiValues = new ArrayList<>();
//            for (int qiIndex : qiIndices) {
//                qiValues.add(row.get(qiIndex));
//            }
//            groups.merge(qiValues, 1, Integer::sum);
//        }
//        return groups.values().stream().allMatch(count -> count >= k);
//    }
//
//    public static List<List<String>> loadCsv(String filePath) throws IOException {
//        List<List<String>> rows = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                rows.add(Arrays.asList(line.split(",")));
//            }
//        }
//        return rows;
//    }
//
//    public static void saveCsv(List<List<String>> data, String filePath) throws IOException {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
//            for (List<String> row : data) {
//                bw.write(String.join(",", row));
//                bw.newLine();
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            String dataFilePath = "dataset.csv";
//            int k = 5;
//            int[] qiIndices = {0, 1, 2}; // Quasi-identifier column indices
//
//            List<List<String>> data = loadCsv(dataFilePath);
//            List<List<String>> anonymizedData = mondrian(data, qiIndices, k);
//
//            if (!checkKAnonymity(anonymizedData, qiIndices, k)) {
//                throw new RuntimeException("Not all partitions are k-anonymous");
//            }
//
//            saveCsv(anonymizedData, "anonymized_dataset.csv");
//
//            System.out.println("Anonymized data saved to: anonymized_dataset.csv");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}