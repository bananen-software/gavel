import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: 'elementSize',
  standalone: true
})
export class ElementSizePipe implements PipeTransform {
  transform(value: string): string {
    //TODO: Consider i18n
    switch (value) {
      case 'UNKNOWN':
        return 'Unknown';

      case 'EMPTY':
        return 'Empty';

      case 'SMALL':
        return 'Small';

      case 'MEDIUM':
        return "Medium";

      case 'LARGE':
        return "Large";

      case 'VERY_LARGE':
        return "Very Large";
    }

    return value;
  }

}
