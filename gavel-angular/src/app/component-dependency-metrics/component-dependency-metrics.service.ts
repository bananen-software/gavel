import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";

export class ComponentDependency {
  constructor(public packageName: string,
              public efferentCoupling: number,
              public afferentCoupling: number,
              public instability: number,
              public abstractness: number,
              public normalizedDistanceFromMainSequence: number) {
  }
}

export enum DependencyZone {
  GrayArea,
  ZoneOfUselessness,
  ZoneOfPain
}

class DependencyZoneBoundaries {
  constructor(public zone: DependencyZone,
              public upperBound: number,
              public lowerBound: number) {
  }
}

const grayArea = new DependencyZoneBoundaries(DependencyZone.GrayArea, 0.3, 0);
const yellow = new DependencyZoneBoundaries(DependencyZone.ZoneOfUselessness, 0.6, 0.3);
const red = new DependencyZoneBoundaries(DependencyZone.ZoneOfPain, 1, 0.6);

const zoneBoundaries: DependencyZoneBoundaries[] = [red, yellow, grayArea];

@Injectable({
  providedIn: 'root'
})
export default class ComponentDependencyMetricsService {
  constructor(private httpClient: HttpClient) {
  }

  public loadMetrics(): Observable<ComponentDependency[]> {
    //TODO: Handle errors
    return this.httpClient.get<ComponentDependency[]>("http://127.0.0.1:8080/component-dependency-metrics");
  }

  public determineZone(measurement: ComponentDependency): DependencyZone {
    let match = grayArea;

    for (const zoneBoundary of zoneBoundaries) {
      if (measurement.normalizedDistanceFromMainSequence <= zoneBoundary.upperBound) {
        match = zoneBoundary;
      }
    }

    return match.zone;
  }
}
