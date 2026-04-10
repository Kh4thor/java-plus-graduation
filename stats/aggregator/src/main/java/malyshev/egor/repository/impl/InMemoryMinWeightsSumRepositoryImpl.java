package malyshev.egor.repository.impl;

import lombok.extern.slf4j.Slf4j;
import malyshev.egor.repository.InMemoryMinWeightsSumRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class InMemoryMinWeightsSumRepositoryImpl implements InMemoryMinWeightsSumRepository {

    private final Map<Long, Map<Long, Double>> minSums = new ConcurrentHashMap<>();

    @Override
    public void addToSum(long eventA, long eventB, double delta) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        Map<Long, Double> inner = minSums.computeIfAbsent(first, k -> new ConcurrentHashMap<>());
        Double oldValue = inner.get(second);
        double newValue = (oldValue == null ? 0.0 : oldValue) + delta;
        inner.put(second, newValue);
        log.info("addToSum: ({},{}) delta={}, old={}, new={}", first, second, delta, oldValue, newValue);
    }

    @Override
    public double getSum(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        Map<Long, Double> inner = minSums.get(first);
        double sum = inner == null ? 0.0 : inner.getOrDefault(second, 0.0);
        log.info("getSum: ({},{}) = {}", first, second, sum);
        return sum;
    }
}