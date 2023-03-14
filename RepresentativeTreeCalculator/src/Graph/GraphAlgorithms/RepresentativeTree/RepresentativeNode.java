/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph.GraphAlgorithms.RepresentativeTree;

import Graph.Edge;
import Graph.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author MaxSondag, SemLommers
 */
public class RepresentativeNode<N extends Node<N, E>, E extends Edge<N, E>> extends Node<RepresentativeNode<N, E>, RepresentativeEdge<N, E>> {

    /**
     * Holds the distances at which point additional nodes are represented
     * by this node
     */
    private final HashMap<Integer, List<N>> representsNodes = new HashMap<>();

    public RepresentativeNode(int id) {
        super(id);
    }


    @Override
    public RepresentativeNode<N, E> deepCopy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public HashMap<Integer, List<N>> getRepresentNodesMapping() {
        return representsNodes;
    }

    public void addToRepresentsNodes(int distance, N newN) {
        List<N> representNodes = getRepresentNodes(distance);
        representNodes.add(newN);
        representsNodes.put(distance, representNodes);
    }

    /**
     * Returns the nodes this node additionally represents at distance
     */
    public List<N> getRepresentNodes(int distance) {
        return representsNodes.getOrDefault(distance, new ArrayList<>());
    }


}
