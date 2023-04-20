package Graph.GraphAlgorithms.MetaDataAlgorithms;

import Graph.DecisionTree.DecisionTreeEdge;
import Graph.DecisionTree.DecisionTreeGraph;
import Graph.DecisionTree.DecisionTreeNode;
import Graph.Tree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DataInstanceMetaDataConstructor {
    private final List<List<Double>> data = new ArrayList<>();

    public DataInstanceMetaDataConstructor(String inputFileLocation) throws IOException {
        // Input file should be a csv file containing rows of the dataset with last column holding the target values.
        parseDataSet(Files.readAllLines(Paths.get(inputFileLocation)));
    }

    private void parseDataSet(List<String> lines) {
        for (int i = 1; i < lines.size(); i++) { //skip header
            String line = lines.get(i);
            List<String> values = new ArrayList<>(Arrays.asList(line.split(",")));

            // Create new list for values of double type
            List<Double> doubleValues = new ArrayList<>();
            for (String value : values) {
                doubleValues.add(Double.valueOf(value));
            }

            data.add(doubleValues);
        }
    }

    public void addMisclassifiedDataCounterToForest(Set<Tree<DecisionTreeNode, DecisionTreeEdge>> forest) {
        for (List<Double> instance : data) {
            int correctLabel = instance.get(instance.size() - 1).intValue();

            for (Tree<DecisionTreeNode, DecisionTreeEdge> tree : forest) {
                DecisionTreeGraph decisionTree = (DecisionTreeGraph) tree;
                DecisionTreeNode leafNode = decisionTree.getLeafNodeByPrediction(instance);
                boolean correctPrediction = (leafNode.predictedLabel.equals(correctLabel));

                DecisionTreeNode currentNode = leafNode;
                List<DecisionTreeEdge> incomingEdges = currentNode.getIncomingEdges();
                currentNode.addPredictionStatistics(correctPrediction);

                while (incomingEdges.size() > 0) {
                    currentNode = incomingEdges.get(0).source;
                    currentNode.addPredictionStatistics(correctPrediction);
                    incomingEdges = currentNode.getIncomingEdges();
                }
            }
        }
    }
}