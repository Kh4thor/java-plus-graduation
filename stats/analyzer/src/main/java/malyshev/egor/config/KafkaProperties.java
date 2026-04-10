package malyshev.egor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafka-analyzer")
public class KafkaProperties {
    private Topics topics = new Topics();

    @Data
    public static class Topics {
        private String userActionsTopic;
        private String eventsSimilarityTopic;
    }
}