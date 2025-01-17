package org.example;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class HierarchyTree {
    private final String hierarchyType;
    private final Map<String, HierarchyTreeNode> nodeDict;
    private final HierarchyTreeNode root;
    private final Map<String, HierarchyTreeNode> leafIdDict;

    /**
     * Constructs a HierarchyTree from a CSV file located at the specified file path.
     * The CSV file is expected to contain hierarchy data where each row represents a path in the hierarchy.
     * The hierarchy type is inferred from the file name.
     *
     * @param filePath the path to the CSV file containing the hierarchy data
     * @throws IOException if an I/O error occurs while reading the file
     */
    public HierarchyTree(String filePath) throws IOException {
        Table df = Table.read().csv(CsvReadOptions.builder(new File(filePath))
                .header(false)
                .build());

        this.hierarchyType = new File(filePath).getName().split("_")[2];
        this.nodeDict = buildTree(df);
        this.root = this.nodeDict.get("#");
        this.leafIdDict = buildLeafIdDict();
        saveCoveredSubtreeNodes();
    }

    /**
     * Builds a hierarchy tree from the given table data.
     * Each row in the table represents a path in the hierarchy, with the last column being the leaf node ID.
     * The method constructs nodes and establishes parent-child relationships based on the table data.
     *
     * @param df the table containing hierarchy data, where each row represents a path in the hierarchy
     * @return a map of node values to their corresponding HierarchyTreeNode objects, representing the entire hierarchy
     */
    private Map<String, HierarchyTreeNode> buildTree(Table df) {
        Map<String, HierarchyTreeNode> nodeDict = new HashMap<>();

        // Create root node
        nodeDict.put("#", new HierarchyTreeNode("#", null, false, "0", 0));

        // Process each row
        for (int rowNum = 0; rowNum < df.rowCount(); rowNum++) {
            List<String> rowList = new ArrayList<>();
            for (int col = 0; col < df.columnCount(); col++) {
                rowList.add(df.get(rowNum, col).toString());
            }
            Collections.reverse(rowList);

            // Process each value in the row
            for (int i = 0; i < rowList.size(); i++) {
                String value = rowList.get(i);
                boolean isLeaf = (i == rowList.size() - 2);

                if (i != rowList.size() - 1) {
                    if (!nodeDict.containsKey(value)) {
                        HierarchyTreeNode parent = nodeDict.get(rowList.get(i - 1));
                        HierarchyTreeNode newNode = new HierarchyTreeNode(value, parent, isLeaf, i + 1);
                        nodeDict.put(value, newNode);
                        parent.getChildren().add(newNode);
                    }
                } else {
                    // This is the first column representing IDs for leaf nodes
                    nodeDict.get(rowList.get(i - 1)).setLeafId(value);
                }
            }
        }
        return nodeDict;
    }

    private Map<String, HierarchyTreeNode> buildLeafIdDict() {
        Map<String, HierarchyTreeNode> leafIdDict = new HashMap<>();
        for (HierarchyTreeNode node : nodeDict.values()) {
            if (node.isLeaf()) {
                leafIdDict.put(node.getLeafId(), node);
            }
        }
        return leafIdDict;
    }

    private void saveCoveredSubtreeNodes() {
        for (HierarchyTreeNode node : nodeDict.values()) {
            HierarchyTreeNode parent = node.getParent();
            while (parent != null) {
                parent.getCoveredSubtreeNodes().add(node);
                parent = parent.getParent();
            }
        }
    }


    /**
     * Finds the common ancestor of two leaf nodes identified by their IDs.
     *
     * @param leaf1Id the ID of the first leaf node
     * @param leaf2Id the ID of the second leaf node
     * @return the common ancestor node of the two leaf nodes, or null if either leaf node is not found
     */
    public HierarchyTreeNode findCommonAncestor(String leaf1Id, String leaf2Id) {
        HierarchyTreeNode leaf1 = leafIdDict.get(leaf1Id);
        HierarchyTreeNode leaf2 = leafIdDict.get(leaf2Id);

        if (leaf1 == null || leaf2 == null) {
            return null;
        }

        Set<HierarchyTreeNode> ancestors = new HashSet<>();

        // Find all ancestors of leaf1
        while (leaf1 != null) {
            ancestors.add(leaf1);
            leaf1 = leaf1.getParent();
        }

        // Find first common ancestor
        while (leaf2 != null && !ancestors.contains(leaf2)) {
            leaf2 = leaf2.getParent();
        }

        return leaf2;
    }

    public Map<String, HierarchyTreeNode> getNodeDict() {
        return nodeDict;
    }

    public Map<String, HierarchyTreeNode> getLeafIdDict() {
        return leafIdDict;
    }
}
