function createMainPanel() {
    let mainPanel = d3.select("#mainPanel");

    let sidePanel = d3.select("#sidePanel");


    // TODO: clean this at the end
    // let firstTabs = sidePanel.insert("ul", ":first-child").attr("class", "nav nav-pills").style("justify-content", "center");
    // let firstTab1 = firstTabs.append("li").attr("class", "active");
    // firstTab1.append("a").attr("data-toggle", "pill").attr("href", "#tab1Content").text("Overview");
    //
    // let firstTab2 = firstTabs.append("li");
    // firstTab2.append("a").attr("data-toggle", "pill").attr("href", "#tab2Content").text("Detail");
    //
    // let tabContentDiv = mainPanel.append("div").attr('class', "tab-content");
    //
    // let tab1Content = tabContentDiv.append("div")
    //     .attr("id", "tab1Content")
    //     .attr("class", "tab-pane fade in active")
    //     .style("height", "100%")
    //
    // let tab2Content = tabContentDiv.append("div")
    //     .attr("id", "tab2Content")
    //     .attr("class", "tab-pane fade")
    //     .style("height", "100%");

    createOverviewContent(mainPanel);

    // createDetailContent(tab2Content);


    window.addEventListener('mousedown', function(e) {
        // If mousedown event is fired from .handler, toggle flag to true
        if (e.target.classList.contains("handler")) {
            isHandlerDragging = true;
            if (e.target.id === "tab1Handler") {
                draggingTabContent = "#mainPanel"
            } else if (e.target.id === "tab2Handler") {
                draggingTabContent = "#tab2Content"
            }
        }
    });

    document.addEventListener('mousemove', function(e) {
        // Don't do anything if dragging flag is false
        if (!isHandlerDragging) {
            return false;
        }

        let tabContent = d3.select(draggingTabContent)

        // Get offset
        const containerOffsetLeft = tabContent.node().offsetLeft;

        // Get x-coordinate of pointer relative to container
        const pointerRelativeXPos = e.clientX - containerOffsetLeft;

        // Arbitrary minimum width set on box A, otherwise its inner content will collapse to width of 0
        const boxAminWidth = 60;

        // Resize box A
        // * 8px is the left/right spacing between .handler and its inner pseudo-element
        // * Set flex-grow to 0 to prevent it from growing

        let firstPanel = tabContent.select("#firstPanel")
        firstPanel.node().style.width = (Math.max(boxAminWidth, pointerRelativeXPos - 8)) + 'px';
        firstPanel.node().style.flexGrow = "0";
    });

    document.addEventListener('mouseup', function() {
        // Turn off dragging flag when user mouse is up
        if (isHandlerDragging && draggingTabContent === "#mainPanel") {
            updatePositions();
            updateExplorationPanelAnimated();
        }
        isHandlerDragging = false;
        draggingTabContent = null;
    });

    generateTreeGrid();
    createManifoldPlot();
    resetExplorationPanel();

}

function createOverviewContent(tab1Content) {
    let tabContent = tab1Content.append("div").style("display", "flex").style("height", "100%").style("width", "100%");

    let firstPanel = tabContent.append("div").attr("class", "box").attr("id", "firstPanel").style("width", "400px");

    const div = firstPanel.append("div")
        .attr("id", "treeGridDiv")
        .attr("class", "treeGridDiv");
    div.append("svg")
        .attr("id", "treeGrid")
        .attr("class", "treeGrid")

    tabContent.append("div").attr("class", "handler").attr("id", "tab1Handler");

    let secondPanel = tabContent.append("div").attr("class", "box").attr("id", "secondPanel").style("width", "1px");
    let secondTabs = secondPanel.append("ul").attr("class", "nav nav-pills");
    let secondTab1 = secondTabs.append("li").attr("class", "active");
    secondTab1.append("a").attr("data-toggle", "pill").attr("href", "#tab11Content").text("t-SNE");

    let secondTab2 = secondTabs.append("li");
    let explorationButton = secondTab2.append("a").attr("data-toggle", "pill").attr("href", "#tab12Content")
        .text("Exploration");

    $('a[href="#tab11Content"]').on('hidden.bs.tab', function (e) {
        updateExplorationPanelAnimated();
    })

    let contentHeight = secondPanel.node().clientHeight - secondTabs.node().clientHeight;

    let contentDiv = secondPanel.append("div").attr('class', "tab-content").style("height", contentHeight + "px"); // Make room for the navigation

    let tab11Content = contentDiv.append("div")
        .attr("id", "tab11Content")
        .attr("class", "tab-pane fade in active");

    let tab12Content = contentDiv.append("div")
        .attr("id", "tab12Content")
        .attr("class", "tab-pane fade")
        .style("height", "100%");
}

function createDetailContent(tab2Content) {
    let tabContent = tab2Content.append("div").style("display", "flex").style("height", "100%");

    let firstPanel = tabContent.append("div").attr("class", "box").attr("id", "firstPanel").text("main Panel");

    tabContent.append("div").attr("class", "handler").attr("id", "tab2Handler");

    let secondPanel = tabContent.append("div").attr("class", "box").attr("id", "secondPanel");
    let secondTabs = secondPanel.append("ul").attr("class", "nav nav-pills");
    let secondTab1 = secondTabs.append("li").attr("class", "active");
    secondTab1.append("a").attr("data-toggle", "pill").attr("href", "#tab21Content").text("test21");

    let secondTab2 = secondTabs.append("li");
    secondTab2.append("a").attr("data-toggle", "pill").attr("href", "#tab22Content").text("test22");

    let contentDiv = secondPanel.append("div").attr('class', "tab-content");

    let tab21Content = contentDiv.append("div")
        .attr("id", "tab21Content")
        .attr("class", "tab-pane fade in active").text("test21");

    let tab22Content = contentDiv.append("div")
        .attr("id", "tab22Content")
        .attr("class", "tab-pane fade").text("test22");
}