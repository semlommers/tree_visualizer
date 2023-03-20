package Graph.GraphAlgorithms.DistanceMeasures;

import Graph.DecisionTree.DecisionTreeEdge;
import Graph.DecisionTree.DecisionTreeNode;
import Graph.Tree;

import java.util.List;
import java.util.Objects;

public class EditDistanceNoChildSwapping implements TreeDistanceMeasure<DecisionTreeNode, DecisionTreeEdge> {
    String name = "EditDistance";
    private final Integer addNodeCost;
    private final Integer changeSplitFeature;

    public EditDistanceNoChildSwapping(int addNodeCost, int changSplitFeature) {
        this.addNodeCost = addNodeCost;
        this.changeSplitFeature = changSplitFeature;
        this.name = this.name + addNodeCost + changSplitFeature;
    }

    @Override
    public int getDistance(Tree<DecisionTreeNode, DecisionTreeEdge> t1, Tree<DecisionTreeNode, DecisionTreeEdge> t2) {
        // Get the roots of both trees
        DecisionTreeNode root1 = t1.calculateRoot();
        DecisionTreeNode root2 = t2.calculateRoot();

        return calculateDistanceRecursive(root1, root2);
    }

    @Override
    public String getName() {
        return name;
    }

    @SuppressWarnings("DuplicatedCode")
    private int calculateDistanceRecursive(DecisionTreeNode node1, DecisionTreeNode node2) {
        int distance = 0;

        if (!Objects.equals(node1.featureId, node2.featureId) && node1.featureId != null && node2.featureId != null) {
            distance = distance + changeSplitFeature;
        }

        List<DecisionTreeEdge> edges1 = node1.getOutgoingEdgesSorted();
        List<DecisionTreeEdge> edges2 = node2.getOutgoingEdgesSorted();

        int maxEdges = Math.max(edges1.size(), edges2.size());

        if (edges1.size() > edges2.size()) {
            // Tree 1 has more children compared to tree 2
            for (int i = 0; i < maxEdges; i++) {
                if (i < edges2.size()) {
                    DecisionTreeNode target1 = edges1.get(i).target;
                    DecisionTreeNode target2 = edges2.get(i).target;

                    distance = distance + calculateDistanceRecursive(target1, target2);
                } else {
                    DecisionTreeNode targetNode = edges1.get(i).target;
                    distance = distance + calculateDistanceNonExistentSubGraph(targetNode);
                }
            }

        } else if (edges1.size() < edges2.size()) {
            // Tree 2 has more children compared to tree 1
            for (int i = 0; i < maxEdges; i++) {
                if (i < edges1.size()) {
                    DecisionTreeNode target1 = edges1.get(i).target;
                    DecisionTreeNode target2 = edges2.get(i).target;

                    distance = distance + calculateDistanceRecursive(target1, target2);
                } else {
                    DecisionTreeNode targetNode = edges2.get(i).target;
                    distance = distance + calculateDistanceNonExistentSubGraph(targetNode);
                }
            }

        } else {
            // Equal amount of edges
            for (int i = 0; i < maxEdges; i++){
                DecisionTreeNode target1 = edges1.get(i).target;
                DecisionTreeNode target2 = edges2.get(i).target;

                distance = distance + calculateDistanceRecursive(target1, target2);
            }
        }

        return distance;
    }

    private int calculateDistanceNonExistentSubGraph(DecisionTreeNode node) {
        // Starting with distance equal to ADD_NODE_COST to punish current node
        int distance = addNodeCost;

        List<DecisionTreeEdge> edges = node.getOutgoingEdgesSorted();

        for (DecisionTreeEdge edge : edges) {
            DecisionTreeNode target = edge.target;
            distance = distance + calculateDistanceNonExistentSubGraph(target);
        }

        return distance;
    }
}
