/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph.GraphAlgorithms.RepresentativeTree;

import Graph.GraphAlgorithms.NodeMappingAlgorithms.TreeMap;
import Graph.GraphAlgorithms.NodeMappingAlgorithms.TreeMappingCalculator;
import Utility.Pair;
import Graph.Edge;
import Graph.Node;
import Graph.Tree;

import java.util.*;

/**
 * @author MaxSondag, SemLommers
 */
public class RepresentativeTree<N extends Node<N, E>, E extends Edge<N, E>> extends Tree<RepresentativeNode<N, E>, RepresentativeEdge<N, E>> {

    /**
     * Holds the maximum distance where this tree still represents at least
     * 1 tree
     */
    public int maxDistance = 0;

    /**
     * Holds which trees are already mapped to this tree for some distance.
     * Used to prevent double mappings
     */
    public Set<Tree<N, E>> treesAlreadyMapped = new HashSet<>();

    Tree<N, E> originalTree;

    /**
     * Initializes a Representative trees with the same id's on the nods and
     * edgeMapping.
     */
    public RepresentativeTree(Tree<N, E> t) {
        id = t.id;
        //mapping from id to node for adding edgeMapping.
        HashMap<Integer, RepresentativeNode<N, E>> nodeMap = new HashMap<>();

        Collection<N> gNodes = t.getNodes();
        for (N n : gNodes) {
            RepresentativeNode<N, E> rn = new RepresentativeNode<>(n.id);
            nodeMap.put(rn.id, rn);
            addNode(rn);
        }

        Collection<E> gEdges = t.getEdges();
        List<E> edgeList = new ArrayList<>(gEdges);
        Collections.sort(edgeList);

        for (E e : edgeList) {
            RepresentativeNode<N, E> source = nodeMap.get(e.source.id);
            RepresentativeNode<N, E> target = nodeMap.get(e.target.id);
            RepresentativeEdge<N, E> re = new RepresentativeEdge<>(source, target);
            addEdge(re);
        }

        originalTree = t;
        treesAlreadyMapped.add(t);
        //map to self at distance 0
        mapToSelf(t);
    }

    private void mapToSelf(Tree<N, E> t) {
        Collection<N> tNodes = t.getNodes();
        for (N tN : tNodes) {
            RepresentativeNode<N, E> n = getNode(tN.id);
            n.addToRepresentsNodes(0, tN);
        }

        Collection<E> tEdges = t.getEdges();
        for (E tE : tEdges) {
            RepresentativeEdge<N, E> e = getEdge(tE.source.id, tE.target.id);
            e.addToRepresentsEdges(0, tE);
        }
    }

    /**
     * store which trees map to this RepresentativeTree for this distance
     * and how they map to this RepresentativeTree
     */
    public void addToMapping(int distance, List<Tree<N, E>> treesMapped, TreeMappingCalculator<N, E> tmCalc) {

        //don't change anything if no new trees are mapped to this node.
        if (treesMapped.isEmpty()) {
            return;
        }

        //at least one node mapped at this distance, so we have a new important distance
        maxDistance = Math.max(maxDistance, distance);

        //go trough all trees that are mapped to this one, and store the mapping
        for (Tree<N, E> otherT : treesMapped) {

            //only add the tree if it wasn't mapped already
            if (treesAlreadyMapped.contains(otherT)) {
                continue;
            }
            treesAlreadyMapped.add(otherT);

            TreeMap<N, E> treeMap = tmCalc.treesMapping.get(new Pair<>(otherT, originalTree));

            //go through the node mappings.
            Collection<N> otherTNodes = otherT.getNodes();
            for (N otherN : otherTNodes) {
                //get to which node otherN maps to
                N mappedN = treeMap.getMappedNode(otherN);
                if (mappedN == null) {//otherN was deleted in the mapping
                    continue;
                }
                //use the id to find the associated representativeNode
                RepresentativeNode<N, E> mappedRepN = getNode(mappedN.id);
                //add otherN to the mapping
                mappedRepN.addToRepresentsNodes(distance, otherN);
            }

            //go through the edge mappings
            Collection<E> otherTEdges = otherT.getEdges();
            for (E otherE : otherTEdges) {
                //get to which edge otherE maps to
                E mappedEdge = treeMap.getMappedEdge(otherE);
                if (mappedEdge == null) {//otherE was deleted in the mapping
                    continue;
                }
                //use the ids to find the associated representativeNode
                RepresentativeEdge<N, E> thisE =edgeMapping.get(new Pair<>(mappedEdge.source.id, mappedEdge.target.id));

                //add otherN to the mapping
                thisE.addToRepresentsEdges(distance, otherE);
            }
        }

    }

}
