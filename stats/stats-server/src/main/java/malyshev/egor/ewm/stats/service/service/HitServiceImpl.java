package malyshev.egor.ewm.stats.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import malyshev.egor.ewm.stats.dto.EndpointHitDto;
import malyshev.egor.ewm.stats.dto.ViewStatsDto;
import malyshev.egor.ewm.stats.service.mapper.HitMapper;
import malyshev.egor.ewm.stats.service.model.EndpointHit;
import malyshev.egor.ewm.stats.service.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Transactional
    @Override
    public EndpointHitDto create(EndpointHitDto hitDto) {
        EndpointHit entity = HitMapper.toEndpointHit(hitDto);
        if (entity.getUri() != null) {
            entity.setUri(entity.getUri().trim());
        }
        entity = hitRepository.save(entity);
        log.info("Saved hit with id={}", entity.getId());
        return HitMapper.toDto(entity); // 201 + json тело в контроллере
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> viewStats(LocalDateTime start,
                                        LocalDateTime end,
                                        List<String> uris,
                                        boolean unique) {
        // Валидируем ЗДЕСЬ, чтобы юнит-тесты (которые идут в сервис) видели IllegalArgumentException
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end,   "end must not be null");

        if (start.isEqual(end)) {
            throw new IllegalArgumentException("start and end must be different");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start must be before end");
        }

        List<ViewStatsDto> result;
        if (uris != null && !uris.isEmpty()) {
            result = unique
                    ? hitRepository.getUniqueStatsByUris(start, end, uris)
                    : hitRepository.getStatsByUris(start, end, uris);
        } else {
            result = unique
                    ? hitRepository.getUniqueStats(start, end)
                    : hitRepository.getStats(start, end);
        }

        log.info("Query returned {} results", result.size());

        if (result == null || result.isEmpty()) {
            return List.of();
        }
        result.sort(Comparator.comparingLong(ViewStatsDto::getHits).reversed());

        long totalHits = hitRepository.count();
        log.info("Total hits in DB: {}", totalHits);
        return result;
    }
}
