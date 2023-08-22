function resetFeatureThresholdsPlot() {
    const div = d3.select("#tab14ContentDiv");
    div.selectAll("*").remove();

    div.text("Please select a representative tree to see the feature thresholds used.");
}

function createFeatureThresholdsPlot(treeId, secondaryTree) {
    const div = d3.select("#tab14ContentDiv");
    if (!secondaryTree) {
        div.selectAll("*").remove();
        div.text(""); // Remove the text
    }

    let outline;
    if (secondaryTree) {
        outline = "1px solid #00d0ff";
    } else {
        outline = "1px solid red";
    }

    let div2 = div.append("div")
        .style("display", "grid")
        .style("outline", outline)
        .style("margin", "5px")
        .style("height", "max-content");

    let data = collectTheDataForFeatureThresholdsPlot(treeId)

    let dataLengths = [];
    for (let i = 0; i < data.length; i++) {
        dataLengths.push(data[i].length);
    }

    let sortedIndices;
    if (featureOrder == null) {
        sortedIndices = sortWithIndices(dataLengths);

        featureOrder = sortedIndices;
    } else {
        sortedIndices = featureOrder;
    }

    for (let i = 0; i < sortedIndices.length; i++) {
        let dataPart = data[sortedIndices[i]];
        if (dataPart.length === 0) {
            continue;
        }

        let featureName = namesData["feature_names"][sortedIndices[i]];

        drawPlot(div2, dataPart, featureName);
    }



}

function drawPlot(divToAppendTo, data, featureName) {
    // set the dimensions and margins of the graph
    let margin = {top: 10, right: 30, bottom: 50, left: 60},
        width = 400 - margin.left - margin.right,
        height = 150 - margin.top - margin.bottom;

    // append the svg object to the body of the page
    let svg = divToAppendTo
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    // Add X axis --> it is a date format
    let x = d3.scaleLinear()
        .domain([d3.min(data, function(d) { return +d[0]; }), d3.max(data, function(d) { return +d[0]; })])
        .range([ 0, width ]);
    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x));

    // Add Y axis
    let y = d3.scaleLinear()
        .domain([0, d3.max(data, function(d) { return +d[1]; })])
        .range([ height, 0 ]);
    svg.append("g")
        .call(d3.axisLeft(y).ticks(5));

    // Add the line
    svg.append("path")
        .datum(data)
        .attr("fill", "none")
        .attr("stroke", "steelblue")
        .attr("stroke-width", 1.5)
        .attr("d", d3.line()
            .x(function(d) { return x(d[0]) })
            .y(function(d) { return y(d[1]) })
        );

    svg.append("text")
        .attr("transform", `translate(${width/2 - 25},${height + margin.top + 25})`)
        .text(featureName)
}

function collectTheDataForFeatureThresholdsPlot(treeId) {
    let allNodes = [];
    let allTrees = getTreesRepresentedById(treeId, currentDistance);
    for (let i = 0; i < allTrees.length; i++) {
        let newNodes = getNodes(allTrees[i]);
        for (let j = 0; j < newNodes.length; j++) {
            let metaData = metaDataFromNodeById.get(newNodes[j].id);
            allNodes.push(metaData);
        }
    }

    let dataArray = []

    for (let i = 0; i < allNodes.length; i++) {
        let node = allNodes[i];
        let featureId = node["featureId"];
        let threshold = node["threshold"];
        if (featureId == null) {
            continue;
        }
        while (dataArray.length < featureId + 1) {
            dataArray.push([]);
        }

        dataArray[featureId].push(threshold);
    }

    let data = [];

    for (let i = 0; i < dataArray.length; i++) {
        let resultArray = [];
        let thresholds = dataArray[i];
        if (thresholds.length === 0) {
            data.push([]);
            continue;
        }
        thresholds.sort((a, b) => {
            return a - b;
        });

        let std = standardDeviation(thresholds);
        let mean = arrayMean(thresholds);

        // Apply 3 sigma rule
        let minThreshold = mean - 3*std;
        let maxThreshold = mean + 3*std;

        let thresholdsWith3SigmaRule = []
        for (let j = 0; j < thresholds.length; j++) {
            if (thresholds[j] > minThreshold && thresholds[j] < maxThreshold) {
                thresholdsWith3SigmaRule.push(thresholds[j])
            }
        }

        let counter = thresholdsWith3SigmaRule.length;

        for (let j = 0; j < thresholdsWith3SigmaRule.length; j++) {
            resultArray.push([thresholdsWith3SigmaRule[j], counter]);
            counter--;
            resultArray.push([thresholdsWith3SigmaRule[j], counter]);
        }

        data.push(resultArray);
    }

    return data;
}

function standardDeviation(arr) {

    // Creating the mean with Array.reduce
    let mean = arr.reduce((acc, curr) => {
        return acc + curr
    }, 0) / arr.length;

    // Assigning (value - mean) ^ 2 to
    // every array item
    arr = arr.map((k) => {
        return (k - mean) ** 2
    });

    // Calculating the sum of updated array
    let sum = arr.reduce((acc, curr) => acc + curr, 0);

    // Calculating the variance
    let variance = sum / arr.length

    // Returning the standard deviation
    return Math.sqrt(sum / arr.length)
}

function arrayMean(arr) {
    // Creating the mean with Array.reduce
    let mean = arr.reduce((acc, curr) => {
        return acc + curr
    }, 0) / arr.length;

    return mean
}
