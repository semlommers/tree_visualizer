package Graph.GraphAlgorithms.NodeMappingAlgorithms;

import Graph.Edge;
import Graph.Node;
import Graph.Tree;
import Utility.Pair;

import java.util.HashMap;
import java.util.List;

public class TreeMappingCalculator<N extends Node<N, E>, E extends Edge<N, E>> {

    /**
     * Holds the node mapping of all calculated pairs of trees so far
     */
    public HashMap<Pair<Tree<N, E>, Tree<N, E>>, TreeMap<N, E>> treesMapping = new HashMap<>();

    public void computeMapping(Tree<N, E> t1, Tree<N, E> t2) {
        TreeMap<N, E> treeMap = computeTreeMap(t1, t2);
        treesMapping.put(new Pair<>(t1, t2), treeMap);
        treesMapping.put(new Pair<>(t2, t1), treeMap.inverse());
    }

    private TreeMap<N, E> computeTreeMap(Tree<N, E> t1, Tree<N, E> t2) {
        HashMap<N, N> nodeMapping = new HashMap<>();
        HashMap<E, E> edgeMapping = new HashMap<>();

        N root1 = t1.calculateRoot();
        N root2 = t2.calculateRoot();

        nodeMapping.put(root1, root2);

        traverseTreeAndComputeMapping(nodeMapping, edgeMapping, root1, root2);

        return new TreeMap<>(nodeMapping, edgeMapping);
    }

    private void traverseTreeAndComputeMapping(HashMap<N, N> nodeMapping, HashMap<E, E> edgeMapping,
                                               Node<N, E> node1, Node<N, E> node2) {
        List<E> edges1 = node1.getOutgoingEdges();
        List<E> edges2 = node2.getOutgoingEdges();

        int minEdges = Math.min(edges1.size(), edges2.size());

        for (int i = 0; i < minEdges; i++) {
            // Get children and edges
            N child1 = edges1.get(i).target;
            N child2 = edges2.get(i).target;
            E edge1 = edges1.get(i);
            E edge2 = edges2.get(i);

            // Add to mapping
            nodeMapping.put(child1, child2);
            edgeMapping.put(edge1, edge2);

            // Traverse next to next children of tree
            traverseTreeAndComputeMapping(nodeMapping, edgeMapping, child1, child2);
        }
    }
}
