package malyshev.egor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.model.EventSimilarity;
import malyshev.egor.model.UserAction;
import malyshev.egor.repository.EventSimilarityRepository;
import malyshev.egor.repository.UserActionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    @Value("${recommendation.recent-events-count:10}")
    private int recentEventsCount;

    @Value("${recommendation.similar-events-limit:5}")
    private int similarEventsLimit;

    public List<RecommendedEvent> getRecommendationsForUser(long userId, int maxResults) {
        List<Long> recentEvents = getRecentEvents(userId, recentEventsCount);
        if (recentEvents.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> interactedEvents = getInteractedEventIds(userId);

        Map<Long, Double> candidateScores = new HashMap<>();
        for (Long eventId : recentEvents) {
            List<EventSimilarity> similarities = eventSimilarityRepository.findAllByEventId(eventId);
            for (EventSimilarity sim : similarities) {
                Long otherEvent = sim.getEventA().equals(eventId) ? sim.getEventB() : sim.getEventA();
                if (!interactedEvents.contains(otherEvent)) {
                    candidateScores.merge(otherEvent, sim.getSimilarity(), Math::max);
                }
            }
        }

        List<Long> candidates = candidateScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(maxResults)
                .map(Map.Entry::getKey)
                .toList();

        List<RecommendedEvent> result = new ArrayList<>();
        for (Long candidate : candidates) {
            double predicted = predictScore(userId, candidate);
            result.add(new RecommendedEvent(candidate, predicted));
        }
        return result;
    }

    public List<RecommendedEvent> getSimilarEvents(long eventId, long userId, int maxResults) {
        Set<Long> interactedEvents = getInteractedEventIds(userId);
        List<EventSimilarity> similarities = eventSimilarityRepository.findAllByEventId(eventId);

        List<RecommendedEvent> result = new ArrayList<>();
        for (EventSimilarity sim : similarities) {
            Long otherEvent = sim.getEventA().equals(eventId) ? sim.getEventB() : sim.getEventA();
            if (!interactedEvents.contains(otherEvent)) {
                result.add(new RecommendedEvent(otherEvent, sim.getSimilarity()));
            }
        }

        result.sort((a, b) -> Double.compare(b.score(), a.score()));
        if (result.size() > maxResults) {
            result = result.subList(0, maxResults);
        }
        return result;
    }

    public List<RecommendedEvent> getInteractionsCount(List<Long> eventIds) {
        List<Object[]> results = userActionRepository.sumWeightsByEventIds(eventIds);
        Map<Long, Double> sumMap = results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Number) row[1]).doubleValue()
                ));
        return eventIds.stream()
                .map(eventId -> new RecommendedEvent(eventId, sumMap.getOrDefault(eventId, 0.0)))
                .collect(Collectors.toList());
    }

    private List<Long> getRecentEvents(long userId, int limit) {
        PageRequest page = PageRequest.of(0, limit);
        return userActionRepository.findRecentEventIdsByUserId(userId, page);
    }

    private Set<Long> getInteractedEventIds(long userId) {
        List<Long> events = userActionRepository.findAllEventIdsByUserId(userId);
        return new HashSet<>(events);
    }

    private double predictScore(long userId, long candidateEventId) {
        List<EventSimilarity> similarities = eventSimilarityRepository.findAllByEventId(candidateEventId);
        List<UserAction> userActions = userActionRepository.findAllByUserId(userId);
        Map<Long, Double> userWeights = userActions.stream()
                .collect(Collectors.toMap(UserAction::getEventId, UserAction::getWeight));

        List<Neighbor> neighbors = new ArrayList<>();
        for (EventSimilarity sim : similarities) {
            Long neighborId = sim.getEventA().equals(candidateEventId) ? sim.getEventB() : sim.getEventA();
            Double weight = userWeights.get(neighborId);
            if (weight != null) {
                neighbors.add(new Neighbor(neighborId, sim.getSimilarity(), weight));
            }
        }

        neighbors.sort((a, b) -> Double.compare(b.similarity, a.similarity));
        if (neighbors.size() > similarEventsLimit) {
            neighbors = neighbors.subList(0, similarEventsLimit);
        }

        if (neighbors.isEmpty()) return 0.0;

        double weightedSum = 0.0;
        double similaritySum = 0.0;
        for (Neighbor n : neighbors) {
            weightedSum += n.similarity * n.weight;
            similaritySum += n.similarity;
        }
        return similaritySum == 0 ? 0.0 : weightedSum / similaritySum;
    }

    private static class Neighbor {
        long eventId;
        double similarity;
        double weight;

        Neighbor(long eventId, double similarity, double weight) {
            this.eventId = eventId;
            this.similarity = similarity;
            this.weight = weight;
        }
    }

    public record RecommendedEvent(long eventId, double score) {
    }
}