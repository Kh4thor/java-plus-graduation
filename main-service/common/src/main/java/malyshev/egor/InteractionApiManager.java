package malyshev.egor;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.feign.category.CategoryPublicFeignClient;
import malyshev.egor.feign.event.EventAdminFeignClient;
import malyshev.egor.feign.event.EventPublicFeignClient;
import malyshev.egor.feign.request.EventRequestPrivateFeignClient;
import malyshev.egor.feign.request.RequestEventPublicFeignClient;
import malyshev.egor.feign.user.UserAdminFeignClient;
import malyshev.egor.mapper.CategoryMapper;
import malyshev.egor.mapper.EventMapper;
import malyshev.egor.mapper.UserMapper;
import malyshev.egor.model.category.Category;
import malyshev.egor.model.event.Event;
import malyshev.egor.model.request.RequestStatus;
import malyshev.egor.model.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InteractionApiManager {

    private final UserAdminFeignClient userAdminFeignClient;
    private final EventAdminFeignClient eventAdminFeignClient;
    private final CategoryPublicFeignClient categoryPublicFeignClient;
    private final EventRequestPrivateFeignClient eventRequestPrivateFeignClient;
    private final EventPublicFeignClient eventPublicFeignClient;

    private final int search_from = 0;
    private final int search_size = 10;

    // admin method
    public User getUserById(Long userId) {
        List<Long> userIds = List.of(userId);
        List<UserDto> usersList = userAdminFeignClient.list(userIds, search_from, search_size);
        UserDto userDto = usersList.stream()
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("User with id=" + userId + " was not found"));
        return UserMapper.toUser(userDto);
    }


    // public method
    public Event getEventByUserIdAndEventId(Long userId, Long eventId) {
        EventFullDto eventFullDto = eventPublicFeignClient.getById(eventId);
        User initiator = getUserById(userId);
        if (eventFullDto.getInitiator() != null && !initiator.getId().equals(eventFullDto.getInitiator().getId())) {
            throw new ValidationException("User with id=" + eventFullDto.getInitiator().getId() +
                    " is not initiator of event with id=" + eventId);
        }
        return EventMapper.toEvent(eventFullDto, initiator.getEmail());
    }

    // public method
    public Category getCategoryById(Long categoryId) {
        CategoryDto categoryDto = categoryPublicFeignClient.get(categoryId);
        return CategoryMapper.toCategory(categoryDto);
    }

    // admin method
    public int countByEventIdAndStatus(Long eventId, RequestStatus requestStatus) {
        List<String> states = List.of(requestStatus.name());

        List<EventFullDto> eventFullDtoList = eventAdminFeignClient.search(
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


}
