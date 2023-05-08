//Simple line chart including data processing for underneath the R_t slider.

let treesPerSize = []; //for each R_t value (represented by the index), holds how many trees there are

function createScentedRtLineChart(chartDiv, scentIndex) {
    treesPerSize = [];

    for (let i = 0; i < maxMaxDistance; i++) {
        treesPerSize[i] = 0;
    }

    for (let treeI = 0; treeI < repTreesData.length; treeI++) {
        //tree exists up to maxDistance
        for (let i = 0; i < repTreesData[treeI].maxDistance; i++) {
            treesPerSize[i] = treesPerSize[i] + 1;
        }
    }
    const width = 150 - 25; //offset by 25 for the length of the slider knob
    const height = 20;

    const lineChartDiv = chartDiv.append("div")
        .attr("id", "RtScentedChart")
        .attr("class", "LineChart")
        .style("margin-left", "12.5px")

    createLineChart(lineChartDiv, width, height, treesPerSize, scentIndex);
}


/**
 *
 * @param {*} chartDiv
 * @param {*} usableWidth
 * @param {*} usableHeight
 * @param  inputData series of values corresponding to y-axis
 * @param  scentIndex dataIndex for what is currently selected
 */
function createLineChart(chartDiv, usableWidth, usableHeight, inputData, scentIndex) {
    const margin = { top: 0, right: 0, bottom: 0, left: 0 };
    const width = usableWidth - margin.left - margin.right;
    const height = usableHeight - margin.top - margin.bottom;

    const svg = chartDiv.append("svg")
        .attr("class", "LineChartSvg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    //setup x and y scale functions
    const x = d3.scaleLinear()
        .range([0, width])
        .domain([0, inputData.length - 1]);

    const y = d3.scaleLinear()
        .range([height, 0])
        .domain([0, d3.max(inputData)]);

    //x,y functions for the database on axis
    const shape = d3.line()
        .x(function (d) {
            return x(d[0]);
        })
        .y(function (d) {
            return y(d[1]);
        });


    //get x,y positions for the SHAPE to fill. If filled=false this is just a line, otherwise we add points to fill underneath
    let data = [];
    let scentData;
    const dataLength = inputData.length;

    for (let i = 0; i < dataLength; i++) {
        data[i] = [i, inputData[i]];
    }
    scentData = data.slice(0, scentIndex);

    completeShape(data);
    completeShape(scentData)


    //append shape
    svg.append("path")
        .datum(data)
        .attr("class", "filledShape")
        .attr("d", shape);

    svg.append("path")
        .datum(scentData)
        .attr("class", "filledShapeBlue")
        .attr("d", shape);

}

/**
 * Completes the shape by drawing a value down from the last value, then through the origin, and finally back to the start. Modifies the array
 * @param  data array of [x,y] values
 */
function completeShape(data) {
    let dataLength = data.length;
    data[dataLength] = [dataLength - 1, 0]; //go to 0 on the y-axis
    data[dataLength + 1] = [0, 0]; //go to 0,0
    data[dataLength + 2] = [0, data[0][1]]; //close the shape
}
