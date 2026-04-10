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
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final InteractionApiManager interactionApiManager;

    public EventShortDto toShortDto(Event e) {
        if (e == null) {
            return null;
        }
        long confirmedRequests = 0;
        if (e.getId() != null) {
            confirmedRequests = interactionApiManager.countConfirmedRequests(e.getId());
        }
        CategoryDto category = null;
        if (e.getCategory() != null) {
            category = interactionApiManager.getCategoryByPublic(e.getCategory());
        }
        UserShortDto initiator = null;
        if (e.getInitiator() != null) {
            UserDto userDto = interactionApiManager.getUserByAdmin(e.getInitiator());
            initiator = UserShortDto.builder()
                    .id(userDto.getId())
                    .name(userDto.getName())
                    .build();
        }

        return EventShortDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .eventDate(e.getEventDate())
                .initiator(initiator)
                .paid(e.isPaid())
                .title(e.getTitle())
                .rating(0.0) // rating будет установлен в сервисном слое (из GrpcAnalyzerClient)
                .build();
    }

    public EventFullDto toFullDto(Event e) {
        if (e == null) {
            return null;
        }
        long confirmedRequests = 0;
        if (e.getId() != null) {
            confirmedRequests = interactionApiManager.countConfirmedRequests(e.getId());
        }
        CategoryDto category = null;
        if (e.getCategory() != null) {
            category = interactionApiManager.getCategoryByPublic(e.getCategory());
        }
        UserShortDto initiator = null;
        if (e.getInitiator() != null) {
            UserDto userDto = interactionApiManager.getUserByAdmin(e.getInitiator());
            initiator = UserShortDto.builder()
                    .id(userDto.getId())
                    .name(userDto.getName())
                    .build();
        }

        LocationDto location = null;
        if (e.getLocation() != null) {
            location = new LocationDto(e.getLocation().getLat(), e.getLocation().getLon());
        }

        return EventFullDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .createdOn(e.getCreatedOn())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .initiator(initiator)
                .location(location)
                .paid(e.isPaid())
                .participantLimit(e.getParticipantLimit())
                .publishedOn(e.getPublishedOn())
                .requestModeration(e.isRequestModeration())
                .state(e.getState())
                .title(e.getTitle())
                .rating(0.0) // rating будет установлен в сервисном слое (из GrpcAnalyzerClient)
                .build();
    }
}