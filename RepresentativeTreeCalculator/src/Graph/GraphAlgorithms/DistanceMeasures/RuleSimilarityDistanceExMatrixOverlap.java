package Graph.GraphAlgorithms.DistanceMeasures;

import Graph.DecisionTree.DecisionTreeEdge;
import Graph.DecisionTree.DecisionTreeGraph;
import Graph.DecisionTree.DecisionTreeNode;
import Graph.Tree;
import com.google.gson.annotations.Expose;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RuleSimilarityDistanceExMatrixOverlap extends TreeDistanceMeasure<DecisionTreeNode, DecisionTreeEdge> {
    @Expose
    String name = "RuleSimilarityDistanceExMatrixOverlap";
    private final List<List<Double>> data = new ArrayList<>();
    private final int SCALE_FACTOR = 1000;

    public RuleSimilarityDistanceExMatrixOverlap(String inputFileLocation) throws IOException {
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
        DecisionTreeGraph dt1 = (DecisionTreeGraph) t1;
        DecisionTreeGraph dt2 = (DecisionTreeGraph) t2;

        HashMap<Integer, HashMap<Integer, Set<Integer>>> dataInstancesPerNode1 = runDatasetThroughTree(dt1);

        HashMap<Integer, HashMap<Integer, Set<Integer>>> dataInstancesPerNode2 = runDatasetThroughTree(dt2);

        assert dataInstancesPerNode1.keySet() == dataInstancesPerNode2.keySet();

        List<Double[][]> distanceMatrices = computeDistanceMatricesPerClass(dataInstancesPerNode1, dataInstancesPerNode2);

        double distance = computeDistanceByMatrices(distanceMatrices);

        assert distance <= 1.0;
        assert distance >= 0.0;

        double totalDistance = distance * SCALE_FACTOR;

        return (int) Math.round(totalDistance);
    }

    @Override
    public String getName() {
        return name;
    }

    private HashMap<Integer, HashMap<Integer, Set<Integer>>> runDatasetThroughTree(DecisionTreeGraph decisionTree) {
        HashMap<Integer, HashMap<Integer, Set<Integer>>> dataInstancesPerNode = new HashMap<>();

        for (int i = 0; i < data.size(); i++) {
            List<Double> instance = data.get(i);
            DecisionTreeNode leafNode = decisionTree.getLeafNodeByPrediction(instance);
            HashMap<Integer, Set<Integer>> instancesPerNode = dataInstancesPerNode.getOrDefault(leafNode.predictedLabel, new HashMap<>());
            Set<Integer> instances = instancesPerNode.getOrDefault(leafNode.id, new HashSet<>());
            instances.add(i);
            instancesPerNode.put(leafNode.id, instances);
            dataInstancesPerNode.put(leafNode.predictedLabel, instancesPerNode);
        }

        return dataInstancesPerNode;
    }

    private List<Double[][]> computeDistanceMatricesPerClass(HashMap<Integer, HashMap<Integer, Set<Integer>>> dataInstancesPerNodePerClass1,
                                                             HashMap<Integer, HashMap<Integer, Set<Integer>>> dataInstancesPerNodePerClass2) {
        List<Double[][]> result = new ArrayList<>();

        Set<Integer> classes = dataInstancesPerNodePerClass1.keySet();

        for (Integer dataClass : classes) {
            HashMap<Integer, Set<Integer>> dataInstancesPerNode1 = dataInstancesPerNodePerClass1.get(dataClass);
            HashMap<Integer, Set<Integer>> dataInstancesPerNode2 = dataInstancesPerNodePerClass2.get(dataClass);

            Double[][] distanceMatrix = computeDistanceMatrix(dataInstancesPerNode1, dataInstancesPerNode2);
            result.add(distanceMatrix);
        }

        return result;
    }

    private Double[][] computeDistanceMatrix(HashMap<Integer, Set<Integer>> dataInstancesPerNode1,
                                             HashMap<Integer, Set<Integer>> dataInstancesPerNode2) {
        int amountOfNodes1 = dataInstancesPerNode1.keySet().size();
        int amountOfNodes2 = dataInstancesPerNode2.keySet().size();

        Double[][] result = new Double[amountOfNodes1][amountOfNodes2];

        int i = 0;
        for (Set<Integer> node1 : dataInstancesPerNode1.values()) {
            int j = 0;
            for (Set<Integer> node2 : dataInstancesPerNode2.values()) {
                result[i][j] = computeDistanceForPair(node1, node2);
                j++;
            }
            i++;
        }

        return result;
    }

    private Double computeDistanceForPair(Set<Integer> dataInstances1, Set<Integer> dataInstances2) {
        // Jacard distance
        double intersectionCount = 0;
        for (Integer instance : dataInstances1) {
            if (dataInstances2.contains(instance)) {
                intersectionCount++;
            }
        }

//        return 1 - intersectionCount / (dataInstances1.size() + dataInstances2.size() - intersectionCount);
        return 1 - intersectionCount / Math.min(dataInstances1.size(), dataInstances2.size());
    }

    private Double computeDistanceByMatrices(List<Double[][]> distanceMatrices) {
        double sum = 0.0;
        double count = 0.0;

        for (Double[][] distanceMatrix : distanceMatrices) {
            sum = sum + computeDistanceByMatrix(distanceMatrix);
            // Count both axes of the matrix
            count = count + distanceMatrix.length + distanceMatrix[0].length;
        }

        return sum / count;
    }

    private Double computeDistanceByMatrix(Double[][] distanceMatrix) {
        List<Double> distances1 = new ArrayList<>();
        List<Double> distances2 = new ArrayList<>();

        for (int i = 0; i < distanceMatrix.length; i++) {
            distances1.add(1.0);
        }
        for (int j = 0; j < distanceMatrix[0].length; j++) {
            distances2.add(1.0);
        }

        // Compute minimums for each row and column
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                if (distances1.get(i) > distanceMatrix[i][j]) {
                    distances1.set(i, distanceMatrix[i][j]);
                }
                if (distances2.get(j) > distanceMatrix[i][j]) {
                    distances2.set(j, distanceMatrix[i][j]);
                }
            }
        }

        // Then sum all minimum values and divide by the total amount of values
        double sum = 0.0;

        for (Double value : distances1) {
            sum = sum + value;
        }

        for (Double value : distances2) {
            sum = sum + value;
        }

        return sum;
    }
}
