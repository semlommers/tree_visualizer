/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph.GraphAlgorithms;

import Utility.Log;
import Graph.Node;
import Graph.Edge;
import Graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author MaxSondag, SemLommers
 */
public class DominatingSetCalculator<N extends Node<N, E>, E extends Edge<N, E>> {

    /**
     * Returns a set of node-ids that is a dominating set.
     */
    public List<Integer> getDominatingSet(Graph<N, E> g) {
        ArrayList<Integer> dominatingSet = new ArrayList<>();

        //nodes that are not connected always need to be in
        Collection<N> nodes = g.getNodes();

        //Trivial algorithm. Go through the nodes. For each node if it is not yet dominated, add it to the set.
        for (N n : nodes) {
            if (isNotDominated(dominatingSet, n)) {
                dominatingSet.add(n.id);
            }
        }
        return dominatingSet;
    }

    /**
     * Returns a new dominating set containing only the ids of the nodes from
     * {@code indSet} to dominate newG
     *
     * @param domSet A set of nodeIds.
     * @param newG A graph that is covered by {@code domSet}
     */
    public ArrayList<Integer> trimDominatingSet(Graph<N, E> newG, List<Integer> domSet) {
        //start by taking all nodes, and keep removing them as long as the result remains a dominating set
        ArrayList<Integer> domSetTrimmed = new ArrayList<>(domSet);
        ArrayList<Integer> idsToConsider = new ArrayList<>(domSetTrimmed);

        for (Integer id : idsToConsider) {
            //check if removing node with id from the dominating set still gives a dominating set
            domSetTrimmed.remove(id);
            if (!isDominatingSet(newG, domSetTrimmed)) {//TODO: Can be optimized by only checking nodes around {id}
                //not a dominating set anymore, put it back
                domSetTrimmed.add(id);
            }
        }

        return domSetTrimmed;
    }

    private boolean isDominatingSet(Graph<N, E> g, ArrayList<Integer> domSet) {
        Collection<N> nodes = g.getNodes();
        for (N n : nodes) {
            if (isNotDominated(domSet, n)) {
                return false;
            }
        }
        return true;//all nodes are dominated
    }

    public boolean isNotDominated(List<Integer> domSet, N n) {

        if (domSet.contains(n.id)) {
            return false;
        }
        List<E> edges = n.edges;

        for (E e : edges) {//directed edge
            if (domSet.contains(e.source.id)) {
                return false;
            }
        }

        return true;//there is a node not dominated
    }
}
