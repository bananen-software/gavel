import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";

export type AuthorComplexity = {
  name: string;
  email: string;
  complexityDelta: number;
  numberOfChanges: number;
  averageComplexityAdded: number;
}

@Injectable({
  providedIn: 'root'
})
export default class AuthorComplexityMetricsService {
  constructor(private httpClient: HttpClient) {
  }

  public loadMetrics(): Observable<AuthorComplexity[]> {
    //TODO: Handle errors
    return this.httpClient.get<AuthorComplexity[]>("http://127.0.0.1:8080/author-complexity-metrics");
  }
}
