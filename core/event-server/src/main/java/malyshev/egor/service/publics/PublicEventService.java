package malyshev.egor.service.publics;

import malyshev.egor.dto.event.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PublicEventService {

    // PUBLIC
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

    // PUBLIC
    EventFullDto publicGet(Long eventId, String requestUri, String ip);


    // PRIVATE
    List<EventShortDto> getUserEvents(Long userId, Pageable pageable);

    // PRIVATE
    EventFullDto addEvent(Long userId, NewEventDto dto);

    // PRIVATE
    EventFullDto getUserEvent(Long userId, Long eventId);

    // PRIVATE
    EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest dto);

    // ADMIN
    List<EventFullDto> adminSearch(List<Long> users,
                                   List<String> states,
                                   List<Long> categories,
                                   String rangeStart,
                                   String rangeEnd,
                                   Pageable pageable);

    // ADMIN
    EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequest dto);
}
