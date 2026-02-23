package malyshev.egor;

import lombok.AllArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.dto.request.RequestStatus;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.feign.category.PublicCategoryFeignClient;
import malyshev.egor.feign.event.AdminEventFeignClient;
import malyshev.egor.feign.event.PrivateEventFeignClient;
import malyshev.egor.feign.event.PublicEventFeignClient;
import malyshev.egor.feign.request.CountRequestFeignClient;
import malyshev.egor.feign.request.PrivateRequestFeignClient;
import malyshev.egor.feign.user.AdminUserFeignClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InteractionApiManager {

    private final AdminUserFeignClient adminUserFeignClient;

    private final AdminEventFeignClient adminEventFeignClient;
    private final PrivateEventFeignClient privateEventFeignClient;
    private final PublicEventFeignClient publicEventFeignClient;

    private final PrivateRequestFeignClient privateRequestFeignClient;
    private final CountRequestFeignClient countRequestFeignClient;

    private final PublicCategoryFeignClient publicCategoryFeignClient;

    private final String ip = "127.0.0.1";

    public UserDto getUserByAdmin(Long userId) {
        List<Long> users = List.of(userId);
        int from = 0;
        int size = 20;

        List<UserDto> userDtoList = adminUserFeignClient.list(users, from, size);
        if (userDtoList.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return userDtoList.getFirst();
    }

    public EventFullDto getEventOfUserByPrivate(Long userId, Long eventId) {
        return privateEventFeignClient.getUserEvent(userId, eventId);
    }

    public ParticipationRequestDto getRequestOfUserByPrivate(Long userId, Long eventId) {
        return privateRequestFeignClient.list(userId, eventId).getFirst();
    }

    public List<UserDto> getAllUsersByAdmin(List<Long> list) {
        int from = 0;
        int size = 10;
        return adminUserFeignClient.list(list, from, size);
    }

    public List<EventShortDto> getAllEventsByPublic(String uri) {
        int from = 0;
        int size = 10;
        return publicEventFeignClient.get(
                null,
                null,
                null,
                null,
                null,
                true,
                null,
                from,
                size,
                ip,
                uri
        );
    }

    public EventFullDto getEventByPublic(Long eventId, String uri) {
        return publicEventFeignClient.getById(eventId, ip, uri);
    }

    public CategoryDto getCategoryByPublic(Long categoryId) {
        return publicCategoryFeignClient.get(categoryId);
    }

    public List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId) {
        return privateRequestFeignClient.list(userId, eventId);
    }

    public Long countByEventAndStatus(Long eventId, RequestStatus status) {
        Long count = countRequestFeignClient.countByEventAndStatus(eventId, status);
        if (count == null)
            return 0L;
        return count;
    }
}