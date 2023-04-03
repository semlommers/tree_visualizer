function iciclePlotLayout(treeSvg, root, width, height, isRepTree) {
    let partitionLayout = d3.partition();
    partitionLayout
        .size([width, height - (marginWithinTree / 2 + fontSizeRepAmount)])
        .padding(1);
    root.sum(function(d) {
        if (d.children.length === 0) {
            let classProportions = metaDataFromNodeById.get(d.id).classProportions;
            let count = 0;

            for (let i = 0; i < classProportions.length; i++) {
                count = count + classProportions[i];
            }

            return count;
        }
    });
    partitionLayout(root);

    const g = treeSvg.append("g")
        .attr("transform", `translate(${0},${marginWithinTree / 2 + fontSizeRepAmount})`); //make sure no clipping occurs

    const node = g.append("g") //nodes
        .attr("class", "node")
        .selectAll("g")
        .data(root.descendants())
        .join("g")
        .attr('x', function(d) { return d.x0; })
        .attr('y', function(d) { return d.y0; })
        .attr('width', function(d) { return d.x1 - d.x0; })
        .attr('height', function(d) { return d.y1 - d.y0; })
        .attr("id", function(d) {
            return d.data.id
        })

    //glyphs for each node
    node.each(function(d) {
        makeNodeGlyph(d3.select(this), d.data.id, isRepTree)
    })


    //add how many trees this node represents if the data is present
    if (isRepTree && typeof root.data.representations !== 'undefined') {
        const repNumber = getAmountOfTreesRepresented(root, currentDistance);

        const textG = treeSvg.append("g").attr("class", "textG")
        const text = textG.append("text")
            .attr("class", "textRepAmount")
            .attr("font-size", fontSizeRepAmount)
            .text(repNumber)


        //position text such that the top is 2 pixels below the root
        const textX = (root.x0 + root.x1) / 2 - text.node().getBBox().width / 2;
        const textY = root.y + fontSizeRepAmount * 0.9;

        text.attr("transform", `translate(${textX},${textY})`); //make sure no clipping occurs
    }

    return treeSvg;
}


// TODO: Remove this if you decide not to use a horizontal chart
function makeStackedChartIciclePlot(gElement, nodeId, isRepTree, isLeftChart) {
    let startX = gElement.attr("x");
    let rectWidth = gElement.attr("width");

    for (let partI = 0; partI < maxParts; partI++) {
        constructRectIciclePlot(gElement, nodeId, isRepTree, isLeftChart, partI, startX, rectWidth);
    }
}

function constructRectIciclePlot(gElement, nodeId, isRepTree, isLeftChart, partIndex, startX, rectWidth) {
    const baseY = parseFloat(gElement.attr("y"))
    const baseHeight = gElement.attr("height")

    const color = getPartColor(partIndex, isLeftChart);
    const [y, height] = getRectGlyphYPositionsIciclePlot(nodeId, partIndex, isRepTree, isLeftChart, baseHeight);

    if (height > 0) { //only add rectangles that have a height
        gElement.append("rect")
            .attr("x", startX)
            .attr("y", baseY + y)
            .attr("width", rectWidth)
            .attr("height", height)
            .attr("fill", color)
            .attr("class", "glyphRectangle")
    }
}

function makeStackedChartIciclePlotVertical(gElement, nodeId, isRepTree, isLeftChart) {
    let startY = gElement.attr("y");
    let rectHeight = gElement.attr("height");

    for (let partI = 0; partI < maxParts; partI++) {
        constructRectIciclePlotVertical(gElement, nodeId, isRepTree, isLeftChart, partI, startY, rectHeight);
    }
}

function constructRectIciclePlotVertical(gElement, nodeId, isRepTree, isLeftChart, partIndex, startY, rectHeight) {
    const baseX = parseFloat(gElement.attr("x"))
    const baseWidth = gElement.attr("width")

    const color = getPartColor(partIndex, isLeftChart);
    const [x, width] = getRectGlyphYPositionsIciclePlotVertical(nodeId, partIndex, isRepTree, isLeftChart, baseWidth);

    if (width > 0) { //only add rectangles that have a height
        gElement.append("rect")
            .attr("y", startY)
            .attr("x", baseX + x)
            .attr("width", width)
            .attr("height", rectHeight)
            .attr("fill", color)
            .attr("class", "glyphRectangle")
    }
}

function getRectGlyphYPositionsIciclePlotVertical(id, partIndex, isRepTree, isLeftChart, baseSize) {

    const partRange = getPartPercentages(id, partIndex, isRepTree, isLeftChart);

    const x1 = partRange[0] * baseSize;
    const x2 = partRange[1] * baseSize;
    const rectWidth = x2 - x1;

    return [x1, rectWidth];
}



function getRectGlyphYPositionsIciclePlot(id, partIndex, isRepTree, isLeftChart, baseSize) {

    const partRange = getPartPercentages(id, partIndex, isRepTree, isLeftChart);

    const y1 = partRange[0] * baseSize;
    const y2 = partRange[1] * baseSize;
    const rectHeight = y2 - y1;

    return [y1, rectHeight];
}