package malyshev.egor;

import lombok.AllArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.dto.user.UserDto;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.feign.category.PublicCategoryFeignClient;
import malyshev.egor.feign.event.AdminEventFeignClient;
import malyshev.egor.feign.event.PrivateEventFeignClient;
import malyshev.egor.feign.event.PublicEventFeignClient;
import malyshev.egor.feign.request.ConfirmedRequestsFeignClient;
import malyshev.egor.feign.request.PrivateRequestFeignClient;
import malyshev.egor.feign.user.AdminUserFeignClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class InteractionApiManager {

    private final AdminUserFeignClient adminUserFeignClient;

    private final AdminEventFeignClient adminEventFeignClient;
    private final PrivateEventFeignClient privateEventFeignClient;
    private final PublicEventFeignClient publicEventFeignClient;

    private final PrivateRequestFeignClient privateRequestFeignClient;
    private final ConfirmedRequestsFeignClient confirmedRequestsFeignClient;

    private final PublicCategoryFeignClient publicCategoryFeignClient;

    public UserDto getUserByAdmin(Long userId) {
        List<Long> users = List.of(userId);
        int from = 0;
        int size = 10;

        List<UserDto> userDtoList = adminUserFeignClient.list(users, from, size);
        if (userDtoList.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return userDtoList.getFirst();
    }

    public EventFullDto getEventOfUserByPrivate(Long userId, Long eventId) {
        try {
            return privateEventFeignClient.getUserEvent(userId, eventId);
        } catch (feign.FeignException.NotFound e) {
            throw new NotFoundException("Event with id=" + eventId + " not found for user " + userId);
        }
    }

    public ParticipationRequestDto getRequestOfUserByPrivate(Long userId, Long eventId) {
        return privateRequestFeignClient.list(userId, eventId).getFirst();
    }

    public EventFullDto getEventByPublic(Long eventId, Long userId) {
        try {
            return publicEventFeignClient.getById(eventId, userId);
        } catch (feign.FeignException.NotFound e) {
            throw new IllegalStateException("Event with id=" + eventId + " was not found");
        }
    }

    public CategoryDto getCategoryByPublic(Long categoryId) {
        return publicCategoryFeignClient.get(categoryId);
    }

    public List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId) {
        return privateRequestFeignClient.list(userId, eventId);
    }

    public Long countConfirmedRequests(Long eventId) {
        try {
            // все динамические поля feign-client должны быть в path конкретного метода, а не класса
            Long count = confirmedRequestsFeignClient.countConfirmedRequests(eventId);
            System.out.println(">>> countConfirmedRequests: eventId=" + eventId + ", count=" + count);
            return count == null ? 0L : count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public List<EventShortDto> getEventsByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return adminEventFeignClient.getEventsByIds(List.copyOf(ids));
    }

    public boolean existsEventsByCategoryId(long categoryId) {
        return adminEventFeignClient.existsEventsByCategoryId(categoryId);
    }
}