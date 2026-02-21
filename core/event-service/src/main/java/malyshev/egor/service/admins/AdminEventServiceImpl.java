package malyshev.egor.service.admins;

import lombok.RequiredArgsConstructor;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.dto.event.*;
import malyshev.egor.ewm.stats.client.StatsClient;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.EventMapper;
import malyshev.egor.model.category.Category;
import malyshev.egor.model.event.Event;
import malyshev.egor.model.event.EventState;
import malyshev.egor.model.event.Location;
import malyshev.egor.model.request.RequestStatus;
import malyshev.egor.repository.EventRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final InteractionApiManager interactionApiManager;

    // форматтеры для строгого парсинга
    private static final DateTimeFormatter F_SPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter F_T = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    @Override
    public List<EventFullDto> adminSearch(List<Long> users,
                                          List<String> states,
                                          List<Long> categories,
                                          String rangeStart,
                                          String rangeEnd,
                                          Pageable pageable) {
        // строгая валидация диапазона дат
        LocalDateTime start = parseStrict(rangeStart);
        LocalDateTime end = parseStrict(rangeEnd);
        validateRangeOrThrow(start, end);

        // говняное решение
        var all = eventRepository.findAll().stream()
                .filter(e -> users == null || users.contains(e.getInitiator().getId()))
                .filter(e -> states == null || states.contains(e.getState().name()))
                .filter(e -> categories == null || categories.contains(e.getCategory().getId()))
                .filter(e -> start == null || !e.getEventDate().isBefore(start))
                .filter(e -> end == null || !e.getEventDate().isAfter(end))
                .sorted(Comparator.comparing(Event::getEventDate))
                .toList();

        int from = (int) pageable.getOffset();
        int size = pageable.getPageSize();

        return all.stream()
                .skip(from)
                .limit(size)
                .map(e -> EventMapper.toFullDto(e, interactionApiManager.adminCountByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED), statsClient.viewsForEvent(e.getId())))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequest dto) {
        var e = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (dto.getAnnotation() != null) {
            e.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {
            Category c = interactionApiManager.publicGetCategoryById(dto.getCategory());
            e.setCategory(c);
        }

        if (dto.getDescription() != null) {
            e.setDescription(dto.getDescription());
        }

        if (dto.getEventDate() != null) {
            if (!dto.getEventDate().isAfter(LocalDateTime.now())) {
                throw new IllegalArgumentException("Event date must be in the future");
            }
            e.setEventDate(dto.getEventDate());
        }

        if (dto.getLocation() != null) {
            e.setLocation(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()));
        }

        if (dto.getPaid() != null) {
            e.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            if (dto.getParticipantLimit() < 0) {
                throw new IllegalArgumentException("participantLimit must be >= 0");
            }
            e.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            e.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            e.setTitle(dto.getTitle());
        }

        if ("PUBLISH_EVENT".equalsIgnoreCase(dto.getStateAction())) {
            if (e.getState() != EventState.PENDING) {
                throw new IllegalStateException("Cannot publish the event because it's not in the right state: " + e.getState());
            }
            var pubTime = LocalDateTime.now();
            if (e.getEventDate().isBefore(pubTime.plusHours(1))) {
                throw new IllegalStateException("Event date must be at least 1 hour after publication");
            }
            e.setPublishedOn(pubTime);
            e.setState(EventState.PUBLISHED);

        } else if ("REJECT_EVENT".equalsIgnoreCase(dto.getStateAction())) {
            if (e.getState() == EventState.PUBLISHED) {
                throw new IllegalStateException("Cannot reject the event because it's already published");
            }
            e.setState(EventState.CANCELED);
        }
        return getEventFullDto(e);
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

    private int countConfirmedRequests(Long eventId) {
        return interactionApiManager.adminCountByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private EventFullDto getEventFullDto(Event e) {
        return EventMapper.toFullDto(e, countConfirmedRequests(e.getId()), statsClient.viewsForEvent(e.getId()));
    }
}
