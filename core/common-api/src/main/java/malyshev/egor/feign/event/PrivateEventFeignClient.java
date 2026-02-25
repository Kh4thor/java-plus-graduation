package malyshev.egor.feign.event;

import malyshev.egor.dto.event.EventFullDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Validated
@FeignClient(name = "event-service",
        contextId = "private-event-service")
public interface PrivateEventFeignClient {
    @GetMapping("/users/{userId}/events/{eventId}")
    EventFullDto getUserEvent(@PathVariable Long userId,
                              @PathVariable Long eventId);
}