package malyshev.egor.repository.impl;

import malyshev.egor.repository.InMemoryEventUserWeightsRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Для каждого события (внешний ключ Long) хранится вложенная мапа,
 * которая для каждого пользователя (внутренний ключ Long) содержит
 * текущий максимальный вес его взаимодействия с этим событием (значение Integer).
 */
@Repository
public class InMemoryEventUserWeightsRepositoryImpl implements InMemoryEventUserWeightsRepository {

    private final Map<Long, Map<Long, Double>> eventUserWeights = new ConcurrentHashMap<>();

    // Возвращает разницу между старым и обновленным значениями
    @Override
    public void setWeight(long eventId, long userId, double weight) {
        Map<Long, Double> userWeights = eventUserWeights.computeIfAbsent(eventId, k -> new ConcurrentHashMap<>());
        userWeights.merge(userId, weight, Math::max);
    }

    @Override
    public double getWeight(long eventId, long userId) {
        Map<Long, Double> userWeights = eventUserWeights.get(eventId);
        return userWeights == null ? 0 : userWeights.getOrDefault(userId, 0.0);
    }

    @Override
    public Map<Long, Double> getUserMapWeights(long eventId) {
        return eventUserWeights.getOrDefault(eventId, Map.of());
    }

    @Override
    public Set<Long> getAllEventIds() {
        return eventUserWeights.keySet();
    }
}