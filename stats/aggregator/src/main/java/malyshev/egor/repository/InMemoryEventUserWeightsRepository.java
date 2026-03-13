package malyshev.egor.repository;

import java.util.Map;
import java.util.Set;

/*
 * Для каждого события (внешний ключ Long) хранится вложенная мапа,
 * которая для каждого пользователя (внутренний ключ Long) содержит
 * текущий максимальный вес его взаимодействия с этим событием (значение Integer).
 */
public interface InMemoryEventUserWeightsRepository {

    // Устанавливает вес реакции на событие и возвращает разницу между старым и обновленным значениями
    void setWeight(long eventId, long userId, int weight);

    // Получает вес реакции пользователя на событие
    int getWeight(long eventId, long userId);

    // Получает словарь всех пользователей с их весами реакции для данного события
    Map<Long, Integer> getUserMapWeights(long eventId);

    // Получает список id всех событий
    Set<Long> getAllEventIds();
}
