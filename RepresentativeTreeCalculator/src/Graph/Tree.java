/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author MaxSondag
 */
public class Tree<N extends Node<N, E>, E extends Edge<N, E>> extends Graph<N, E> {

    //returns if possibleAncestor is an ancestor of n
    public boolean isAncestor(N possibleAncestor, N n) {

        N parent = getParent(n);
        if (parent == null) {//root node
            return false;
        } else if (parent.id == possibleAncestor.id) {
            return true;
        }
        //if we are not at the root and haven't found it yet, recurse up.
        return isAncestor(possibleAncestor, parent);
    }

    /**
     * Returns the parent or null if this node is the root
     */
    public N getParent(N n) {
        //At most a single incoming edge for trees
        List<E> incomingEdge = n.getIncomingEdges();
        if (incomingEdge.isEmpty()) {
            return null;
        } else {
            return incomingEdge.get(0).source;
        }
    }

    public N calculateRoot() {
        Collection<N> nodes = getNodes();
        //get a node from the collection
        N node = null;
        for (N n : nodes) {
            node = n;
            break;
        }
        //keep going towards it's ancestors until we don't find a parent anymore.
        while (true) {
            assert node != null;
            N parent = getParent(node);
            if (parent == null) {//found the root
                return node;
            } else {//recurse
                node = parent;
            }
        }
    }

    public int getDepth() {
        HashMap<Integer, Set<N>> depthMap = getDepthMap();
        int maxDepth = -1;
        for (Integer depth : depthMap.keySet()) {
            maxDepth = Math.max(maxDepth, depth);
        }
        return maxDepth;
    }

    public int getDepth(N node) {
        HashMap<Integer, Set<N>> depthMap = getDepthMap();
        for (Entry<Integer, Set<N>> depthSet : depthMap.entrySet()) {
            if (depthSet.getValue().contains(node)) {
                return depthSet.getKey();
            }
        }
        throw new IllegalArgumentException("Node " + node + " is not in the tree");
    }

    /**
     * For each depth, returns how many nodes are on this level
     */
    private HashMap<Integer, Set<N>> getDepthMap() {
        N root = calculateRoot();
        HashMap<Integer, Set<N>> nodesPerDepth = new HashMap<>();
        HashSet<N> d0 = new HashSet<>(Collections.singletonList(root));

        nodesPerDepth.put(0, d0);
        int currentDepth = 0;
        while (nodesPerDepth.containsKey(currentDepth)) {
            //go through all the nodes, and add the next level
            Set<N> nodes = nodesPerDepth.get(currentDepth);
            Set<N> nextDepthSet = nodesPerDepth.getOrDefault(currentDepth + 1, new HashSet<>());

            for (N node : nodes) {
                List<E> edges = node.getOutgoingEdges();
                for (E edge : edges) {
                    nextDepthSet.add(edge.target);
                }
            }
            if (!nextDepthSet.isEmpty()) {
                nodesPerDepth.put(currentDepth + 1, nextDepthSet);
            }
            currentDepth++;
        }
        return nodesPerDepth;
    }


}
