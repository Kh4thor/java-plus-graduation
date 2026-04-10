package malyshev.egor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.config.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import stats.avro.UserActionAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, UserActionAvro> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void sendUserAction(UserActionAvro userActionAvro) {
        String topic = kafkaProperties.getTopics().getUserActions();
        kafkaTemplate.send(topic, userActionAvro)
                .whenComplete((result, exception) -> {
                    if (exception == null) log.debug("Sent: {}", userActionAvro);
                    else log.error("Failed to send: {}", userActionAvro, exception);
                });
    }
}