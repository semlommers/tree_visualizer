/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph;

import Utility.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * By default the graph is directed
 *
 * @author MaxSondag, SemLommers
 */
public class Graph<N extends Node<N, E>, E extends Edge<N, E>> {

    /**
     * Not automatically set
     */
    public int id;

    /**
     * Maps id's to nodes
     */
    protected HashMap<Integer, N> nodeMapping = new HashMap<>();
    /**
     * Maps the id's of the nodes of the edge to an edge
     */
    protected HashMap<Pair<Integer, Integer>, E> edgeMapping = new HashMap<>();

    public void addNode(N n) {
        assert (!nodeMapping.containsKey(n.id));
        nodeMapping.put(n.id, n);
    }

    /**
     * Adds the edge to the graph and updates the nodes to reference this edge.
     */
    public void addEdge(E e) {
        Pair<Integer, Integer> pair = new Pair<>(e.source.id, e.target.id);
        assert (!edgeMapping.containsKey(pair));
        edgeMapping.put(pair, e);

        e.source.addEdge(e);
        e.target.addEdge(e);
    }

    /**
     * Adds the edge to the graph, but does not add it to the nodes.
     */
    public void addEdgeWithoutNodeUpdate(E e) {
        Pair<Integer, Integer> pair = new Pair<>(e.source.id, e.target.id);
        assert (!edgeMapping.containsKey(pair));
        edgeMapping.put(pair, e);
    }

    public void removeEdge(E e) {
        Pair<Integer, Integer> pair = new Pair<>(e.source.id, e.target.id);
        assert (edgeMapping.containsKey(pair));
        edgeMapping.remove(pair);

        e.source.removeEdge(e);
        e.target.removeEdge(e);
    }

    public boolean hasNodeWithId(int id) {
        return nodeMapping.containsKey(id);
    }

    public N getNode(int id) {
        return nodeMapping.get(id);
    }

    public Collection<N> getNodes() {
        return nodeMapping.values();
    }

    public Collection<E> getEdges() {
        return edgeMapping.values();
    }

    public E getEdge(int id1, int id2) {
        return edgeMapping.get(new Pair<>(id1, id2));
    }

    public Collection<E> getEdges(N n) {
        Set<E> nEdges = new HashSet<>();
        for (E e : edgeMapping.values()) {
            if (e.source == n || e.target == n) {
                nEdges.add(e);
            }
        }
        return nEdges;
    }

    @SuppressWarnings("unchecked")
    public Graph<N, E> deepCopy() {
        Graph<N, E> g = GraphFactory.getNewGraph(this.getClass().getSimpleName());

        HashMap<Integer, N> newNodeMapping = new HashMap<>();

        for (N n : getNodes()) {
            N newN = (N) n.deepCopy();
            g.addNode(newN);
            newNodeMapping.put(newN.id, newN);
        }

        for (E e : getEdges()) {
            E newE = (E) e.deepCopy(newNodeMapping);
            g.addEdge(newE);
        }

        return g;
    }

    public void removeEdges(Set<E> toRemove) {
        for (E e : toRemove) {
            removeEdge(e);
        }
    }

}
