import { Chart, registerables } from 'chart.js';

/**
 * Reading defaults out of the CSS custom properties means the charts stay on
 * palette automatically, including if a dark theme is added later. Called once
 * from main.ts, before bootstrap.
 */
export function applyChartDefaults(): void {
  Chart.register(...registerables);

  const css = getComputedStyle(document.documentElement);
  const token = (name: string, fallback: string) =>
    css.getPropertyValue(name).trim() || fallback;

  Chart.defaults.font.family = "'IBM Plex Sans', system-ui, sans-serif";
  Chart.defaults.font.size = 11;
  Chart.defaults.color = token('--ink-3', '#8496a6');
  Chart.defaults.borderColor = token('--line-soft', '#eaeff4');
  Chart.defaults.maintainAspectRatio = false;
  Chart.defaults.animation = { duration: 180 };

  // No legend by default: most charts here have a single series, and direct
  // labels beat a legend whenever there is room for them.
  Chart.defaults.plugins.legend.display = false;
  Chart.defaults.plugins.tooltip.padding = 8;
  Chart.defaults.plugins.tooltip.displayColors = false;
  Chart.defaults.plugins.tooltip.backgroundColor = token('--ink', '#14202e');
  Chart.defaults.plugins.tooltip.cornerRadius = 4;
}

/** Resolve a `var(--x)` token to a literal colour, which canvas needs. */
export function resolveColor(value: string): string {
  const match = /^var\((--[\w-]+)\)$/.exec(value.trim());
  if (!match) return value;
  return getComputedStyle(document.documentElement).getPropertyValue(match[1]).trim();
}
