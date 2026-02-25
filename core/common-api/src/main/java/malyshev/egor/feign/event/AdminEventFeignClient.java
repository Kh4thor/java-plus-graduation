package malyshev.egor.feign.event;

import malyshev.egor.dto.event.EventShortDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Validated
@FeignClient(name = "event-service",
        contextId = "admin-event-service",
        url = "${gateway.url:http://localhost:8080}",
        path = "/admin/events")
public interface AdminEventFeignClient {

    @GetMapping("/by-ids")
    List<EventShortDto> getEventsByIds(@RequestParam("ids") List<Long> ids);
}