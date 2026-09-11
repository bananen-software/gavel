/** Formatting helpers. Raw float artefacts in a metrics tool destroy trust faster
 *  than any design flaw, so every number reaching the screen goes through here. */

export const num = (v: number | null | undefined): number => v ?? 0;

export const count = (v: number | null | undefined): string => num(v).toLocaleString();

export const decimal = (v: number | null | undefined, digits = 2): string =>
  num(v).toFixed(digits);

export const percent = (v: number | null | undefined, digits = 0): string =>
  `${(num(v) * 100).toFixed(digits)}%`;

export function sum<T>(items: readonly T[], pick: (item: T) => number | null | undefined): number {
  return items.reduce((acc, item) => acc + num(pick(item)), 0);
}

export const ratio = (numerator: number, denominator: number): number =>
  denominator === 0 ? 0 : numerator / denominator;

/** The API returns timestamps as String, so parse defensively and never crash on it. */
export function parseDate(value: string | null | undefined): Date | null {
  if (!value) return null;
  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? null : parsed;
}

export function formatDateTime(value: string | null | undefined): string {
  const date = parseDate(value);
  if (!date) return 'unknown';
  return date.toLocaleString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export function formatDate(value: string | null | undefined): string {
  const date = parseDate(value);
  if (!date) return 'unknown';
  return date.toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
}

/** Java package names share long prefixes; the tail is what distinguishes them. */
export function shortPackageName(name: string | null | undefined): string {
  if (!name) return 'unnamed';
  const parts = name.split('.');
  return parts.length <= 2 ? name : parts.slice(-2).join('.');
}

export function simpleClassName(name: string | null | undefined): string {
  if (!name) return 'unnamed';
  const parts = name.split('.');
  return parts[parts.length - 1];
}
