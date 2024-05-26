/**
 * Determines the status of the given measurement.
 *
 * Packages with a relational cohesion lower than 1.5 are categorized as having too few internal dependencies.
 *
 * Packages with a relation higher than 4 are categorized as having too many internal dependencies.
 *
 * Packages in between those regions are considered to be well-designed by this metric.
 *
 * @param measurement The measurement.
 * @returns {string}
 */
function determineStatus(measurement) {
    if (measurement["Relational cohesion"] < 1.5) {
        return "Low Cohesion";
    } else if (measurement["Relational cohesion"] > 4) {
        return "High Cohesion";
    } else {
        return "Ok";
    }
}