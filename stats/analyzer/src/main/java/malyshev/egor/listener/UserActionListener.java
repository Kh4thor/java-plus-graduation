package malyshev.egor.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.service.UserActionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import stats.avro.UserActionAvro;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActionListener {

    private final UserActionService userActionService;

    @KafkaListener(
            topics = "#{kafkaProperties.topics.userActionsTopic}",
            containerFactory = "userActionKafkaListenerContainerFactory"
    )
    public void consumeUserAction(UserActionAvro action) {
        userActionService.process(action);
    }
}