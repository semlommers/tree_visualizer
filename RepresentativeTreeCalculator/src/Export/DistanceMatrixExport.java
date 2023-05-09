package Export;

import Graph.Edge;
import Graph.Graph;
import Graph.Node;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DistanceMatrixExport<N extends Node<N, E>, E extends Edge<N, E>> {

    public void distanceMatrixExport(String outputFileLocation, Graph<N, E> connectedGraph, String distanceMetricName) throws IOException {
        List<String> distanceMatrix = new ArrayList<>();

        Collection<N> nodes = connectedGraph.getNodes();
        List<Integer> ids = new ArrayList<>();
        for (N node : nodes) {
            ids.add(node.id);
        }
        Collections.sort(ids);

        for (int i = 0; i < ids.size(); i++) {
            String[] row = new String[ids.size()];
            for (int j = 0; j < ids.size(); j++) {
                if (i == j) {
                    row[j] = Double.toString(0.0);
                } else {
                    E edge = connectedGraph.getEdge(ids.get(i), ids.get(j));
                    row[j] = Double.toString(edge.weight);
                }
            }
            distanceMatrix.add(String.join(",", row));
        }

        Files.createDirectories(Paths.get(outputFileLocation));
        try (PrintWriter pw = new PrintWriter(outputFileLocation + distanceMetricName + ".csv")) {
            for (int i = 0; i < ids.size() - 1; i++) {
                pw.print(ids.get(i) + ",");
            }
            pw.println(ids.get(ids.size() - 1));
            distanceMatrix.forEach(pw::println);
        }


    }
}
