function resetExplorationPanel() {
    const div = d3.select("#tab12Content");
    div.selectAll("*").remove();

    div.text("Please select a representative tree to explore their represented trees.");
}

function createRepresentedTreeGrid(repTreeId) {
    const div = d3.select("#tab12Content");
    div.selectAll("*").remove();
    div.text(""); // Remove the text

    const treeGridDiv = div.append("div")
        .attr("id", "ExplorationGridDiv")
        .attr("class", "treeGridDiv");
    treeGridDiv.append("p").text("The represented trees of the current selected representative tree:")
        .style("overflow", "hidden")
        .style("height", "18px")
    const svg = treeGridDiv.append("svg")
        .attr("id", "explorationGrid")
        .attr("class", "treeGrid");

    let targetContainerWidth = treeGridDiv.node().clientWidth;

    const treesRepresented = getTreeHierarchiesRepresented(repTreeId);

    //use function from offsetCalculator to calculate the offsets
    let offSets = getOffSets(treesRepresented, targetContainerWidth,false);

    for (let i = 0; i < treesRepresented.length; i++) {
        const xOffset = offSets[i][0];
        const yOffset = offSets[i][1];
        const treeRoot = treesRepresented[i];
        const idI = treeRoot.data.id;

        //use function from treeLayout to layout a single tree
        createSingleTree(svg, xOffset, yOffset, treeRoot, idI, false);
    }

    //size treeGridSVG according to it's bounding box
    resizeSVG(svg);
}

function updateExplorationPanelAnimated() {
    if (focusedTree == null) {
        return;
    }

    const treeGridDiv = d3.select("#ExplorationGridDiv");
    const svg = d3.select("#explorationGrid");

    let targetContainerWidth = treeGridDiv.node().clientWidth;

    const treesRepresented = getTreeHierarchiesRepresented(focusedTree);

    //use function from offsetCalculator to calculate the offsets
    let offSets = getOffSets(treesRepresented, targetContainerWidth,false);

    const xOffsetMap = new Map();
    const yOffsetMap = new Map();


    for (let i = 0; i < treesRepresented.length; i++) {
        const id = treesRepresented[i].data.id;
        xOffsetMap.set(id, offSets[i][0]);
        yOffsetMap.set(id, offSets[i][1]);
    }

    svg
        .interrupt()
        .transition()
        .duration(1000)
        .selectAll("svg")
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
        .end()
        .then(() => {
            resizeSVG(svg);
        })
        .catch((error) => {
            console.error(error);
            //ignore the error. Can come from it being interrupted in which
            //case there is no need to resize
        })
}