
import Graph.GraphAlgorithms.DistanceMeasures.*;
import Graph.GraphAlgorithms.MetaDataAlgorithms.DataInstanceMetaDataConstructor;
import Import.RandomForestParser;
import Graph.DecisionTree.DecisionTreeEdge;
import Graph.DecisionTree.DecisionTreeNode;
import Graph.DecisionTree.DecisionTreeGraph;
import Export.GraphWriter;
import Graph.GraphAlgorithms.ForestFinder;
import Graph.GraphAlgorithms.RepresentativeTree.RepresentativeTreesFinder;
import Graph.Tree;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
            String dataFolderLocation = "./RepresentativeTreeCalculator/Data/wineSplit";
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

        Files.copy(Paths.get(inputFolderLocation + "/names.json"), Paths.get(outputFileLocation + "/names.json"), StandardCopyOption.REPLACE_EXISTING);

        GraphWriter<DecisionTreeNode, DecisionTreeEdge> treeWriter = new GraphWriter<>();

        System.out.println("Finding the forest");
        ForestFinder<DecisionTreeGraph, Tree<DecisionTreeNode, DecisionTreeEdge>, DecisionTreeNode, DecisionTreeEdge>
                forestFinder = new ForestFinder<DecisionTreeGraph, Tree<DecisionTreeNode, DecisionTreeEdge>,
                DecisionTreeNode, DecisionTreeEdge>(decisionTreeGraph, DecisionTreeGraph.class);
        Set<Tree<DecisionTreeNode, DecisionTreeEdge>> forest = forestFinder.getForest();

        treeWriter.writeForest(outputFileLocation + "/AllTrees.json", forest);

        DataInstanceMetaDataConstructor dataInstanceMetaDataConstructor = new DataInstanceMetaDataConstructor(inputFolderLocation + "/dataset.csv");
        dataInstanceMetaDataConstructor.addDataInstanceMetaDataToForest(forest);

        HashMap<Integer, Integer> dataToCorrectClass = dataInstanceMetaDataConstructor.getDataToCorrectClass();

        List<TreeDistanceMeasure<DecisionTreeNode, DecisionTreeEdge>> treeDistanceMeasures = Arrays.asList(
                new EditDistanceNoChildSwapping(1,0),
                new EditDistanceNoChildSwapping(1,1),
                new EditDistanceNoChildSwapping(1,2),
//                new EditDistanceNoChildSwapping(1,3),
                new PredictionSimilarityDistance(inputFolderLocation + "/dataset.csv"),
                new RuleSimilarityDistance(inputFolderLocation + "/dataset.csv"),
//                new RuleSimilarityDistanceSum(inputFolderLocation + "/dataset.csv"),
//                new RuleSimilarityDistanceUnion(inputFolderLocation + "/dataset.csv"),
                new RuleSimilarityDistanceExMatrixJacard(inputFolderLocation + "/dataset.csv"),
                new RuleSimilarityDistanceExMatrixOverlap(inputFolderLocation + "/dataset.csv"));
        RepresentativeTreesFinder<DecisionTreeNode, DecisionTreeEdge> representativeTreesFinder = new RepresentativeTreesFinder<DecisionTreeNode, DecisionTreeEdge>();
        representativeTreesFinder.getAndWriteRepresentativeTreeData(forest, treeDistanceMeasures, outputFileLocation, dataToCorrectClass);

        treeWriter.writeMetaDataGraph(outputFileLocation + "/NodesAndMeta.json", forest);
    }

    private void printStatistics(DecisionTreeGraph dtg) {
        System.out.println("Amount of nodes: " + dtg.getNodes().size());
    }
}
