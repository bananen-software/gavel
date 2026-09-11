/**
 * Three documents cover the whole application.
 *
 * The snapshot deliberately stops at package level: it is the one request the
 * overview and the navigation rail need, and pulling `classes` into it would
 * multiply the payload by two orders of magnitude for data most sessions never
 * open. Classes arrive lazily, per package.
 */

/**
 * ⚠️ Adjust this to match your schema.
 *
 * The workspace additions were not in the schema.graphqls I was given, so the
 * field names below are inferred from your CreateWorkspaceRequest record
 * (name, path, excludedPaths, basePackage). If your query is called something
 * other than `workspaces`, or the fields differ, this is the only place that
 * needs changing — and the app falls back to the flat project list if this
 * query fails, so a mismatch degrades rather than breaks.
 */
export const WORKSPACES = /* GraphQL */ `
  query Workspaces {
    workspaces {
      id
      name
      path
      basePackage
      excludedPaths
      projects {
        id
        name
        analysisStatus
        lastAnalyzed
      }
    }
  }
`;

export const PROJECTS = /* GraphQL */ `
  query Projects {
    projects {
      id
      name
      analysisStatus
      lastAnalyzed
    }
  }
`;

export const PROJECT_SNAPSHOT = /* GraphQL */ `
  query ProjectSnapshot($id: ID) {
    projectById(id: $id) {
      id
      name
      analysisStatus
      lastAnalyzed
      packages {
        id
        name
        complexity
        complexityRating
        complexityOrdinal
        numberOfTypes
        defectDensity
        highDefectDensity
        linesOfCode
        linesOfComments
        commentToCodeRatio
        numberOfVeryHighComplexityTypes
        numberOfHighComplexityTypes
        numberOfMediumComplexityTypes
        numberOfLowComplexityTypes
        numberOfHighPriorityFindings
        totalNumberOfFindings
        size
        stratum
        stratumOrdinal
        relationalCohesion {
          rating
          numberOfTypes
          numberOfInternalRelationships
          relationalCohesion
        }
        componentDependency {
          afferentCoupling
          efferentCoupling
          abstractness
          instability
          distance
        }
      }
    }
  }
`;

export const CLASSES_BY_PACKAGE = /* GraphQL */ `
  query ClassesByPackage($packageId: ID) {
    classesByPackage(packageId: $packageId) {
      id
      packageId
      name
      programmingLanguage
      lastModified
      numberOfChanges
      numberOfAuthors
      size
      complexity
      complexityRating
      totalLinesOfCode
      totalLinesOfComments
      commentToCodeRatio
      numberOfResponsibilities
      status
      totalNumberOfFindings
      numberOfHighPriorityFindings
      defectDensity
      highDefectDensity
      stratum
    }
  }
`;

export const CLASS_DETAIL = /* GraphQL */ `
  query ClassDetail($classId: ID) {
    classById(classId: $classId) {
      id
      packageId
      name
      programmingLanguage
      lastModified
      numberOfChanges
      numberOfAuthors
      size
      complexity
      complexityRating
      totalLinesOfCode
      totalLinesOfComments
      commentToCodeRatio
      numberOfResponsibilities
      status
      totalNumberOfFindings
      numberOfHighPriorityFindings
      defectDensity
      highDefectDensity
      stratum
      contributions {
        id
        timestamp
        vcsIdentifier
        authorId
        author {
          id
          name
          email
        }
        complexity {
          complexity
          rating
          addedComplexity
        }
      }
      findings {
        id
        description
        ruleName
        ruleDescription
        severity
        tool
      }
    }
  }
`;
