package malyshev.egor.ewm.stats.client;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.ewm.stats.client.props.ClientProperties;
import malyshev.egor.ewm.stats.dto.EndpointHitDto;
import malyshev.egor.ewm.stats.dto.ViewStatsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class StatsClient {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final ClientProperties props;
    private final String appName;

    // Теперь инжектим RestTemplate, а не RestTemplateBuilder
    public StatsClient(@LoadBalanced RestTemplate restTemplate,
                       ClientProperties props,
                       @Value("${spring.application.name:ewm-service}") String appName) {
        this.restTemplate = restTemplate;
        this.props = props;
        this.appName = appName;
        log.debug("StatsClient initialized with load-balanced RestTemplate");
    }

    public void hit(@NonNull String uri, @NonNull String ip) {
        EndpointHitDto dto = new EndpointHitDto();
        dto.setApp(appName);
        dto.setUri(uri.trim());
        dto.setIp(ip);
        dto.setTimestamp(LocalDateTime.now());
        hit(dto);
    }

    public void hit(@NonNull EndpointHitDto dto) {
        int attempt = 1;
        final int max = props.getHitMaxAttempts();
        final long baseBackoff = props.getHitBackoffMillis();
        final long cap = props.getHitBackoffCapMillis();

        while (true) {
            try {
                // Вместо относительного пути используем полный URL с именем сервиса
                restTemplate.postForEntity("http://stats-server/hit", dto, EndpointHitDto.class);
                log.info("Hit sent successfully: id={}, uri={}", dto.getId(), dto.getUri());
                return;
            } catch (RestClientException ex) {
                if (attempt >= max) {
                    log.warn("StatsClient: POST /hit failed after {} attempt(s). Continue without stats. reason={}",
                            attempt, ex.toString(), ex);
                    return;
                }
                long delay = baseBackoff * (1L << (attempt - 1));
                if (delay > cap) delay = cap;
                log.info("StatsClient: POST /hit failed on attempt {}/{}. Retry in {} ms. reason={}",
                        attempt, max, delay, ex.toString());
                safeSleep(delay);
                attempt++;
            } catch (RuntimeException ex) {
                log.info("StatsClient: unexpected error on POST /hit. Continue without stats. {}", ex.toString(), ex);
                return;
            }
        }
    }

    public long viewsForEvent(@NonNull Long eventId) {
        return viewsForUri("/events/" + eventId, true);
    }

    public long viewsForUri(@NonNull String uri, boolean unique) {
        try {
            List<ViewStatsDto> list = stats(
                    LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                    LocalDateTime.now(),
                    List.of(uri.trim()),
                    unique
            );
            return list.stream()
                    .filter(v -> uri.trim().equals(v.getUri()))
                    .mapToLong(ViewStatsDto::getHits)
                    .sum();
        } catch (Exception e) {
            log.warn("Stats unavailable for {}: {}", uri, e.toString());
            return 0L;
        }
    }

    public List<ViewStatsDto> stats(@NonNull LocalDateTime start,
                                    @NonNull LocalDateTime end,
                                    List<String> uris,
                                    boolean unique) {
        StringBuilder qs = new StringBuilder()
                .append("/stats")
                .append("?start=").append(fmt(start))
                .append("&end=").append(fmt(end))
                .append("&unique=").append(unique);

        if (uris != null && !uris.isEmpty()) {
            for (String u : uris) {
                qs.append("&uris=").append(u.trim());
            }
        }

        // Используем имя сервиса в URL
        String url = "http://stats-server" + qs.toString();
        ViewStatsDto[] resp = restTemplate.getForObject(url, ViewStatsDto[].class);
        return (resp == null) ? Collections.emptyList() : Arrays.asList(resp);
    }

    private void safeSleep(long millis) {
        try {
            if (millis > 0) Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("StatsClient: retry sleep interrupted; giving up retries.");
        }
    }

    private static String fmt(LocalDateTime dt) {
        return FMT.format(dt).replace(' ', '+');
    }
}