package malyshev.egor.ewm.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.client.props.ClientProperties;

@Configuration
@EnableConfigurationProperties(ClientProperties.class)
public class StatsClientConfig {

    @Bean
    public StatsClient statsClient(RestTemplateBuilder builder,
                                   ClientProperties props,
                                   @Value("${spring.application.name:ewm-service}") String appName) {
        return new StatsClient(builder, props, appName);
    }
}