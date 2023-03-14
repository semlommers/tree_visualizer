/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Export;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
import InfectionTreeGenerator.Graph.Tree;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MaxSondag, SemLommers
 */
public class TreeNodeJson<N extends Node<N, E>, E extends Edge<N, E>> {
    /**
     * Id of the node
     */
    public int id;

    /**
     * Children of this node
     */
    public List<TreeNodeJson<N, E>> children = new ArrayList<>();

    public TreeNodeJson(Tree<N, E> tree) {
        N root = tree.calculateRoot();
        initialize(root);
    }

    protected TreeNodeJson(N node) {
        initialize(node);
    }

    protected void initialize(N root) {
        this.id = root.id;

        //recurse into the children
        List<E> outEdges = root.getOutgoingEdges();
        for (E edge : outEdges) {
            N child = edge.target;
            TreeNodeJson<N, E> rnChild = new TreeNodeJson<>(child);
            children.add(rnChild);
        }
    }
}
