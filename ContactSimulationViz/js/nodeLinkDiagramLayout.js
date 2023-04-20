function nodeLinkDiagramLayout(treeSvg, root, width, height, isRepTree) {
    const g = treeSvg.append("g")
        .attr("transform", `translate(${marginWithinTree / 2},${marginWithinTree / 2 + fontSizeRepAmount})`); //make sure no clipping occurs

    const link = g.append("g") //links
        .attr("class", "edge")
        .selectAll("path")
        .data(root.links())
        .join("path")
        .attr("d", d3.linkVertical()
            .x(d => d.x)
            .y(d => d.y))
        .attr("stroke-width", linkBaseSize);

    const node = g.append("g") //nodes
        .attr("class", "node")
        .selectAll("g")
        .data(root.descendants())
        .join("g")
        .attr("transform", d => `translate(${d.x},${d.y})`)
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
        const textX = root.x + nodeBaseSize - text.node().getBBox().width / 2;
        const textY = root.y + fontSizeRepAmount * 0.9;

        text.attr("transform", `translate(${textX},${textY})`); //make sure no clipping occurs
    }



    return treeSvg;
}


function makeStackedChart(gElement, nodeId, isRepTree, isLeftChart) {
    let [startX, rectWidth] = getRectGlyphXPositions()

    for (let partI = 0; partI < maxParts; partI++) {
        constructRect(gElement, nodeId, isRepTree, isLeftChart, partI, startX, rectWidth);
    }
}


function constructRect(gElement, nodeId, isRepTree, isLeftChart, partIndex, startX, rectWidth) {

    const color = getPartColor(partIndex);
    const [y, height] = getRectGlyphYPositions(nodeId, partIndex, isRepTree, isLeftChart);

    if (height > 0) { //only add rectangles that have a height
        gElement.append("rect")
            .attr("x", startX)
            .attr("y", y)
            .attr("width", rectWidth)
            .attr("height", height)
            .attr("fill", color)
            .attr("class", "glyphRectangle")
    }
}

function getRectGlyphYPositions(id, partIndex, isRepTree, isLeftChart) {

    const partRange = getPartPercentages(id, partIndex, isRepTree, isLeftChart);
    const rectSize = nodeBaseSize * 2; //nodeBaseSize is radius

    const y1 = partRange[0] * rectSize - rectSize / 2;
    const y2 = partRange[1] * rectSize - rectSize / 2;
    const rectHeight = y2 - y1;

    return [y1, rectHeight];
}