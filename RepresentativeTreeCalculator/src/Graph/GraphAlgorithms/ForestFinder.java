/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph.GraphAlgorithms;

import Graph.Edge;
import Graph.Graph;
import Graph.GraphFactory;
import Graph.Node;
import Graph.Tree;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author MaxSondag
 */
@SuppressWarnings("unchecked")
public class ForestFinder<G extends Graph<N, E>, T extends Tree<N, E>, N extends Node<N, E>, E extends Edge<N, E>> {

    G completeGraph;
    private final String graphType;

    public ForestFinder(G completeGraph, Class<G> treeType) {
        this.completeGraph = completeGraph;
        this.graphType = treeType.getSimpleName();
    }

    /**
     * Returns a set of graphs which are all trees. References of nodes and
     * edges will have changed. Each tree will have the id of the root node as
     * an id.
     */
    public Set<T> getForest() {
        Set<T> trees = new HashSet<>();

        //start by making all nodes the root of a tree, and merge them together
        Collection<N> nodes = completeGraph.getNodes();
        for (N node : nodes) {
            T newTree = GraphFactory.getNewGraph(graphType);
            Node<N,E> test = node.deepCopy();
            newTree.addNode((N) test);

            //give the tree the id of the root
            newTree.id = node.id;
            trees.add(newTree);
        }

        Collection<E> edges = completeGraph.getEdges();
        for (E e : edges) {
            T sourceTree = null; //holds the Tree the source of this edge is in.
            T targetTree = null;//holds the Tree the source of this edge is in.

            //check which trees the edge ends in
            for (T t : trees) {
                if (t.hasNodeWithId(e.source.id)) {
                    assert (!t.hasNodeWithId(e.target.id));//if it is a tree this cannot happen
                    sourceTree = t;
                }
                if (t.hasNodeWithId(e.target.id)) {
                    assert (!t.hasNodeWithId(e.source.id));//if it is a tree this cannot happen
                    targetTree = t;
                }
            }
            assert (sourceTree != null);
            assert (targetTree != null);
            //ends in two existing trees, merge them and continue with next edge
            mergeTrees(trees, sourceTree, targetTree, e);//merge the trees as they are connected
        }

        return trees;
    }

    /**
     * Merge the targetTree graph into the sourceTree graph.
     */
    private void mergeTrees(Set<T> trees, T sourceTree, T targetTree, Edge<N, E> connectingEdge) {
        //add the nodes
        Collection<N> nodes = targetTree.getNodes();
        for (N n : nodes) {
            sourceTree.addNode(n);
        }

        //add the edges
        Collection<E> edges = targetTree.getEdges();
        for (E e : edges) {
            sourceTree.addEdgeWithoutNodeUpdate(e);
        }

        //add the connecting edge and remove target tree as it is merges
        N startNode = sourceTree.getNode(connectingEdge.source.id);
        N targetNode = targetTree.getNode(connectingEdge.target.id);

        //make a copy of the edge, as the nodes have been copied initially as well
        Edge<N, E> eCopy = connectingEdge.deepCopy(startNode, targetNode);
        sourceTree.addEdge((E) eCopy);

        //remove the target trees from the list as it has been handles
        trees.remove(targetTree);
    }

}
