import {Observable, of} from "rxjs";
import {randomizedDelay} from "../../util/fakes";
import {Injectable} from "@angular/core";

export class Author {
  constructor(public name: string,
              public email: string) {
  }
}

export class AuthorComplexity {
  constructor(public author: Author,
              public complexityDelta: number,
              public numberOfChanges: number,
              public relativeComplexityAdded: number) {
  }
}

@Injectable({
  providedIn: 'root'
})
export default class AuthorComplexityMetricsService {

  private metrics: AuthorComplexity[] = [{
    "author": {
      "name": "Dennis Reichenberg",
      "email": "9849919+soylentbob@users.noreply.github.com"
    },
    "complexityDelta": 37892,
    "numberOfChanges": 233,
    "relativeComplexityAdded": 162.62660944206007
  }]

  public loadMetrics(): Observable<AuthorComplexity[]> {
    return of(this.metrics).pipe(randomizedDelay());
  }
}
