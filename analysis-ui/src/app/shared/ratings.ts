import type {
  AnalysisStatus,
  ClassComplexityRating,
  PackageComplexityRating,
  RelationalCohesionRating,
  Severity,
  Size,
  Stratum,
} from '../api/schema.types';

/**
 * The schema has five different enums that all end up on screen next to each
 * other. Rather than give each its own palette, everything is projected onto
 * one five-step concern scale. A user learns the colours once.
 */
export type Concern = 'none' | 'low' | 'medium' | 'high' | 'very-high';

const CONCERN_COLOR: Record<Concern, string> = {
  none: 'var(--rate-none)',
  low: 'var(--rate-low)',
  medium: 'var(--rate-medium)',
  high: 'var(--rate-high)',
  'very-high': 'var(--rate-very-high)',
};

const CONCERN_BG: Record<Concern, string> = {
  none: 'var(--rate-none-bg)',
  low: 'var(--rate-low-bg)',
  medium: 'var(--rate-medium-bg)',
  high: 'var(--rate-high-bg)',
  'very-high': 'var(--rate-very-high-bg)',
};

export const concernColor = (c: Concern): string => CONCERN_COLOR[c];
export const concernBackground = (c: Concern): string => CONCERN_BG[c];

export function classComplexityConcern(rating: ClassComplexityRating | null): Concern {
  switch (rating) {
    case 'LOW':
      return 'low';
    case 'MEDIUM':
      return 'medium';
    case 'HIGH':
      return 'high';
    case 'VERY_HIGH':
      return 'very-high';
    default:
      return 'none';
  }
}

export function packageComplexityConcern(rating: PackageComplexityRating | null): Concern {
  switch (rating) {
    case 'MOSTLY_SIMPLE':
      return 'low';
    case 'BALANCED':
      return 'medium';
    case 'COMPLEX':
      return 'high';
    case 'HIGHLY_COMPLEX':
      return 'very-high';
    default:
      return 'none';
  }
}

export function severityConcern(severity: Severity | null): Concern {
  switch (severity) {
    case 'LOW':
      return 'low';
    case 'MEDIUM':
      return 'medium';
    case 'HIGH':
      return 'very-high';
    default:
      return 'none';
  }
}

/**
 * Cohesion is the one metric where the scale is not monotonic. A package with
 * too few internal relationships is under-cohesive and one with too many is
 * over-coupled; GOOD sits in the middle and is the target. Colouring HIGH as
 * "best" would actively mislead, so it gets the caution tone.
 */
export function cohesionConcern(rating: RelationalCohesionRating | null): Concern {
  switch (rating) {
    case 'GOOD':
      return 'low';
    case 'LOW':
      return 'high';
    case 'HIGH':
      return 'medium';
    default:
      return 'none';
  }
}

const STRATUM_COLOR: Record<Stratum, string> = {
  SURFACE: 'var(--stratum-surface)',
  INTERMEDIATE: 'var(--stratum-intermediate)',
  DEEP: 'var(--stratum-deep)',
  SEDIMENT: 'var(--stratum-sediment)',
  NONE: 'var(--rate-none)',
};

/** Stratum is architectural depth, not a verdict, so it gets its own single hue. */
export const stratumColor = (s: Stratum | null): string =>
  s ? STRATUM_COLOR[s] : STRATUM_COLOR.NONE;

export const STRATUM_ORDER: Stratum[] = ['SURFACE', 'INTERMEDIATE', 'DEEP', 'SEDIMENT', 'NONE'];

const SIZE_ORDER: Record<Size, number> = {
  UNKNOWN: -1,
  EMPTY: 0,
  SMALL: 1,
  MEDIUM: 2,
  LARGE: 3,
  VERY_LARGE: 4,
};
export const sizeOrder = (s: Size | null): number => (s ? SIZE_ORDER[s] : -1);

/** ENUM_CASE is a wire format, not something to put in front of a person. */
export function humanise(value: string | null | undefined, fallback = 'Unknown'): string {
  if (!value) return fallback;
  const lower = value.replace(/_/g, ' ').toLowerCase();
  return lower.charAt(0).toUpperCase() + lower.slice(1);
}

export function analysisStatusConcern(status: AnalysisStatus | null): Concern {
  switch (status) {
    case 'COMPLETED':
      return 'low';
    case 'RUNNING':
    case 'PENDING':
      return 'medium';
    case 'FAILED':
      return 'very-high';
    default:
      return 'none';
  }
}

export const isAnalysisInProgress = (status: AnalysisStatus | null): boolean =>
  status === 'PENDING' || status === 'RUNNING';
