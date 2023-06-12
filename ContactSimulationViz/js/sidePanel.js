function createSidePanel() {
    createSelectors();
    createColorLegends();
}


function createSelectors() {
    const selectorDiv = d3.select("#sidePanel").append("div")
        .attr("id", "SelectorDiv")
        .attr("class", "SidePanelPanelDiv");

    createDistanceSlider(selectorDiv);
    createAccuracyWidgetDiv(selectorDiv);
    createDistanceMetricSelector(selectorDiv);
    createVisualizationTypeSelector(selectorDiv);
    createSizeSlider(selectorDiv);
    createNodeColorSelectors(selectorDiv);
    createRecalculateButton(selectorDiv);
}


function createDistanceSlider(selectorDiv) {

    createSlider(selectorDiv, "DistanceSlider", "Max tree distance", 0, maxMaxDistance, Math.round(maxMaxDistance / 2))
    currentDistance = Math.round(maxMaxDistance / 2);

    d3.select("#DistanceSlider")
        .on("input", function() {
            currentDistance = parseInt(this.value); //keep the value up to date
            updateSliderPreview() //Show a preview
            d3.select("#DistanceSliderNumber").text(this.value);
            changePending();
        })

    //create it at the end of the sliderDiv so the slider aligns with the scented widget
    createScentedRtLineChart(selectorDiv.select("#DistanceSliderdiv"), (maxMaxDistance / 2));
}

function createAccuracyWidgetDiv(selectorDiv) {
    let parentDiv = selectorDiv.append("div").attr("id", "AccuraciesWidgetDivParent");
    createAccuracyWidget(parentDiv, currentDistance);
}

function createAccuracyWidget(parentDiv, distance) {
    let accuraciesDiv = parentDiv.append("div").attr("id", "AccuraciesWidgetDiv");
    let accuracies = currentDistanceMetricMetaData["accuracyByDistance"][distance];
    accuraciesDiv.append("div")
        .text("Total accuracy: " + Math.round(accuracies[0] * 100) / 100)
        .style("color", accuraciesColors[0]);
    for (let i = 1; i < accuracies.length; i++) {
        accuraciesDiv.append("div")
            .text("Accuracy class " + i + ": " + Math.round(accuracies[i] * 100) / 100)
            .style("color", accuraciesColors[i]);
    }
    createAccuracyLineCharts(accuraciesDiv, distance, currentDistanceMetricMetaData["accuracyByDistance"])

    accuraciesDiv.append("div").text("Accuracies to original model");
    let accuraciesToOriginal = currentDistanceMetricMetaData["accuracyToOriginalModelByDistance"][distance];
    accuraciesDiv.append("div")
        .text("Total accuracy: " + Math.round(accuraciesToOriginal[0] * 100) / 100)
        .style("color", accuraciesColors[0]);
    for (let i = 1; i < accuraciesToOriginal.length; i++) {
        accuraciesDiv.append("div")
            .text("Accuracy class " + i + ": " + Math.round(accuraciesToOriginal[i] * 100) / 100)
            .style("color", accuraciesColors[i]);
    }
    createAccuracyLineCharts(accuraciesDiv, distance, currentDistanceMetricMetaData["accuracyToOriginalModelByDistance"])
}

function createDistanceMetricSelector(selectorDiv) {

    let containerDiv = selectorDiv.append("div")
        .style("display", "grid")
        .style("justify-content", "center");

    containerDiv.append("p")
        .attr("class", "text title")
        .text("Distance metric")

    let distanceMetrics = []
    for (let i = 0; i < distanceMetricMetaData.length; i++) {
        distanceMetrics.push({"NAME": distanceMetricMetaData[i].name})
    }

    const changeFunction = function() {
        currentDistanceMetric = this.value;
        loadDifferentDistanceMetric = true;
        changePending();
    };

    createComboBox(containerDiv, "distanceMetricSelector", distanceMetrics, currentDistanceMetric, changeFunction)
}

function createVisualizationTypeSelector(selectorDiv) {

    let containerDiv = selectorDiv.append("div")
        .style("display", "grid")
        .style("justify-content", "center");

    containerDiv.append("p")
        .attr("class", "text title")
        .text("Visualization type")

    let visualizationTypes = [
        // {"NAME": "Node-link diagram"},
        {"NAME": "Icicle plot"},
        {"NAME": "Sunburst plot"}
    ]

    const changeFunction = function() {
        currentTreeVisualization = this.value;
        recalculate = true;
        changePending();
    };

    createComboBox(containerDiv, "visualizationTypeSelector", visualizationTypes, currentTreeVisualization, changeFunction)
}

function createSizeSlider(selectorDiv) {

    createSlider(selectorDiv, "SizeSlider", "Node Size", 1, 10, nodeBaseSize)

    d3.select("#SizeSlider")
        .on("input", function() {
            setVizSizes(parseInt(this.value)); //update all the sizes that are dependent on node size


            d3.select("#SizeSliderNumber").text(this.value);
            recalculate = true;
            changePending();
        })
}


function createNodeColorSelectors(selectorDiv) {

    let containerDiv = selectorDiv.append("div")
        .style("display", "grid")
        .style("justify-content", "center");

    containerDiv.append("p")
        .attr("class", "text title")
        .text("Color encoding")

    // createLeftRightSubtitles(selectorDiv);

    //get the properties of the selectors
    const colorOptions = [
        { "NAME": "DT Structure"},
        { "NAME": "DT Comparison"},
        { "NAME": "Class Proportions"},
        { "NAME": "Correct Classification"}
    ];

    const leftChangeFunction = function() {
        currentColor = this.value; //keep the color up to date
        changePending();
    };

    createComboBox(containerDiv, "leftNodeColorSelector", colorOptions, currentColor, leftChangeFunction);
}
function createRecalculateButton(selectorDiv) {

    const recalculateDiv = selectorDiv.append("div")
        .attr("class", "recalculateDiv")

    const text = "Press to recalculate";

    const recalculateFunction = function() {
        updateAll();
    };

    createButton(recalculateDiv, "recalculateButton", text, recalculateFunction)
}




function createColorLegends() {
    d3.select("#sidePanel")
        .append("div")
        .attr("id", "colorLegendDiv")
        .attr("class", "colorLegend SidePanelPanelDiv")

    updateColorLegend();
}

function updateColorLegend() {

    const colorLegendDiv = d3.select("#sidePanel").select("#colorLegendDiv");
    colorLegendDiv.selectAll("*").remove(); //remove current legend
    createStateColorLegend(colorLegendDiv);
}

function createStateColorLegend(colorLegendDiv) {
    const halfColorDivLeft = colorLegendDiv.append("div")
        .attr("class", "halfColorLegendDiv")

    const halfColorDivRight = colorLegendDiv.append("div")
        .attr("class", "halfColorLegendDiv")

    let halfColorDivs = [halfColorDivLeft, halfColorDivRight];


    let startI = 0;

    //get the colors and names to display
    let colors, names
    if (currentColor === "Class Proportions") {
        colors = classProportionsColorScheme;
        names = classProportionsColorSchemeOrderDisplay;
    } else if (currentColor === "DT Structure") {
        colors = decisionTreeStructureColorScheme;
        names = decisionTreeStructureSchemeOrderDisplay;
    } else if (currentColor === "DT Comparison") {
        colors = decisionTreeColorScheme;
        names = decisionTreeSchemeOrderDisplay;
    } else if (currentColor === "Correct Classification") {
        colors = correctClassifiedColorScheme;
        names = correctClassifiedSchemeOrderDisplay;
    }



    let isLeft = true;
    for (let i = startI; i < names.length; i++) {
        let halfColorDiv = halfColorDivs[i % 2]
        const color = colors[i];
        let name = names[i];
        createStateColorLegendItem(color, name, isLeft, halfColorDiv);
        isLeft = !isLeft;
    }

}

function createStateColorLegendItem(color, name, isLeft, divToAddTo) {
    const item = divToAddTo.insert("div")
        .attr("class", "colorLegendItem");



    item.insert("div")
        .attr("class", "colorLegendDot")
        .style("background-color", color);

    const p = item.insert("p")
        .text(name)
        .attr("class", "text colorLegendText")
        .style("text-align", "left")

    if (!isLeft) { //inverse direction of items if this is the right column
        item.style("flex-direction", "row-reverse");
        p.style("text-align", "right")
    }

}

function createDistributionChartPanel() {
    const distributionDiv = d3.select("#sidePanel").append("div")
        .attr("id", "distributionChartPanel")
        .attr("class", "distributionChartPanel SidePanelPanelDiv");


    distributionDiv.append("p").attr("class", "title text").text("Distribution of properties")

    createDistributionChartSelectors(distributionDiv);

    // createDistributionChart(distributionDiv);
    // createDistributionLegend(distributionDiv);

    createComponentBarChart(distributionDiv);
}

function createDistributionChartSelectors(divToAddTo) {

    const comboOptions = [
        { "NAME": "All" },
    ]

    const maxDepth = getMaxDepth();

    for (let i = 0; i < maxDepth; i++) {
        comboOptions.push({ "NAME": "Level " + i });
    }

    const selectLeftLevelFunction = function() {
        currentDistributionSelection = [];
        for (let option of this.selectedOptions) {
            if (option.value === "All") {
                currentDistributionSelection.push("All")
            } else {
                //take only the number. Represent as int for ease of manipulation later
                currentDistributionSelection.push(parseInt(option.value.split(" ")[1]))
            }
        }
        updateGlobalChart();
    };

    const selectRightLevelFunction = function() {
        currentRightDistributionSelection = [];
        for (let option of this.selectedOptions) {
            if (option.value === "All") {
                currentRightDistributionSelection.push("All")
            } else {
                //take only the number. Represent as int for ease of manipulation later
                currentRightDistributionSelection.push(parseInt(option.value.split(" ")[1]))
            }
        }
        updateGlobalChart();
    };


    const normalizeCheckBoxFunction = function() {
        normalizeComponentChart = this.checked;
        updateGlobalChart();
    };
    const comboBoxDiv = divToAddTo.append("div").attr("class", "comboBoxesDiv")

    createComboBox(comboBoxDiv, "levelComboBox", comboOptions, "Tree size", selectLeftLevelFunction, false);

    createCheckBox(comboBoxDiv, "normalizeCheckbox", false, normalizeCheckBoxFunction, "Normalized")

    createComboBox(comboBoxDiv, "levelComboBox", comboOptions, "Tree size", selectRightLevelFunction, false);

}
function createComboBox(divToAppendTo, id, valueList, initVal, changeFunction, multiple) {

    //attach the combobox
    const dropDown = divToAppendTo.append("select")
        .attr("class", "form-control")
        .attr("id", id);

    if (multiple) {
        dropDown.attr("multiple", "multiple")
    }

    const options = dropDown.selectAll("option")
        .data(valueList)
        .enter()
        .append("option")

    options
        .text(function(d) {
            return d.NAME;
        })
        .attr("value", function(d) {
            return d.NAME;
        })
        .property("selected", function(d) { return d.NAME === initVal; }); //set default value

    //attach the change function
    dropDown
        .on("change", changeFunction);

}

function createCheckBox(divToAppendTo, id, initVal, changeFunction, labelName) {

    if (labelName !== undefined) {
        divToAppendTo = divToAppendTo.append("label").text(labelName);
    }

    // const checkboxDiv = divToAppendTo
    //     .insert("div") //insert a div for the checkbox
    //     .attr("class", "checkdiv")

    //attach the checkbox itself
    divToAppendTo.append("input")
        .attr("class", "sidePanelCheckBox")
        .attr("id", id)
        .attr("type", "checkbox")
        .property("checked", initVal)
        .on("change", changeFunction);
}


function createButton(divToAppendTo, id, text, clickFunction) {

    const buttonDiv = divToAppendTo
        .insert("div") //insert comboDiv before svg
        .attr("class", "buttonDiv")

    //attach the checkbox itself
    buttonDiv.append("button")
        .attr("class", "btn btn-primary")
        .attr("id", id)
        .text(text)
        .on("click", clickFunction);
}


function createSlider(divToAppendTo, id, text, minVal, maxVal, initVal) {

    const sliderDiv = divToAppendTo
        .insert("div") //insert sliderDiv before svg
        .attr("id", id + "div")
        .attr("class", "sliderdiv")

    //text above slider
    sliderDiv.append("p")
        .attr("class", "text title")
        .text(text)

    //slider itself
    const slideContainer = sliderDiv.append("div")
        .attr("class", "slidecontainer")

    slideContainer.append("input")
        .attr("type", "range")
        .attr("class", "range")
        .attr("id", id)
        .attr("min", minVal)
        .attr("max", maxVal)
        .attr("value", initVal)

    //attach the number behind the slider
    slideContainer.append("div")
        .attr("class", "slidernumber")
        .attr("id", id + "Number")
        .text(initVal)
}