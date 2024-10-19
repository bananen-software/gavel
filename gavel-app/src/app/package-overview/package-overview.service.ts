import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";

export type ClassComplexityRatings = {
  lowComplexityPercentage: number;
  mediumComplexityPercentage: number;
  highComplexityPercentage: number;
  veryHighComplexityPercentage: number;
};

export type PackageOverview = {
  packageName: string;
  complexity: number;
  totalLinesOfCode: number;
  totalLinesOfComments: number;
  size: "SMALL" | "MEDIUM" | "LARGE" | "UNKNOWN" | "VERY_LARGE";
  packageComplexity: "MOSTLY_SIMPLE" | "BALANCED" | "COMPLEX" | "HIGHLY_COMPLEX";
  numberOfTypes: number;
  classComplexityRatings: ClassComplexityRatings;
};

@Injectable({
  providedIn: 'root'
})
export class PackageOverviewService {
  constructor(private httpClient: HttpClient) {
  }

  public loadMetrics(): Observable<PackageOverview[]> {
    //TODO: Handle errors
    // ZOD validation?
    return this.httpClient.get<PackageOverview[]>("http://127.0.0.1:8080/packages/");
  }
}
