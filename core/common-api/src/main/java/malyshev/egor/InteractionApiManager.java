package malyshev.egor;

import lombok.AllArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.dto.user.UserShortDto;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.feign.category.PublicCategoryFeignClient;
import malyshev.egor.feign.event.AdminEventFeignClient;
import malyshev.egor.feign.event.PublicEventFeignClient;
import malyshev.egor.feign.request.PrivateRequestFeignClient;
import malyshev.egor.feign.user.AdminUserFeignClient;
import malyshev.egor.mapper.CategoryMapper;
import malyshev.egor.mapper.EventMapper;
import malyshev.egor.mapper.UserMapper;
import malyshev.egor.model.category.Category;
import malyshev.egor.model.event.Event;
import malyshev.egor.model.request.ParticipationRequest;
import malyshev.egor.model.request.RequestStatus;
import malyshev.egor.model.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InteractionApiManager {

    private final AdminUserFeignClient adminUserFeignClient;
    private final AdminEventFeignClient adminEventFeignClient;
    private final PublicEventFeignClient publicEventFeignClient;
    private final PublicCategoryFeignClient publicCategoryFeignClient;
    private final PrivateRequestFeignClient privateRequestFeignClient;

    private final int searchFrom = 0;
    private final int searchSize = 10;

    // admin method
    public User adminGetUserById(Long userId) {
        List<Long> userIds = List.of(userId);
        List<UserDto> usersList = adminUserFeignClient.list(userIds, searchFrom, searchSize);
        UserDto userDto = usersList.stream()
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("User with id=" + userId + " was not found"));
        return UserMapper.toUser(userDto);
    }

    // public (+ admin) method
    public Event adminGetEventByUserIdAndEventId(Long userId, Long eventId) {
        EventFullDto eventFullDto = publicEventFeignClient.getById(eventId);
        User initiator = adminGetUserById(userId);
//        if (eventFullDto.getInitiator() != null && !initiator.getId().equals(eventFullDto.getInitiator().getId())) {
//            throw new ValidationException("User with id=" + eventFullDto.getInitiator().getId() +
//                    " is not initiator of event with id=" + eventId);
//        }
        return EventMapper.toEvent(eventFullDto, initiator.getEmail());
    }

    // public method
    public Category publicGetCategoryById(Long categoryId) {
        CategoryDto categoryDto = publicCategoryFeignClient.get(categoryId);
        return CategoryMapper.toCategory(categoryDto);
    }

    // admin method
    public int adminCountByEventIdAndStatus(Long eventId, RequestStatus requestStatus) {
        List<String> states = List.of(requestStatus.name());

        List<EventFullDto> eventFullDtoList = adminEventFeignClient.search(
                null,
                states,
                null,
                null,
                null,
                searchFrom,
                searchSize);

        return (int) eventFullDtoList.stream()
                .filter(event -> event.getId().equals(eventId))
                .count();
    }

    public ParticipationRequest adminFindByRequesterIdAndEventId(Long userId, Long eventId) {
        List<ParticipationRequestDto> requests = privateRequestFeignClient.list(userId, eventId);
        if (requests.isEmpty()) {
            throw new NotFoundException("Request with id=" + eventId + " was not found");
        }
        ParticipationRequestDto requestDto = requests.getFirst();
        Event event = adminGetEventByUserIdAndEventId(userId, eventId);
        User requester = adminGetUserById(userId);
        RequestStatus status = requestDto.getStatus();
        Long requestId = requestDto.getId();
        LocalDateTime created = requestDto.getCreated();

        return ParticipationRequest.builder()
                .id(requestId)
                .created(created)
                .event(event)
                .requester(requester)
                .status(status)
                .build();
    }

    public List<Event> adminFindAllById(Set<Long> eventIdsSet) {
        List<Long> eventIdsToSearch = eventIdsSet.stream().toList();
        List<EventFullDto> eventFullDtos = adminEventFeignClient.search(
                eventIdsToSearch,
                null,
                null,
                null,
                null,
                searchFrom,
                searchFrom);

        // поиск списка инициаторов событий
        List<Long> userIds = eventFullDtos.stream()
                .map(EventFullDto::getInitiator)
                .map(UserShortDto::getId)
                .toList();

        // список инициаторов событий
        List<UserDto> userDtos = adminUserFeignClient.list(userIds, searchFrom, searchSize);

        // <initiatorId, UserDto> словарь инициаторов событий
        Map<Long, UserDto> userShoprtMap = userDtos.stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));

        List<Event> events = new ArrayList<>();
        for (EventFullDto eventFullDto : eventFullDtos) {
            Long initiatorId = eventFullDto.getInitiator() == null
                    ? null
                    : eventFullDto.getInitiator().getId();
            UserDto initiator = userShoprtMap.get(initiatorId);
            Event event = EventMapper.toEvent(eventFullDto, initiator.getEmail());
            events.add(event);
        }
        return events;
    }
}