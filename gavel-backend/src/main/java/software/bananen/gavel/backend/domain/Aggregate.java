package software.bananen.gavel.backend.domain;

public interface Aggregate<T> {
    
    T getAggregateRoot();
}
