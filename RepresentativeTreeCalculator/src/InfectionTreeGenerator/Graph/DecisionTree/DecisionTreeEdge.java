package InfectionTreeGenerator.Graph.DecisionTree;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DecisionTreeEdge extends Edge<DecisionTreeNode> {

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
        DecisionTreeEdge e = new DecisionTreeEdge(newSource, newTarget, featureId, minValue, maxValue);
        return e;
    }

    @Override
    public DecisionTreeEdge deepCopy(Node sourceCopy, Node targetCopy) {
        assert (sourceCopy.id == source.id);
        assert (targetCopy.id == target.id);

        DecisionTreeNode newSource = (DecisionTreeNode) sourceCopy;
        DecisionTreeNode newTarget = (DecisionTreeNode) targetCopy;
        DecisionTreeEdge e = new DecisionTreeEdge(newSource, newTarget, featureId, minValue, maxValue);
        return e;
    }

    @Override
    public int compareTo(@NotNull Edge o) {
        DecisionTreeEdge edge = (DecisionTreeEdge) o;
        return this.minValue.compareTo(edge.minValue);
    }
}
