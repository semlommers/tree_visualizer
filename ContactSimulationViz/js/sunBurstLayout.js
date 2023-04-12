function sunBurstLayout(treeSvg, root, width, height, isRepTree) {
    let partitionLayout = d3.partition();
    partitionLayout
        .size([2 * Math.PI, height - (marginWithinTree / 2 + fontSizeRepAmount)])
        .padding(1);

    let arcGenerator = d3.arc()
        .startAngle(function(d) { return d.x0; })
        .endAngle(function(d) { return d.x1; })
        .innerRadius(function(d) { return d.y0; })
        .outerRadius(function(d) { return d.y1; });

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
    root.count();
    partitionLayout(root);

    const g = treeSvg.append("g")
        .attr("transform", `translate(${height/2},${height/2 - (marginWithinTree / 2 + fontSizeRepAmount)})`); //make sure no clipping occurs

    const node = g.append("g") //nodes
        .selectAll('path')
        .data(root.descendants())
        .join('path')
        .attr('d', arcGenerator)
        .attr("class", "node")
        // .selectAll("g")
        // .data(root.descendants())
        // .join("g")
        // .attr('x', function(d) { return d.x0; })
        // .attr('y', function(d) { return d.y0; })
        // .attr('width', function(d) { return d.x1 - d.x0; })
        // .attr('height', function(d) { return d.y1 - d.y0; })
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

function makeStackedChartSunburstVertical(gElement, nodeId, isRepTree, isLeftChart) {
    let startY = gElement.attr("y");
    let rectHeight = gElement.attr("height");

    for (let partI = 0; partI < maxParts; partI++) {
        constructRectSunburstVertical(gElement, nodeId, isRepTree, isLeftChart, partI, startY, rectHeight);
    }
}

function constructRectSunburstVertical(gElement, nodeId, isRepTree, isLeftChart, partIndex, startY, rectHeight) {
    const baseX = parseFloat(gElement.attr("x"))
    const baseWidth = gElement.attr("width")

    const color = getPartColor(partIndex, isLeftChart);
    const [x, width] = getRectGlyphYPositionsSunburstPlotVertical(nodeId, partIndex, isRepTree, isLeftChart, baseWidth);

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

function getRectGlyphYPositionsSunburstPlotVertical(id, partIndex, isRepTree, isLeftChart, baseSize) {

    const partRange = getPartPercentages(id, partIndex, isRepTree, isLeftChart);

    const x1 = partRange[0] * baseSize;
    const x2 = partRange[1] * baseSize;
    const rectWidth = x2 - x1;

    return [x1, rectWidth];
}