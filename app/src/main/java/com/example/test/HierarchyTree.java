//package com.example.test;
//
//import com.opencsv.CSVReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.*;
//
//public class HierarchyTree {
//    private final String hierarchyType;
//    private final Map<String, HierarchyTreeNode> nodeDict;
//    private final HierarchyTreeNode root;
//    private final Map<String, HierarchyTreeNode> leafIdDict;
//
//    /**
//     * Constructs a HierarchyTree from a CSV file located at the specified file path.
//     * The CSV file is expected to contain hierarchy data where each row represents a path in the hierarchy.
//     * The hierarchy type is inferred from the file name.
//     *
//     * @param filePath the path to the CSV file containing the hierarchy data
//     * @throws IOException if an I/O error occurs while reading the file
//     */
//    public HierarchyTree(String filePath) throws IOException {
//        List<String[]> csvData = readCsv(filePath);
//
//        this.hierarchyType = new File(filePath).getName().split("_")[2];
//        this.nodeDict = buildTree(csvData);
//        this.root = this.nodeDict.get("#");
//        this.leafIdDict = buildLeafIdDict();
//        saveCoveredSubtreeNodes();
//    }
//
//    /**
//     * Reads the CSV file and parses its content into a list of string arrays.
//     *
//     * @param filePath the path to the CSV file
//     * @return a list of string arrays where each array represents a row in the CSV
//     * @throws IOException if an I/O error occurs while reading the file
//     */
//    private List<String[]> readCsv(String filePath) throws IOException {
//        List<String[]> csvData = new ArrayList<>();
//        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
//            String[] nextLine;
//            while ((nextLine = reader.readNext()) != null) {
//                csvData.add(nextLine);
//            }
//        }
//        return csvData;
//    }
//
//    /**
//     * Builds a hierarchy tree from the given CSV data.
//     * Each row represents a path in the hierarchy, with the last column being the leaf node ID.
//     * The method constructs nodes and establishes parent-child relationships.
//     *
//     * @param csvData the CSV data where each row represents a path in the hierarchy
//     * @return a map of node values to their corresponding HierarchyTreeNode objects
//     */
//    private Map<String, HierarchyTreeNode> buildTree(List<String[]> csvData) {
//        Map<String, HierarchyTreeNode> nodeDict = new HashMap<>();
//
//        // Create root node
//        nodeDict.put("#", new HierarchyTreeNode("#", null, false, "0", 0));
//
//        // Process each row
//        for (String[] row : csvData) {
//            List<String> rowList = Arrays.asList(row);
//            Collections.reverse(rowList);
//
//            // Process each value in the row
//            for (int i = 0; i < rowList.size(); i++) {
//                String value = rowList.get(i);
//                boolean isLeaf = (i == rowList.size() - 2);
//
//                if (i != rowList.size() - 1) {
//                    if (!nodeDict.containsKey(value)) {
//                        HierarchyTreeNode parent = nodeDict.get(rowList.get(i - 1));
//                        HierarchyTreeNode newNode = new HierarchyTreeNode(value, parent, isLeaf, i + 1);
//                        nodeDict.put(value, newNode);
//                        parent.getChildren().add(newNode);
//                    }
//                } else {
//                    // This is the first column representing IDs for leaf nodes
//                    nodeDict.get(rowList.get(i - 1)).setLeafId(value);
//                }
//            }
//        }
//        return nodeDict;
//    }
//
//    private Map<String, HierarchyTreeNode> buildLeafIdDict() {
//        Map<String, HierarchyTreeNode> leafIdDict = new HashMap<>();
//        for (HierarchyTreeNode node : nodeDict.values()) {
//            if (node.isLeaf()) {
//                leafIdDict.put(node.getLeafId(), node);
//            }
//        }
//        return leafIdDict;
//    }
//
//    private void saveCoveredSubtreeNodes() {
//        for (HierarchyTreeNode node : nodeDict.values()) {
//            HierarchyTreeNode parent = node.getParent();
//            while (parent != null) {
//                parent.getCoveredSubtreeNodes().add(node);
//                parent = parent.getParent();
//            }
//        }
//    }
//
//    /**
//     * Finds the common ancestor of two leaf nodes identified by their IDs.
//     *
//     * @param leaf1Id the ID of the first leaf node
//     * @param leaf2Id the ID of the second leaf node
//     * @return the common ancestor node of the two leaf nodes, or null if either leaf node is not found
//     */
//    public HierarchyTreeNode findCommonAncestor(String leaf1Id, String leaf2Id) {
//        HierarchyTreeNode leaf1 = leafIdDict.get(leaf1Id);
//        HierarchyTreeNode leaf2 = leafIdDict.get(leaf2Id);
//
//        if (leaf1 == null || leaf2 == null) {
//            return null;
//        }
//
//        Set<HierarchyTreeNode> ancestors = new HashSet<>();
//
//        // Find all ancestors of leaf1
//        while (leaf1 != null) {
//            ancestors.add(leaf1);
//            leaf1 = leaf1.getParent();
//        }
//
//        // Find first common ancestor
//        while (leaf2 != null && !ancestors.contains(leaf2)) {
//            leaf2 = leaf2.getParent();
//        }
//
//        return leaf2;
//    }
//
//    public Map<String, HierarchyTreeNode> getNodeDict() {
//        return nodeDict;
//    }
//
//    public Map<String, HierarchyTreeNode> getLeafIdDict() {
//        return leafIdDict;
//    }
//}
