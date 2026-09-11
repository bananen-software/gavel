import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { concernBackground, concernColor, type Concern } from './ratings';

/**
 * A metric. No sparkline, no percentage-change badge: the API returns one
 * snapshot with no history, and implying a trend that does not exist is worse
 * than showing a plain number.
 */
@Component({
  selector: 'app-metric',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="label">{{ label() }}</div>
    <div class="value numeric">
      {{ value() }}
      @if (suffix()) {
        <span class="suffix">{{ suffix() }}</span>
      }
    </div>
    @if (hint()) {
      <div class="hint">{{ hint() }}</div>
    }
  `,
  styles: [
    `
      :host {
        display: block;
        background: var(--surface);
        border: 1px solid var(--line);
        border-radius: var(--radius);
        padding: 12px 14px;
      }
      .label {
        color: var(--ink-2);
        font-size: 12px;
        margin-bottom: 4px;
      }
      .value {
        font-size: 22px;
        font-weight: 600;
        text-align: left;
        line-height: 1.2;
        letter-spacing: -0.02em;
      }
      .suffix {
        font-size: 13px;
        font-weight: 400;
        color: var(--ink-2);
        margin-left: 4px;
      }
      .hint {
        margin-top: 4px;
        font-size: 12px;
        color: var(--ink-3);
      }
    `,
  ],
})
export class MetricComponent {
  readonly label = input.required<string>();
  readonly value = input.required<string>();
  readonly suffix = input<string>('');
  readonly hint = input<string>('');
}

/** A rating pill. Colour is always paired with the label, never used alone. */
@Component({
  selector: 'app-rating',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<span
    class="pill"
    [style.background]="background()"
    [style.color]="color()"
    >{{ label() }}</span
  >`,
  styles: [
    `
      .pill {
        display: inline-block;
        padding: 1px 8px;
        border-radius: 10px;
        font-size: 12px;
        font-weight: 500;
        white-space: nowrap;
      }
    `,
  ],
})
export class RatingComponent {
  readonly label = input.required<string>();
  readonly concern = input.required<Concern>();
  readonly color = computed(() => concernColor(this.concern()));
  readonly background = computed(() => concernBackground(this.concern()));
}

/** A titled surface. One border, no shadow — elevation is for overlays only. */
@Component({
  selector: 'app-panel',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <header>
      <h2>{{ heading() }}</h2>
      @if (note()) {
        <span class="note">{{ note() }}</span>
      }
      <ng-content select="[panel-actions]" />
    </header>
    <div class="body" [class.flush]="flush()">
      <ng-content />
    </div>
  `,
  styles: [
    `
      :host {
        display: block;
        background: var(--surface);
        border: 1px solid var(--line);
        border-radius: var(--radius);
        overflow: hidden;
      }
      header {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 10px 14px;
        border-bottom: 1px solid var(--line);
      }
      .note {
        color: var(--ink-3);
        font-size: 12px;
      }
      header ::ng-deep [panel-actions] {
        margin-left: auto;
      }
      .body {
        padding: 14px;
      }
      .body.flush {
        padding: 0;
      }
    `,
  ],
})
export class PanelComponent {
  readonly heading = input.required<string>();
  readonly note = input<string>('');
  readonly flush = input(false);
}

/** An empty screen is an invitation to act, so it names the next step. */
@Component({
  selector: 'app-empty',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <p class="headline">{{ headline() }}</p>
    @if (detail()) {
      <p class="detail">{{ detail() }}</p>
    }
  `,
  styles: [
    `
      :host {
        display: block;
        padding: 32px 16px;
        text-align: center;
      }
      .headline {
        margin: 0;
        color: var(--ink-2);
      }
      .detail {
        margin: 4px 0 0;
        color: var(--ink-3);
        font-size: 13px;
      }
    `,
  ],
})
export class EmptyComponent {
  readonly headline = input.required<string>();
  readonly detail = input<string>('');
}
