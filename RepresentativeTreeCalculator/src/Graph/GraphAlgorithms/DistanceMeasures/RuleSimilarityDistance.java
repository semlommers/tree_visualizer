package Graph.GraphAlgorithms.DistanceMeasures;

import Graph.DecisionTree.DecisionTreeEdge;
import Graph.DecisionTree.DecisionTreeNode;
import Graph.Tree;
import com.google.gson.annotations.Expose;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RuleSimilarityDistance implements TreeDistanceMeasure<DecisionTreeNode, DecisionTreeEdge> {
    @Expose
    String name = "RuleSimilarityDistance";
    private final List<MinMaxValues> baseMinMaxValues = new ArrayList<>();
    private final int SCALE_FACTOR = 1000;

    public RuleSimilarityDistance(String inputFileLocation) throws IOException {
        // Input file should be a csv file containing rows of the dataset with last column holding the target values.
        retrieveMinMaxValuesOfFeatures(Files.readAllLines(Paths.get(inputFileLocation)));
    }

    private void retrieveMinMaxValuesOfFeatures(List<String> lines) {
        // Find out if last feature is the target value
        String header = lines.get(0);
        List<String> headerValues = new ArrayList<>(Arrays.asList(header.split(",")));
        int compensateTargetValue = 0;
        if (headerValues.get(headerValues.size() - 1).equals("target")) {
            compensateTargetValue = 1;
        }

        for (int i = 1; i < lines.size(); i++) { //skip header
            String line = lines.get(i);
            List<String> values = new ArrayList<>(Arrays.asList(line.split(",")));

            for (int j = 0; j < values.size() - compensateTargetValue; j++) { // Skip last value which should be the target
                Double value = Double.valueOf(values.get(j));

                if (baseMinMaxValues.size() - 1 < j) {
                    baseMinMaxValues.add(new MinMaxValues(value, value));
                }

                MinMaxValues baseMinMaxValue = baseMinMaxValues.get(j);

                if (baseMinMaxValue.getMinValue() > value) {
                    baseMinMaxValue.setMinValue(value);
                }

                if (baseMinMaxValue.getMaxValue() < value) {
                    baseMinMaxValue.setMaxValue(value);
                }
            }
        }
    }

    @Override
    public int getDistance(Tree<DecisionTreeNode, DecisionTreeEdge> t1, Tree<DecisionTreeNode, DecisionTreeEdge> t2) {
        HashMap<Integer, List<RuleIntervals>> intervals1 = new HashMap<>();
        traverseTreeRecursively(t1.calculateRoot(), new RuleIntervals(baseMinMaxValues), intervals1);

        HashMap<Integer, List<RuleIntervals>> intervals2 = new HashMap<>();
        traverseTreeRecursively(t2.calculateRoot(), new RuleIntervals(baseMinMaxValues), intervals2);

        double computedDistance1 = computeOneSidedDistance(intervals1, intervals2);
        double computedDistance2 = computeOneSidedDistance(intervals2, intervals1);

        assert computedDistance1 <= 1.0;
        assert computedDistance1 >= 0.0;
        assert computedDistance2 <= 1.0;
        assert computedDistance2 >= 0.0;

        double totalDistance = (computedDistance1 + computedDistance2) / 2.0 * SCALE_FACTOR;

        return (int) Math.round(totalDistance);
    }

    @Override
    public String getName() {
        return name;
    }

    private void traverseTreeRecursively(DecisionTreeNode currentNode, RuleIntervals ruleIntervals,
                                         HashMap<Integer, List<RuleIntervals>> currentRules) {
        RuleIntervals copyRuleIntervals = new RuleIntervals(ruleIntervals);
        List<DecisionTreeEdge> edges = currentNode.getOutgoingEdgesSorted();
        if (edges.size() == 0) {
            int predictedClass = currentNode.predictedLabel;
            List<RuleIntervals> predictedClassIntervals = currentRules.getOrDefault(predictedClass, new ArrayList<>());
            predictedClassIntervals.add(copyRuleIntervals);
            currentRules.put(predictedClass, predictedClassIntervals);
            return;
        }

        for (DecisionTreeEdge edge : edges) {
            copyRuleIntervals = new RuleIntervals(ruleIntervals);
            copyRuleIntervals.setIntervalsByEdge(edge);
            traverseTreeRecursively(edge.target, copyRuleIntervals, currentRules);
        }

    }

    private Double embeddingFunction(RuleIntervals ruleIntervals1, RuleIntervals ruleIntervals2) {
        double totalDistance = 0.0;

        List<MinMaxValues> intervals1 = ruleIntervals1.getIntervals();
        List<MinMaxValues> intervals2 = ruleIntervals2.getIntervals();

        double intervalSizes = intervals1.size();

        for (int i = 0; i < intervalSizes; i++) {
            MinMaxValues values1 = intervals1.get(i);
            MinMaxValues values2 = intervals2.get(i);

            double overlay = Math.max(Math.min(values1.maxValue, values2.maxValue) - Math.max(values1.minValue, values2.minValue), 0.0);
            double distance = 1 - overlay / (values1.maxValue - values1.minValue);
            totalDistance = totalDistance + distance;
        }

        return totalDistance / intervalSizes;
    }

    private Double computeOneSidedDistance(HashMap<Integer, List<RuleIntervals>> intervals1, HashMap<Integer, List<RuleIntervals>> intervals2) {
        double totalDistance = 0.0;

        Set<Integer> classLabels1 = intervals1.keySet();

        for (Integer classLabel : classLabels1) {
            double distanceForClassLabel = 0;
            List<RuleIntervals> ruleIntervals1 = intervals1.get(classLabel);
            List<RuleIntervals> ruleIntervals2 = intervals2.getOrDefault(classLabel, new ArrayList<>());

            for (RuleIntervals checkedRule : ruleIntervals1) {
                double distance = 1;

                for (RuleIntervals otherRule : ruleIntervals2) {
                    double computedDistance = embeddingFunction(checkedRule, otherRule);

                    assert computedDistance <= 1;
                    assert computedDistance >= 0;

                    distance = Math.min(distance, computedDistance);
                }

                distanceForClassLabel = distanceForClassLabel + distance;
            }
            totalDistance = totalDistance + distanceForClassLabel / ruleIntervals1.size();
        }

        return totalDistance / classLabels1.size();
    }

    private class MinMaxValues {
        private Double minValue;
        private Double maxValue;

        public MinMaxValues(Double minValue, Double maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public MinMaxValues(MinMaxValues minMaxValues) {
            this.minValue = minMaxValues.getMinValue();
            this.maxValue = minMaxValues.getMaxValue();
        }

        public Double getMaxValue() {
            return maxValue;
        }

        public Double getMinValue() {
            return minValue;
        }

        public void setMaxValue(Double maxValue) {
            this.maxValue = maxValue;
        }

        public void setMinValue(Double minValue) {
            this.minValue = minValue;
        }
    }

    private class RuleIntervals {
        private List<MinMaxValues> intervals;

        public RuleIntervals(RuleIntervals intervals) {
            List<MinMaxValues> intervalsList = intervals.getIntervals();
            this.intervals = new ArrayList<>();
            for (MinMaxValues interval : intervalsList) {
                this.intervals.add(new MinMaxValues(interval));
            }
        }

        public RuleIntervals(List<MinMaxValues> intervals) {
            this.intervals = new ArrayList<>();
            for (MinMaxValues interval : intervals) {
                this.intervals.add(new MinMaxValues(interval));
            }
        }

        private List<MinMaxValues> getIntervals() {
            return intervals;
        }

        public void setIntervalsByEdge(DecisionTreeEdge edge) {
            int featureId = edge.featureId;
            MinMaxValues minMaxValues = intervals.get(featureId);
            if (minMaxValues.getMinValue() < edge.minValue) {
                minMaxValues.setMinValue(edge.minValue);
            }
            if (minMaxValues.getMaxValue() > edge.maxValue) {
                minMaxValues.setMaxValue(edge.maxValue);
            }
            intervals.set(featureId, minMaxValues);
        }
    }
}
