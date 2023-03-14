/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph.GraphAlgorithms.DistanceMeasures;

import Graph.Edge;
import Graph.Node;
import Graph.Tree;

/**
 *
 * @author MaxSondag
 */
public interface TreeDistanceMeasure<N extends Node<N, E>, E extends Edge<N, E>> {

    /**
     * Distance between two infectionTrees. Values are rounded up in case of
     * internal double values
     *
     * @param t1
     * @param t2
     * @return
     */
    public abstract int getDistance(Tree<N, E> t1, Tree<N, E> t2);

}
