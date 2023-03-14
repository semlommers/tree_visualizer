package Export;

import InfectionTreeGenerator.Graph.Edge;
import InfectionTreeGenerator.Graph.Node;
import com.google.gson.Gson;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTree;
import InfectionTreeGenerator.Graph.Tree;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MaxSondag
 */
public class GraphWriter<N extends Node<N, E>, E extends Edge<N, E>> {

    public void writeMetaDataGraph(String outputFileLocation, Tree<N, E> tree) throws IOException {
        Gson gson = new GsonBuilder().serializeNulls().create();
        FileWriter fw = new FileWriter(outputFileLocation);

        Collection<N> nodes = tree.getNodes();
        gson.toJson(nodes, fw);

        fw.flush();
        fw.close();
    }

    public void writeForest(String outputFileLocation, Set<Tree<N, E>> forest) throws IOException {
        Gson gson = new Gson();

        ArrayList<Tree<N, E>> sortedForest = new ArrayList<>(forest);

        //sort first by amount of nodes, then by depth
        sortedForest.sort((t1, t2) -> {
            int compareResult = Integer.compare(t1.getNodes().size(), t2.getNodes().size());
            if (compareResult == 0) {
                return Integer.compare(t1.getDepth(), t2.getDepth());
            }
            return compareResult;
        });

        ArrayList<TreeNodeJson<N, E>> trees = new ArrayList<>();
        for (Tree<N, E> t : sortedForest) {
            TreeNodeJson<N, E> treeNodeJson = new TreeNodeJson<>(t);
            trees.add(treeNodeJson);
        }
        FileWriter fw = new FileWriter(outputFileLocation);
        gson.toJson(trees, fw);
        fw.flush();
        fw.close();
    }

    public void writeRepresentativeTrees(String outputFileLocation, Collection<RepresentativeTree<N, E>> repTrees) throws IOException {
        Gson gson = new Gson();

        ArrayList<RepresentativeNodeJson<N, E>> trees = new ArrayList<>();
        for (RepresentativeTree<N, E> repTree : repTrees) {
            RepresentativeNodeJson<N, E> repNodeJson = new RepresentativeNodeJson<>(repTree);
            trees.add(repNodeJson);
        }
        FileWriter fw = new FileWriter(outputFileLocation);
        gson.toJson(trees, fw);
        fw.flush();
        fw.close();
    }
}
