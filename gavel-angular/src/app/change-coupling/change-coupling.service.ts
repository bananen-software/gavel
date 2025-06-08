import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

export type ChangeCoupling = {
  sourcePackageName: string;
  sourceClassName: string;
  targetPackageName: string
  targetClassName: string;
  totalChanges: number;
  numberOfSharedChanges: number;
  changeCoupling: number;
};

@Injectable({
  providedIn: 'root'
})
export default class ChangeCouplingService {
  constructor(private httpClient: HttpClient) {
  }

  public loadMetrics(): Observable<ChangeCoupling[]> {
    //TODO: Handle errors
    return this.httpClient.get<ChangeCoupling[]>("http://127.0.0.1:8080/change-coupling");
  }
}
