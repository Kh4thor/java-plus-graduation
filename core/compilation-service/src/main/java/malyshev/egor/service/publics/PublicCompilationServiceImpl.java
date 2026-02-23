package malyshev.egor.service.publics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.dto.compilation.CompilationDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.ewm.stats.client.StatsClient;
import malyshev.egor.exception.CompilationNotFoundException;
import malyshev.egor.mapper.CompilationMapper;
import malyshev.egor.model.Compilation;
import malyshev.egor.repository.CompilationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository repository;
    private final InteractionApiManager interactionApiManager;

    // добавили зависимости для счётчиков
    private final StatsClient statsClient;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("PUBLIC: запрос подборок: pinned={}, from={}, size={}", pinned, from, size);
        var pageable = PageRequest.of(from / size, size);

        List<Compilation> compilations = (pinned == null)
                ? repository.findAll(pageable).getContent()
                : repository.findByPinned(pinned, pageable);

        return compilations.stream()
                .map(this::mapCompilationWithEvents)
                .toList();
    }

    @Override
    public CompilationDto getById(Long compId) {
        log.info("PUBLIC: запрос подборки id={}", compId);
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(compId));
        return mapCompilationWithEvents(compilation);
    }

    private CompilationDto mapCompilationWithEvents(Compilation compilation) {
        Set<Long> eventIds = compilation.getEvents();
        if (eventIds == null || eventIds.isEmpty()) {
            return CompilationMapper.toDto(compilation, List.of());
        }
        String uri = String.format("/compilations/%d", compilation.getId());
        List<EventShortDto> all = interactionApiManager.getAllEventsByPublic(uri);
        List<EventShortDto> filteredById = all.stream()
                .filter(e -> eventIds.contains(e.getId()))
                .toList();

        // Дополняем каждое DTO актуальными просмотрами из сервиса статистики
        for (EventShortDto dto : filteredById) {
            long views = statsClient.viewsForEvent(dto.getId());
            dto.setViews(views);
        }
        return CompilationMapper.toDto(compilation, filteredById);
    }
}
