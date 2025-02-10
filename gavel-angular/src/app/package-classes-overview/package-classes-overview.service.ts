import {map, Observable, of} from "rxjs";
import {Injectable} from "@angular/core";
import {Apollo, gql} from "apollo-angular";

const QUERY_DATA = gql`
  query package($packageId: Int!) {
    packageById(id: $packageId) {
      id
      name
      classes {
        name
        complexity
        complexityRating
        lastModified
        numberOfChanges
        numberOfAuthors
        commentToCodeRatio
        numberOfResponsibilities
        defectDensity
        totalLinesOfCode
        totalLinesOfComments
        commentToCodeRatio
      }
    }
  }
`

const CLASS_QUERY_DATA = gql`
  query packageClasses {
    classById {
      id
      name
      lastModified
      numberOfAuthors
      complexity
      complexityRating
      totalLinesOfCode
      totalLinesOfComments
      numberOfResponsibilities
      package {
        name
        complexityRating
      }
      findings {
        description
        ruleName
        ruleDescription
        severity
        tool
      }
      contributions {
        author {
          name
          email
        }
        timestamp
        complexity {
          rating
          complexity
          addedComplexity
        }
      }
    }
  }
`

export type PackageClassData = {
  className: string,
  lastModified: string,
  numberOfChanges: number,
  numberOfAuthors: number,
  complexity: number,
  complexityRating: "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH",
  totalLinesOfCode: number,
  totalLinesOfComments: number,
  commentToCodeRatio: number
}

export type PackageData = {
  id: number,
  packageName: string,
  classes: PackageClassData[]
}

@Injectable({
  providedIn: 'root'
})
export default class PackageClassesOverviewService {

  constructor(private graphqlClient: Apollo) {
  }

  loadMetrics(packageId: string | null): Observable<PackageData> {
    if (packageId) {
      return this.graphqlClient.watchQuery({
        query: QUERY_DATA,
        variables: {
          packageId: +packageId,
        },
      }).valueChanges.pipe(map(result => {
        return {
          // @ts-ignore
          id: result.data?.packageById.id,
          // @ts-ignore
          packageName: result.data?.packageById.name,
          // @ts-ignore
          classes: result.data?.packageById.classes.map(cls => {
            return {// @ts-ignore
              packageName: result.data?.packageName,
              className: cls.name,
              lastModified: cls.lastModified,
              numberOfChanges: cls.numberOfChanges,
              numberOfAuthors: cls.numberOfAuthors,
              complexity: cls.complexity,
              complexityRating: cls.complexityRating,
              totalLinesOfCode: cls.totalLinesOfCode,
              totalLinesOfComments: cls.totalLinesOfComments,
              commentToCodeRatio: cls.commentToCodeRatio,
              numberOfResponsibilities: cls.numberOfResponsibilities,
              defectDensity: cls.defectDensity,
            }
          })
        }
      }));
    } else {
      return of()
    }
  }
}
