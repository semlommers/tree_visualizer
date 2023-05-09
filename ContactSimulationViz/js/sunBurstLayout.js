function sunBurstLayout(treeSvg, root, width, height, isRepTree) {
    let radius = width/2;

    let partitionLayout = d3.partition();
    partitionLayout
        .size([2 * Math.PI, radius]);

    root.sum(function(d) {
        if (d.children.length === 0) {
            let classProportions = metaDataFromNodeById.get(d.id)["classProportions"];
            let count = 0;

            for (let i = 0; i < classProportions.length; i++) {
                count = count + classProportions[i];
            }

            return count;
        }
    });
    partitionLayout(root);

    const g = treeSvg.append("g")
        .attr("transform", `translate(${radius},${radius - (marginWithinTree / 2 + fontSizeRepAmount)})`); //make sure no clipping occurs

    const node = g.append("g") //nodes
        .attr("class", "node")
        .selectAll("g")
        .data(function(d) {
            let nodes = d.descendants();
            let visualizedNodes = []
            for (let i = 0; i < nodes.length; i++) {
                let node = nodes[i];
                if ((node.x1 - node.x0) > 0.05) { // Remove if node is too small to visualize
                    visualizedNodes.push(node);
                }
            }
            return visualizedNodes;
        })
        .join("g")
        .attr('x0', function(d) { return d.x0; })
        .attr('y0', function(d) { return d.y0; })
        .attr('x1', function(d) { return d.x1; })
        .attr('y1', function(d) { return d.y1; })
        .attr("id", function(d) {
            return d.data.id
        })

    //glyphs for each node
    node.each(function(d) {
        makeNodeGlyph(d3.select(this), d.data.id, isRepTree)
    })


    //add how many trees this node represents if the data is present
    if (isRepTree && typeof root.data["representations"] !== 'undefined') {
        const repNumber = getAmountOfTreesRepresented(root, currentDistance);

        const textG = treeSvg.append("g").attr("class", "textG")
        const text = textG.append("text")
            .attr("class", "textRepAmount")
            .attr("font-size", fontSizeRepAmount)
            .text(repNumber)


        //position text such that the top is 2 pixels below the root
        const textX = width/2 - fontSizeRepAmount/2;
        text.attr("transform", `translate(${textX},${fontSizeRepAmount})`); //make sure no clipping occurs
    }

    treeSvg.on("click", function() {
        if (focusedTree === null) {
            focusedTree = root.data.id;
        } else {
            focusedTree = null;
        }
        updatePositions(true);
    })

    return treeSvg;
}

function makeStackedChartSunburstVertical(gElement, nodeId, isRepTree) {
    let startY = gElement.attr("y0");
    let arcHeight = gElement.attr("y1");

    for (let partI = 0; partI < maxParts; partI++) {
        constructRectSunburstVertical(gElement, nodeId, isRepTree, partI, startY, arcHeight);
    }
}

function constructRectSunburstVertical(gElement, nodeId, isRepTree, partIndex, startY, arcHeight) {

    const startAngle = parseFloat(gElement.attr("x0"))
    const endAngle = gElement.attr("x1")
    const arcSize = endAngle - startAngle;

    const color = getPartColor(partIndex);
    const [x, width] = getRectGlyphYPositionsSunburstPlotVertical(nodeId, partIndex, isRepTree, arcSize);

    if (width > 0) { //only add rectangles that have a height
        let radius = squarePlotSize + marginWithinTree;
        let padding = squarePlotPadding;

        let arc = d3.arc()
            .innerRadius(startY)
            .outerRadius(arcHeight - padding)
            .startAngle(startAngle + x)
            .endAngle(startAngle + x + width);

        if ((startAngle + x + width) === parseFloat(endAngle)) { // Add padding if last glyph
            arc.padAngle(Math.min((endAngle - startAngle) / 2, 2 * padding / radius))
                .padRadius(radius)
        }


        gElement.append("path")
            .attr("d", arc)
            .attr("fill", color)
            .attr("class", "glyphRectangle")
    }
}

function getRectGlyphYPositionsSunburstPlotVertical(id, partIndex, isRepTree, baseSize) {

    const partRange = getPartPercentages(id, partIndex, isRepTree);

    const x1 = partRange[0] * baseSize;
    const x2 = partRange[1] * baseSize;
    const rectWidth = x2 - x1;

    return [x1, rectWidth];
}
