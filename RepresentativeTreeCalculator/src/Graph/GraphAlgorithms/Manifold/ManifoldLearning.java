package Graph.GraphAlgorithms.Manifold;

import Graph.Edge;
import Graph.Graph;
import Graph.GraphAlgorithms.DistanceMeasures.TreeDistanceMeasure;
import Graph.Node;
import Utility.Log;
import smile.manifold.MDS;
import smile.manifold.TSNE;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ManifoldLearning<N extends Node<N, E>, E extends Edge<N, E>> {
    double[][] distanceMatrix;
    HashMap<Integer, Integer> idToTreeId = new HashMap<>();
    String manifoldTechnique = "mds";

    public ManifoldLearning(Integer nTrees) {
        distanceMatrix = new double[nTrees][nTrees];
    }

    public void addDistance(Integer id1, Integer id2, double distance) {
        distanceMatrix[id1][id2] = distance;
        distanceMatrix[id2][id1] = distance;
    }

    public void addIdMapping(Integer id, Integer treeId) {
        Integer result = idToTreeId.put(id, treeId);
    }

    public void computeAndStoreManifold(TreeDistanceMeasure<N, E> distanceMeasure) {
        double[][] coordinates = computeManifold();
        for (Integer id : idToTreeId.keySet()) {
            Integer treeId = idToTreeId.get(id);
            List<Double> coordinatePair = Arrays.stream(coordinates[id])
                    .boxed()
                    .collect(Collectors.toList());
            distanceMeasure.setTreeIdToManifoldCoordinates(treeId, coordinatePair);
        }
    }

    private double[][] computeManifold() {
        if (Objects.equals(manifoldTechnique, "t-sne")) {
            return computeTSNECoordinates();
        } else if (Objects.equals(manifoldTechnique, "mds")) {
            return computeMDSCoordinates();
        } else {
            Log.printOnce("Manifold technique: " + manifoldTechnique + " is not a valid technique");
            return new double[0][0];
        }
    }

    private double[][] computeMDSCoordinates() {
        MDS mds = MDS.of(distanceMatrix);
        return mds.coordinates;
    }

    private double[][] computeTSNECoordinates() {
        TSNE tsne = new TSNE(distanceMatrix, 2);
        return tsne.coordinates;
    }


    public void distanceMatrixExport(String outputFileLocation, String distanceMetricName) throws IOException {

        List<Object> ids = Arrays.asList(idToTreeId.values().toArray());

        Files.createDirectories(Paths.get(outputFileLocation));
        try (PrintWriter pw = new PrintWriter(outputFileLocation + distanceMetricName + ".csv")) {
            for (int i = 0; i < ids.size() - 1; i++) {
                pw.print(ids.get(i) + ",");
            }
            pw.println(ids.get(ids.size() - 1));

            List<String> list = Arrays.stream(distanceMatrix)
                    .map(line -> Arrays.stream(line)
                            .mapToObj(String::valueOf)
                            .collect(Collectors.joining(",")))
                    .collect(Collectors.toList());


            list.forEach(pw::println);
        }

    }
}
