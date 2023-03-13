
import Import.RandomForestParser;
import InfectionTreeGenerator.Graph.DecisionTree.DecisionTreeEdge;
import InfectionTreeGenerator.Graph.DecisionTree.DecisionTreeNode;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.EditDistanceNoChildSwapping;
import InfectionTreeGenerator.Graph.DecisionTree.DecisionTreeGraph;
import Export.GraphWriter;
import InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures.TreeDistanceMeasure;
import InfectionTreeGenerator.Graph.GraphAlgorithms.ForestFinder;
import InfectionTreeGenerator.Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTreesFinder;
import InfectionTreeGenerator.Graph.Tree;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MaxSondag, SemLommers
 */
public class DataToJsonTree {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String dataFolderLocation = "./RepresentativeTreeCalculator/Data";
            String inputFolderLocation = dataFolderLocation + "/Input";
            String outputFileLocation = dataFolderLocation + "/Output";

            new DataToJsonTree(inputFolderLocation, outputFileLocation);
        } catch (IOException ex) {
            System.out.println("Invalid input or outputFileLocation");
            Logger.getLogger(DataToJsonTree.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private DataToJsonTree(String inputFolderLocation, String outputFileLocation) throws IOException {
        System.out.println("Working on data from: " + inputFolderLocation);
        //read data
        RandomForestParser randomForestParser = new RandomForestParser(inputFolderLocation + "/randomForestMap.json");
        randomForestParser.constructGraph();

        DecisionTreeGraph decisionTreeGraph = randomForestParser.decisionTreeGraph;

        //output data
        printStatistics(decisionTreeGraph);

        GraphWriter<DecisionTreeNode, DecisionTreeEdge> treeWriter = new GraphWriter<>();
        treeWriter.writeMetaDataGraph(outputFileLocation + "/NodesAndMeta.json", decisionTreeGraph);

        System.out.println("Finding the forest");
        ForestFinder<DecisionTreeGraph, Tree<DecisionTreeNode, DecisionTreeEdge>, DecisionTreeNode, DecisionTreeEdge> forestFinder = new ForestFinder<>(decisionTreeGraph, Tree.class);
        Set<Tree<DecisionTreeNode, DecisionTreeEdge>> forest = forestFinder.getForest();

        treeWriter.writeForest(outputFileLocation + "/AllTrees.json", forest);

        TreeDistanceMeasure<DecisionTreeNode, DecisionTreeEdge> treeDistanceMeasure = new EditDistanceNoChildSwapping();
        RepresentativeTreesFinder<DecisionTreeNode, DecisionTreeEdge> representativeTreesFinder = new RepresentativeTreesFinder<>();
        representativeTreesFinder.getAndWriteRepresentativeTreeData(forest, treeDistanceMeasure, outputFileLocation + "/RepTreesRTDistance");
    }

    private void printStatistics(DecisionTreeGraph dtg) {
        System.out.println("Amount of nodes: " + dtg.getNodes().size());
    }
}
