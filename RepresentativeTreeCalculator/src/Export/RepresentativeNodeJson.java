/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Export;

import Graph.Edge;
import Graph.GraphAlgorithms.RepresentativeTree.RepresentativeEdge;
import Graph.GraphAlgorithms.RepresentativeTree.RepresentativeNode;
import Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTree;
import Graph.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author MaxSondag, SemLommers
 */
public class RepresentativeNodeJson<N extends Node<N, E>, E extends Edge<N, E>> {

    /**
     * Id of the node
     */
    public int id;
    
    /**
     * maximum distance that this tree is still representing nodes
     */
    public int maxDistance;
    /**
     * (distance,List<id's>) object that holds the first distance when
     * the nodes with id: "id's" are represented by this node
     */
    public List<RepresentationJson> representations = new ArrayList<>();

    /**
     * Children of this node
     */
    public List<RepresentativeNodeJson<N, E>> children = new ArrayList<>();

    public RepresentativeNodeJson(RepresentativeTree<N, E> repTree) {
        this.maxDistance = repTree.maxDistance;
        RepresentativeNode<N, E> root = repTree.calculateRoot();
        initialize(root);
    }

    private RepresentativeNodeJson(RepresentativeNode<N, E> repNode) {
        initialize(repNode);
    }

    private void initialize(RepresentativeNode<N, E> root) {
        this.id = root.id;
        //store the nodes that root represents
        HashMap<Integer, List<N>> representNodesMapping = root.getRepresentNodesMapping();
        for (Entry<Integer, List<N>> entry : representNodesMapping.entrySet()) {
            RepresentationJson repJson = new RepresentationJson(entry.getKey(), entry.getValue());
            representations.add(repJson);
        }

        //recurse into the children
        List<RepresentativeEdge<N, E>> outEdges = root.getOutgoingEdges();
        for (RepresentativeEdge<N, E> outEdge : outEdges) {
            RepresentativeNode<N, E> child = outEdge.target;
            RepresentativeNodeJson<N, E> nodeJson = new RepresentativeNodeJson<>(child);
            children.add(nodeJson);
        }
    }

    public class RepresentationJson {

        /**
         * First distance the nodes are represented by this node
         */
        public int distance;
        /**
         * Nodes that are represented at Distance
         */
        public Set<Integer> representationIds = new HashSet<>();

        public RepresentationJson(Integer distance, List<N> nodes) {
            this.distance = distance;
            for (N node : nodes) {
                representationIds.add(node.id);
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + this.distance;
            hash = 31 * hash + Objects.hashCode(this.representationIds);
            return hash;
        }

        @SuppressWarnings({"RedundantIfStatement", "unchecked"})
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
            final RepresentationJson other = (RepresentationJson) obj;
            if (this.distance != other.distance) {
                return false;
            }
            if (!Objects.equals(this.representationIds, other.representationIds)) {
                return false;
            }
            return true;
        }

    }

}
