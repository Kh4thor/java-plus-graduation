package malyshev.egor.feign.event;

import malyshev.egor.dto.event.EventFullDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Validated
@FeignClient(name = "event-service",
        contextId = "public-event-service",
        url = "${gateway.url:http://localhost:8080}",
        path = "/events")
public interface PublicEventFeignClient {

    @GetMapping("/{id}")
    EventFullDto getById(@PathVariable("id") Long id);
}
