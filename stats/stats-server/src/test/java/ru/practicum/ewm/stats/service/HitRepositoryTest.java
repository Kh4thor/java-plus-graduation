package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.service.model.EndpointHit;
import ru.practicum.ewm.stats.service.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class HitRepositoryTest {
    private final HitRepository hitRepository;

    private EndpointHit firstHit;
    private EndpointHit secondHit;
    private EndpointHit thirdHit;
    private EndpointHit fourthHit;
    private EndpointHit fifthHit;

    private LocalDateTime start;
    private LocalDateTime end;

    @Test
    void getStatsShouldReturnCorrectList() {
        initialize();

        List<ViewStatsDto> result = hitRepository.getStats(start, end);
        ViewStatsDto resultsFirst = result.getFirst();

        assertThat(result, hasSize(2));
        assertThat(resultsFirst, notNullValue());
        assertThat(resultsFirst.getApp(), equalTo(firstHit.getApp()));
        assertThat(resultsFirst.getUri(), equalTo(firstHit.getUri()));
        assertThat(resultsFirst.getHits(), equalTo(3L));
    }

    @Test
    void getUniqueStatsShouldReturnCorrectListWithUniqueIp() {
        initialize();

        List<ViewStatsDto> result = hitRepository.getUniqueStats(start, end);
        ViewStatsDto resultsFirst = result.getFirst();

        assertThat(result, hasSize(2));
        assertThat(resultsFirst, notNullValue());
        assertThat(resultsFirst.getApp(), equalTo(firstHit.getApp()));
        assertThat(resultsFirst.getUri(), equalTo(firstHit.getUri()));
        assertThat(resultsFirst.getHits(), equalTo(2L));
    }

    @Test
    void getStatsByUrisShouldReturnCorrectList() {
        initialize();

        List<ViewStatsDto> result = hitRepository.getStatsByUris(start, end, List.of("/someUri/1"));
        ViewStatsDto resultsFirst = result.getFirst();

        assertThat(result, hasSize(1));
        assertThat(resultsFirst, notNullValue());
        assertThat(resultsFirst.getApp(), equalTo(fourthHit.getApp()));
        assertThat(resultsFirst.getUri(), equalTo(fourthHit.getUri()));
        assertThat(resultsFirst.getHits(), equalTo(1L));
    }

    @Test
    void getUniqueStatsByUrisShouldReturnCorrectListWithUniqueIp() {
        initialize();

        List<ViewStatsDto> result = hitRepository.getUniqueStatsByUris(start, end, List.of("/events/1"));
        ViewStatsDto resultsFirst = result.getFirst();

        assertThat(result, hasSize(1));
        assertThat(resultsFirst, notNullValue());
        assertThat(resultsFirst.getApp(), equalTo(firstHit.getApp()));
        assertThat(resultsFirst.getUri(), equalTo(firstHit.getUri()));
        assertThat(resultsFirst.getHits(), equalTo(2L));
    }

    private void initialize() {
        firstHit = new EndpointHit();
        firstHit.setApp("ewm-main-service");
        firstHit.setUri("/events/1");
        firstHit.setIp("10.10.10.10");
        firstHit.setHitTimestamp(LocalDateTime.of(2025, 10, 1, 12, 20));
        firstHit = hitRepository.save(firstHit);

        secondHit = new EndpointHit();
        secondHit.setApp("ewm-main-service");
        secondHit.setUri("/events/1");
        secondHit.setIp("10.10.10.11");
        secondHit.setHitTimestamp(LocalDateTime.of(2025, 10, 1, 13, 20));
        secondHit = hitRepository.save(secondHit);

        thirdHit = new EndpointHit();
        thirdHit.setApp("ewm-main-service");
        thirdHit.setUri("/events/1");
        thirdHit.setIp("10.10.10.11");
        thirdHit.setHitTimestamp(LocalDateTime.of(2025, 10, 1, 13, 30));
        thirdHit = hitRepository.save(thirdHit);

        fourthHit = new EndpointHit();
        fourthHit.setApp("ewm-main-service");
        fourthHit.setUri("/someUri/1");
        fourthHit.setIp("10.10.10.12");
        fourthHit.setHitTimestamp(LocalDateTime.of(2025, 10, 1, 13, 40));
        fourthHit = hitRepository.save(fourthHit);

        fifthHit = new EndpointHit();
        fifthHit.setApp("ewm-main-service");
        fifthHit.setUri("/someUri/1");
        fifthHit.setIp("10.10.10.13");
        fifthHit.setHitTimestamp(LocalDateTime.of(2025, 10, 1, 13, 50));
        fifthHit = hitRepository.save(fifthHit);

        start = LocalDateTime.of(2025, 10, 1, 12, 10);
        end = LocalDateTime.of(2025, 10, 1, 13, 45);
    }
}
