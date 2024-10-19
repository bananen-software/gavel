import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";

export class CumulativeComponentDependency {
  constructor(public packageName: string,
              public cumulative: number,
              public average: number,
              public relativeAverage: number,
              public normalized: number) {
  }
}

@Injectable({
  providedIn: 'root'
})
export default class CumulativeComponentDependencyMetricsService {

  constructor(private httpClient: HttpClient) {
  }

  public loadMetrics(): Observable<CumulativeComponentDependency[]> {
    //TODO: Handle errors
    return this.httpClient.get<CumulativeComponentDependency[]>("http://127.0.0.1:8080/cumulative-component-dependency-metrics");
  }
}
