/**
 * Hand-written mirror of schema.graphqls.
 *
 * Swap this for graphql-codegen output when you want it generated:
 *   npm i -D @graphql-codegen/cli @graphql-codegen/typescript
 *   npx graphql-codegen
 * The shapes below match what codegen would emit, so nothing else changes.
 */

export type AnalysisStatus = 'NOT_RUN' | 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED';
export type Stratum = 'SURFACE' | 'INTERMEDIATE' | 'DEEP' | 'SEDIMENT' | 'NONE';
export type ClassComplexityRating = 'EMPTY' | 'LOW' | 'MEDIUM' | 'HIGH' | 'UNKNOWN' | 'VERY_HIGH';
export type PackageComplexityRating =
  | 'EMPTY'
  | 'MOSTLY_SIMPLE'
  | 'BALANCED'
  | 'COMPLEX'
  | 'HIGHLY_COMPLEX';
export type ClassStatus = 'ACTIVE' | 'DELETED';
export type RelationalCohesionRating = 'LOW' | 'GOOD' | 'HIGH';
export type Severity = 'LOW' | 'MEDIUM' | 'HIGH';
export type Size = 'UNKNOWN' | 'EMPTY' | 'SMALL' | 'MEDIUM' | 'LARGE' | 'VERY_LARGE';

/** Every scalar field in the schema is nullable, so the model says so too. */
type Nullable<T> = T | null;

export interface ProjectRef {
  id: string;
  name: Nullable<string>;
  analysisStatus: Nullable<AnalysisStatus>;
  lastAnalyzed: Nullable<string>;
}

export interface RelationalCohesion {
  rating: Nullable<RelationalCohesionRating>;
  numberOfTypes: Nullable<number>;
  numberOfInternalRelationships: Nullable<number>;
  relationalCohesion: Nullable<number>;
}

export interface ComponentDependency {
  afferentCoupling: Nullable<number>;
  efferentCoupling: Nullable<number>;
  abstractness: Nullable<number>;
  instability: Nullable<number>;
  distance: Nullable<number>;
}

export interface PackageSummary {
  id: string;
  name: Nullable<string>;
  complexity: Nullable<number>;
  complexityRating: Nullable<PackageComplexityRating>;
  complexityOrdinal: Nullable<number>;
  numberOfTypes: Nullable<number>;
  defectDensity: Nullable<number>;
  highDefectDensity: Nullable<number>;
  linesOfCode: Nullable<number>;
  linesOfComments: Nullable<number>;
  commentToCodeRatio: Nullable<number>;
  numberOfVeryHighComplexityTypes: Nullable<number>;
  numberOfHighComplexityTypes: Nullable<number>;
  numberOfMediumComplexityTypes: Nullable<number>;
  numberOfLowComplexityTypes: Nullable<number>;
  numberOfHighPriorityFindings: Nullable<number>;
  totalNumberOfFindings: Nullable<number>;
  size: Nullable<Size>;
  stratum: Nullable<Stratum>;
  stratumOrdinal: Nullable<number>;
  relationalCohesion: Nullable<RelationalCohesion>;
  componentDependency: Nullable<ComponentDependency>;
}

export interface ClassSummary {
  id: string;
  packageId: Nullable<string>;
  name: Nullable<string>;
  programmingLanguage: Nullable<string>;
  lastModified: Nullable<string>;
  numberOfChanges: Nullable<number>;
  numberOfAuthors: Nullable<number>;
  size: Nullable<Size>;
  complexity: Nullable<number>;
  complexityRating: Nullable<ClassComplexityRating>;
  totalLinesOfCode: Nullable<number>;
  totalLinesOfComments: Nullable<number>;
  commentToCodeRatio: Nullable<number>;
  numberOfResponsibilities: Nullable<number>;
  status: Nullable<ClassStatus>;
  totalNumberOfFindings: Nullable<number>;
  numberOfHighPriorityFindings: Nullable<number>;
  defectDensity: Nullable<number>;
  highDefectDensity: Nullable<number>;
  stratum: Nullable<Stratum>;
}

export interface Author {
  id: Nullable<string>;
  name: Nullable<string>;
  email: Nullable<string>;
}

export interface ClassComplexity {
  complexity: Nullable<number>;
  rating: Nullable<ClassComplexityRating>;
  addedComplexity: Nullable<number>;
}

export interface ClassContribution {
  id: Nullable<string>;
  timestamp: Nullable<string>;
  vcsIdentifier: Nullable<string>;
  authorId: Nullable<string>;
  author: Nullable<Author>;
  complexity: Nullable<ClassComplexity>;
}

export interface Finding {
  id: Nullable<string>;
  description: Nullable<string>;
  ruleName: Nullable<string>;
  ruleDescription: Nullable<string>;
  severity: Nullable<Severity>;
  tool: Nullable<string>;
}

export interface ClassDetail extends ClassSummary {
  contributions: ClassContribution[];
  findings: Finding[];
}

export interface ProjectSnapshot extends ProjectRef {
  packages: PackageSummary[];
}

export interface ProjectsQuery {
  projects: ProjectRef[];
}
export interface ProjectSnapshotQuery {
  projectById: Nullable<ProjectSnapshot>;
}
export interface ClassesByPackageQuery {
  classesByPackage: ClassSummary[];
}
export interface ClassDetailQuery {
  classById: Nullable<ClassDetail>;
}
