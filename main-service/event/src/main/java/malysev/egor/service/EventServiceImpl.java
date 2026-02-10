package malysev.egor.service;

import lombok.RequiredArgsConstructor;
import malysev.egor.repository.EventRepository;
import malyshev.egor.dto.event.*;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.EventMapper;
import malyshev.egor.model.event.Event;
import malyshev.egor.model.event.EventState;
import malyshev.egor.model.request.RequestStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    // форматтеры для строгого парсинга
    private static final DateTimeFormatter F_SPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter F_T = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    @Transactional
    public List<EventShortDto> publicSearch(
            String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Pageable pageable,
            String uri,
            String ip
    ) {
        // строгая валидация диапазона дат
        LocalDateTime start = parseStrict(rangeStart);   // 400 если формат некорректный
        LocalDateTime end   = parseStrict(rangeEnd);     // 400 если формат некорректный
        validateRangeOrThrow(start, end);                // 400 если end < start

        // только опубликованные
        Specification<Event> spec = (root, q, cb)
                -> cb.equal(root.get("state"), EventState.PUBLISHED);

        if (text != null && !text.isBlank()) {
            String pattern = "%" + text.toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(cb.lower(root.get("annotation")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            ));
        }
        if (paid != null) {
            spec = spec.and((root, q, cb)
                    -> cb.equal(root.get("paid"), paid));
        }
        if (categories != null && !categories.isEmpty()) {
            spec = spec.and((root, q, cb)
                    -> root.get("category").get("id").in(categories));
        }
        if (start != null) {
            spec = spec.and((root, q, cb)
                    -> cb.greaterThanOrEqualTo(root.get("eventDate"), start));
        }
        if (end != null) {
            spec = spec.and((root, q, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), end));
        }

        // фиксируем просмотр
        statsClient.hit(uri, ip);

        if ("VIEWS".equalsIgnoreCase(sort)) {
            // сортировка по просмотрам делается в памяти
            List<Event> all = eventRepository.findAll(spec);
            List<Event> sorted = all.stream()
                    .sorted(Comparator
                            .comparingLong((Event e) -> statsClient.viewsForEvent(e.getId()))
                            .reversed())
                    .toList();

            int from = (int) pageable.getOffset();
            int size = pageable.getPageSize();

            return sorted.stream()
                    .skip(from).limit(size)
                    .map(e -> EventMapper.toShortDto(
                            e,
                            requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED),
                            statsClient.viewsForEvent(e.getId())))
                    .toList();
        } else {
            // по умолчанию сортируем по EVENT_DATE
            var page = eventRepository.findAll(
                    spec,
                    PageRequest.of((int) (pageable.getOffset() / pageable.getPageSize()),
                            pageable.getPageSize(),
                            Sort.by("eventDate").ascending())
            );
            return page.getContent().stream()
                    .map(e -> EventMapper.toShortDto(
                            e,
                            requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED),
                            statsClient.viewsForEvent(e.getId())))
                    .toList();
        }
    }

    @Override
    @Transactional
    public EventFullDto publicGet(Long eventId, String uri, String ip) {
        var e = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found")
        );

        if (e.getState() != PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        statsClient.hit(uri, ip);
        return EventMapper.toFullDto(
                e,
                requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED),
                statsClient.viewsForEvent(e.getId())
        );
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " was not found")
        );

        var events = eventRepository.findAllByInitiator_Id(userId).stream()
                .sorted(Comparator.comparing(Event::getCreatedOn).reversed())
                .toList();

        int from = (int) pageable.getOffset();
        int size = pageable.getPageSize();

        return events.stream()
                .skip(from)
                .limit(size)
                .map(e -> EventMapper.toShortDto(
                        e,
                        requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED),
                        statsClient.viewsForEvent(e.getId())))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto dto) {
        var initiator = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " was not found")
        );

        var cat = categoryRepository.findById(dto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found")
        );

        // дата минимум +2 часа от «сейчас»
        if (dto.getEventDate() == null || dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2)))  {
            throw new IllegalArgumentException("Event date must be at least 2 hours in the future");
        }
        // запрет отрицательного лимита
        if (dto.getParticipantLimit() < 0) {
            throw new IllegalArgumentException("participantLimit must be >= 0");
        }

        var e = Event.builder()
                .annotation(dto.getAnnotation())
                .category(cat)
                .initiator(initiator)
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
        return EventMapper.toFullDto(e, 0L, 0L);
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        var e = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found")
        );

        if (!e.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        return EventMapper.toFullDto(
                e,
                requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED),
                statsClient.viewsForEvent(e.getId())
        );
    }

    @Override
    @Transactional
    public EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        var e = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found")
        );

        if (!e.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        if (e.getState() == PUBLISHED) {
            throw new IllegalStateException("Only pending or canceled events can be changed");
        }

        if (dto.getAnnotation() != null) {
            e.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {
            var c = categoryRepository.findById(dto.getCategory()).orElseThrow(
                    () -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));
            e.setCategory(c);
        }

        if (dto.getDescription() != null) e.setDescription(dto.getDescription());

        if (dto.getEventDate() != null) {
            LocalDateTime newDate = dto.getEventDate();
            if (!newDate.isAfter(LocalDateTime.now().plusHours(2))) {
                throw new IllegalArgumentException("Event date must be at least 2 hours in the future");
            }
            e.setEventDate(newDate);
        }

        if (dto.getLocation() != null) {
            e.setLocation(new Location(dto.getLocation().getLat(), dto.getLocation().getLon()));
        }

        if (dto.getPaid() != null) e.setPaid(dto.getPaid());

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

        return EventMapper.toFullDto(
                e,
                requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED),
                statsClient.viewsForEvent(e.getId())
        );
    }

    @Override
    public List<EventFullDto> adminSearch(List<Long> users,
                                          List<String> states,
                                          List<Long> categories,
                                          String rangeStart,
                                          String rangeEnd,
                                          Pageable pageable) {
        // строгая валидация диапазона дат
        LocalDateTime start = parseStrict(rangeStart);
        LocalDateTime end   = parseStrict(rangeEnd);
        validateRangeOrThrow(start, end);

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
                .map(e -> EventMapper.toFullDto(
                        e,
                        requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED),
                        statsClient.viewsForEvent(e.getId())))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequest dto) {
        var e = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found")
        );

        if (dto.getAnnotation() != null) {
            e.setAnnotation(dto.getAnnotation());
        }

        if (dto.getCategory() != null) {
            var c = categoryRepository.findById(dto.getCategory()).orElseThrow(
                    () -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));
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
            e.setState(PUBLISHED);

        } else if ("REJECT_EVENT".equalsIgnoreCase(dto.getStateAction())) {
            if (e.getState() == PUBLISHED) {
                throw new IllegalStateException("Cannot reject the event because it's already published");
            }
            e.setState(EventState.CANCELED);
        }

        return EventMapper.toFullDto(
                e,
                requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED),
                statsClient.viewsForEvent(e.getId())
        );
    }

    /** Парсит строго. Если строка присутствует, но формат неверный — кидаем 400. Если null/blank — возвращаем null. */
    private LocalDateTime parseStrict(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            // поддерживаем оба часто встречающихся формата
            return (s.indexOf('T') >= 0) ? LocalDateTime.parse(s, F_T) : LocalDateTime.parse(s, F_SPACE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Date must match 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd'T'HH:mm:ss': " + s
            );
        }
    }

    /** Если оба конца заданы и end < start — 400. */
    private void validateRangeOrThrow(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("rangeEnd must be after rangeStart");
        }
    }
}
