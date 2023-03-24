//Exposed should only ever a root node. Recovered and dead should not happen
const infectionColorSchemeOrder = ["removedByPolicyOrigin", "removedByPolicy", "initial", "asymptomatic", "presymptomatic", "symptomatic", "severely_symptomatic"]
const infectionColorScheme = ["#005a32", "#74c476", "#AAAAAA", "#ebd594", "#bcbddc", "#fcae91", "#fb6a4a", "#cb181d"];
const infectionColorSchemeOrderDisplay = ["Contact avoided due to isolation", "Infection route prevented earlier", "Initial node", "Asymptomatic", "Presymptomatic", "Symptomatic", "Severely symptomatic"]

const noneColorSchemeOrder = ["removedByPolicyOrigin", "removedByPolicy", "initial", "other"]
const noneColorScheme = ["#005a32", "#74c476", "#AAAAAA", "#fee0d2"];
const noneColorSchemeOrderDisplay = ["Contact avoided due to isolation", "Infection route prevented earlier", "Initial node", "Node"]


const locationColorSchemeOrder = ["removedByPolicyOrigin", "removedByPolicy", "initial", "School", "Restaurant", "Office", "Family", "Other"]
const locationColorScheme = ["#005a32", "#74c476", "#AAAAAA", "#8dd3c7", "#bebada", "#fccde5", "#80b1d3", "#fb8072"];
const locationColorSchemeOrderDisplay = ["Contact avoided due to isolation", "Infection route prevented earlier", "initial", "School", "Restaurant", "Office", "Family", "Other"]

const ageColorSchemeOrder = ["removedByPolicyOrigin", "removedByPolicy", "0-20", "20-40", "40-60", "60-80", "80-100"]
const ageColorScheme = ["#005a32", "#74c476", "#dadaeb", "#bcbddc", "#9e9ac8", "#807dba", "#6a51a3", "#4a1486"];
const ageColorSchemeOrderDisplay = ["Contact avoided due to isolation", "Infection route prevented earlier", "0-20", "20-40", "40-60", "60-80", "80-100"]

const infectionTimeColorSchemeOrder = ["removedByPolicyOrigin", "removedByPolicy", "0-5", "5-10", "10-15", "15-20", "20-25", "25-30", "30-50", "50-100"]
const infectionTimeColorScheme = ["#005a32", "#74c476", "#efedf5", "#dadaeb", "#bcbddc", "#9e9ac8", "#807dba", "#6a51a3", "#54278f", "#3f007d"];
const infectionTimeColorSchemeOrderDisplay = ["Contact avoided due to isolation", "Infection route prevented earlier", "0-5", "5-10", "10-15", "15-20", "20-25", "25-30", "30-50", "50-100"]

const familyStates = ["SmallFamily", "LargeTwoAdultFamily", "LargeManyAdultFamily", "SingleParent"]

// Not used, maybe later
const decisionTreeColorSchemeOrder = ["equal", "nonExistingNode", "differentSplitFeaturePrediction", "nodeLeafComparison"]
const decisionTreeColorScheme = ['#4daf4a','#e41a1c','#377eb8','#984ea3'];
const decisionTreeSchemeOrderDisplay = ["Equal nodes", "Non-existing node", "Different split feature / prediction", "Node-leaf comparison"]

const decisionTreeStructureColorSchemeOrder = ["existingNode", "nonExistingNode"]
const decisionTreeStructureColorScheme = ['#4daf4a','#ff0000'];
const decisionTreeStructureSchemeOrderDisplay = ["Node exists", "Node does not exists"]

const classProportionsColorSchemeOrder = ["class1", "class2", "class3", "class4", "class5", "class6", "class7", "class8", "class9", "class10"];
const classProportionsColorScheme = ['#1f78b4','#33a02c','#e31a1c','#ff7f00','#a6cee3','#b2df8a','#fb9a99','#fdbf6f','#cab2d6'];
const classProportionsColorSchemeOrderDisplay = [];

//"DEAD", "RECOVERED", 
const distributionChartColorSchemeOrder = ["EXPOSED", "ASYMPTOMATIC", "PRESYMPTOMATIC", "SYMPTOMATIC", "SEVERELY_SYMPTOMATIC"];
const distributionChartColorScheme = ["#fee5d9", "#bcbddc", "#fcae91", "#fb6a4a", "#cb181d"];
const distributionChartColorSchemeOrderDisplay = ["Exposed", "Asymptomatic", "Presymptomatic", "Symptomatic", "Severely symptomatic"];
const distributionTimeStep = 0.5;