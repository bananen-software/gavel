import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: "relationalCohesionStatus",
  standalone: true
})
export default class RelationalCohesionStatusPipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case "HIGH":
        return "High";

      case "GOOD":
        return "Good";

      case "LOW":
        return "Low";
    }

    return value;
  }

}
