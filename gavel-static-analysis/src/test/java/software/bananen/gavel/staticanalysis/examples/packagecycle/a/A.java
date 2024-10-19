package software.bananen.gavel.staticanalysis.examples.packagecycle.a;

import software.bananen.gavel.staticanalysis.examples.packagecycle.b.B;

public record A(B b) {
}
