import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";

export class CodeHotspot {
  constructor(public packageName: string,
              public className: string,
              public numberOfChanges: number,
              public complexity: number,
              public complexityRating: "EMPTY" | "LOW" | "MEDIUM" | "HIGH" | "UNKNOWN" | "VERY_HIGH",
              public totalLinesOfCode: number,
              public size: "UNKNOWN" | "EMPTY" | "SMALL" | "MEDIUM" | "LARGE" | "VERY_LARGE",
              public lastModified: string,
              public numberOfAuthors: number,
              public defectDensity: number) {
  }
}

@Injectable({
  providedIn: 'root'
})
export default class CodeHotspotMetricsService {

  constructor(private httpClient: HttpClient) {
  }

  public loadMetrics(): Observable<CodeHotspot[]> {
    //TODO: Handle errors
    return this.httpClient.get<CodeHotspot[]>("http://127.0.0.1:8080/code-hotspots");
  }
}
