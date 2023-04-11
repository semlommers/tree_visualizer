/**
 * 
 * @param {*} treeRoots 
 * @param {*} containerWidth 
 * @param {If true, scales the trees according to how many nodes they represent } represtativeTrees 
 * @returns 
 */
function getOffSets(treeRoots, containerWidth, representativeTrees) {
    //uses the tree with the maximum width to figure out the minimum size of the container to prevent clipping


    let widths = [];
    let heights = [];
    let horMargins = [];
    for (let i = 0; i < treeRoots.length; i++) {
        if (representativeTrees) {
            const id = treeRoots[i].data.id;
            const repAmount = getAmountOfTreesRepresentedById(id, currentDistance);
            const scaleFactor = getScaleFactorByRepAmount(repAmount);
            widths[i] = treeBaseWidthById.get(id) * scaleFactor;
            heights[i] = treeBaseHeightById.get(id) * scaleFactor; //get base height
        } else {
            widths[i] = getDisplayWidth(treeRoots[i]);
            heights[i] = getDisplayHeight(treeRoots[i]);
        }
        horMargins[i] = horizontalMarginBetweenTrees; //all trees are initially visible
    }



    //Holds the [x,y] offset of the trees in order. Calculate from left bottom instead of left top.
    const offSets = calculateOffsets(widths, heights, horMargins, containerWidth);
    return offSets;
}

/**
 * 
 * @param {*} d3 
 * @param {*} svgToAddTo 
 * @param {*} xOffset 
 * @param {*} yOffset 
 * @param {*} root 
 * @param {*} treeId 
 * @param {if true, treats this tree as a representative tree. If false, does not take the representations into account} isRepTree 
 * @returns 
 */
function createSingleTree(svgToAddTo, xOffset, yOffset, root, treeId, isRepTree) {

    let scaleFactor = 1;
    if (isRepTree) {
        const repAmount = getAmountOfTreesRepresentedById(treeId, currentDistance);
        scaleFactor = getScaleFactorByRepAmount(repAmount);
    }

    const width = getDisplayWidth(root);
    const height = getDisplayHeight(root);

    const treeSvg = svgToAddTo
        .insert("svg")
        .attr("id", "tid" + treeId)
        .attr("class", "svgtree visible")
        .attr("viewBox", [0, 0, width, height])
        .attr("width", width * scaleFactor)
        .attr("height", height * scaleFactor)
        .attr("x", xOffset)
        .attr("y", yOffset)
        .data(root) //bind the data

    //add a background so everything is clickable
    const background = treeSvg.append("g")
        .append("rect")
        .attr("x", 0)
        .attr("y", 0)
        .attr("width", width)
        .attr("height", height)
        .style("opacity", 0.0) //make it invisible. TODO: Check performance issues

    if (currentTreeVisualization === "Node-link diagram") {
        return nodeLinkDiagramLayout(treeSvg, root, width, height, isRepTree);
    } else if (currentTreeVisualization === "Icicle plot") {
        return iciclePlotLayout(treeSvg, root, width, height, isRepTree);
    } else {
        console.error("No valid tree visualization selected");
    }
}


function getTreeRoots(treeData) {
    let treeRoots = [];
    for (let i = 0; i < treeData.length; i++) {
        const treeRoot = getTree(treeData[i]);
        treeRoots[i] = treeRoot;
    }
    return treeRoots;
}
/**
 * Returns a tree layout of the data with the correct nodesizes and all positive coordinates.
 * @param {*} data 
 * @returns 
 */
function getTree(data) {
    const dataRoot = d3.hierarchy(data);
    if (currentTreeVisualization === "Node-link diagram") {
        const treeRoot = d3.tree()
            .nodeSize([horNodeSpace, verNodeSpace])
            (dataRoot)


        moveTreeToFirstQuadrant(treeRoot);
        return treeRoot;
    }

    return dataRoot;
}


/**
 * Moves the position of the nodes in the tree with root {@code root} 
 * such that it is completely in the first quadrant with at least one node with x=0 and one node with y=0.
 * @param {The tree we are moving} root 
 */
function moveTreeToFirstQuadrant(root) {
    //put back into  quadrant 1 starting at 0,0
    let minY = Infinity;
    let minX = Infinity;
    root.each(d => {
        if (minY > d.y) minY = d.y;
        if (minX > d.x) minX = d.x;
    });
    root.each(d => {
        d.x -= minX;
        d.y -= minY;
    });
}


//Small utility functions

/**
 * Returns the width of the svg as it will be rendered on screen for a single tree
 * @param {} treeRoot 
 * @returns 
 */
function getDisplayWidth(treeRoot) {
    const width = getWidth(treeRoot) + marginWithinTree; //TODO: Adjust width and offset for label placement
    return width;
}

function getWidth(treeRoot) {
    if (currentTreeVisualization === "Node-link diagram") {
        let maxX = -Infinity;
        let minX = Infinity;
        treeRoot.each(d => {
            if (d.x > maxX) maxX = d.x;
            if (d.x < minX) minX = d.x;
        });
        if (maxX - minX === -Infinity) {

        }
        return maxX - minX;
    } else {
        return 40 * nodeBaseSize;
    }
}


/**
 * Returns the height of the svg as it will be rendered on screen for a single tree
 * @param {} treeRoot 
 * @returns 
 */
function getDisplayHeight(treeRoot) {
    return getHeight(treeRoot) + marginWithinTree + fontSizeRepAmount;
}

function getHeight(treeRoot) {
    if (currentTreeVisualization === "Node-link diagram") {
        let maxY = -Infinity;
        let minY = Infinity;
        treeRoot.each(d => {
            if (d.y > maxY) maxY = d.y;
            if (d.y < minY) minY = d.y;
        });
        if (maxY - minY === -Infinity) {

        }
        return maxY - minY;
    } else {
        return 40 * nodeBaseSize;
    }
}