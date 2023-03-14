package InfectionTreeGenerator.Graph.DecisionTree;

import InfectionTreeGenerator.Graph.Node;

import java.util.Collections;
import java.util.List;

public class DecisionTreeNode extends Node<DecisionTreeNode, DecisionTreeEdge> {
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

    @Override
    public List<DecisionTreeEdge> getOutgoingEdges() {
        return getOutgoingEdgesSorted();
    }

    public List<DecisionTreeEdge> getOutgoingEdgesSorted() {
        List<DecisionTreeEdge> edges = super.getOutgoingEdges();
        Collections.sort(edges);
        return edges;
    }
}
