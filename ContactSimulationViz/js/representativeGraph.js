function generateTreeGrid() {
    //get the svg grid where the trees will be added to.
    //using svg instead of flexbox for animations purposes.
    const treeGridSVG = d3.select("#treeGrid");
    const div = d3.select("#treeGridDiv");


    let targetContainerWidth = div.node().clientWidth;

    const treeRoots = getTreeRoots(repTreesData);
    setBaseWidthAndHeightById(treeRoots); //used later when scaling nodes


    const offSets = getOffSets(treeRoots, targetContainerWidth, true);

    for (let i = 0; i < treeRoots.length; i++) {
        const xOffset = offSets[i][0];
        const yOffset = offSets[i][1];
        const treeRoot = treeRoots[i];
        const id = treeRoot.data.id;

        const treeSvg = createSingleTree(treeGridSVG, xOffset, yOffset, treeRoot, id, true);
        treeSvg.on("mouseenter", function (event) {
            let id = parseInt(event.target.id.substring(3));
            highlightTreesRepresentedBy(id);
        });
        treeSvg.on("mouseleave", function (event) {
            resetHighlight();
        });

        treeSvg.on("click", function(event) {
            if (focusedTree === null) {
                focusedTree = treeRoot.data.id;
                createRepresentedTreeGrid(focusedTree);
                createFeaturePerDepthPlot(focusedTree);
            } else {
                if (secondaryFocusedTree === null) {
                    if (focusedTree === treeRoot.data.id) {
                        focusedTree = null;
                        updateSecondaryPanel();
                    } else {
                        secondaryFocusedTree = treeRoot.data.id;
                    }
                } else {
                    focusedTree = null;
                    secondaryFocusedTree = null;
                    updateSecondaryPanel();
                }
            }
            updatePositions(true);
        })
    }


    //size treeGridSVG according to it's bounding box
    resizeSVG(treeGridSVG);
}


function getScaleFactorByRepAmount(repAmount) {
    if (currentTreeVisualization === "Node-link diagram") {
        return 1 + Math.log10(repAmount);
    } else {
        return 1
    }
}

/**
 * Sets the width and height of the svg element to fit the content
 * @param svg The d3 svg element we are resizing
 */
function resizeSVG(svg) {
    //size svg according to it's bounding box
    const bbox = svg.node().getBBox();
    svg.attr("width", bbox.width);
    svg.attr("height", bbox.height);
}


function setBaseWidthAndHeightById(treeRoots) {
    treeRoots.sort((a, b) => {
        return b.data.maxDistance - a.data.maxDistance;
    })

    for (let i = 0; i < treeRoots.length; i++) {
        const treeRoot = treeRoots[i];
        const width = getDisplayWidth(treeRoot);
        const height = getDisplayHeight(treeRoot);

        treeBaseWidthById.set(treeRoot.data.id, width);
        treeBaseHeightById.set(treeRoot.data.id, height);
        treeOrder[i] = treeRoot.data.id;
    }
}




/**
 * True if d.maxDistance > distance
 * @param {*} d 
 * @param {*} distance
 * @returns 
 */
function contains(d, distance) {
    return (d.maxDistance > distance)
}