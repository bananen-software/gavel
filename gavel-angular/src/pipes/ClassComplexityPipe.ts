import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: "classComplexity",
  standalone: true
})
export default class ClassComplexityPipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case "EMPTY":
        return "Empty";

      case "LOW":
        return "Low";

      case "MEDIUM":
        return "Medium";

      case "HIGH":
        return "High";

      case "UNKNOWN":
        return "Unknown";

      case "VERY_HIGH":
        return "Very high"
    }

    return value;
  }
}
