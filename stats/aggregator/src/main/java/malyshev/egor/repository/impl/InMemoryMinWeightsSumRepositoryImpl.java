package malyshev.egor.repository.impl;

import malyshev.egor.repository.InMemoryMinWeightsSumRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMinWeightsSumRepositoryImpl implements InMemoryMinWeightsSumRepository {

    private final Map<Long, Map<Long, Double>> minSums = new ConcurrentHashMap<>();

    @Override
    public void addToSum(long eventA, long eventB, double delta) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        minSums.computeIfAbsent(first, k -> new ConcurrentHashMap<>())
                .merge(second, delta, Double::sum);
    }

    @Override
    public double getSum(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        Map<Long, Double> inner = minSums.get(first);
        return inner == null ? 0.0 : inner.getOrDefault(second, 0.0);
    }
}