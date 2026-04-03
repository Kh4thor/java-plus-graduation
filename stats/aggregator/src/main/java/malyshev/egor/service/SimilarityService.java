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
//@Profile("docker")
@Service
public class SimilarityService {

    private static final double WEIGHT_VIEW = 0.4;
    private static final double WEIGHT_REGISTER = 0.8;
    private static final double WEIGHT_LIKE = 1;

    private final KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final InMemoryMinWeightsSumRepository minWeightsSumRepo;
    private final InMemoryEventUserWeightsRepository userWeightsRepo;
    private final InMemoryEventTotalWeightRepository totalWeightsRepo;
    private final InMemoryUserEventsRepository userEventsRepo;

    public SimilarityService(KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate, KafkaProperties kafkaProperties, InMemoryMinWeightsSumRepository minWeightsSumRepo, InMemoryEventUserWeightsRepository userWeightsRepo, InMemoryEventTotalWeightRepository totalWeightsRepo, InMemoryUserEventsRepository userEventsRepo) {
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

        double oldWeight = userWeightsRepo.getWeight(eventA, userId);
        if (newWeight <= oldWeight) {
            log.debug("Пропускаем: вес не увеличился для userId={}, eventId={}", userId, eventA);
            return;
        }

        // Обновление максимального веса реакции пользователя на событие
        userWeightsRepo.setWeight(eventA, userId, newWeight);
        double diff = newWeight - oldWeight;
        totalWeightsRepo.addDiff(eventA, diff);

        // Если это первое взаимодействие пользователя с событием, запоминаем
        if (oldWeight == 0) {
            userEventsRepo.addEvent(userId, eventA);
        }

        // Получаем все события, с которыми пользователь уже взаимодействовал (кроме текущего)
        Set<Long> otherEvents = userEventsRepo.getEventsByUser(userId);
        for (Long eventB : otherEvents) {
            if (eventB.equals(eventA)) continue;

            double weightB = userWeightsRepo.getWeight(eventB, userId); // всегда > 0
            double oldContribution = Math.min(oldWeight, weightB);
            double newContribution = Math.min(newWeight, weightB);
            double delta = newContribution - oldContribution;

            if (delta != 0) {
                // Обновляем S_min для пары (eventA, eventB)
                minWeightsSumRepo.addToSum(eventA, eventB, delta);

                // Пересчитываем сходство
                double totalWeightEventA = totalWeightsRepo.getTotalWeightByEventId(eventA);
                double totalWeightEventB = totalWeightsRepo.getTotalWeightByEventId(eventB);
                double sMin = minWeightsSumRepo.getSum(eventA, eventB);

                double similarity = (totalWeightEventA > 0 && totalWeightEventB > 0) ? sMin / Math.sqrt(totalWeightEventA * totalWeightEventB) : 0.0;

                // Отправляем в Kafka (упорядочиваем идентификаторы)
                long first = Math.min(eventA, eventB);
                long second = Math.max(eventA, eventB);
                EventSimilarityAvro similarityMsg = EventSimilarityAvro.newBuilder().setEventA(first).setEventB(second).setScore((float) similarity).setTimestamp(timestamp).build();

                kafkaTemplate.send(kafkaProperties.getProducer().getTopic(), similarityMsg).whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Отправлено сходство для ({}, {}): {}", first, second, similarity);
                    } else {
                        log.error("Ошибка отправки сходства для ({}, {})", first, second, ex);
                    }
                });
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