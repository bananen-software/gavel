import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";

export class Visibility {
  constructor(public packageName: string,
              public relativeVisibility: number,
              public averageRelativeVisibility: number,
              public globalRelativeVisibility: number) {
  }
}

@Injectable({
  providedIn: 'root'
})
export default class VisibilityMetricsService {
  constructor(private httpClient: HttpClient) {
  }

  public loadMetrics(): Observable<Visibility[]> {
    //TODO: Handle errors
    return this.httpClient.get<Visibility[]>("http://127.0.0.1:8080/component-visibility-metrics");
  }
}
