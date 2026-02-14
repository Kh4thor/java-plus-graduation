package malyshev.egor;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.feign.category.PublicCategoryFeignClient;
import malyshev.egor.feign.event.AdminEventFeignClient;
import malyshev.egor.feign.event.PublicEventFeignClient;
import malyshev.egor.feign.request.PrivateEventRequestFeignClient;
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
import java.util.List;

@Service
@AllArgsConstructor
public class InteractionApiManager {

    private final AdminUserFeignClient adminUserFeignClient;
    private final AdminEventFeignClient adminEventFeignClient;
    private final PublicCategoryFeignClient publicCategoryFeignClient;
    private final PublicEventFeignClient publicEventFeignClient;
    private final PrivateEventRequestFeignClient eventRequestPrivateFeignClient;

    private final int search_from = 0;
    private final int search_size = 10;

    // admin method
    public User adminGetUserById(Long userId) {
        List<Long> userIds = List.of(userId);
        List<UserDto> usersList = adminUserFeignClient.list(userIds, search_from, search_size);
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
        if (eventFullDto.getInitiator() != null && !initiator.getId().equals(eventFullDto.getInitiator().getId())) {
            throw new ValidationException("User with id=" + eventFullDto.getInitiator().getId() +
                    " is not initiator of event with id=" + eventId);
        }
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
                search_from,
                search_size);

        return (int) eventFullDtoList.stream()
                .filter(event -> event.getId().equals(eventId))
                .count();
    }

    public ParticipationRequest adminFindByRequesterIdAndEventId(Long userId, Long eventId) {
        List<ParticipationRequestDto> requests = eventRequestPrivateFeignClient.list(userId, eventId);
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
}
