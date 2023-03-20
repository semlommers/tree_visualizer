/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph.GraphAlgorithms.RepresentativeTree;

import Export.GraphWriter;
import Graph.GraphAlgorithms.NodeMappingAlgorithms.TreeMappingCalculator;
import Utility.Log;
import Graph.Edge;
import Graph.Graph;
import Graph.GraphAlgorithms.DominatingSetCalculator;
import Graph.GraphAlgorithms.DistanceMeasures.TreeDistanceMeasure;
import Graph.Node;
import Graph.Tree;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * @author MaxSondag, SemLommers
 */
public class RepresentativeTreesFinder<N extends Node<N, E>, E extends Edge<N, E>> {

    public void getAndWriteRepresentativeTreeData(Set<Tree<N, E>> forest, List<TreeDistanceMeasure<N, E>> dms, String outputFileLocation) throws IOException {
        Log.printProgress(forest.size() + " total trees");
        String outputFolderLocation = outputFileLocation + "/RepTrees/";
        Files.createDirectories(Paths.get(outputFolderLocation));

        List<Tree<N, E>> trees = new ArrayList<>(forest);

        GraphWriter<N, E> tw = new GraphWriter<>();

        for (TreeDistanceMeasure<N, E> dm : dms) {
            //calculate the representative trees
            Collection<RepresentativeTree<N, E>> repTrees = calculateRepresentativeTrees(trees, dm);
            List<RepresentativeTree<N, E>> allRepTrees = new ArrayList<>(repTrees);


            String outputFileName = outputFolderLocation + dm.getName() + ".json";
            tw.writeRepresentativeTrees(outputFileName, allRepTrees);
        }

        tw.writeDistanceMetricOutputLocations(outputFileLocation + "/DistanceMeasures.json", dms);
    }

    /**
     * Returns a set of representativeTrees for the collection of {@code trees}.
     */
    private Collection<RepresentativeTree<N, E>> calculateRepresentativeTrees(List<Tree<N, E>> trees, TreeDistanceMeasure<N, E> dm) {

        //Holds the graphs as nodes, and uses the specified distance measure as weights between the nodes
        Graph<N, E> g = makeWeightedGraph(trees, dm);

        Collection<E> edges = g.getEdges();

        int maxDistance = 0;
        for (E edge : edges) {
            if (edge.weight > maxDistance) {
                maxDistance = (int) edge.weight;
            }
        }

        //Start calculating representative trees.
        //We find it by using dominating set on filtered trees. The dominating set are the set of representative nodes
        //where all other nodes can transform into within {distance} graph change moves.
        DominatingSetCalculator<N, E> dsc = new DominatingSetCalculator<N, E>();

        //start with an graph on distance 0 and slowly add edges. Used to initialize the dominating set
        Graph<N, E> fg0 = getFilteredGraph(g, 0);
        List<Integer> currentDsIds = dsc.getDominatingSet(fg0);

        //initialize the representing trees, they only get filtered down, so these are all Representative trees that will exists.
        //maps from id to a representative tree
        HashMap<Integer, RepresentativeTree<N, E>> repTrees = initRepTrees(currentDsIds, trees);

        //go through the distances
        int distance = 0;
        // Go on until only one representative tree exists
        while (currentDsIds.size() > 1) {
            //Get graph with only edgeMapping with weight <= distance
            Graph<N, E> fgTED = getFilteredGraph(g, distance);

            //trim the dominating set down instead of recalculating so we keep the original trees. Trees thus only disappear
            List<Integer> dsTrimmed = dsc.trimDominatingSet(fgTED, currentDsIds);

            //holds the set of trees that are assigned to a dominating tree at distance {distance}
            HashMap<Integer, List<Tree<N, E>>> mapping = calculateDominationMapping(fgTED, dsTrimmed, trees);

            for (Integer id : dsTrimmed) {
                RepresentativeTree<N, E> repTree = repTrees.get(id);
                List<Tree<N, E>> treesMapped = mapping.get(id);

                //calculate the mapping from repTree to all treesMapped
                TreeMappingCalculator<N, E> tmCalc = new TreeMappingCalculator<>();
                for (Tree<N, E> tm : treesMapped) {
                    if (!repTree.treesAlreadyMapped.contains(tm)) {//not mapped yet, so mapping isn't stored yet
                        tmCalc.computeMapping(repTree.originalTree, tm);
                    }
                }
                repTree.addToMapping(distance, treesMapped, tmCalc);
            }
            currentDsIds = dsTrimmed;
            distance++;
        }

        return repTrees.values();

    }

    @SuppressWarnings("unchecked")
    private Graph<N, E> makeWeightedGraph(List<Tree<N, E>> trees, TreeDistanceMeasure<N, E> tdm) {
        Graph<N, E> g = new Graph<N, E>();

        //make nodes for each tree
        HashMap<Integer, N> nodeMapping = new HashMap<>();
        for (Tree<N, E> t : trees) {
            N n = (N) new Node<N, E>(t.id, 1);
            nodeMapping.put(n.id, n);

            g.addNode(n);
        }

        int count = 0;//counter for progress
        for (int i = 0; i < (trees.size() - 1); i++) {
            Tree<N, E> t1 = trees.get(i);
            for (int j = i + 1; j < trees.size(); j++) {

                count++;
                if (count % 10 == 0) {
                    Log.printProgress("Calculating distance " + count + " out of " + ((trees.size() * trees.size() - trees.size()) / 2), 1000);
                }

                Tree<N, E> t2 = trees.get(j);

                int distance = tdm.getDistance(t1, t2);

                N n1 = nodeMapping.get(t1.id);
                N n2 = nodeMapping.get(t2.id);
                E weOut = (E) new Edge<N, E>(n1, n2, distance);
                //ted is symmetric
                E weIn = (E) new Edge<N, E>(n2, n1, distance);
                g.addEdge(weOut);
                g.addEdge(weIn);
            }
        }
        return g;
    }

    /**
     * Returns a new graph with the same id's that only has edges with weight
     * below or equal to {@code weight}
     */
    private Graph<N, E> getFilteredGraph(Graph<N, E> g, double weight) {
        Graph<N, E> deepCopy = g.deepCopy();
        Set<E> toRemove = new HashSet<>();
        for (E e : deepCopy.getEdges()) {
            if (e.weight > weight) {
                toRemove.add(e);
            }
        }
        deepCopy.removeEdges(toRemove);
        return deepCopy;
    }

    private HashMap<Integer, RepresentativeTree<N, E>> initRepTrees(List<Integer> dsIds, List<Tree<N, E>> trees) {
        HashMap<Integer, RepresentativeTree<N, E>> idMapping = new HashMap<>();
        for (Tree<N, E> t : trees) {
            //only add the trees that are representing something.
            if (dsIds.contains(t.id)) {
                RepresentativeTree<N, E> rt = new RepresentativeTree<>(t);
                idMapping.put(t.id, rt);
            }
        }
        return idMapping;
    }

    /**
     * Returns for a given graphId the set of graphs that it is "assigned" to to
     * dominate. Each graph is only assigned to one other graph even if
     * dominated by more graphs. If a tree is in the dominating set, it always
     * dominates itself
     *
     * @param trimmedGraph The graph the dominating set is based upon
     * @param dsIds Set of graph ids that are in the dominating set.
     * @param trees all trees
     */
    private HashMap<Integer, List<Tree<N, E>>> calculateDominationMapping(Graph<N, E> trimmedGraph, List<Integer> dsIds, List<Tree<N, E>> trees) {
        HashMap<Integer, List<Tree<N, E>>> mapping = new HashMap<>();

        //make a copy so we can freely delete those that we have mapped
        List<Tree<N, E>> remainingTrees = new ArrayList<>(trees);

        for (Integer id : dsIds) {
            //the current tree we are looking into the dominance relations of
            Tree<N, E> domTree = getTreeWithId(trees, id);

            N domTreeNode = trimmedGraph.getNode(id);
            List<E> edges = domTreeNode.edges;

            List<Tree<N, E>> dominated = new ArrayList<>();

            //it dominated itself
            dominated.add(domTree);
            //and all trees on outgoing edges
            for (E e : edges) {
                Tree<N, E> t = getTreeWithId(remainingTrees, e.target.id);
                if (t == null) {//already processed
                    continue;
                }
                //unless t is in the dominating set
                if (!dsIds.contains(t.id)) {
                    dominated.add(t);
                }
            }

            mapping.put(id, dominated);

            //a tree can only be dominated once
            remainingTrees.removeAll(dominated);
        }
        assert (remainingTrees.isEmpty());
        return mapping;
    }

    private Tree<N, E> getTreeWithId(List<Tree<N, E>> trees, Integer id) {
        for (Tree<N, E> t : trees) {
            if (t.id == id) {
                return t;
            }
        }
        return null;
    }

}
