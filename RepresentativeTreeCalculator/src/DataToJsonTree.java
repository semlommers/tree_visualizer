
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
 * @author MaxSondag
 */
public class DataToJsonTree {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String inputFolderLocation = "./RepresentativeTreeCalculator/Data/SimulationData/";
            String outputFileLocation = inputFolderLocation;

            int startTreeSize = 1;//calculate starting from trees of size 1
            int endTreeSize = 2000; //stop calculating for trees of size 200

            new DataToJsonTree(inputFolderLocation, outputFileLocation, startTreeSize, endTreeSize);
        } catch (IOException ex) {
            System.out.println("Invalid input or outputFileLocation");
            Logger.getLogger(DataToJsonTree.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    final private String inputFolderLocation;
    final private String outputFileLocation;

    private DataToJsonTree(String inputFolderLocation, String outputFileLocation, int startTreeSize, int endTreeSize) throws IOException {
        this.inputFolderLocation = inputFolderLocation;
        this.outputFileLocation = outputFileLocation;
        System.out.println("Working on data from: " + inputFolderLocation);
        //read data
        RandomForestParser rfp = new RandomForestParser(inputFolderLocation + "/randomForestMap.json");
        rfp.constructGraph();

        DecisionTreeGraph dtg = rfp.dtg;

        //output data
        printStatistics(dtg);

        GraphWriter tw = new GraphWriter();
        tw.writeModelGraph(outputFileLocation + "/NodesAndMeta.json", dtg);

        System.out.println("Finding the forest");
        ForestFinder ff = new ForestFinder(dtg, Tree.class);
        Set<Tree> forest = ff.getForest();

        tw.writeForest(outputFileLocation + "/AllTrees.json", forest);

        TreeDistanceMeasure tdm = new EditDistanceNoChildSwapping();
        RepresentativeTreesFinder rgf = new RepresentativeTreesFinder();
        rgf.getAndWriteRepresentativeTreeData(forest, startTreeSize, endTreeSize, tdm, outputFileLocation + "/RepTreesRTDistance");
    }

    private void printStatistics(DecisionTreeGraph dtg) {
        System.out.println("Amount of nodes: " + dtg.getNodes().size());
    }
}
