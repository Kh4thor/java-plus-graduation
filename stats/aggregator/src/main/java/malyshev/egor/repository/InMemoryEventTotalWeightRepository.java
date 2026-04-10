package malyshev.egor.repository;

/*
 * Хранилище суммы максимальных весов реакций всех пользователей на событие
 */
public interface InMemoryEventTotalWeightRepository {

    // получить сумму весов события
    double getTotalWeightByEventId(long eventId);

    // прибавить значение к текущему весу события
    void addDiff(long eventId, double diff);
}