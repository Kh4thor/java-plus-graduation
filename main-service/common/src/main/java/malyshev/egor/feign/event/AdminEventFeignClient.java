package malyshev.egor.feign.event;

import feign.FeignException;
import jakarta.validation.Valid;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.UpdateEventAdminRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@FeignClient(name = "event-service",
        contextId = "adminEventApiClient",
        url = "${gateway.url:http://localhost:8080}",
        path = "/admin/events")
public interface AdminEventFeignClient {

    @GetMapping
    List<EventFullDto> search(@RequestParam(value = "users", required = false) List<Long> users,
                              @RequestParam(value = "states", required = false) List<String> states,
                              @RequestParam(value = "categories", required = false) List<Long> categories,
                              @RequestParam(value = "rangeStart", required = false) String rangeStart,
                              @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                              @RequestParam(value = "from", defaultValue = "0") int from,
                              @RequestParam(value = "size", defaultValue = "10") int size) throws FeignException;

    @PatchMapping("/{eventId}")
    EventFullDto update(@PathVariable Long eventId,
                        @Valid @RequestBody UpdateEventAdminRequest dto) throws FeignException;
}
