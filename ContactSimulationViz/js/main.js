console.log("ensure all data is used")

const repTreesDataInputBaseLocation = "data/RepTrees/";
const allTreesDataInputLocation = "data/AllTrees.json";
const metaDataInputLocation = "data/NodesAndMeta.json";
const distanceMetricsMetaDataLocation = "data/DistanceMeasures.json"
const namesLocation = "data/names.json"

//Visual variables for the tree visualization
let nodeBaseSize = 4; //radius of the node
let linkBaseSize; //Width of links
let verNodeSpace; //vertical space between nodes
let horNodeSpace; //horizontal space between nodes
let marginWithinTree; //margin between the trees
let horizontalMarginBetweenTrees; //Horizontal space between trees.
let fontSizeRepAmount; //Base font size for the number that tells how much is represented
let squarePlotSize;



function setVizSizes(nodeSize) {
    nodeBaseSize = nodeSize;
    linkBaseSize = nodeSize / 2; //constant link size depending on node size
    verNodeSpace = nodeSize * 2 + 3; //Vertical space between nodes. *2 as this is the diameter of a node.
    horNodeSpace = nodeSize * 2 + 2; // Horizontal space between nodes. *2 as this is the diameter of a node.
    marginWithinTree = nodeSize * 2; //Makes sure the tree doesn't get clipped
    horizontalMarginBetweenTrees = nodeBaseSize * 2;
    fontSizeRepAmount = nodeSize * 2; //Base font size for the number that tells how much is represented
    squarePlotSize = 40 * nodeSize; // Plot size for the square plots like icicle plot and sunburst plot
}


//Space for the layout
const verticalMarginBetweenTrees = 4; //Vertical space between trees.
const hiddenTreesScalingFactor = 0.001; //how much the trees that are represented by other trees are scaled down
const squarePlotPadding = 1;






const initDistanceSliderVal = 15; //start the slider at 0


const popupWidth = 500; //width of the popup when clicking a node to see which trees it represents.

let treeOrder = []; //order of the trees in the viz
let treeBaseWidthById = new Map(); //Base width of the tree by id of the root. Uses nodes of {@code nodeBaseSize} size
let treeBaseHeightById = new Map(); //Base  height of the tree by id of the root. Uses nodes of {@code nodeBaseSize} size

let repTreeById = new Map(); //holds the representative tree data by id of the root
let repNodeById = new Map(); //holds all node data represented by the specified node
let allTreeById = new Map(); //holds the allTree data by id of the root
let metaDataFromNodeById = new Map(); //holds the metadata for each node by id of the node.
let nodesRepresentedBy;


let currentDistance = initDistanceSliderVal; //Current distance
let maxMaxDistance = 0;


let currentColor = "DT Structure"; //What we are currently coloring the nodes by for the left sides of the glyphs
let currentTreeVisualization = "Icicle plot";

let currentDistributionSelection = "All"; //which levels of the distribution we are currently showing
let currentRightDistributionSelection = "All"; //which levels of the distribution we are currently showing

let currentDistanceMetric;
let currentDistanceMetricMetaData;
let loadDifferentDistanceMetric = false;

let focusedTree = null;
let secondaryFocusedTree = null;

let recalculate = false; //Holds whether we need to recalculate the tree grid. Can happen in case of node size change or data change

const maxParts = 10; //How many different parts we can have at maximum in the glyph.

let repTreesData, allTreesData, metaData, distanceMetricMetaData, namesData;
let d3;

let isHandlerDragging = false;
let draggingTabContent;


//Load in all the javascript files
requirejs(["js/d3/d3.js", "js/ColorSchemes.js", "js/BarChart.js", "js/LineChart.js", "js/dataQueries.js", "js/stateCounters.js", "js/nodeViz.js", "js/sidePanel.js", "js/treeLayout.js", "js/representativeGraph.js", "js/popup.js", "js/updateFunctions.js", "js/offsetCalculator.js", "js/iciclePlotLayout.js", "js/nodeLinkDiagramLayout.js", "js/sunBurstLayout.js", "js/mainPanel.js", "js/manifoldPlot.js"], function(d3Var) {
    //load in all the data
    d3 = d3Var;
    d3.json(distanceMetricsMetaDataLocation).then(function(distanceMetricMetaDataInput) {
        d3.json(allTreesDataInputLocation).then(function(allTreesDataInput) {
            d3.json(metaDataInputLocation).then(function(metaDataInput) {
                d3.json(namesLocation).then(function (namesDataInput) {
                    distanceMetricMetaData = distanceMetricMetaDataInput;
                    currentDistanceMetric = distanceMetricMetaData[0].name;
                    currentDistanceMetricMetaData = distanceMetricMetaData[0];
                    d3.json(repTreesDataInputBaseLocation + currentDistanceMetric + ".json").then(function(repTreesDataInput) {
                        repTreesData = repTreesDataInput;
                        allTreesData = allTreesDataInput;
                        metaData = metaDataInput;
                        namesData = namesDataInput;
                        setVizSizes(nodeBaseSize);
                        mainRepresentativeGraph();
                        updateAll(); //update to use slider values
                    });
                });
            });
        });
    });
});


function mainRepresentativeGraph() {
    preprocessData();


    createSidePanel()


    createMainPanel();


    window.addEventListener('resize', function() { updatePositions() }, true);

}