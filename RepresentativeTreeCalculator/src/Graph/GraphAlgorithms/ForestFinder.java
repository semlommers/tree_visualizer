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

import java.util.*;

/**
 *
 * @author MaxSondag, SemLommers
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

        Collection<N> nodes = new ArrayList<>(completeGraph.getNodes());
        for (N n : nodes) {
            // Extract the root node
            N root = n;
            List<E> incomingEdges = root.getIncomingEdges();
            while (incomingEdges.size() != 0) {
                root = incomingEdges.get(0).source;
                incomingEdges = root.getIncomingEdges();
            }
            boolean alreadyExists = false;
            for (T tree : trees) {
                if (tree.id == root.id) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                T newTree = extractTreeByRoot(n);
                trees.add(newTree);
            }
        }

        return trees;
    }

    public T extractTreeWithNode(N node) {
        // Extract the root node
        N root = node;
        List<E> incomingEdges = root.getIncomingEdges();
        while (incomingEdges.size() != 0) {
            root = incomingEdges.get(0).source;
            incomingEdges = root.getIncomingEdges();
        }

        return extractTreeByRoot(root);
    }

    public T extractTreeByRoot(N root) {
        // Create the tree
        Queue<N> treeNodes = new LinkedList<>();
        treeNodes.add(root);
        T newTree = GraphFactory.getNewGraph(graphType);
        newTree.id = root.id;
        newTree.addNode(root);

        // Add the entire trees
        while (!treeNodes.isEmpty()) {
            N treeNode = treeNodes.poll();
            List<E> childrenEdges = treeNode.getOutgoingEdges();
            for (E edge : childrenEdges) {
                N targetNode = edge.target;
                newTree.addNode(targetNode);
                newTree.addEdgeWithoutNodeUpdate(edge);
                treeNodes.add(targetNode);
            }
        }

        return newTree;
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
