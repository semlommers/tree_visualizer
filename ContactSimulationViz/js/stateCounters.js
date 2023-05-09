//Holds several methods to count how many nodes are in states

function classProportionsCount(nodeId) {
    const metadata = metaDataFromNodeById.get(nodeId);
    return metadata["classProportions"];
}


/**
 * Counts how many nodes that the node with id={id} represents are in a certain exposed time group
 * @param nodeId
 * @param isRepTree If true, takes the trees that are represented by this node into account as well
 */
function decisionTreeStructureCount(nodeId, isRepTree) {
    if (isRepTree) {
        const rootId = metaDataFromNodeById.get(nodeId).rootId;
        const existingNodes = getRepresentedNodesMetaData(nodeId, currentDistance).length;
        const totalTrees = getAmountOfTreesRepresentedById(rootId, currentDistance);
        const nonExistingNodes = totalTrees - existingNodes;

        return [existingNodes, nonExistingNodes];
    } else { //only use the tree itself
        if (nodesRepresentedBy.has(nodeId)) {
            return [1, 0];
        } else {
            return [0, 1];
        }
    }
}

function decisionTreeComparisonCount(nodeId, isRepTree){
    if (isRepTree) {
        const repTreeMetaData = metaDataFromNodeById.get(nodeId);
        const rootId = repTreeMetaData.rootId;
        const representedNodes = getRepresentedNodesMetaData(nodeId, currentDistance);
        const totalTrees = getAmountOfTreesRepresentedById(rootId, currentDistance);
        const repNodeIsLeaf = (repTreeMetaData["predictedLabel"] != null);

        let equalNodeCount = 0;
        let differentNodeCount = 0;
        let nodeLeafComparisonCount = 0;

        for (const node of representedNodes) {
            const nodeIsLeaf = (node["predictedLabel"] != null);

            // Logical XOR
            if (repNodeIsLeaf !== nodeIsLeaf) {
                nodeLeafComparisonCount++;
            } else {
                if (repNodeIsLeaf) {
                    if (repTreeMetaData["predictedLabel"] === node["predictedLabel"]) {
                        equalNodeCount++;
                    } else {
                        differentNodeCount++;
                    }
                } else {
                    if (repTreeMetaData["featureId"] === node["featureId"]) {
                        equalNodeCount++;
                    } else {
                        differentNodeCount++;
                    }
                }
            }
        }

        const nonExistingNodes = totalTrees - representedNodes.length;

        return [equalNodeCount, nonExistingNodes, differentNodeCount, nodeLeafComparisonCount];
    } else { //only use the tree itself
        if (!nodesRepresentedBy.has(nodeId)) {
            return [0, 1, 0, 0];
        } else {
            let repNodeId = nodesRepresentedBy.get(nodeId);
            let repNodeMetaData = metaDataFromNodeById.get(repNodeId);
            let nodeMetaData = metaDataFromNodeById.get(nodeId);

            const repNodeIsLeaf = (repNodeMetaData["predictedLabel"] != null);
            const nodeIsLeaf = (nodeMetaData["predictedLabel"] != null);

            // Logical XOR
            if (repNodeIsLeaf !== nodeIsLeaf) {
                return [0, 0, 0, 1];
            } else {
                if (repNodeIsLeaf) {
                    if (repNodeMetaData["predictedLabel"] === nodeMetaData["predictedLabel"]) {
                        return [1, 0, 0, 0];
                    } else {
                        return [0, 0, 1, 0];
                    }
                } else {
                    if (repNodeMetaData["featureId"] === nodeMetaData["featureId"]) {
                        return [1, 0, 0, 0];
                    } else {
                        return [0, 0, 1, 0];
                    }
                }
            }
        }
    }
}

function correctClassificationCount(nodeId) {
    const metadata = metaDataFromNodeById.get(nodeId);
    return metadata["correctVsIncorrectClassifiedData"];
}
