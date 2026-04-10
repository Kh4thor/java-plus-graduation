package malyshev.egor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.model.UserAction;
import malyshev.egor.repository.UserActionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionService {

    private static final double WEIGHT_VIEW = 0.4;
    private static final double WEIGHT_REGISTER = 0.8;
    private static final double WEIGHT_LIKE = 1;

    private final UserActionRepository userActionRepository;

    @Transactional
    public void process(UserActionAvro action) {
        long userId = action.getUserId();
        long eventId = action.getEventId();
        double newWeight = getWeight(action.getActionType());
        Instant timestamp = Instant.ofEpochMilli(action.getTimestamp());

        Optional<UserAction> existing = userActionRepository.findByUserIdAndEventId(userId, eventId);
        if (existing.isPresent()) {
            UserAction userAction = existing.get();
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

    private double getWeight(stats.avro.ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> WEIGHT_VIEW;
            case REGISTER -> WEIGHT_REGISTER;
            case LIKE -> WEIGHT_LIKE;
        };
    }
}