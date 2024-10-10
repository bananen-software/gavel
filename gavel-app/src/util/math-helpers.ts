/**
 * Calculates the percentage based on the given values.
 * @param dividend The dividend.
 * @param divisor The divisor.
 * @returns {number} The percentage value.
 */
export function toPercent(dividend: number, divisor: number): number {
  return Math.round((dividend / divisor) * 100);
}

/**
 * Converts a numeric value to its percentage string representation.
 *
 * @param {number} value - The numeric value to be converted.
 * @return {string} The percentage string representation of the input value, rounded to the nearest whole number followed by a percent sign.
 */
export function toPercentString(value: number): string {
  return value + "%"
}

export function calculatePercentString(dividend: number, divisor: number): string {
  return toPercentString(toPercent(dividend, divisor));
}
