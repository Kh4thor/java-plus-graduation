package malyshev.egor.service.privates;

import lombok.RequiredArgsConstructor;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.dto.event.*;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.EventMapper;
import malyshev.egor.mapper.LocationMapper;
import malyshev.egor.model.category.Category;
import malyshev.egor.model.event.Event;
import malyshev.egor.model.event.EventState;
import malyshev.egor.model.event.Location;
import malyshev.egor.model.request.RequestStatus;
import malyshev.egor.model.user.User;
import malyshev.egor.repository.EventRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final InteractionApiManager interactionApiManager;

    // форматтеры для строгого парсинга
    private static final DateTimeFormatter F_SPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter F_T = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // PRIVATE
    @Override
    public List<EventShortDto> getUserEvents(Long userId, Pageable pageable) {

        User user = interactionApiManager.getUserById(userId);

        var events = eventRepository.findAllByInitiator_Id(userId).stream()
                .sorted(Comparator.comparing(Event::getCreatedOn).reversed())
                .toList();

        int from = (int) pageable.getOffset();
        int size = pageable.getPageSize();

        return events.stream()
                .skip(from)
                .limit(size)
                .map(e -> EventMapper.toShortDto(e, countConfirmedRequests(e.getId()),
                        statsClient.viewsForEvent(e.getId())))
                .toList();
    }
    // PRIVATE
    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto dto) {

        User initiator = interactionApiManager.getUserById(userId);
        Category category = interactionApiManager.getCategoryById(dto.getCategory());
        Location location = LocationMapper.toLocation(dto.getLocation());

        // дата минимум +2 часа от «сейчас»
        if (dto.getEventDate() == null || dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IllegalArgumentException("Event date must be at least 2 hours in the future");
        }
        // запрет отрицательного лимита
        if (dto.getParticipantLimit() < 0) {
            throw new IllegalArgumentException("participantLimit must be >= 0");
        }

        var e = Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .initiator(initiator)
                .description(dto.getDescription())
                .location(location)
                .paid(dto.isPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.isRequestModeration())
                .eventDate(dto.getEventDate())
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .title(dto.getTitle())
                .build();

        e = eventRepository.save(e);
        return EventMapper.toFullDto(e, 0L, 0L);
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        var e = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!e.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        return EventMapper.toFullDto(e,
                countConfirmedRequests(e.getId()),
                statsClient.viewsForEvent(e.getId()));
    }
    // PRIVATE
    @Override
    @Transactional
    public EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        var e = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found"));


        if (!e.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        if (e.getState() == EventState.PUBLISHED) {
            throw new IllegalStateException("Only pending or canceled events can be changed");
        }

        if (dto.getAnnotation() != null) {
            e.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {
            Category c = interactionApiManager.getCategoryById(dto.getCategory());
            e.setCategory(c);
        }

        if (dto.getDescription() != null)
            e.setDescription(dto.getDescription());

        if (dto.getEventDate() != null) {
            LocalDateTime newDate = dto.getEventDate();
            if (!newDate.isAfter(LocalDateTime.now().plusHours(2))) {
                throw new IllegalArgumentException("Event date must be at least 2 hours in the future");
            }
            e.setEventDate(newDate);
        }

        if (dto.getLocation() != null) {
            e.setLocation(LocationMapper.toLocation(dto.getLocation()));
        }

        if (dto.getPaid() != null)

            e.setPaid(dto.getPaid());

        if (dto.getParticipantLimit() != null) {
            if (dto.getParticipantLimit() < 0) {
                throw new IllegalArgumentException("participantLimit must be >= 0");
            }
            e.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) e.setRequestModeration(dto.getRequestModeration());
        if (dto.getTitle() != null) e.setTitle(dto.getTitle());

        if ("SEND_TO_REVIEW".equalsIgnoreCase(dto.getStateAction())) {
            e.setState(EventState.PENDING);
        }
        if ("CANCEL_REVIEW".equalsIgnoreCase(dto.getStateAction())) {
            e.setState(EventState.CANCELED);
        }

        e = eventRepository.save(e);
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

    private int countConfirmedRequests(Long eventId) {
        return interactionApiManager.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private EventFullDto getEventFullDto(Event e) {
        return EventMapper.toFullDto(e, countConfirmedRequests(e.getId()), statsClient.viewsForEvent(e.getId()));
    }
}
