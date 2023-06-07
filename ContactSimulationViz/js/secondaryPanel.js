function createSecondaryPanel() {
    const div = d3.select("#tab11Content");

    const w = 400;
    const h = 256;

    let svg = div.append("svg").attr("id", "manifoldPlot").attr("width", w).attr("height", h).style("border", "solid");

    // Margin object with four properties
    const margin = {top: 20, right: 20, bottom: 20, left: 30}

    //create innerWidth and innerHeight for our margin
    const innerW = w - margin.right - margin.left;
    const innerH = h- margin.top - margin.bottom;

    // append a g element to our svg and give it a new origin inside svg
    const g = svg.append('g')
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    let coordinates = currentDistanceMetricMetaData["treeIdToManifoldCoordinates"];
    let treeIds = [];
    for (let value in coordinates) {
        treeIds.push(value);
    }

    const xValue = d => coordinates[d][0];
    const yValue = d => coordinates[d][1];

    const xScale = d3.scaleLinear();
    const yScale = d3.scaleLinear();

    xScale
        .domain(d3.extent(treeIds, xValue))
        .range([0, innerW]);

    yScale
        .domain(d3.extent(treeIds, yValue))
        .range([innerH, 0]);

    const circles = g
        .selectAll('circle')
        .data(treeIds)
        .enter()
        .append('circle');

    circles.attr('cx', d=>xScale(xValue(d)))
        .attr('cy', d=>yScale(yValue(d)))
        .attr('r', 5)
        .attr("opacity", 0.7)
        .attr("id", d=>d)

}

function highlightTreesRepresentedBy(repTreeId) {
    let treeToHighlight
    if (focusedTree == null) {
        treeToHighlight = repTreeId;
    } else {
        treeToHighlight = focusedTree;
    }
    let representedTreeIds = getTreeIdsRepresentedById(treeToHighlight, currentDistance)
    let circles = d3.select("#manifoldPlot").selectAll("circle").attr("fill", function (d) {
        if (representedTreeIds.includes(parseInt(d))) {
            return "red";
        } else {
            return "black";
        }
    });
}

function resetHighlight() {
    if (focusedTree == null) {
        let circles = d3.select("#manifoldPlot").selectAll("circle").attr("fill", function (d) {
            return "black";
        });
    }
}