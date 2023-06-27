let featureOrder = null;

function resetFeaturePerDepth() {
    const div = d3.select("#tab13ContentDiv");
    div.selectAll("*").remove();

    div.text("Please select a representative tree to see the feature used per depth.");
    featureOrder = null;
}

function createFeaturePerDepthPlot(repTreeId, secondaryTree) {
    const div = d3.select("#tab13ContentDiv");
    if (!secondaryTree) {
        div.selectAll("*").remove();
        div.text(""); // Remove the text
    }

    let tabHeight = d3.select("#secondPanelTabContent").node().clientHeight;

    let margin = {top: 10, right: 160, bottom: 50, left: 50};
    let width = 500 - margin.left - margin.right;
    let height = tabHeight - margin.top - margin.bottom;

    let svg = div.append("svg").attr("id", "featurePerDepthPlot")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .style("border", "solid")
        .style("margin-right", "10px");

    // append a g element to our svg and give it a new origin inside svg
    const g = svg.append('g')
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    let data = collectTheDataForFeaturePerDepthPlot(repTreeId);

    let maxSum = 0;
    for (let i = 0; i < data.length; i++) {
        if (maxSum < data[i].sum) {
            maxSum = data[i].sum;
        }
    }

    let keys = Object.keys(data[0]).slice(2);
    let maxKeys = keys.length;
    for (let i = 1; i < data.length; i++) {
        let nextKeys = Object.keys(data[i]).slice(2);
        if (maxKeys < nextKeys.length) {
            keys = nextKeys;
            maxKeys = nextKeys.length;
        }
    }

    let stack = d3.stack().keys(keys)(data)

    let x = d3.scaleBand()
        .domain(d3.range(0, data.length))
        .range([0, height])
        .padding([0.2]);
    g.append("g")
        .call(d3.axisLeft(x));

    let y = d3.scaleLinear()
        .domain([0, 1])
        .range([ 0, width ]);
    g.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(y).tickSizeOuter(0));

    let color = d3.scaleOrdinal()
        .domain(Array(keys.length).keys())
        .range(featurePerDepthColors)

    let minHeightPercentage = 0.2;

    g.append("g")
        .selectAll("g")
        .data(stack)
        .enter().append("g")
        .attr("fill", function(d) { return color(namesData["feature_names"].indexOf(d.key)); })
        .attr("feature", function (d) { return d.key })
        .selectAll("rect")
        // enter a second time = loop subgroup per subgroup to add all rectangles
        .data(function(d) { return d; })
        .enter().append("rect")
        .attr("y", function(d) {
            let height = x.bandwidth() * minHeightPercentage +
                (1 - minHeightPercentage) * x.bandwidth() * d.data.sum / maxSum;
            return x(d.data.depth) + (x.bandwidth() * 0.5 - height * 0.5);
        })
        .attr("x", function(d) {
            return y(d[0]);
        })
        .attr("width", function(d) {
            if (isNaN(d[1])) {
                return 0;
            } else {
                return y(d[1]) - y(d[0]);
            }
        })
        .attr("height", function(d) {
            return x.bandwidth() * minHeightPercentage + (1 - minHeightPercentage) * x.bandwidth() * d.data.sum / maxSum;
        })
        .append("title")
        .text(function () {
            return this.parentNode.parentNode.getAttribute("feature")
        })

    g.append("text")
        .attr("transform", `translate(${-margin.left},${height/2})rotate(45)`)
        .text("depth")

    // Add one dot in the legend for each name.
    g.selectAll("dots")
        .data(keys)
        .enter()
        .append("circle")
        .attr("cx", width + 20)
        .attr("cy", function(d,i){ return 20 + i*25}) // 100 is where the first dot appears. 25 is the distance between dots
        .attr("r", 7)
        .style("fill", function(d){ return color(namesData["feature_names"].indexOf(d))})

// Add one dot in the legend for each name.
    g.selectAll("labels")
        .data(keys)
        .enter()
        .append("text")
        .attr("x", width + 40)
        .attr("y", function(d,i){ return 20 + i*25}) // 100 is where the first dot appears. 25 is the distance between dots
        .style("fill", function(d){ return color(namesData["feature_names"].indexOf(d))})
        .text(function(d){ return d})
        .attr("text-anchor", "left")
        .style("alignment-baseline", "middle")
}



function collectTheDataForFeaturePerDepthPlot(treeId) {
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
        let depth = node["depth"];
        let featureId = node["featureId"];
        if (featureId == null) {
            continue;
        }
        while (dataArray.length < depth + 1) {
            dataArray.push([]);
        }
        while (dataArray[depth].length < featureId + 1) {
            dataArray[depth].push(0);
        }
        dataArray[depth][featureId]++;
    }

    let dataMap = [];

    const sumArray = (array) => {
        const newArray = [];
        array.forEach(sub => {
            sub.forEach((num, index) => {
                if(newArray[index]){
                    newArray[index] += num;
                }else{
                    newArray[index] = num;
                }
            });
        });
        return newArray;
    }

    let sortedIndices;
    if (featureOrder == null) {
        let columnSum = sumArray(dataArray);

        sortedIndices = sortWithIndices(columnSum);

        featureOrder = sortedIndices;
    } else {
        let columnSum = sumArray(dataArray);

        sortedIndices = sortWithIndices(columnSum);
    }


    for (let i = 0; i < dataArray.length; i++) {
        let dataInstance = dataArray[i];
        let sum = dataInstance.reduce((partialSum, a) => partialSum + a, 0);
        let instance = {};
        instance["depth"] = i;
        instance["sum"] = sum;
        for (let j = 0; j < sortedIndices.length; j++) {
            let dataIndex = sortedIndices[j];
            instance[namesData["feature_names"][dataIndex.toString()]] = dataInstance[dataIndex] / sum;
        }
        dataMap.push(instance);
    }

    return dataMap;
}

function sortWithIndices(toSort) {
    for (let i = 0; i < toSort.length; i++) {
        toSort[i] = [toSort[i], i];
    }
    toSort.sort(function(left, right) {
        return right[0] - left[0];
    });
    toSort.sortIndices = [];
    for (let j = 0; j < toSort.length; j++) {
        toSort.sortIndices.push(toSort[j][1]);
        toSort[j] = toSort[j][0];
    }
    return toSort.sortIndices;
}