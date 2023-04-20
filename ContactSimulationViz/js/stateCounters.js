//Holds several methods to count how many nodes are in states

function classProportionsCount(nodeId) {
    const metadata = metaDataFromNodeById.get(nodeId);
    return metadata.classProportions;
}


/**
 * Counts how many nodes that the node with id={id} represents are in a certain exposed time group
 * @param {} nodeId
 * @param {If true, takes the trees that are represented by this node into account as well} isRepTree
 * @param {Which policy data to use} policy
 * @param {How much the appPercentage of the policy is} appPercentage
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
        const repNodeIsLeaf = (repTreeMetaData.predictedLabel != null);

        let equalNodeCount = 0;
        let differentNodeCount = 0;
        let nodeLeafComparisonCount = 0;

        for (const node of representedNodes) {
            const nodeIsLeaf = (node.predictedLabel != null);

            // Logical XOR
            if (repNodeIsLeaf !== nodeIsLeaf) {
                nodeLeafComparisonCount++;
            } else {
                if (repNodeIsLeaf) {
                    if (repTreeMetaData.predictedLabel === node.predictedLabel) {
                        equalNodeCount++;
                    } else {
                        differentNodeCount++;
                    }
                } else {
                    if (repTreeMetaData.featureId === node.featureId) {
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

            const repNodeIsLeaf = (repNodeMetaData.predictedLabel != null);
            const nodeIsLeaf = (nodeMetaData.predictedLabel != null);

            // Logical XOR
            if (repNodeIsLeaf !== nodeIsLeaf) {
                return [0, 0, 0, 1];
            } else {
                if (repNodeIsLeaf) {
                    if (repNodeMetaData.predictedLabel === nodeMetaData.predictedLabel) {
                        return [1, 0, 0, 0];
                    } else {
                        return [0, 0, 1, 0];
                    }
                } else {
                    if (repNodeMetaData.featureId === nodeMetaData.featureId) {
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
    return metadata.correctVsIncorrectClassifiedData;
}

/**
 * Holds whether the node with the specified metadata is removed by the policy
 */
function isRemovedByPolicy(nodeMetaData, policy, appPercentage, origin = false) {
    return false;
    for (const metaPolicy of nodeMetaData.policies) {

        //combine policyAndAppPercentage to get the right number
        let policyApp = policy;
        if (policy != "1x") { //1x policy doesn't have numbers
            policyApp += "A" + appPercentage;
        }
        if (origin) {
            policyApp += "Origin";
        }
        if (metaPolicy == policyApp) {
            return true;
        }
    }
    return false;
}


/**
 * returns the state of the virus of the node at time t 
 * @param {the meta data of the node} nodeMetaData
 */
function getInfectorState(nodeMetaData) {
    if (nodeMetaData === undefined) {
        console.log("check")
    }

    const sourceId = nodeMetaData.sourceInfectionId;
    const time = nodeMetaData.exposedTime;

    const metaDataInfector = metaDataFromNodeById.get(sourceId);
    if (metaDataInfector == undefined) {
        console.log("No meta data available for node with id " + sourceId);
        return "";
    }
    const virusProgression = metaDataInfector.virusProgression;

    const lastVirusTime = getLastVirusTimeBeforeTime(virusProgression, time);

    const stateAtExposedTime = virusProgression[lastVirusTime];
    return stateAtExposedTime;
}

/**
 * Virusprogression is sorted
 * @param {} virusProgression 
 * @param {*} time 
 * @returns 
 */
function getLastVirusTimeBeforeTime(virusProgression, time) {
    let lastVirusTime = '0.0';
    for (const virusTime in virusProgression) {
        //need to convert everything to floats
        const virusTimeF = parseFloat(virusTime)
        if (virusTimeF <= time) { //this virusevent happened before or at the time we are interested in
            lastVirusTime = virusTime;
        } else {
            break; //sorted, so can't find a new one anymore
        }
    }
    return lastVirusTime;
}


/**
 * returns whether this is an intial node or an other node
 * @param {the meta data of the node} nodeMetaData
 */
function getNoneState(nodeMetaData) {
    let state = getInfectorState(nodeMetaData);
    if (state.toUpperCase() != "initial".toUpperCase()) { //root node
        state = "Other";
    }
    return state;
}

/**
 * Returns the age group of the node
 */
function getAge(nodeMetaData) {
    let age = nodeMetaData.age

    for (let i = 0; i < 10; i++) {
        const bottomAgeRange = i * 20;
        const topAgeRange = (i + 1) * 20;
        if (bottomAgeRange <= age && age < topAgeRange) {
            age = bottomAgeRange + "-" + topAgeRange;
        }
    }
    return age;
}

/**
 * Returns the age group of the node
 */
function getInfectionTime(nodeMetaData) {
    let time = nodeMetaData.exposedTime


    if (time < 5) {
        time = "0-5";
    } else if (time < 10) {
        time = "5-10";
    } else if (time < 15) {
        time = "10-15";
    } else if (time < 20) {
        time = "15-20";
    } else if (time < 25) {
        time = "20-25";
    } else if (time < 30) {
        time = "25-30";
    } else if (time < 50) {
        time = "30-50";
    } else {
        time = "50-100";
    }

    return time;
}