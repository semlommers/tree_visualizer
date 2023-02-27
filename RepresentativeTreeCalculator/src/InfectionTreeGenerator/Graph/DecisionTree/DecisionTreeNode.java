package InfectionTreeGenerator.Graph.DecisionTree;

import InfectionTreeGenerator.Graph.Node;

import java.util.Collections;
import java.util.List;

public class DecisionTreeNode extends Node<DecisionTreeEdge> {
    public Integer featureId;
    public Integer predictedLabel;

    public DecisionTreeNode(int id, Integer featureId, Integer predictedLabel) {
        super(id);
        this.featureId = featureId;
        this.predictedLabel = predictedLabel;
    }

    public DecisionTreeNode(int id) {
        super(id);
    }

    @Override
    public DecisionTreeNode deepCopy() {
        return new DecisionTreeNode(id, featureId, predictedLabel);
    }

    public List<DecisionTreeEdge> getOutgoingEdgesSorted() {
        List<DecisionTreeEdge> edges = getOutgoingEdges();
        Collections.sort(edges);
        return edges;
    }
}
