package malyshev.egor.service.admins;

import malyshev.egor.dto.event.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventAdminService {

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
