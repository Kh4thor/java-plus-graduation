package malyshev.egor.mapper;

import lombok.RequiredArgsConstructor;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.dto.event.LocationDto;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.dto.user.UserShortDto;
import malyshev.egor.model.Event;
import malyshev.egor.model.Location;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final InteractionApiManager interactionApiManager;

    public EventShortDto toShortDto(Event e, long confirmed, long views) {
        if (e == null) {
            return null;
        }
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
                .confirmedRequests(confirmed)
                .eventDate(e.getEventDate())
                .initiator(initiator)
                .paid(e.isPaid())
                .title(e.getTitle()).views(views)
                .build();
    }

    public EventFullDto toFullDto(Event e, long confirmed, long views) {
        if (e == null) {
            return null;
        }
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
                .confirmedRequests(confirmed)
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

    public Event toEvent(EventFullDto eventFullDto, String userEmail) {
        if (eventFullDto == null) {
            return null;
        }
        Location location = LocationMapper.toLocation(eventFullDto.getLocation());
        return Event.builder()
                .id(eventFullDto.getId())
                .annotation(eventFullDto.getAnnotation())
                .category(eventFullDto.getCategory() == null
                        ? null
                        : eventFullDto.getCategory().getId())
                .initiator(eventFullDto.getInitiator() == null
                        ? null
                        : eventFullDto.getInitiator().getId())
                .description(eventFullDto.getDescription())
                .location(location)
                .paid(eventFullDto.isPaid())
                .participantLimit(eventFullDto.getParticipantLimit())
                .requestModeration(eventFullDto.isRequestModeration())
                .eventDate(eventFullDto.getEventDate())
                .createdOn(eventFullDto.getCreatedOn())
                .publishedOn(eventFullDto.getPublishedOn())
                .state(eventFullDto.getState())
                .title(eventFullDto.getTitle())
                .build();
    }
}
