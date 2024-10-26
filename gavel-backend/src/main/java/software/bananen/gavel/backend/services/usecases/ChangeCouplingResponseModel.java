package software.bananen.gavel.backend.services.usecases;

public record ChangeCouplingResponseModel(String sourcePackageName,
                                          String sourceClassName,
                                          String targetPackageName,
                                          String targetClassName,
                                          Integer totalChanges,
                                          int numberOfSharedChanges,
                                          double changeCoupling) {
}
