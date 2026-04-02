package malyshev.egor.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import stats.avro.UserActionAvro;
import malyshev.egor.service.SimilarityService;

@Profile("docker")
@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionListener {

    private final SimilarityService similarityService;

    @KafkaListener(topics = "#{kafkaProperties.consumer.topic}")
    public void consumeUserAction(UserActionAvro action) {
        log.info("Received user action: {}", action);
        similarityService.processUserAction(action);
    }
}
