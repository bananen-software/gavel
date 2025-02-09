import {Injectable} from "@angular/core";
import {map, Observable} from "rxjs";
import {Apollo, gql} from "apollo-angular";

const QUERY_DATA = gql`
  query packageOverview {
    projectById(id: 1) {
      id
      name
      packages {
        id
        name
        size,
        complexityRating,
        complexityOrdinal,
        numberOfTypes,
        complexity,
        commentToCodeRatio,
        linesOfCode,
        linesOfComments,
        defectDensity
      }
    }
  }
`

export type PackageOverview = {
  packageName: string;
  complexity: number;
  complexityOrdinal: number;
  commentToCodeRatio: number;
  totalLinesOfCode: number;
  totalLinesOfComments: number;
  size: "SMALL" | "MEDIUM" | "LARGE" | "UNKNOWN" | "VERY_LARGE";
  packageComplexity: "MOSTLY_SIMPLE" | "BALANCED" | "COMPLEX" | "HIGHLY_COMPLEX";
  numberOfTypes: number;
  defectDensity: number;
};

@Injectable({
  providedIn: 'root'
})
export class PackageOverviewService {
  constructor(private graphqlClient: Apollo) {
  }

  public loadMetrics(): Observable<PackageOverview[]> {
    return this.graphqlClient.watchQuery({
      query: QUERY_DATA,
      variables: {
        id: 1,
      },
    }).valueChanges.pipe(map(result => {// @ts-ignore
      return result.data?.projectById.packages.map(pkg => {
        return {
          packageName: pkg.name,
          complexity: pkg.complexity,
          complexityOrdinal: pkg.complexityOrdinal,
          commentToCodeRatio: pkg.commentToCodeRatio,
          totalLinesOfCode: pkg.linesOfCode,
          totalLinesOfComments: pkg.linesOfComments,
          size: pkg.size,
          packageComplexity: pkg.complexityRating,
          numberOfTypes: pkg.numberOfTypes,
          defectDensity: pkg.defectDensity
        }
      });
    }));
  }
}
