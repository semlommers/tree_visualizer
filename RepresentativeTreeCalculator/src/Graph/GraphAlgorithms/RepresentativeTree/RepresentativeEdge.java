/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph.GraphAlgorithms.RepresentativeTree;

import Graph.Node;
import Graph.Edge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author MaxSondag, SemLommers
 */
public class RepresentativeEdge<N extends Node<N, E>, E extends Edge<N, E>> extends Edge<RepresentativeNode<N, E>, RepresentativeEdge<N, E>> {

    /**
     * Holds the distances at which point additional edges are represented
     * by this edge
     */
    private final HashMap<Integer, List<E>> representsEdges = new HashMap<>();

    public RepresentativeEdge(RepresentativeNode<N, E> source, RepresentativeNode<N, E> target) {
        super(source, target);
    }

    @Override
    public RepresentativeEdge<N, E> deepCopy(HashMap<Integer, RepresentativeNode<N, E>> newNodes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<E> getRepresentEdges(int distance) {
        return representsEdges.getOrDefault(distance, new ArrayList<>());
    }

    public void addToRepresentsEdges(int distance, E newEdge) {
        List<E> representEdges = getRepresentEdges(distance);
        representEdges.add(newEdge);
        representsEdges.put(distance, representEdges);
    }
}
