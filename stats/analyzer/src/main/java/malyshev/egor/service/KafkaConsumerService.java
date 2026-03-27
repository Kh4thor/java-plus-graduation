package malyshev.egor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.model.EventSimilarity;
import malyshev.egor.model.UserAction;
import malyshev.egor.repository.EventSimilarityRepository;
import malyshev.egor.repository.UserActionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import stats.avro.ActionTypeAvro;
import stats.avro.EventSimilarityAvro;
import stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final UserActionRepository userActionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    private static final int WEIGHT_VIEW = 1;
    private static final int WEIGHT_REGISTER = 2;
    private static final int WEIGHT_LIKE = 3;

    @KafkaListener(topics = "#{kafkaProperties.topics.userActions}", containerFactory = "userActionKafkaListenerContainerFactory")
    public void consumeUserAction(UserActionAvro action) {
        log.debug("Received user action: {}", action);

        long userId = action.getUserId();
        long eventId = action.getEventId();
        int newWeight = getWeight(action.getActionType());
        Instant timestamp = Instant.ofEpochMilli(action.getTimestamp());

        Optional<UserAction> userActionOpt = userActionRepository.findByUserIdAndEventId(userId, eventId);

        if (userActionOpt.isPresent()) {
            UserAction userAction = userActionOpt.get();
            if (newWeight > userAction.getWeight()) {
                userAction.setWeight(newWeight);
                userAction.setLastInteraction(timestamp);
                userActionRepository.save(userAction);
                log.debug("Updated weight for user {} event {} to {}", userId, eventId, newWeight);
            } else {
                userAction.setLastInteraction(timestamp);
                userActionRepository.save(userAction);
                log.debug("Updated timestamp for user {} event {}", userId, eventId);
            }
        } else {
            UserAction newAction = UserAction.builder()
                    .userId(userId)
                    .eventId(eventId)
                    .weight(newWeight)
                    .lastInteraction(timestamp)
                    .build();
            userActionRepository.save(newAction);
            log.debug("Created new interaction for user {} event {} weight {}", userId, eventId, newWeight);
        }
    }

    @KafkaListener(topics = "#{kafkaProperties.topics.eventsSimilarity}", containerFactory = "eventSimilarityKafkaListenerContainerFactory")
    public void consumeEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        log.debug("Received event similarity: {}", eventSimilarityAvro);

        long eventA = eventSimilarityAvro.getEventA();
        long eventB = eventSimilarityAvro.getEventB();
        double score = eventSimilarityAvro.getScore();
        Instant timestamp = eventSimilarityAvro.getTimestamp();

        Optional<EventSimilarity> eventSimilarityOpt = eventSimilarityRepository.findByEventAAndEventB(eventA, eventB);

        if (eventSimilarityOpt.isPresent()) {
            EventSimilarity eventSimilarity = eventSimilarityOpt.get();
            eventSimilarity.setSimilarity(score);
            eventSimilarity.setTimestamp(timestamp);
            eventSimilarityRepository.save(eventSimilarity);
            log.debug("Updated similarity for ({},{}) to {}", eventA, eventB, score);
        } else {
            EventSimilarity newSimilarity = EventSimilarity.builder()
                    .eventA(eventA)
                    .eventB(eventB)
                    .similarity(score)
                    .timestamp(timestamp)
                    .build();
            eventSimilarityRepository.save(newSimilarity);
            log.debug("Created new similarity for ({},{}) = {}", eventA, eventB, score);
        }
    }

    private int getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> WEIGHT_VIEW;
            case REGISTER -> WEIGHT_REGISTER;
            case LIKE -> WEIGHT_LIKE;
            default -> throw new IllegalArgumentException("Unknown action type: " + actionType);
        };
    }
}
