import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";

export class RelationalCohesion {
  constructor(public packageName: string,
              public numberOfTypes: number,
              public numberOfInternalRelationships: number,
              public relationalCohesion: number,
              public rating: "HIGH" | "GOOD" | "LOW") {
  }
}

@Injectable({
  providedIn: 'root'
})
export default class RelationalCohesionMetricsService {
  constructor(private httpClient: HttpClient) {
  }

  public loadMetrics(): Observable<RelationalCohesion[]> {
    //TODO: Handle errors
    return this.httpClient.get<RelationalCohesion[]>("http://127.0.0.1:8080/relational-cohesion-metrics");
  }
}
