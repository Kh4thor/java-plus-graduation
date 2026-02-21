package malyshev.egor.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.compilation.dto.CompilationDto;
import malyshev.egor.compilation.dto.NewCompilationDto;
import malyshev.egor.compilation.dto.UpdateCompilationRequest;
import malyshev.egor.compilation.exception.CompilationNotFoundException;
import malyshev.egor.compilation.exception.TitleAlreadyExistsException;
import malyshev.egor.compilation.mapper.CompilationMapper;
import malyshev.egor.compilation.model.Compilation;
import malyshev.egor.compilation.repository.CompilationRepository;
import malyshev.egor.event.dto.EventShortDto;
import malyshev.egor.event.mapper.EventMapper;
import malyshev.egor.event.repository.EventRepository;
import malyshev.egor.ewm.stats.client.StatsClient;
import malyshev.egor.request.model.RequestStatus;
import malyshev.egor.request.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
