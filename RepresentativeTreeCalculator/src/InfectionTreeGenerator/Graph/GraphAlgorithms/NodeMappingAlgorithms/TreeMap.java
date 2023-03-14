package InfectionTreeGenerator.Graph.GraphAlgorithms.NodeMappingAlgorithms;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author Sem Lommers
 */
public class TreeMap<N extends Node<N, E>, E extends Edge<N, E>> {

    //holds the mapping from t1 to t2
    private HashMap<N, N> nodeMapping = new HashMap<>();
    private HashMap<E, E> edgeMapping = new HashMap<>();

    public TreeMap(HashMap<N, N> nodeMapping, HashMap<E, E> edgeMapping) {
        //make a copy
        this.nodeMapping.putAll(nodeMapping);
        this.edgeMapping.putAll(edgeMapping);
    }

    private TreeMap() {
    }

    public N getMappedNode(N n) {
        return nodeMapping.get(n);
    }

    public E getMappedEdge(E e) {
        return edgeMapping.get(e);
    }

    public TreeMap<N, E> inverse() {

        HashMap<N, N> invertedNodes = new HashMap<>();
        for (Entry<N, N> nMap : nodeMapping.entrySet()) {
            invertedNodes.put(nMap.getValue(), nMap.getKey());
        }

        HashMap<E, E> invertedEdges = new HashMap<>();
        for (Entry<E, E> eMap : edgeMapping.entrySet()) {
            invertedEdges.put(eMap.getValue(), eMap.getKey());
        }

        TreeMap<N, E> invertedTreeMap = new TreeMap<>();
        invertedTreeMap.nodeMapping = invertedNodes;
        invertedTreeMap.edgeMapping = invertedEdges;
        return invertedTreeMap;
    }

}
