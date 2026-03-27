package malyshev.egor.service.privates;

import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.dto.event.NewEventDto;
import malyshev.egor.dto.event.UpdateEventUserRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

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

    // Новый метод: лайк мероприятия
    @Transactional
    void likeEvent(Long userId, Long eventId);

    // Новый метод: рекомендации для пользователя
    List<EventShortDto> getRecommendations(Long userId, int maxResults);
}
