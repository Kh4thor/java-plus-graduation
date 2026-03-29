package malyshev.egor.service.publics;

import lombok.RequiredArgsConstructor;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.client.GrpcAnalyzerClient;
import malyshev.egor.client.GrpcCollectorClient;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.EventMapper;
import malyshev.egor.model.Event;
import malyshev.egor.repository.EventRepository;
import malyshev.egor.service.admins.AdminEventService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static malyshev.egor.dto.event.EventState.PUBLISHED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final GrpcCollectorClient collectorGrpcClient;
    private final GrpcAnalyzerClient analyzerGrpcClient;
    private final AdminEventService adminEventService;
    private final EventMapper eventMapper;
    private final InteractionApiManager interactionApiManager;

    // форматтеры для строгого парсинга
    private static final DateTimeFormatter F_SPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter F_T = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // PUBLIC
    @Override
    @Transactional
    public List<EventShortDto> publicSearch(String text, List<Long> categories, Boolean paid,
                                            String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                            String sort, Pageable pageable) {
        // строгая валидация диапазона дат
        LocalDateTime start = parseStrict(rangeStart);   // 400 если формат некорректный
        LocalDateTime end = parseStrict(rangeEnd);     // 400 если формат некорректный
        validateRangeOrThrow(start, end);                // 400 если end < start

        // только опубликованные
        Specification<Event> spec = (root, q, cb)
                -> cb.equal(root.get("state"), PUBLISHED);

        if (text != null && !text.isBlank()) {
            String pattern = "%" + text.toLowerCase() + "%";
            spec = spec.and((root, q, cb)
                    -> cb.or(cb.like(cb.lower(root.get("annotation")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)));
        }
        if (paid != null) {
            spec = spec.and((root, q, cb)
                    -> cb.equal(root.get("paid"), paid));
        }
        if (categories != null && !categories.isEmpty()) {
            spec = spec.and((root, q, cb)
                    -> root.get("category").in(categories));
        }
        if (start != null) {
            spec = spec.and((root, q, cb)
                    -> cb.greaterThanOrEqualTo(root.get("eventDate"), start));
        }
        if (end != null) {
            spec = spec.and((root, q, cb)
                    -> cb.lessThanOrEqualTo(root.get("eventDate"), end));
        }

        // сортировка по дате (по умолчанию)
        var page = eventRepository.findAll(
                spec,
                PageRequest.of((int) (pageable.getOffset() / pageable.getPageSize()),
                        pageable.getPageSize(),
                        Sort.by("eventDate").ascending())
        );
        return page.getContent().stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    // PUBLIC
    @Transactional
    @Override
    public EventFullDto publicGet(Long eventId, Long userId) {
        var e = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found")
        );

        if (e.getState() != PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        // отправляем просмотр в Collector
        collectorGrpcClient.sendView(userId, eventId);

        // получаем рейтинг от Analyzer
        double rating = analyzerGrpcClient.getEventRating(eventId);

        EventFullDto dto = eventMapper.toFullDto(e);
        dto.setRating(rating);

        return dto;
    }

    /**
     * Парсит строго. Если строка присутствует, но формат неверный — кидаем 400. Если null/blank — возвращаем null.
     */
    private LocalDateTime parseStrict(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            // поддерживаем оба часто встречающихся формата
            return (s.indexOf('T') >= 0) ? LocalDateTime.parse(s, F_T) : LocalDateTime.parse(s, F_SPACE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must match 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd'T'HH:mm:ss': " + s);
        }
    }

    /**
     * Если оба конца заданы и end < start — 400.
     */
    private void validateRangeOrThrow(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("rangeEnd must be after rangeStart");
        }
    }
}