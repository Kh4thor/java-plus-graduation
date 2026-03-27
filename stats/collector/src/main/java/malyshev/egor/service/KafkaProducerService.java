package malyshev.egor.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import stats.avro.UserActionAvro;
import malyshev.egor.config.KafkaProperties;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, UserActionAvro> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void sendUserAction(UserActionAvro message) {
        String topic = kafkaProperties.getTopics().getUserActions();
        CompletableFuture<SendResult<String, UserActionAvro>> future = kafkaTemplate.send(topic, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Sent user action to Kafka: {}", message);
            } else {
                log.error("Failed to send user action to Kafka: {}", message, ex);
            }
        });
    }
}
