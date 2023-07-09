const barChartHeight = 100;

function createComponentBarChart(divToAddTo) {

    let colors = [];
    for (let i = 0; i < maxParts; i++) {
        colors[i] = getPartColor(i);
    }

    //get the distribution
    let values = new Array(maxParts).fill(0);
    metaDataFromNodeById.forEach((metaData, id) => {
        //only count it this node is at the right depth
        const nodeDepth = metaData.depth;

        if (currentDistributionSelection.includes("All") || currentDistributionSelection.includes(nodeDepth)) {
            const partCounts = getPartCounts(id, false);
            for (let i = 0; i < maxParts; i++) {
                values[i] += partCounts[i];
            }
        }
    })


    const barchartDiv = divToAddTo.append("div").attr("class", "barChartsContainer")

    const barChartG = barchartDiv.append("svg")
        .attr("class", "barChartSvg")
        .attr("height", barChartHeight)
        .append("g")

    createBarChart(barChartG, barChartHeight, values, colors)

}



function createBarChart(gElement, totalHeight, dataValues, colors) {
    const parts = dataValues.length;
    let sum = dataValues.reduce((accumulator, currentVal) => accumulator + currentVal)

    let currentY = 0;
    for (let partI = 0; partI < parts; partI++) {
        const height = dataValues[partI] / sum * totalHeight;
        const color = colors[partI];

        if (height > 0) { //only add rectangles that have a height
            gElement.append("rect")
                .attr("x", 0)
                .attr("y", currentY)
                .attr("height", height)
                .attr("fill", color)
                .attr("class", "barChartRectangle")

            currentY += height;
        }
    }
}