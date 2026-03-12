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
class InMemoryEventUserWeightsRepositoryImpl implements InMemoryEventUserWeightsRepository {

    private final Map<Long, Map<Long, Integer>> eventUserWeights = new ConcurrentHashMap<>();

    // Возвращает разницу между старым и обновленным значениями
    @Override
    public int add(long eventId, long userId, int weight) {
        Map<Long, Integer> userWeights = eventUserWeights.computeIfAbsent(eventId, k -> new ConcurrentHashMap<>());
        int currentWeight = userWeights.getOrDefault(userId, 0);
        return userWeights.merge(userId, weight, Math::max) - currentWeight;
    }

    @Override
    public int getWeight(long eventId, long userId) {
        Map<Long, Integer> userWeights = eventUserWeights.get(eventId);
        return userWeights.getOrDefault(userId, 0);
    }
}