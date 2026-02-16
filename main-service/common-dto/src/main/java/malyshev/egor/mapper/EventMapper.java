package malyshev.egor.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.dto.event.LocationDto;
import malyshev.egor.model.category.Category;
import malyshev.egor.model.event.Event;
import malyshev.egor.model.event.Location;
import malyshev.egor.model.user.User;

@UtilityClass
public final class EventMapper {
    public static EventShortDto toShortDto(Event e, long confirmed, long views) {
        if (e == null) {
            return null;
        }
        return EventShortDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(CategoryMapper.toDto(e.getCategory()))
                .confirmedRequests(confirmed)
                .eventDate(e.getEventDate())
                .initiator(UserMapper.toUserShort(e.getInitiator()))
                .paid(e.isPaid())
                .title(e.getTitle()).views(views)
                .build();
    }

    public static EventFullDto toFullDto(Event e, long confirmed, long views) {
        if (e == null) {
            return null;
        }
        return EventFullDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(CategoryMapper.toDto(e.getCategory()))
                .confirmedRequests(confirmed)
                .createdOn(e.getCreatedOn())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .initiator(UserMapper.toUserShort(e.getInitiator()))
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

    public static Event toEvent(EventFullDto eventFullDto, String userEmail) {
        if (eventFullDto == null) {
            return null;
        }

        Category category = CategoryMapper.toCategory(eventFullDto.getCategory());
        User initiator = UserMapper.toUser(eventFullDto.getInitiator(), userEmail);
        Location location = LocationMapper.toLocation(eventFullDto.getLocation());

        return Event.builder()
                .id(eventFullDto.getId())
                .annotation(eventFullDto.getAnnotation())
                .category(category)
                .initiator(initiator)
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
