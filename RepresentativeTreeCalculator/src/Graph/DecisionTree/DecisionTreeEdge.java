package Graph.DecisionTree;

import Graph.Edge;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DecisionTreeEdge extends Edge<DecisionTreeNode, DecisionTreeEdge> {

    /**
     * Holds the id of the feature
     */
    public Integer featureId;

    /**
     * Holds the range of values for the specified feature
     */
    public Double minValue;
    public Double maxValue;

    public DecisionTreeEdge(DecisionTreeNode source, DecisionTreeNode target, int featureId, double minValue, double maxValue) {
        super(source, target);
        this.featureId = featureId;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public String toString() {
        return "Edge{" + "start=" + source.id + ", end=" + target.id + ", featureId=" + featureId +
                ", minValue=" + minValue + ", maxValue=" + maxValue + '}';
    }

    @Override
    public String toJson() {
        return "{\"sourceId\":" + source.id + ","
                + "\"targetId\":" + target.id + ","
                + "\"featureId\":" + featureId + ","
                + "\"minValue\":" + minValue + ","
                + "\"maxValue\":" + maxValue
                + "}";
    }

    @Override
    public DecisionTreeEdge deepCopy(HashMap<Integer, DecisionTreeNode> nodeMapping) {
        DecisionTreeNode newSource = nodeMapping.get(source.id);
        DecisionTreeNode newTarget = nodeMapping.get(target.id);
        return new DecisionTreeEdge(newSource, newTarget, featureId, minValue, maxValue);
    }

    @Override
    public DecisionTreeEdge deepCopy(DecisionTreeNode sourceCopy, DecisionTreeNode targetCopy) {
        assert (sourceCopy.id == source.id);
        assert (targetCopy.id == target.id);

        return new DecisionTreeEdge(sourceCopy, targetCopy, featureId, minValue, maxValue);
    }

    @Override
    public int compareTo(@NotNull Edge o) {
        DecisionTreeEdge edge = (DecisionTreeEdge) o;
        return this.minValue.compareTo(edge.minValue);
    }
}
