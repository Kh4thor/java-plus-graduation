package malyshev.egor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka-aggregator")
public class KafkaProperties {
    private Consumer consumer = new Consumer();
    private Producer producer = new Producer();

    @Data
    public static class Consumer {
        private String groupId;
        private String topic;
    }

    @Data
    public static class Producer {
        private String topic;
    }
}