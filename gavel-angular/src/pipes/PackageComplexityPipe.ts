import {Pipe, PipeTransform} from "@angular/core";

@Pipe({
  name: 'packageComplexity',
  standalone: true
})
export class PackageComplexityPipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case 'EMPTY':
        return "Empty";

      case 'MOSTLY_SIMPLE':
        return "Mostly simple";

      case 'BALANCED':
        return 'Balanced';

      case 'COMPLEX':
        return 'Complex';

      case 'HIGHLY_COMPLEX':
        return 'Highly Complex';
    }

    return value;
  }
}
