package Graph.GraphAlgorithms.DistanceMeasures;

import Graph.DecisionTree.DecisionTreeEdge;
import Graph.DecisionTree.DecisionTreeGraph;
import Graph.DecisionTree.DecisionTreeNode;
import Graph.Tree;
import com.google.gson.annotations.Expose;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PredictionSimilarityDistance implements TreeDistanceMeasure<DecisionTreeNode, DecisionTreeEdge> {
    @Expose
    String name = "PredictionSimilarityDistance";
    private final List<List<Double>> data = new ArrayList<>();
    Integer DIFFERENT_PREDICTION_PUNISHMENT = 1;

    public PredictionSimilarityDistance(String inputFileLocation) throws IOException {
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

    @Override
    public int getDistance(Tree<DecisionTreeNode, DecisionTreeEdge> t1, Tree<DecisionTreeNode, DecisionTreeEdge> t2) {
        int distance = 0;

        DecisionTreeGraph dt1 = (DecisionTreeGraph) t1;
        DecisionTreeGraph dt2 = (DecisionTreeGraph) t2;

        for (List<Double> instance : data) {
            int prediction1 = dt1.predictTarget(instance);
            int prediction2 = dt2.predictTarget(instance);

            if (prediction1 != prediction2) {
                distance = distance + DIFFERENT_PREDICTION_PUNISHMENT;
            }
        }

        return distance;
    }

    @Override
    public String getName() {
        return name;
    }
}
