package malyshev.egor.mapper;

import lombok.RequiredArgsConstructor;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.dto.event.LocationDto;
import malyshev.egor.dto.request.RequestStatus;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.dto.user.UserShortDto;
import malyshev.egor.ewm.stats.client.StatsClient;
import malyshev.egor.model.Event;
import malyshev.egor.model.Location;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final InteractionApiManager interactionApiManager;
    private final StatsClient statsClient;

    public EventShortDto toShortDto(Event e) {
        if (e == null) {
            return null;
        }
        long views = statsClient.viewsForEvent(e.getId());
        long confirmedRequests = interactionApiManager.countByEventAndStatus(e.getId(), RequestStatus.CONFIRMED);
        CategoryDto category = interactionApiManager.getCategoryByPublic(e.getCategory());
        UserDto userDto = interactionApiManager.getUserByAdmin(e.getInitiator());
        UserShortDto initiator = UserShortDto.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .build();

        return EventShortDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .eventDate(e.getEventDate())
                .initiator(initiator)
                .paid(e.isPaid())
                .title(e.getTitle())
                .views(views)
                .build();
    }

    public EventFullDto toFullDto(Event e) {
        if (e == null) {
            return null;
        }
        long views = statsClient.viewsForEvent(e.getId());
        long confirmedRequests = interactionApiManager.countByEventAndStatus(e.getId(), RequestStatus.CONFIRMED);
        CategoryDto category = interactionApiManager.getCategoryByPublic(e.getCategory());
        UserDto userDto = interactionApiManager.getUserByAdmin(e.getInitiator());
        UserShortDto initiator = UserShortDto.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .build();

        return EventFullDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .createdOn(e.getCreatedOn())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .initiator(initiator)
                .location(new LocationDto(e.getLocation().getLat(), e.getLocation().getLon()))
                .paid(e.isPaid())
                .participantLimit(e.getParticipantLimit())
                .publishedOn(e.getPublishedOn())
                .requestModeration(e.isRequestModeration())
                .state(e.getState())
                .title(e.getTitle())
                .views(views)
                .build();
    }
}
