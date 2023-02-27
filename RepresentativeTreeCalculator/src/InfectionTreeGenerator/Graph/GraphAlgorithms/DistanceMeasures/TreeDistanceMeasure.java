/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Infection.InfectionEdge;
import InfectionTreeGenerator.Graph.Infection.InfectionNode;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;

/**
 *
 * @author MaxSondag
 */
public interface TreeDistanceMeasure<N extends Node<E>, E extends Edge<N>> {

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
