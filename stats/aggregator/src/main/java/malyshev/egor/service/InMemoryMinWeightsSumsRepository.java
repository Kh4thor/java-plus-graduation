package malyshev.egor.service;

import java.util.HashMap;
import java.util.Map;

/*
 * Хранилище суммы минимальных "весов" для двух различных событий
 */
public class InMemoryMinWeightsSumsRepository {

    private final Map<Long, Map<Long, Double>> minWeightsSums = new HashMap<>();

    public void put(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightsSums
                .computeIfAbsent(first, e -> new HashMap<>())
                .put(second, sum);
    }

    public double get(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        return minWeightsSums
                .computeIfAbsent(first, e -> new HashMap<>())
                .getOrDefault(second, 0.0);
    }
}
