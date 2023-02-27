
import InfectionTreeGenerator.Graph.DecisionTree.DecisionTreeEdge;
import InfectionTreeGenerator.Graph.DecisionTree.DecisionTreeGraph;
import InfectionTreeGenerator.Graph.DecisionTree.DecisionTreeNode;
import JsonObjects.JsonNode;
import JsonObjects.JsonNode.JsonChild;
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
 * @author MaxSondag
 */
class RandomForestParser {

    DecisionTreeGraph dtg = new DecisionTreeGraph();
    String fileLocation;

    public RandomForestParser(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public DecisionTreeGraph constructGraph() throws IOException {
        parseData(fileLocation);
        return dtg;
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

    private int createNode(JsonNode rootNode) {
        int id = rootNode.nodeId;
        if (!dtg.hasNodeWithId(id)) {
            DecisionTreeNode n = new DecisionTreeNode(id, rootNode.featureId, rootNode.predictedLabel);
            dtg.addNode(n);
        } else { // Node has already been created as a child
            DecisionTreeNode node = dtg.getNode(id);
            node.featureId = rootNode.featureId;
            node.predictedLabel = rootNode.predictedLabel;
        }
        return id;
    }

    private int createChild(JsonChild childNode) {
        int id = childNode.childId;
        if (!dtg.hasNodeWithId(id)) {
            DecisionTreeNode n = new DecisionTreeNode(id);
            dtg.addNode(n);
        }
        return id;
    }

    private void createEdge(JsonNode rootNode, JsonChild childNode) {
        DecisionTreeNode root =  dtg.getNode(rootNode.nodeId);
        DecisionTreeNode target =  dtg.getNode(childNode.childId);
        DecisionTreeEdge e = new DecisionTreeEdge(root, target, rootNode.featureId, childNode.minValue, childNode.maxValue);
        dtg.addEdge(e);
    }
}
