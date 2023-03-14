/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph;

import java.util.HashMap;

/**
 * An edge is uniquely identified by {@code source.id} and {@code target.id}
 * @author MaxSondag, SemLommers
 */
public class Edge<N extends Node<N, E>, E extends Edge<N, E>> implements Comparable<Edge<N, E>> {

    final public N source, target;
    /**
     * Weight of this edge, unit weight by default
     */
    public double weight = 1.0;//unit weight by default

    public Edge(N source, N target) {
        this.source = source;
        this.target = target;
    }

    public Edge(N source, N target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge{" + "start=" + source.id + ", end=" + target.id + '}';
    }

    /**
     * Returns a deep copy of this node. Nodes assigned to source and target will
     * be taken from newNodes with the same id
     */
    public Edge<N, E> deepCopy(HashMap<Integer, N> nodeMapping) {
        N newSource = nodeMapping.get(source.id);
        N newTarget = nodeMapping.get(target.id);
        return new Edge<N, E>(newSource, newTarget, weight);
    }

    /**
     * Returns a deep copy of this node. Nodes assigned to source and target will
     * be n1Copy and n2Copy
     */
    public Edge<N, E> deepCopy(N sourceCopy, N targetCopy) {
        assert (sourceCopy.id == source.id);
        assert (targetCopy.id == target.id);

        return new Edge<N, E>(sourceCopy, targetCopy, weight);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.target.id;
        return hash;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge<?, ?> other = (Edge<?, ?>) obj;
        if (this.source.id != other.source.id) {
            return false;
        }
        if (this.target.id != other.target.id) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Edge o) {
        return 0;
    }
}
