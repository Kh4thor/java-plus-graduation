package malyshev.egor.repository.impl;

import lombok.extern.slf4j.Slf4j;
import malyshev.egor.repository.InMemoryEventUserWeightsRepository;
import malyshev.egor.repository.InMemoryMinWeightsSumsRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;
import stats.avro.EventSimilarityAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Матрица сумм минимальных весов для каждой упорядоченной пары событий.
 * Внешний ключ — меньший идентификатор события (first).
 * Внутренняя мапа — для каждого большего идентификатора события (second) хранит значение S_min(first, second) типа Double.
 */

@Slf4j
@Repository
public class InMemoryMinWeightsSumsRepositoryImpl implements InMemoryMinWeightsSumsRepository {

    private final Map<Long, Map<Long, Double>> minWeightsSumForPairs = new ConcurrentHashMap<>();

    @Override
    public void putPairSimilarity(long eventA, long eventB, double weight) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        minWeightsSumForPairs
                .computeIfAbsent(first, e -> new ConcurrentHashMap<>())
                .put(second, weight);
    }

    @Override
    public double getPairSimilarity(long eventA, long eventB) {
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
