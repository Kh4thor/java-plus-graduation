package malyshev.egor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kafkaAnalyzer")
public class KafkaProperties {
    private Topics topics = new Topics();

    @Data
    public static class Topics {
        private String userActionsTopic = "stats.user-actions.v1";
        private String eventsSimilarityTopic = "stats.events-similarity.v1";
    }
}
