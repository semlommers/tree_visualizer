import InfectionTreeGenerator.Graph.DecisionTree.DecisionTreeEdge;
import InfectionTreeGenerator.Graph.DecisionTree.DecisionTreeGraph;
import InfectionTreeGenerator.Graph.DecisionTree.DecisionTreeNode;
import Import.JsonNode;
import Import.JsonNode.JsonChild;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MaxSondag, SemLommers
 */
class RandomForestParser {

    DecisionTreeGraph decisionTreeGraph = new DecisionTreeGraph();
    String fileLocation;

    public RandomForestParser(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public void constructGraph() throws IOException {
        parseData(fileLocation);
    }

    private void parseData(String fileLocation) throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(fileLocation));
        JsonNode[] jsonNodes = gson.fromJson(reader, JsonNode[].class);

        for (JsonNode node : jsonNodes) {
            createNode(node);

            // Children have to be created before adding edges
            if (node.children != null){
                for (JsonChild child : node.children) {
                    createChild(child);
                    createEdge(node, child);
                }
            }
        }
    }

    private void createNode(JsonNode rootNode) {
        int id = rootNode.nodeId;
        if (!decisionTreeGraph.hasNodeWithId(id)) {
            DecisionTreeNode node = new DecisionTreeNode(id, rootNode.featureId, rootNode.predictedLabel);
            decisionTreeGraph.addNode(node);
        } else { // Node has already been created as a child
            DecisionTreeNode node = decisionTreeGraph.getNode(id);
            node.featureId = rootNode.featureId;
            node.predictedLabel = rootNode.predictedLabel;
        }
    }

    private void createChild(JsonChild childNode) {
        int id = childNode.childId;
        if (!decisionTreeGraph.hasNodeWithId(id)) {
            DecisionTreeNode node = new DecisionTreeNode(id);
            decisionTreeGraph.addNode(node);
        }
    }

    private void createEdge(JsonNode rootNode, JsonChild childNode) {
        DecisionTreeNode root =  decisionTreeGraph.getNode(rootNode.nodeId);
        DecisionTreeNode target =  decisionTreeGraph.getNode(childNode.childId);
        DecisionTreeEdge edge = new DecisionTreeEdge(root, target, rootNode.featureId, childNode.minValue, childNode.maxValue);
        decisionTreeGraph.addEdge(edge);
    }
}
