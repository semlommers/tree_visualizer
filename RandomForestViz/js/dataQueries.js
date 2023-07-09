/**
 * Preprocess the data to useful datastructures
 */
function preprocessData() {
    initializeRepTreeData()

    for (let i = 0; i < metaData.length; i++) {
        const node = metaData[i];
        const id = node.id;
        metaDataFromNodeById.set(id, node);
    }
    metaData = undefined;

    for (let i = 0; i < allTreesData.length; i++) {
        const tree = allTreesData[i];
        const id = tree.id;
        allTreeById.set(id, tree);

        //add extra metadata for each tree.
        addExtraMetaData(tree, 0, id);
    }
    allTreesData = undefined;

    for (let i = 0; i < namesData["target_names"].length; i++) {
        classProportionsColorSchemeOrderDisplay.push(namesData["target_names"][i]);
    }

    updateNodesRepresentedBy();
}

function initializeRepTreeData() {
    // Reset the global variables related to repTrees
    repTreeById = new Map();
    repNodeById = new Map();
    maxMaxDistance = 0;
    treeOrder = [];
    treeBaseWidthById = new Map();
    treeBaseHeightById = new Map();

    for (let i = 0; i < repTreesData.length; i++) {
        const repTree = repTreesData[i];
        const treeId = repTree.id;
        repTreeById.set(treeId, repTree);

        if (maxMaxDistance < repTree.maxDistance) {
            maxMaxDistance = repTree.maxDistance;
        }

        const nodes = getNodes(repTree);
        for (let j = 0; j < nodes.length; j++) {
            const repNode = nodes[j];
            const repId = repNode.id;
            repNodeById.set(repId, repNode);
        }
    }
}

function addExtraMetaData(tree, depth, rootId) {
    //save node reference
    metaDataFromNodeById.get(tree.id).depth = depth;
    metaDataFromNodeById.get(tree.id).rootId = rootId;

    //recurse into children
    for (let child of tree.children) {
        addExtraMetaData(child, depth + 1, rootId);
    }
}

/**
 * Returns the maximum depth of any tree
 */
function getMaxDepth() {
    let maxDepth = 0;
    for (let tree of allTreeById.values()) {
        maxDepth = Math.max(maxDepth, getTreeHeight(tree))
    }
    return maxDepth;
}

/**
 * 
 * Returns the height of the subtree rooted at treeNode
 */
function getTreeHeight(treeNode) {
    let height = 0;
    for (let tree of treeNode.children) {
        let newHeight = getTreeHeight(tree) + 1; //1 further down the tree
        height = Math.max(height, newHeight);
    }
    return height;
}



/**
 * Gets the amount of trees represented by the tree {@code d} before distance {@code distance}
 * @param d
 * @param {*} distance
 */
function getAmountOfTreesRepresented(d, distance) {
    let count = 0;
    let reps = d.data["representations"];
    for (let repI = 0; repI < reps.length; repI++) {
        const repIData = reps[repI];
        if (repIData.distance <= distance) {
            count += repIData["representationIds"].length;
        }
    }
    return count;
}


/**
 * Gets the amount of trees represented by the tree with id {@code id} before distance {@code distance}
 * @param id
 * @param {*} distance
 */
function getAmountOfTreesRepresentedById(id, distance) {
    const repTree = repTreeById.get(id);
    if (repTree === undefined) { //Occurs when looking at AllTrees which do not have representations
        return 1;
    }
    let reps = repTree["representations"];

    let count = 0;
    for (let repI = 0; repI < reps.length; repI++) {
        const repIData = reps[repI];
        if (repIData.distance <= distance) {
            count += repIData["representationIds"].length;
        }
    }
    return count;
}

/**
 * Gets the amount of trees represented by the tree with id {@code id} before distance {@code distance}
 * @param id
 * @param {*} distance
 */
function getTreesRepresentedById(id, distance) {
    const repTree = repTreeById.get(id);
    let reps = repTree["representations"];

    let repTreeIds = [];
    for (let i = 0; i < reps.length; i++) {
        const repIData = reps[i];
        if (repIData.distance <= distance) {
            const repIds = repIData["representationIds"];
            for (let j = 0; j < repIds.length; j++) {
                repTreeIds.push(repIds[j]);
            }
        }
    }

    let allTreesRepresented = [];
    for (let i = 0; i < repTreeIds.length; i++) {
        const tree = allTreeById.get(repTreeIds[i]);
        allTreesRepresented.push(tree);
    }


    return allTreesRepresented;
}

function getTreeIdsRepresentedById(id, distance) {
    if (id === -1) {
        return [];
    }
    const repTree = repTreeById.get(id);
    let reps = repTree["representations"];

    let repTreeIds = [];
    for (let i = 0; i < reps.length; i++) {
        const repIData = reps[i];
        if (repIData.distance <= distance) {
            const repIds = repIData["representationIds"];
            for (let j = 0; j < repIds.length; j++) {
                repTreeIds.push(repIds[j]);
            }
        }
    }

    return repTreeIds;
}


/**
 * Get all nodes that the node with nodeId represents at the specified distance. treeId is the tree nodeId belong to
 */
function getRepresentedNodesMetaData(nodeId, distance) {
    const node = repNodeById.get(nodeId);
    let reps = node["representations"];

    let repNodeIds = [];
    for (let i = 0; i < reps.length; i++) {
        const repIData = reps[i];
        if (repIData.distance <= distance) {
            const repIds = repIData["representationIds"];
            for (let j = 0; j < repIds.length; j++) {
                repNodeIds.push(repIds[j]);
            }
        }
    }

    let metaDataNodes = [];
    for (let i = 0; i < repNodeIds.length; i++) {
        const tree = metaDataFromNodeById.get(repNodeIds[i]);
        if (tree === undefined) {
            console.error("Tree with id " + repNodeIds[i] + " is not present in the metadata")
            continue;
        }
        metaDataNodes.push(tree);
    }

    return metaDataNodes;
}


function getNodes(rootNode) {
    let nodeList = [rootNode]; //add the current node to the list
    //recurse in all children
    for (let i = 0; i < rootNode.children.length; i++) {
        const newNodes = getNodes(rootNode.children[i]);
        if (newNodes) {
            nodeList = nodeList.concat(newNodes);
        }
    }
    return nodeList;
}
