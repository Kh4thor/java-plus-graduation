package malysev.egor.service;

import malyshev.egor.ewm.service.event.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {
    List<EventShortDto> publicSearch(String text,
                                     List<Long> categories,
                                     Boolean paid,
                                     String rangeStart,
                                     String rangeEnd,
                                     Boolean onlyAvailable,
                                     String sort,
                                     Pageable pageable,
                                     String requestUri,
                                     String ip);

    EventFullDto publicGet(Long eventId, String requestUri, String ip);

    List<EventShortDto> getUserEvents(Long userId, Pageable pageable);

    EventFullDto addEvent(Long userId, NewEventDto dto);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest dto);

    List<EventFullDto> adminSearch(List<Long> users,
                                   List<String> states,
                                   List<Long> categories,
                                   String rangeStart,
                                   String rangeEnd,
                                   Pageable pageable);

    EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequest dto);
}
