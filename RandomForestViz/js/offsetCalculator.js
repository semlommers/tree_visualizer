/**
 * Returns an array of [x,y] offsets to lay out the rectangles without overlap.
 *
 * @param widths array of heights
 * @param heights array of heights
 * @param horMargins The horizontal margin after each node. Used when filtering out nodes
 * @param maxWidth the maximum width we can use to layout
 */
function calculateOffsets(widths, heights, horMargins, maxWidth) {
    return snakeLayout(widths, heights, horMargins, maxWidth);
}


/**
 * Returns an array of [x,y] offsets to lay out the rectangles without overlap in strips.
 * @param inputWidths Array of widths
 * @param inputHeights array of heights
 * @param horMargins The horizontal margin after each node. Used when filtering out nodes
 * @param maxWidth the maximum width we can use to layout

 */
function snakeLayout(inputWidths, inputHeights, horMargins, maxWidth) {
    //use snake pattern to remove jarring transitions from how the eye goes from one place to the other

    //add the margins to the widths and the heights so we now how much space each element takes
    const widths = addMargin(inputWidths, horMargins);
    const heights = addMarginConstant(inputHeights, verticalMarginBetweenTrees);


    //outputArray
    let outputPositions = [];

    //current offset
    let stripYOffset = 0;
    let currentXOffset = 0;

    let stripDirection = "Right";//whether we are currently laying the strip out towards the right or the left
    let stripStartIndex = 0;//holds the index of the element where the current strip starts
    let stripMaxHeight = 0;//maximum height found of an element in the strip
    let stripWidth = 0;//current width


    //go through the nodes
    for (let i = 0; i < widths.length; i++) {
        let width = widths[i];

        //update strip properties
        stripWidth += width;
        stripMaxHeight = Math.max(stripMaxHeight, heights[i]);



        //whether this is the last element
        let lastElement = (i + 1) === widths.length;

        //If this is the last element or the next element does not fit we need to lay this strip out
        if (lastElement || (stripWidth + widths[i + 1]) > maxWidth) {
            //layout the nodes in the strip            
            for (let j = stripStartIndex; j <= i; j++) {
                let extraXOffset = 0;
                if (stripDirection === "Left") {
                    extraXOffset = -widths[j];//move it to the left by the width of this tree so that the element fits (coordinates at left bottom)
                    extraXOffset -= (maxWidth - stripWidth);//ensure it is always flush against the left side
                }
                outputPositions[j] = [currentXOffset + extraXOffset, stripYOffset];

                //increase or decrease depending on direction
                if (stripDirection === "Right") {
                    currentXOffset += widths[j];
                } else {
                    currentXOffset -= widths[j];
                }
            }

            //initialize new strip
            stripYOffset += stripMaxHeight;//shift strip down
            stripWidth = 0;
            stripMaxHeight = 0;
            stripStartIndex = i + 1;//start the new strip at the next element
            if (stripDirection === "Right" && false) { // We do not use the snake layout anymore since the order of
                                                        // the trees matter
                stripDirection = "Left";
                currentXOffset = maxWidth;
            } else {
                stripDirection = "Right";
                currentXOffset = 0;
            }
        }
    }
    return outputPositions;
}
/**
 * Add the margins to the values for easier calculation. Does not modify the original values
 * @param {*} inputValues 
 * @param horMargins The horizontal margin after each node. Used when filtering out nodes

 * @returns 
 */
function addMargin(inputValues, horMargins) {
    let outputValues = [];
    for (let i = 0; i < inputValues.length; i++) {
        outputValues[i] = inputValues[i] + horMargins[i];
    }
    return outputValues;

}


/**
 * Add the margins to the values for easier calculation. Does not modify the original values
 * @param {*} inputValues 
 * @param margin The constant added margin

 * @returns 
 */
function addMarginConstant(inputValues, margin) {
    let outputValues = [];
    for (let i = 0; i < inputValues.length; i++) {
        outputValues[i] = inputValues[i] + margin;
    }
    return outputValues;

}
