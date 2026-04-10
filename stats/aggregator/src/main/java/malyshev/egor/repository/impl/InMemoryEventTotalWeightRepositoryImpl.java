package malyshev.egor.repository.impl;

import malyshev.egor.repository.InMemoryEventTotalWeightRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryEventTotalWeightRepositoryImpl implements InMemoryEventTotalWeightRepository {
    private final Map<Long, Double> eventTotalWeights = new ConcurrentHashMap<>();

    @Override
    public double getTotalWeightByEventId(long eventId) {
        return eventTotalWeights.getOrDefault(eventId, 0.0);
    }

    @Override
    public void addDiff(long eventId, double diff) {
        eventTotalWeights.merge(eventId, diff, Double::sum);
    }
}