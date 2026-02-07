package ru.practicum.ewm.service.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.compilation.dto.CompilationDto;
import ru.practicum.ewm.service.compilation.exception.CompilationNotFoundException;
import ru.practicum.ewm.service.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.service.compilation.model.Compilation;
import ru.practicum.ewm.service.compilation.repository.CompilationRepository;
import ru.practicum.ewm.service.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.mapper.EventMapper;
import ru.practicum.ewm.service.event.repository.EventRepository;
import ru.practicum.ewm.service.request.model.RequestStatus;
import ru.practicum.ewm.service.request.repository.RequestRepository;
import ru.practicum.ewm.stats.client.StatsClient;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationPublicServiceImpl implements CompilationPublicService {

    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    // добавили зависимости для счётчиков
    private final RequestRepository requestRepository;
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
        List<EventShortDto> events = eventRepository.findAllById(eventIds).stream()
                .map(e -> {
                    long confirmed = requestRepository
                            .countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED);
                    long views = statsClient.viewsForEvent(e.getId());
                    return EventMapper.toShortDto(e, confirmed, views);
                })
                .toList();
        return CompilationMapper.toDto(compilation, events);
    }
}
