package malyshev.egor.repository.impl;

import malyshev.egor.repository.InMemoryMinWeightsSumsRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/*
 * Матрица сумм минимальных весов для каждой упорядоченной пары событий.
 * Внешний ключ — меньший идентификатор события (first).
 * Внутренняя мапа — для каждого большего идентификатора события (second) хранит значение S_min(first, second) типа Double.
 */
@Repository
class InMemoryMinWeightsSumsRepositoryImpl implements InMemoryMinWeightsSumsRepository {

    private final Map<Long, Map<Long, Double>> minWeightsSumForPairs = new HashMap<>();

    @Override
    public void put(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightsSumForPairs
                .computeIfAbsent(first, e -> new HashMap<>())
                .put(second, sum);
    }

    @Override
    public double get(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        final double minWeightValue = 0.0;

        Map<Long, Double> inner = minWeightsSumForPairs.get(first);
        if (inner == null) {
            return minWeightValue;
        }
        return inner.getOrDefault(second, minWeightValue);
    }
}
