package Graph.DecisionTree;

import Graph.Node;

import java.util.Collections;
import java.util.List;

public class DecisionTreeNode extends Node<DecisionTreeNode, DecisionTreeEdge> {
    public Integer featureId;
    public Integer predictedLabel;
    public Integer[] classProportions;

    public DecisionTreeNode(int id, Integer featureId, Integer predictedLabel, Integer[] classProportions) {
        super(id);
        this.featureId = featureId;
        this.predictedLabel = predictedLabel;
        this.classProportions = classProportions;
    }

    public DecisionTreeNode(int id) {
        super(id);
    }

    @Override
    public DecisionTreeNode deepCopy() {
        return new DecisionTreeNode(id, featureId, predictedLabel, classProportions);
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
