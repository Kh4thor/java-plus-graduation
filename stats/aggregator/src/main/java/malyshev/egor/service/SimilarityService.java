package malyshev.egor.service;

import lombok.extern.slf4j.Slf4j;
import malyshev.egor.config.KafkaProperties;
import malyshev.egor.repository.InMemoryEventTotalWeightRepository;
import malyshev.egor.repository.InMemoryEventUserWeightsRepository;
import malyshev.egor.repository.InMemoryMinWeightsSumRepository;
import malyshev.egor.repository.InMemoryUserEventsRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import stats.avro.ActionTypeAvro;
import stats.avro.EventSimilarityAvro;
import stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
public class SimilarityService {

    private static final double WEIGHT_VIEW = 0.4;
    private static final double WEIGHT_REGISTER = 0.8;
    private static final double WEIGHT_LIKE = 1.0;

    private final KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final InMemoryMinWeightsSumRepository minWeightsSumRepo;
    private final InMemoryEventUserWeightsRepository userWeightsRepo;
    private final InMemoryEventTotalWeightRepository totalWeightsRepo;
    private final InMemoryUserEventsRepository userEventsRepo;

    public SimilarityService(KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate,
                             KafkaProperties kafkaProperties,
                             InMemoryMinWeightsSumRepository minWeightsSumRepo,
                             InMemoryEventUserWeightsRepository userWeightsRepo,
                             InMemoryEventTotalWeightRepository totalWeightsRepo,
                             InMemoryUserEventsRepository userEventsRepo) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
        this.minWeightsSumRepo = minWeightsSumRepo;
        this.userWeightsRepo = userWeightsRepo;
        this.totalWeightsRepo = totalWeightsRepo;
        this.userEventsRepo = userEventsRepo;
    }

    public void processUserAction(UserActionAvro action) {
        long userId = action.getUserId();
        long eventA = action.getEventId();
        double newWeight = getActionWeight(action.getActionType());
        Instant timestamp = Instant.ofEpochMilli(action.getTimestamp());

        log.info(">>> processUserAction: userId={}, eventA={}, newWeight={}, timestamp={}",
                userId, eventA, newWeight, timestamp);

        double oldWeight = userWeightsRepo.getWeight(eventA, userId);
        log.debug("oldWeight for event {} user {} = {}", eventA, userId, oldWeight);

        if (newWeight <= oldWeight) {
            log.debug("Skip: weight not increased for userId={}, eventId={}", userId, eventA);
            return;
        }

        // Обновить максимальный вес для события
        userWeightsRepo.setWeight(eventA, userId, newWeight);
        double diff = newWeight - oldWeight;
        totalWeightsRepo.addDiff(eventA, diff);
        log.debug("Updated totalWeight for event {} by diff={}, new total={}",
                eventA, diff, totalWeightsRepo.getTotalWeightByEventId(eventA));

        if (oldWeight == 0) {
            userEventsRepo.addEvent(userId, eventA);
            log.debug("First interaction of user {} with event {}", userId, eventA);
        }

        Set<Long> otherEvents = userEventsRepo.getEventsByUser(userId);
        log.debug("Other events for user {}: {}", userId, otherEvents);

        for (Long eventB : otherEvents) {
            if (eventB.equals(eventA)) continue;

            double weightB = userWeightsRepo.getWeight(eventB, userId);
            double oldContribution = Math.min(oldWeight, weightB);
            double newContribution = Math.min(newWeight, weightB);
            double delta = newContribution - oldContribution;

            log.debug("Pair ({}<->{}): weightB={}, oldContr={}, newContr={}, delta={}",
                    eventA, eventB, weightB, oldContribution, newContribution, delta);

            if (delta != 0) {
                minWeightsSumRepo.addToSum(eventA, eventB, delta);
                double totalA = totalWeightsRepo.getTotalWeightByEventId(eventA);
                double totalB = totalWeightsRepo.getTotalWeightByEventId(eventB);
                double sMin = minWeightsSumRepo.getSum(eventA, eventB);

                log.debug("Before similarity: totalA={}, totalB={}, sMin={}", totalA, totalB, sMin);

                double similarity = (totalA > 0 && totalB > 0)
                        ? sMin / Math.sqrt(totalA * totalB)
                        : 0.0;

                long first = Math.min(eventA, eventB);
                long second = Math.max(eventA, eventB);
                log.info("SIMILARITY: ({},{}) = {}, sMin={}, sqrtProduct={}",
                        first, second, similarity, sMin, Math.sqrt(totalA * totalB));

                EventSimilarityAvro similarityMsg = EventSimilarityAvro.newBuilder()
                        .setEventA(first)
                        .setEventB(second)
                        .setScore(similarity)
                        .setTimestamp(timestamp)
                        .build();

                kafkaTemplate.send(kafkaProperties.getProducer().getTopic(), similarityMsg)
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                log.debug("Sent similarity for ({}, {}): {}", first, second, similarity);
                            } else {
                                log.error("Failed to send similarity for ({}, {})", first, second, ex);
                            }
                        });
            } else {
                log.debug("Delta=0 for pair ({},{}), skip sending", eventA, eventB);
            }
        }
    }

    private double getActionWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> WEIGHT_VIEW;
            case REGISTER -> WEIGHT_REGISTER;
            case LIKE -> WEIGHT_LIKE;
        };
    }
}