package Graph.DecisionTree;

import Graph.Node;
import com.google.gson.annotations.Expose;

import java.util.Collections;
import java.util.List;

public class DecisionTreeNode extends Node<DecisionTreeNode, DecisionTreeEdge> {
    @Expose
    public Integer featureId;
    @Expose
    public Integer predictedLabel;
    @Expose
    public Integer[] classProportions;
    @Expose
    public Integer[] correctVsIncorrectClassifiedData;

    public DecisionTreeNode(int id, Integer featureId, Integer predictedLabel, Integer[] classProportions) {
        super(id);
        this.featureId = featureId;
        this.predictedLabel = predictedLabel;
        this.classProportions = classProportions;
        this.correctVsIncorrectClassifiedData = new Integer[]{0,0};
    }

    public DecisionTreeNode(int id) {
        super(id);
        this.correctVsIncorrectClassifiedData = new Integer[]{0,0};
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

    public void addPredictionStatistics(boolean correct) {
        if (correct) {
            this.correctVsIncorrectClassifiedData[0]++;
        } else {
            this.correctVsIncorrectClassifiedData[1]++;
        }
    }
}
