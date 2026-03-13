package malyshev.egor.service;

import lombok.extern.slf4j.Slf4j;
import malyshev.egor.repository.InMemoryEventTotalWeightRepository;
import malyshev.egor.repository.InMemoryEventUserWeightsRepository;
import malyshev.egor.repository.InMemoryMinWeightsSumsRepository;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import stats.avro.ActionTypeAvro;
import stats.avro.EventSimilarityAvro;
import stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class SimilarityService {

    private final KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final InMemoryMinWeightsSumsRepository minWeightsRepo;
    private final InMemoryEventUserWeightsRepository userWeightsRepo;
    private final InMemoryEventTotalWeightRepository totalWeightsRepo;

    public SimilarityService(KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate, KafkaProperties kafkaProperties,
                             InMemoryEventTotalWeightRepository totalWeightsRepo,
                             InMemoryMinWeightsSumsRepository minWeightsRepo,
                             InMemoryEventUserWeightsRepository userWeightsRepo) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
        this.totalWeightsRepo = totalWeightsRepo;
        this.minWeightsRepo = minWeightsRepo;
        this.userWeightsRepo = userWeightsRepo;
    }

    public void processUserAction(UserActionAvro userActionAvro) {
        long userId = userActionAvro.getUserId();
        long eventId = userActionAvro.getEventId();
        int newWeight = getActionWeight(userActionAvro.getActionType());
        Instant timestamp = Instant.ofEpochMilli(userActionAvro.getTimestamp());

        int currentWeight = userWeightsRepo.getWeight(eventId, userId);
        if (newWeight <= currentWeight) {
            log.debug("Обновление не требуется: userId={}, eventId={}, newWeight={} <= currentWeight={}",
                    userId, eventId, newWeight, currentWeight);
            return;
        }

        int diff = newWeight - currentWeight; // разница для общей суммы
        userWeightsRepo.setWeight(eventId, userId, newWeight); // сохраняем новый вес
        totalWeightsRepo.addDiff(eventId, diff);

        // Получаем все события и обновляем сходство для пар (eventId, otherId)
        Set<Long> allEventIds = userWeightsRepo.getAllEventIds();
        for (Long otherId : allEventIds) {
            if (!otherId.equals(eventId)) {
                updatePairSimilarity(eventId, otherId, timestamp);
            }
        }
    }

    private void updatePairSimilarity(long eventA, long eventB, Instant timestamp) {
        double minWeightSum = getMinWeightSum(eventA, eventB);

        minWeightsRepo.putPairSimilarity(eventA, eventB, minWeightSum);
        double totalA = totalWeightsRepo.getTotalWeightByEventId(eventA);
        double totalB = totalWeightsRepo.getTotalWeightByEventId(eventB);

        if (totalA == 0 || totalB == 0) {
            log.debug("Нулевая сумма для ({}, {}), пропускаем", eventA, eventB);
            return;
        }

        float similarity = (float) (minWeightSum / (totalA * totalB));
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        EventSimilarityAvro eventSimilarityAvro = EventSimilarityAvro.newBuilder()
                .setEventA(first)
                .setEventB(second)
                .setScore(similarity)
                .setTimestamp(timestamp)
                .build();

        kafkaTemplate.send(props.getProducer().getTopic(), eventSimilarityAvro)
                .addCallback(
                        result -> log.debug("Отправлено сходство для ({}, {}): {}", first, second, similarity),
                        ex -> log.error("Ошибка отправки сходства для ({}, {})", first, second, ex)
                );
    }

    private int getActionWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 1;
            case REGISTER -> 2;
            case LIKE -> 3;
        };
    }

    private double getMinWeightSum(long eventA, long eventB) {
        Map<Long, Integer> userMapA = userWeightsRepo.getUserMapWeights(eventA);
        Map<Long, Integer> userMapB = userWeightsRepo.getUserMapWeights(eventB);

        return userMapA.entrySet().stream()
                .filter(e -> userMapB.get(e.getKey()) != null)
                .mapToDouble(e -> Math.min(e.getValue(), userMapB.get(e.getKey())))
                .sum();
    }
}