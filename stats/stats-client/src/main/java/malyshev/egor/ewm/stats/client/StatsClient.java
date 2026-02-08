package malyshev.egor.ewm.stats.client;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.ewm.stats.client.props.ClientProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class StatsClient {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate rest;
    private final ClientProperties props;
    private final String appName; // откуда возьмём app

    public StatsClient(@NonNull RestTemplateBuilder builder,
                       @NonNull ClientProperties props,
                       @Value("${spring.application.name:ewm-service}") String appName) {
        this.props = props;
        this.appName = appName;
        this.rest = builder
                .rootUri(props.getBaseUrl())
                .setConnectTimeout(props.getConnectTimeout())
                .setReadTimeout(props.getReadTimeout())
                .build();
        log.debug("StatsClient initialized: baseUrl={}, connectTimeout={}, readTimeout={}, hitMaxAttempts={}, hitBackoff={}ms, app={}",
                props.getBaseUrl(), props.getConnectTimeout(), props.getReadTimeout(),
                props.getHitMaxAttempts(), props.getHitBackoffMillis(), appName);
    }

    public void hit(@NonNull String uri, @NonNull String ip) {
        // DTO обычно имеет LocalDateTime timestamp с @JsonFormat, значит передаём LDT
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
                // сервер возвращает 201 + json с записанным хит-DTO
                rest.postForEntity("/hit", dto, EndpointHitDto.class);
                log.info("Hit sent successfully: id={}, uri={}", dto.getId(), dto.getUri());
                if (log.isTraceEnabled()) {
                    log.trace("POST /hit sent: app={}, uri={}, ip={}, ts={}",
                            dto.getApp(), dto.getUri(), dto.getIp(), dto.getTimestamp());
                }
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
        // небольшой запас, чтобы свежий /hit точно попал в выборку

        StringBuilder qs = new StringBuilder()
                .append("/stats")
                .append("?start=").append(fmt(start))
                .append("&end=").append(fmt(end))
                .append("&unique=").append(unique);

        if (uris != null && !uris.isEmpty()) {
            for (String u : uris) {
                // здесь не кодируем: сервер нормально принимает /events/7
                qs.append("&uris=").append(u.trim());
            }
        }

        // rootUri уже задан в RestTemplateBuilder, так что относительный путь ок
        ResponseEntity<ViewStatsDto[]> resp = rest.getForEntity(qs.toString(), ViewStatsDto[].class);
        ViewStatsDto[] body = resp.getBody();
        return (body == null) ? Collections.emptyList() : Arrays.asList(body);
    }

    private void safeSleep(long millis) {
        try {
            if (millis > 0) Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("StatsClient: retry sleep interrupted; giving up retries.");
        }
    }

    // helper: "yyyy-MM-dd HH:mm:ss" -> "yyyy-MM-dd+HH:mm:ss"
    private static String fmt(LocalDateTime dt) {
        return FMT.format(dt).replace(' ', '+');
    }
}
