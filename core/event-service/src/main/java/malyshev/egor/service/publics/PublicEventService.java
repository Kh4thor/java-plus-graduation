package malyshev.egor.service.publics;

import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
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
}
