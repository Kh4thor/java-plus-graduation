package malyshev.egor.repository;

public interface InMemoryEventUserWeightsRepository {

    // Обновляет вес реакции на событие и возвращает разницу между старым и обновленным значениями
    int add(long eventId, long userId, int weight);

    int getWeight(long eventId, long userId);
}
