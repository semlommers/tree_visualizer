package Graph.DecisionTree;

import Graph.Tree;
import Utility.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DecisionTreeGraph extends Tree<DecisionTreeNode, DecisionTreeEdge> {

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

    public int predictTarget(List<Double> inputData) {
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
                return -1;
            }
        }

        return currentNode.predictedLabel;
    }
}
