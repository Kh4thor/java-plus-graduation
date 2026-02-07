package ru.practicum.ewm.service.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.compilation.dto.*;
import ru.practicum.ewm.service.compilation.exception.CompilationNotFoundException;
import ru.practicum.ewm.service.compilation.exception.TitleAlreadyExistsException;
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
public class CompilationAdminServiceImpl implements CompilationAdminService {

    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    // добавили зависимости
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto dto) {
        log.info("ADMIN: создание подборки: title={}", dto.getTitle());

        if (repository.existsByTitle(dto.getTitle())) {
            log.warn("Название уже существует: {}", dto.getTitle());
            throw new TitleAlreadyExistsException(dto.getTitle());
        }

        Compilation compilation = repository.save(CompilationMapper.toEntity(dto));
        List<EventShortDto> events = loadEventShortDtos(compilation);

        log.info("ADMIN: подборка создана id={}", compilation.getId());
        return CompilationMapper.toDto(compilation, events);
    }

    @Transactional
    @Override
    public void delete(Long compId) {
        log.info("ADMIN: удаление подборки id={}", compId);
        if (!repository.existsById(compId)) {
            log.warn("Подборка не найдена: id={}", compId);
            throw new CompilationNotFoundException(compId);
        }
        repository.deleteById(compId);
        log.info("ADMIN: подборка удалена id={}", compId);
    }

    @Transactional
    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        log.info("ADMIN: обновление подборки id={}", compId);

        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException(compId));

        if (dto.getTitle() != null && !dto.getTitle().equals(compilation.getTitle())) {
            if (repository.existsByTitle(dto.getTitle())) {
                throw new TitleAlreadyExistsException(dto.getTitle());
            }
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) compilation.setPinned(dto.getPinned());
        if (dto.getEvents() != null) compilation.setEvents(dto.getEvents());

        compilation = repository.save(compilation);
        List<EventShortDto> events = loadEventShortDtos(compilation);

        log.info("ADMIN: подборка обновлена id={}", compilation.getId());
        return CompilationMapper.toDto(compilation, events);
    }

    private List<EventShortDto> loadEventShortDtos(Compilation compilation) {
        Set<Long> eventIds = compilation.getEvents();
        if (eventIds == null || eventIds.isEmpty()) return List.of();

        return eventRepository.findAllById(eventIds).stream()
                .map(e -> {
                    long confirmed = requestRepository
                            .countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED);
                    long views = statsClient.viewsForEvent(e.getId());
                    return EventMapper.toShortDto(e, confirmed, views);
                })
                .toList();
    }
}
