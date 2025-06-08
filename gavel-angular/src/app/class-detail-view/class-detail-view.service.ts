import {Apollo, gql} from "apollo-angular";
import {Injectable} from "@angular/core";
import {map, Observable, of} from "rxjs";

const QUERY_DATA = gql`
  query packageClasses($classId: Int!) {
    classById(classId: $classId) {
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
        id
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

@Injectable({
  providedIn: 'root'
})
export default class ClassDetailViewService {

  constructor(private graphqlClient: Apollo) {
  }

  loadMetrics(classId: string | null): Observable<any> {
    if (classId) {
      return this.graphqlClient.watchQuery({
        query: QUERY_DATA,
        variables: {
          classId: +classId,
        },
      }).valueChanges.pipe(map(result => {
        console.log(result.data);
        return result.data;
      }));
    }

    return of();
  }
}
