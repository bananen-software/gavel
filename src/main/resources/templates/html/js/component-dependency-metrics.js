const green = {
    upperBound: 0.3,
    lowerBound: 0,
    color: "var(--pico-color-green-450)",
    name: "Green"
};

const yellow = {
    upperBound: 0.6,
    lowerBound: 0.3,
    color: "var(--pico-color-yellow-450)",
    name: "Yellow"
};

const red = {
    upperBound: 1,
    lowerBound: 0.6,
    color: "var(--pico-color-red-450)",
    name: "Red"
};

const zones = [red, yellow, green];

/**
 * Determines the zone for the given measurement.
 * @param measurement The measurement.
 * @returns {{color: string, upperBound: number, name: string, lowerBound: number}}
 */
function determineZone(measurement) {
    let match = green;

    for (const zone of zones) {
        if (measurement.normalizedDistanceFromMainSequence <= zone.upperBound) {
            match = zone;
        }
    }

    return match;
}

/**
 * Determines the color for the measurement.
 * @returns {function(*): string}
 */
function determineColor() {
    return function (d) {
        return determineZone(d).color;
    };
}

/**
 * Determines the status for the given measurement.
 * @param d The measurement.
 * @returns {string} The status.
 */
function determineStatus(d) {
    return determineZone(d).name;
}

/**
 * Renders the component dependency metrics graph.
 * @param chart The chart.
 * @param table The table.
 * @returns {(function(*): void)|*}
 */
function renderComponentDependencyMetricsGraph(chart, table) {
    return function (data) {
        let cumulatedMetrics = {}

        for (const d of data) {
            let status = determineStatus(d);

            cumulatedMetrics[status.toLowerCase()] = cumulatedMetrics[status.toLowerCase()] ? cumulatedMetrics[status.toLowerCase()] + 1 : 1;
            cumulatedMetrics["sum"] = cumulatedMetrics["sum"] ? cumulatedMetrics["sum"] + 1 : 1;

            table.appendChild(createRow([
                d.packageName,
                determineStatus(d),
                d.efferentCoupling,
                d.afferentCoupling,
                d.instability,
                d.abstractness,
                d.normalizedDistanceFromMainSequence
            ]));
        }

        const sum = cumulatedMetrics["sum"];

        document.getElementById("green-components").innerText =
            toPercent(cumulatedMetrics["green"], sum) + "% healthy components";
        document.getElementById("yellow-components").innerText =
            toPercent(cumulatedMetrics["yellow"], sum) + "% okayish components";
        document.getElementById("red-components").innerText =
            toPercent(cumulatedMetrics["red"], sum) + "% problematic components";

        const margin = {top: 10, right: 30, bottom: 40, left: 60},
            width = 800 - margin.left - margin.right,
            height = 800 - margin.top - margin.bottom;

        const svg = chart
            .append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform",
                "translate(" + margin.left + "," + margin.top + ")");

        const x = d3.scaleLinear().domain([0, 1]).range([0, width])

        svg.append("text")
            .attr("text-anchor", "end")
            .attr("x", width)
            .attr("y", height + margin.top + 25)
            .text("Abstractness");

        svg.append("g")
            .attr("transform", "translate(0," + height + ")")
            .attr("class", "axis")
            .call(d3.axisBottom(x));

        const y = d3.scaleLinear().domain([0, 1]).range([height, 0]);

        svg.append("text")
            .attr("text-anchor", "end")
            .attr("transform", "rotate(-90)")
            .attr("y", -margin.left + 20)
            .attr("x", -margin.top)
            .text("Instability")

        svg.append("g")
            .attr("class", "axis")
            .call(d3.axisLeft(y));

        svg.append('g')
            .selectAll("dot")
            .data(data)
            .enter()
            .append("circle")
            .attr("cx", function (d) {
                return x(d.instability);
            })
            .attr("cy", function (d) {
                return y(d.abstractness);
            })
            .attr("r", 7)
            .style("opacity", 0.3)
            .style("fill", determineColor())
            .append("svg:title").text((d, i) =>
            `${d.packageName}\nInstability: ${d.instability}\nAbstractness: ${d.abstractness}\nDistance: ${d.normalizedDistanceFromMainSequence}\nCa: ${d.afferentCoupling}\nCe: ${d.efferentCouplin}`)
    };
}