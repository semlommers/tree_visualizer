package Graph.DecisionTree;

import Graph.Tree;
import Utility.Log;
import Utility.Pair;

import java.util.*;

public class DecisionTreeGraph extends Tree<DecisionTreeNode, DecisionTreeEdge> {

    private final HashMap<Integer, Integer> dataInstanceToPrediction = new HashMap<>();

    @Override
    public DecisionTreeNode getNode(int id) {
        return super.getNode(id);
    }

    @Override
    public Collection<DecisionTreeEdge> getEdges() {
        List<DecisionTreeEdge> edges = new ArrayList<>(edgeMapping.values());
        Collections.sort(edges);
        return edges;
    }

    public void addDataInstanceToPrediction(int instanceId, int prediction) {
        dataInstanceToPrediction.put(instanceId, prediction);
    }

    public Integer getDataInstancePredictionById(int instanceId) {
        return dataInstanceToPrediction.get(instanceId);
    }

    public int predictTarget(List<Double> inputData) {
        DecisionTreeNode leafNode = getLeafNodeByPrediction(inputData);

        return leafNode.predictedLabel;
    }

    public DecisionTreeNode getLeafNodeByPrediction(List<Double> inputData) {
        DecisionTreeNode currentNode = calculateRoot();
        List<DecisionTreeEdge> childEdges = currentNode.getOutgoingEdgesSorted();

        while (childEdges.size() != 0) { // While having children
            int splitFeature = currentNode.featureId;
            double value = inputData.get(splitFeature);
            boolean childFound = false;

            for (DecisionTreeEdge childEdge : childEdges) {
                if (childEdge.evaluateValue(value)) {
                    childFound = true;
                    currentNode = childEdge.target;
                    childEdges = currentNode.getOutgoingEdgesSorted();
                    break;
                }
            }

            if (!childFound) {
                Log.printOnce("ERROR: node with id: " + currentNode.id + " has children but value: " + value +
                        " falls not in any range");
                return null;
            }
        }

        return currentNode;
    }
}
