/**
 * Updates the visualization without changing the layout of the trees
 */
function updateSliderPreview() {
    let idsToHide = getIdsToHide(currentDistance)
    hideTrees(idsToHide);
    updateRepresentationText();

    updateScentWidget(currentDistance)
    updateAccuracies(currentDistance)
}


function updateAll() {
    if (loadDifferentDistanceMetric) {
        loadDifferentDistanceMetric = false;
        updateDistanceMetricUsed();
    }
    updateSliderPreview();
    updateNodesRepresentedBy();
    updateColors();
    if (recalculate) { //if we need to reinitialize the grid
        recalculate = false;
        d3.select("#treeGrid").selectAll("*").remove();
        generateTreeGrid();
		//update the position without animating as we are redrawing the tree
        updatePositions(false);
    } else { 
	//only moving and recoloring. Update via position
        updatePositions(true);
    }
    updateSecondaryPanel();

    // updateGlobalChart(); // TODO: remove if it breaks nothing
    changeNoLongerPending();
}

function updateSecondaryPanel() {
    resetHighlight();
    resetExplorationPanel();
    resetFeaturePerDepth();
    resetFeatureThresholdsPlot()
    if (focusedTree != null) {
        createRepresentedTreeGrid(focusedTree);
        createFeaturePerDepthPlot(focusedTree, false);
        createFeatureThresholdsPlot(focusedTree, false);
    }
    if (secondaryFocusedTree != null) {
        createFeaturePerDepthPlot(secondaryFocusedTree, true);
        createFeatureThresholdsPlot(secondaryFocusedTree, true);
    }
}

function updateColors() {
    updateColorLegend(); //Make sure color legend is up-to-date
    updateNodeGlyphs(true); //update the glyphs for the visible trees. 
}

function updatePositions(animate = true) {
    removeAllPopups(); //remove all popups as we are changing the layout and possibly hiding trees/nodes
    let idsToHide = getIdsToHide(currentDistance);
    updateTreesAnimated(idsToHide,animate);
}

function updateGlobalChart() {
    //TODO: Not optimized at all, but works
    const distributionDiv = d3.select("#sidePanel").select("#distributionChartPanel");
    distributionDiv.select(".barChartsContainer").remove()

    createComponentBarChart(distributionDiv);
}

function updateNodesRepresentedBy() {
    // Could be improved by doing this in the back-end
    nodesRepresentedBy = new Map();
    for (let i = 0; i < repTreesData.length; i++) {
        if (repTreesData[i].maxDistance >= currentDistance) {
            updateNodesRepresentedByForSubTree(repTreesData[i])
        }
    }
}

function updateNodesRepresentedByForSubTree(node) {
    let reps = node["representations"];
    let repNodeId = node.id;

    for (let i = 0; i < reps.length; i++) {
        const repIData = reps[i];
        if (repIData.distance <= currentDistance) {
            const repIds = repIData["representationIds"];
            for (let j = 0; j < repIds.length; j++) {
                nodesRepresentedBy.set(repIds[j], repNodeId)
            }
        }
    }

    let children = node.children;
    for (let i = 0; i < children.length; i++) {
        updateNodesRepresentedByForSubTree(children[i])
    }
}

function updateDistanceMetricUsed() {
    focusedTree = null;
    secondaryFocusedTree = null;
    d3.json(repTreesDataInputBaseLocation + currentDistanceMetric + ".json").then(function(repTreesDataInput) {
        repTreesData = repTreesDataInput;
        initializeRepTreeData();
        updateCurrentDistanceMetricMetaData();
        updateDistanceSlider();
        updateSliderPreview();
        updateNodesRepresentedBy();
        d3.select("#treeGrid").selectAll("*").remove();
        generateTreeGrid();
        updatePositions(true);
        updateManifoldPlot();
    });
}

function updateCurrentDistanceMetricMetaData() {
    for (let i = 0; i < distanceMetricMetaData.length; i++) {
        if (distanceMetricMetaData[i].name === currentDistanceMetric) {
            currentDistanceMetricMetaData = distanceMetricMetaData[i];
            break;
        }
    }
}

function updateManifoldPlot() {
    d3.select("#tab11Content").selectAll("*").remove();
    createManifoldPlot();
}

function updateDistanceSlider() {
    d3.select("#DistanceSliderdiv").selectAll("*").remove();
    let divToAppendTo = d3.select("#DistanceSliderdiv");
    createDistanceSlider(divToAppendTo);
}


function changeNoLongerPending() {
    const recalcButton = d3.select("#sidePanel").select(".recalculateDiv").select("#recalculateButton")
    recalcButton.classed("disabled", true)
}

function changePending() {
    const recalcButton = d3.select("#sidePanel").select(".recalculateDiv").select("#recalculateButton")
    recalcButton.classed("disabled", false)
}

function hideTrees(idsToHide) {
    d3.select("#treeGrid")
        .selectAll(".svgtree")
        .attr("class", function() {
            const treeId = parseInt(d3.select(this).attr('id').substring(3));
            //substring 3 as id is "tidXXX" where XXX is a number
            if (idsToHide.includes(treeId)) {
                return "svgtree hiddenNodes"; //todo: Only change hidden of visible without needing to specify the rest
            } else {
                return "svgtree visible";
            }
        })
}

function updateRepresentationText() {
    d3.select("#treeGrid")
        .selectAll(".svgtree")
        .selectAll(".textG")
        .select("text")
        .text(function() {
            const treeId = parseInt(d3.select(this).node().parentNode.parentNode.getAttribute("id").substring(3))
            return getAmountOfTreesRepresentedById(treeId, currentDistance);
        })
}


function updateScentWidget(distance) {
    //delete old
    d3.select("#RtScentedChart").remove();
    //make new
    createScentedRtLineChart(d3.select("#DistanceSliderdiv"), distance);
}

function updateAccuracies(distance) {
    // Delete old
    d3.select("#AccuraciesWidgetDiv").remove();
    // Make new
    createAccuracyWidget(d3.select("#AccuraciesWidgetDivParent"), distance);
}

/**
 * Animates the changes in the trees
 * @param {*} idsToHide 
 * @param animate if false, no animation will be used
 */
function updateTreesAnimated(idsToHide, animate=true) {
    const updatedPlacement = recalculatePlacement(idsToHide);
    const newWidths = updatedPlacement[0];
    const newHeights = updatedPlacement[1];
    const offSets = updatedPlacement[2]

    let transitionTime = 1000;
    if (animate === false) {
        transitionTime = 0;
    }

    animateChanges(newWidths, newHeights, offSets,transitionTime)
}

function recalculatePlacement(idsToHide) {

    //get the new widths and offsets
    const newWidths = [];
    const newHeights = [];
    const newHorizontalMargins = [];

    for (let i = 0; i < treeOrder.length; i++) {
        const id = treeOrder[i];

        const repAmount = getAmountOfTreesRepresentedById(id, currentDistance);

        let width = treeBaseWidthById.get(id); //get base width
        let height = treeBaseHeightById.get(id); //get base height
        let horizontalMargin = horizontalMarginBetweenTrees;

        let scaleFactor; //how much to scale the trees by
        if (idsToHide.includes(id)) { //tree will be hidden, shrink it
            scaleFactor = hiddenTreesScalingFactor;
            horizontalMargin = horizontalMargin * scaleFactor; //shrink margin only if hidden
        } else { //otherwise scale tree
            //hidden trees cannot have a repAmount more than 1
            //scale trees according to repAmount
            scaleFactor = getScaleFactorByRepAmount(repAmount);
        }
        width = width * scaleFactor;
        height = height * scaleFactor;



        newWidths[i] = width;
        newHeights[i] = height;
        newHorizontalMargins[i] = horizontalMargin;
    }

    const div = d3.select("#treeGridDiv");
    let targetContainerWidth = div.node().clientWidth;


    const offSets = calculateOffsets(newWidths, newHeights, newHorizontalMargins, targetContainerWidth);

    return [newWidths, newHeights, offSets];
}


function animateChanges(widthArray, heightArray, offsetArray,transitionTime) {

    const widthMap = new Map();
    const heightMap = new Map();
    const xOffsetMap = new Map();
    const yOffsetMap = new Map();


    for (let i = 0; i < treeOrder.length; i++) {
        const id = treeOrder[i];
        xOffsetMap.set(id, offsetArray[i][0]);
        yOffsetMap.set(id, offsetArray[i][1]);
        widthMap.set(id, widthArray[i]);
        heightMap.set(id, heightArray[i]);
    }


    //Note, d3 using svg is not fast enough to animate the amount of elements we are using. 
    //TODO: Trim down glyphs to only contains parts that exist. Needs a refactor. If we want to animate glyphs, it needs a different technology (canvas?)

    d3.select("#treeGrid")
        .interrupt()
        .transition()
        .duration(transitionTime)
        .selectAll("svg")
        .attr("width", function() { //update the width. Can increase or decrease.
            const treeId = parseInt(d3.select(this).attr('id').substring(3))
            const width = widthMap.get(treeId);
            return width;
        })
        .attr("height", function() { //update the height. Can increase or decrease.
            const treeId = parseInt(d3.select(this).attr('id').substring(3))
            const height = heightMap.get(treeId);
            return height;
        })
        .attr("x", function() { //update x
            const treeId = parseInt(d3.select(this).attr('id').substring(3))
            const xOffset = xOffsetMap.get(treeId);
            return xOffset;
        })
        .attr("y", function() { //update y
            const treeId = parseInt(d3.select(this).attr('id').substring(3))
            const yOffset = yOffsetMap.get(treeId);
            return yOffset;
        })
        .style("outline", function () {
            const treeId = parseInt(d3.select(this).attr('id').substring(3))
            if (treeId === focusedTree) {
                return "1px solid red";
            } else if (treeId === secondaryFocusedTree) {
                return "1px solid #00d0ff"
            } else {
                return "none"
            }
        })
        .end()
        .then(() => {
            const svg = d3.select("#treeGrid");
            resizeSVG(svg);
        })
        .catch((error) => {
            console.error(error);
            //ignore the error. Can come from it being interrupted in which
            //case there is no need to resize
        })

}



function getIdsToHide(distance) {
    let idsToHide = [];
    for (let i = 0; i < repTreesData.length; i++) {
        if (repTreesData[i].maxDistance < distance) {
            idsToHide.push(repTreesData[i].id);
        }
    }
    return idsToHide;
}