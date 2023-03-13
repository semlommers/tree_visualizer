/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree;

import InfectionTreeGenerator.Graph.DecisionTree.DecisionTreeEdge;
import InfectionTreeGenerator.Graph.DecisionTree.DecisionTreeNode;
import InfectionTreeGenerator.Graph.GraphAlgorithms.NodeMappingAlgorithms.TreeMap;
import InfectionTreeGenerator.Graph.GraphAlgorithms.NodeMappingAlgorithms.TreeMappingCalculator;
import Utility.Log;
import Utility.Pair;
import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TEDMapping;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeEditDistance.TreeEditDistanceCalculator;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;

import java.util.*;
import java.util.stream.Stream;

/**
 *
 * @author MaxSondag
 */
public class RepresentativeTree<N extends Node<E>, E extends Edge<N>> extends Tree<RepresentativeNode<N, E>, RepresentativeEdge<N, E>> {

    /**
     * Holds the maximum edit distance where this tree still represents at least
     * 1 tree
     */
    public int maxEditDistance = 0;

    /**
     * Holds which trees are already mapped to this tree for some edit distance.
     * Used to prevent double mappings
     */
    public Set<Tree> treesAlreadyMapped = new HashSet();

    Tree originalTree;

    /**
     * Initializes a Representative trees with the same id's on the nods and
     * edgeMapping.
     *
     * @param t
     */
    public RepresentativeTree(Tree t) {
        id = t.id;
        //mapping from id to node for adding edgeMapping.
        HashMap<Integer, RepresentativeNode> nodeMap = new HashMap();

        Collection<Node> gNodes = t.getNodes();
        for (Node n : gNodes) {
            RepresentativeNode rn = new RepresentativeNode(n.id);
            nodeMap.put(rn.id, rn);
            addNode(rn);
        }

        Collection<Edge> gEdges = t.getEdges();
        List<Edge> edgeList = new ArrayList<>(gEdges);
        Collections.sort(edgeList);

        for (Edge e : edgeList) {
            RepresentativeNode source = nodeMap.get(e.source.id);
            RepresentativeNode target = nodeMap.get(e.target.id);
            RepresentativeEdge re = new RepresentativeEdge(source, target);
            addEdge(re);
        }

        originalTree = t;
        treesAlreadyMapped.add(t);
        //map to self at distance 0
        mapToSelf(t);
    }

    private void mapToSelf(Tree t) {
        Collection<Node> tNodes = t.getNodes();
        for (Node tN : tNodes) {
            RepresentativeNode n = (RepresentativeNode) getNode(tN.id);
            n.addToRepresentsNodes(0, tN);
        }

        Collection<Edge> tEdges = t.getEdges();
        for (Edge tE : tEdges) {
            RepresentativeEdge e = (RepresentativeEdge) getEdge(tE.source.id, tE.target.id);
            e.addToRepresentsEdges(0, tE);
        }
    }

    /**
     * store which trees map to this RepresentativeTree for this edit distance
     * and how they map to this RepresentativeTree
     *
     * @param editDistance
     * @param treesMapped Trees mapped to this tree
     * @param tmCalc Calculator holding the mapping between trees
     */
    public void addToMapping(int editDistance, List<Tree> treesMapped, TreeMappingCalculator tmCalc) {

        //don't change anything if no new trees are mapped to this node.
        if (treesMapped.isEmpty()) {
            return;
        }

        //at least one node mapped at this distance, so we have a new important edit distance
        maxEditDistance = Math.max(maxEditDistance, editDistance);

        //go trough all trees that are mapped to this one, and store the mapping
        for (Tree otherT : treesMapped) {

            //only add the tree if it wasn't mapped already
            if (treesAlreadyMapped.contains(otherT)) {
                continue;
            }
            treesAlreadyMapped.add(otherT);

            TreeMap treeMap = (TreeMap) tmCalc.treesMapping.get(new Pair(otherT, originalTree));

            //go through the node mappings.
            Collection<Node> otherTNodes = otherT.getNodes();
            for (Node otherN : otherTNodes) {
                //get to which node otherN maps to
                Node mappedN = treeMap.getMappedNode(otherN);
                if (mappedN == null) {//otherN was deleted in the mapping
                    continue;
                }
                //use the id to find the associated representativeNode
                RepresentativeNode mappedRepN = (RepresentativeNode) getNode(mappedN.id);
                //add otherN to the mapping
                mappedRepN.addToRepresentsNodes(editDistance, otherN);
            }

            //go through the edge mappings
            Collection<Edge> otherTEdges = otherT.getEdges();
            for (Edge otherE : otherTEdges) {
                //get to which edge otherE maps to
                Edge mappedEdge = treeMap.getMappedEdge(otherE);
                if (mappedEdge == null) {//otherE was deleted in the mapping
                    continue;
                }
                //use the ids to find the associated representativeNode
                RepresentativeEdge thisE = (RepresentativeEdge) edgeMapping.get(new Pair(mappedEdge.source.id, mappedEdge.target.id));

                //add otherN to the mapping
                thisE.addToRepresentsEdges(editDistance, otherE);
            }
        }

    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"maxEditDistance\":" + maxEditDistance + ",");
        sb.append("\"nodes\":[");
        Collection<RepresentativeNode<N, E>> nodes = getNodes();
        for (RepresentativeNode n : nodes) {
            sb.append(n.toJson());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);//remove last comma

        sb.append("],");
        //edges
        sb.append("\"edges\":[");
        Collection<RepresentativeEdge<N, E>> edges = getEdges();
        for (RepresentativeEdge e : edges) {
            sb.append(e.toJson());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);//remove last comma
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

}
