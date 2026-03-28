package malyshev.egor.service.admins;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.client.GrpcAnalyzerClient; // новый импорт
import malyshev.egor.dto.compilation.CompilationDto;
import malyshev.egor.dto.compilation.NewCompilationDto;
import malyshev.egor.dto.compilation.UpdateCompilationRequest;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.exception.CompilationNotFoundException;
import malyshev.egor.exception.TitleAlreadyExistsException;
import malyshev.egor.mapper.CompilationMapper;
import malyshev.egor.model.Compilation;
import malyshev.egor.repository.CompilationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository repository;
    private final InteractionApiManager interactionApiManager;
    private final GrpcAnalyzerClient analyzerClient;  // вместо StatsClient

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
        List<EventShortDto> all = interactionApiManager.getEventsByIds(compilation.getEvents());
        List<EventShortDto> filteredById = all.stream()
                .filter(e -> eventIds.contains(e.getId()))
                .collect(Collectors.toList());

        // Получаем рейтинг через gRPC-клиент Analyzer
        for (EventShortDto dto : filteredById) {
            double rating = analyzerClient.getEventRating(dto.getId());
            dto.setRating(rating);
        }
        return filteredById;
    }
}