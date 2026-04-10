package malyshev.egor.service.publics;

import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

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
                                     Pageable pageable
    );

//    // PUBLIC
//    EventFullDto publicGet(Long eventId);

    // PUBLIC
    @Transactional
    EventFullDto publicGet(Long eventId, Long userId);
}
