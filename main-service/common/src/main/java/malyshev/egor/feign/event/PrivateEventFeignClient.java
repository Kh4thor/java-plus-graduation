package malyshev.egor.feign.event;

import jakarta.validation.Valid;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.dto.event.NewEventDto;
import malyshev.egor.dto.event.UpdateEventUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@FeignClient(name = "event-service",
        contextId = "privateEventApiClient",
        url = "${gateway.url:http://localhost:8080}",
        path = "/users/{userId}/events")
public interface PrivateEventFeignClient {

    @GetMapping
    List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                      @RequestParam(value = "from", defaultValue = "0") int from,
                                      @RequestParam(value = "size", defaultValue = "10") int size);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto addEvent(@PathVariable Long userId,
                          @Valid @RequestBody NewEventDto dto);

    @GetMapping("/{eventId}")
    EventFullDto getUserEvent(@PathVariable Long userId,
                              @PathVariable Long eventId);

    @PatchMapping("/{eventId}")
    EventFullDto updateEventUser(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @Valid @RequestBody UpdateEventUserRequest dto);
}
