/**
 * Creates the stacked chart glyph for each node
 * @param {*} gElement 
 * @param {*} nodeId 
 * @param {*} isRepTree 
 */
function makeNodeGlyph(gElement, nodeId, isRepTree) {
    if (currentTreeVisualization === "Node-link diagram") {
        makeStackedChart(gElement, nodeId, isRepTree);
    } else if (currentTreeVisualization === "Icicle plot") {
        makeStackedChartIciclePlotVertical(gElement, nodeId, isRepTree);
    } else if (currentTreeVisualization === "Sunburst plot") {
        makeStackedChartSunburstVertical(gElement, nodeId, isRepTree);
    } else {
        console.error("No valid tree visualization selected");
        return;
    }

    addNodeInformationToolTip(gElement, nodeId)
}

function updateNodeGlyphs(isRepTree) {
    const gElements = d3.select("#treeGrid") //do not animate these. d3 animations break down at around 20000 svg elements. The largest tree alone has 100 nodes with 10 parts each.
        .selectAll(".svgtree.visible")
        .selectAll(".node")
        .selectAll("g");

    gElements.selectAll("*").remove(); //remove all rectangles, so we can add only those that are needed again

    gElements.each(function() {
        const nodeId = parseInt(d3.select(this).attr("id"));
        makeNodeGlyph(d3.select(this), nodeId, isRepTree)
    });
}

function getPartColor(index) {
    let color = currentColor;

    if (color === "Class Proportions") {
        return classProportionsColorScheme[index];
    }
    if (color === "DT Structure") {
        return decisionTreeStructureColorScheme[index];
    }
    if (color === "DT Comparison") {
        return decisionTreeColorScheme[index];
    }
    if (color === "Correct Classification") {
        return correctClassifiedColorScheme[index];
    }
    console.error("No valid color selected")
}



/**
 * returns [startPercentage,endPercentage] that indicates how much of the value this part has.
 * @param {*} id
 * @param {*} partIndex
 * @param {*} isRepTree
 * @returns
 */
function getPartPercentages(id, partIndex, isRepTree) {
    const counts = getPartCounts(id, isRepTree);

    let startValue = 0; //value of all parts up to index {partIndex}
    let sum = 0;
    for (let i = 0; i < counts.length; i++) {
        sum += counts[i];
        if (i < partIndex) {
            startValue += counts[i];
        }
    }

    if (sum === 0) {
        console.log("Shouldn't happen. Something went wrong in data reading/parsing")
        return [0, 0];
    }

    const startPercentage = startValue / sum;

    const value = counts[partIndex];
    const endPercentage = (startValue + value) / sum;

    return [startPercentage, endPercentage];
}



function getPartCounts(id, isRepTree) {
    let partCounts = new Array(maxParts).fill(0); //array length equal to amount of parts. Fill them in one by one

    let counts, color;

    color = currentColor;

    //get the array, some will have fewer values which we will pad. Each will have how many nodes are "saved" as the first entry
    if (color === "Class Proportions") {
        counts = classProportionsCount(id);
    } else if (color === "DT Structure") {
        counts = decisionTreeStructureCount(id, isRepTree);
    } else if (color === "DT Comparison") {
        counts = decisionTreeComparisonCount(id, isRepTree);
    } else if (color === "Correct Classification") {
        counts = correctClassificationCount(id);
    } else {
        console.error(color + "is not a valid node color and parts cannot be drawn");
        counts = [0];
    }

    for (let i = 0; i < counts.length; i++) {
        partCounts[i] = counts[i];
    }
    return partCounts;
}


function getRectGlyphXPositions() {
    let startX = -nodeBaseSize
    let rectWidth = nodeBaseSize * 2;

    return [startX, rectWidth];
}

function addNodeInformationToolTip(gElement, nodeId) {
    let nodeMetaData = metaDataFromNodeById.get(nodeId);
    const nodeIsLeaf = (nodeMetaData["predictedLabel"] != null);

    if (nodeIsLeaf) {
        gElement.append("title")
            .text(namesData["target_names"][nodeMetaData["predictedLabel"]])
    } else {
        gElement.append("title")
            .text(namesData["feature_names"][nodeMetaData["featureId"]])
    }
}