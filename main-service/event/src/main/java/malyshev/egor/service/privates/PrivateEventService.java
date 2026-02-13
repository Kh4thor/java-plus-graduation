package malyshev.egor.service.privates;

import malyshev.egor.dto.event.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PrivateEventService {

    // PRIVATE
    List<EventShortDto> getUserEvents(Long userId, Pageable pageable);

    // PRIVATE
    EventFullDto addEvent(Long userId, NewEventDto dto);

    // PRIVATE
    EventFullDto getUserEvent(Long userId, Long eventId);

    // PRIVATE
    EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest dto);
}
