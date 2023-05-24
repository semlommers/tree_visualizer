/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Graph.GraphAlgorithms.DistanceMeasures;

import Graph.Edge;
import Graph.Node;
import Graph.Tree;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author MaxSondag, SemLommers
 */
public abstract class TreeDistanceMeasure<N extends Node<N, E>, E extends Edge<N, E>> {
    @Expose
    Map<Integer, List<Double>> accuracyByDistance = new HashMap<>();

    public void setAccuracyForDistance(Integer distance, List<Double> accuracies) {
        accuracyByDistance.put(distance, accuracies);
    }

    /**
     * Distance between two trees. Values are rounded up in case of
     * internal double values
     */
    public abstract int getDistance(Tree<N, E> t1, Tree<N, E> t2);

    public abstract String getName();

}
