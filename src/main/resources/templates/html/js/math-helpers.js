/**
 * Calculates the percentage based on the given values.
 * @param dividend The dividend.
 * @param divisor The divisor.
 * @returns {number} The percentage value.
 */
function toPercent(dividend, divisor) {
    return Math.round((dividend / divisor) * 100);
}

function toPercentString(value) {
    return Math.round(value * 100) + "%"
}