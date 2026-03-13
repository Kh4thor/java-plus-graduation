package malyshev.egor.repository.impl;

import malyshev.egor.repository.InMemoryEventTotalWeightRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class InMemoryEventTotalWeightRepositoryImpl implements InMemoryEventTotalWeightRepository {
    private final Map<Long, Integer> eventTotalWeights = new ConcurrentHashMap<>();

    @Override
    public int getTotalWeightByEventId(long eventId) {
        return eventTotalWeights.getOrDefault(eventId, 0);
    }

    @Override
    public void addDiff(long eventId, int diff) {
        eventTotalWeights.merge(eventId, diff, Integer::sum);
    }
}
