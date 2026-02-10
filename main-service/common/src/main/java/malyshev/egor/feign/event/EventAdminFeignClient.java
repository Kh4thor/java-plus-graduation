package malyshev.egor.feign.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.UpdateEventAdminRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@FeignClient(name = "event-service",
        contextId = "eventAdminApiClient",
        path = "/admin/events")
public interface EventAdminFeignClient {

    @GetMapping
    public List<EventFullDto> search(@RequestParam(value = "users", required = false) List<Long> users,
                                     @RequestParam(value = "states", required = false) List<String> states,
                                     @RequestParam(value = "categories", required = false) List<Long> categories,
                                     @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                     @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                     @RequestParam(value = "from", defaultValue = "0") int from,
                                     @RequestParam(value = "size", defaultValue = "10") int size);

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventAdminRequest dto);
}
