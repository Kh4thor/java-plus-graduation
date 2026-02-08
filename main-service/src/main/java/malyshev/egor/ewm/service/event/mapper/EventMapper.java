package malyshev.egor.ewm.service.event.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.ewm.service.category.model.Category;
import malyshev.egor.ewm.service.event.dto.*;
import malyshev.egor.ewm.service.event.model.Event;
import malyshev.egor.ewm.service.user.model.User;

@UtilityClass
public final class EventMapper {
    public static EventShortDto toShortDto(Event e, long confirmed, long views) {
        return EventShortDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(EventMapper.toCategoryDto(e.getCategory()))
                .confirmedRequests(confirmed)
                .eventDate(e.getEventDate())
                .initiator(EventMapper.toUserShort(e.getInitiator()))
                .paid(e.isPaid())
                .title(e.getTitle()).views(views)
                .build();
    }

    public static EventFullDto toFullDto(Event e, long confirmed, long views) {
        return EventFullDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(EventMapper.toCategoryDto(e.getCategory()))
                .confirmedRequests(confirmed)
                .createdOn(e.getCreatedOn())
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .initiator(EventMapper.toUserShort(e.getInitiator()))
                .location(new LocationDto(e.getLocation().getLat(), e.getLocation().getLon()))
                .paid(e.isPaid())
                .participantLimit(e.getParticipantLimit())
                .publishedOn(e.getPublishedOn())
                .requestModeration(e.isRequestModeration())
                .state(e.getState().name())
                .title(e.getTitle())
                .views(views)
                .build();
    }

    public static UserShortDto toUserShort(User u) {
        return UserShortDto.builder()
                .id(u.getId())
                .name(u.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }
}
