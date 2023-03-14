package Graph.DecisionTree;

import Graph.Tree;

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
        //TODO: implement
        return 0;
    }
}
