package malyshev.egor.repository.impl;

import malyshev.egor.repository.InMemoryEventUserWeightsRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Для каждого события (внешний ключ Long) хранится вложенная мапа,
 * которая для каждого пользователя (внутренний ключ Long) содержит
 * текущий максимальный вес его взаимодействия с этим событием (значение Integer).
 */
@Repository
public class InMemoryEventUserWeightsRepositoryImpl implements InMemoryEventUserWeightsRepository {

    private final Map<Long, Map<Long, Integer>> eventUserWeights = new ConcurrentHashMap<>();

    // Возвращает разницу между старым и обновленным значениями
    @Override
    public void setWeight(long eventId, long userId, int weight) {
        Map<Long, Integer> userWeights = eventUserWeights.computeIfAbsent(eventId, k -> new ConcurrentHashMap<>());
        userWeights.merge(userId, weight, Math::max);
    }

    @Override
    public int getWeight(long eventId, long userId) {
        Map<Long, Integer> userWeights = eventUserWeights.get(eventId);
        return userWeights == null ? 0 : userWeights.getOrDefault(userId, 0);
    }

    @Override
    public Map<Long, Integer> getUserMapWeights(long eventId) {
        return eventUserWeights.getOrDefault(eventId, Map.of());
    }

    @Override
    public Set<Long> getAllEventIds() {
        return eventUserWeights.keySet();
    }
}