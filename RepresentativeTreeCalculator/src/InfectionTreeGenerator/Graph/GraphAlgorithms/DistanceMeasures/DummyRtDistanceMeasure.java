package InfectionTreeGenerator.Graph.GraphAlgorithms.DistanceMeasures;

import InfectionTreeGenerator.Graph.Tree;

import java.util.Random;

public class DummyRtDistanceMeasure implements TreeDistanceMeasure {

    @Override
    public int getDistance(Tree t1, Tree t2) {
        Random random = new Random();
        int randomInteger = random.nextInt(100);
        return randomInteger;
    }
}
