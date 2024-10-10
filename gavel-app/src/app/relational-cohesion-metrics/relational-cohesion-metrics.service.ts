import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";

export class RelationalCohesion {
  constructor(public packageName: string,
              public numberOfTypes: number,
              public numberOfInternalRelationships: number,
              public relationalCohesion: number) {
  }
}

export enum RelationalCohesionLevel {
  Good,
  Low,
  High
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

  public determineRelationalCohesionLevel(measurement: RelationalCohesion): RelationalCohesionLevel {
    if (measurement.relationalCohesion < 1.5) {
      return RelationalCohesionLevel.Low;
    } else if (measurement.relationalCohesion > 4) {
      return RelationalCohesionLevel.High;
    } else {
      return RelationalCohesionLevel.Good;
    }
  }
}
