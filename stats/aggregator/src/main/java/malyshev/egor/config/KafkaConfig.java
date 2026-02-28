package malyshev.egor.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Properties;

@Getter
@ConfigurationProperties("aggregator.kafka")
public class KafkaConfig {
    private final ProducerConfig producer;
    private final ConsumerConfig consumer;

    public KafkaConfig(ProducerConfig producer, ConsumerConfig consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    @Setter @Getter
    @AllArgsConstructor
    public static class ProducerConfig {
        private String topic;
        private Properties properties;
    }

    @Setter @Getter
    @AllArgsConstructor
    public static class ConsumerConfig {
        private String topic;
        private Duration pollTimeout;
        private Properties properties;
    }
}