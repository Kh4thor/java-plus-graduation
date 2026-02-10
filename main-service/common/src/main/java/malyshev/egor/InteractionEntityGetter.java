package malyshev.egor;

import lombok.AllArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.feign.category.CategoryPublicFeignClient;
import malyshev.egor.feign.event.EventAdminFeignClient;
import malyshev.egor.feign.user.UserAdminFeignClient;
import malyshev.egor.mapper.EventMapper;
import malyshev.egor.mapper.UserMapper;
import malyshev.egor.model.event.Event;
import malyshev.egor.model.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InteractionEntityGetter {

    private final UserAdminFeignClient userAdminFeignClient;
    private final EventAdminFeignClient eventAdminFeignClient;
    private final CategoryPublicFeignClient categoryPublicFeignClient;
    private final int search_from = 0;
    private final int search_size = 10;

    public User getUserById(Long userId) {
        List<Long> userIds = List.of(userId);
        List<UserDto> usersList = userAdminFeignClient.list(userIds, search_from, search_size);
        UserDto userDto = usersList.stream()
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("User with id=" + userId + " was not found"));
        return UserMapper.toUser(userDto);
    }

    public Event getEventByUserIdAndEventId(Long userId, Long eventId) {
        List<Long> userIds = List.of(userId);
        List<EventFullDto> eventFullDtoList = eventAdminFeignClient.search(
                userIds,
                null,
                null,
                null,
                null,
                search_from,
                search_size);

        EventFullDto eventFullDto = eventFullDtoList.stream()
                .filter(dto -> eventId.equals(dto.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%d not found", eventId)
                ));

        return EventMapper.toEvent


    }

    CategoryDto getCategoryDById(Long categoryId) {
        return categoryPublicFeignClient.get(categoryId);
    }

}
