import {Observable, of} from "rxjs";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";

export class PackageClass {
  constructor(public packageName: string,
              public className: string,
              public lastModified: string,
              public numberOfChanges: number,
              public numberOfAuthors: number,
              public complexity: number,
              public complexityRating: "LOW" | "MEDIUM" | "HIGH" | "VERY_HIGH",
              public totalLinesOfCode: number,
              public totalLinesOfComments: number,
              public commentToCodeRatio: number) {
  }
}

@Injectable({
  providedIn: 'root'
})
export default class PackageClassesOverviewService {

  constructor(private httpClient: HttpClient) {
  }

  loadMetrics(packageName: string | null): Observable<PackageClass[]> {
    //TODO: Handle errors
    if (packageName && packageName.length > 0) {
      return this.httpClient.get<PackageClass[]>(`http://127.0.0.1:8080/packages/${packageName}/classes`);
    } else {
      return of([]);
    }
  }
}
