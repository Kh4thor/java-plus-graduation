package malyshev.egor.service.privates;

import lombok.RequiredArgsConstructor;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.event.*;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.ewm.stats.client.StatsClient;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.EventMapper;
import malyshev.egor.mapper.LocationMapper;
import malyshev.egor.model.Event;
import malyshev.egor.model.Location;
import malyshev.egor.repository.EventRepository;
import malyshev.egor.service.admins.AdminEventService;
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
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final AdminEventService adminEventService;
    private final EventMapper eventMapper;
    private final InteractionApiManager interactionApiManager;

    // форматтеры для строгого парсинга
    private static final DateTimeFormatter F_SPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter F_T = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // PRIVATE
    @Override
    public List<EventShortDto> getUserEvents(Long userId, Pageable pageable) {
        interactionApiManager.getUserByAdmin(userId);
        var events = eventRepository.findAllByInitiator(userId).stream()
                .sorted(Comparator.comparing(Event::getCreatedOn).reversed())
                .toList();

        int from = (int) pageable.getOffset();
        int size = pageable.getPageSize();

        return events.stream()
                .skip(from)
                .limit(size)
                .map(eventMapper::toShortDto)
                .toList();
    }

    // PRIVATE
    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        UserDto initiator = interactionApiManager.getUserByAdmin(userId);
        CategoryDto category = interactionApiManager.getCategoryByPublic(dto.getCategory());

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
                .category(category.getId())
                .initiator(initiator.getId())
                .description(dto.getDescription())
                .location(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()))
                .paid(dto.isPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.isRequestModeration())
                .eventDate(dto.getEventDate())
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .title(dto.getTitle())
                .build();

        e = eventRepository.save(e);

        return eventMapper.toFullDto(e);
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        var e = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found")
        );

        if (!e.getInitiator().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        return eventMapper.toFullDto(e);
    }

    // PRIVATE
    @Override
    @Transactional
    public EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        var e = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found"));


        if (!e.getInitiator().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        if (e.getState() == EventState.PUBLISHED) {
            throw new IllegalStateException("Only pending or canceled events can be changed");
        }

        if (dto.getAnnotation() != null) {
            e.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {
            e.setCategory(dto.getCategory());
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

        return eventMapper.toFullDto(e);
    }

    /**
     * Парсит строго. Если строка присутствует, но формат неверный — кидаем 400. Если null/blank — возвращаем null.
     */
    private LocalDateTime parseStrict(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            // поддерживаем оба часто встречающихся формата
            return (s.indexOf('T') >= 0)
                    ? LocalDateTime.parse(s, F_T)
                    : LocalDateTime.parse(s, F_SPACE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must match 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd'T'HH:mm:ss': " + s);
        }
    }

    private int countConfirmedRequests(Long eventId) {
        return adminEventService.countConfirmedRequests(eventId);
    }
}
