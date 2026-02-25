package malyshev.egor.service.admins;

import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.UpdateEventAdminRequest;
import malyshev.egor.model.Event;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminEventService {

    // ADMIN - for controller
    List<EventFullDto> adminSearch(List<Long> users,
                                   List<String> states,
                                   List<Long> categories,
                                   String rangeStart,
                                   String rangeEnd,
                                   Pageable pageable);

    // ADMIN - for controller
    EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequest dto);

    // ADMIN - for private and public event services
    int countConfirmedRequests(Long eventId);

    // Дополнительный метод вне ТЗ
    List<Event> getEventsByIds(List<Long> ids);

    // Дополнительный метод вне ТЗ
    boolean existsByCategoryId(Long categoryId);
}
