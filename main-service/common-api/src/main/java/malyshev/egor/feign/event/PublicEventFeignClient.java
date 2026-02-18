package malyshev.egor.feign.event;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Validated
@FeignClient(name = "event-service",
        contextId = "public-event-service",
        url = "${gateway.url:http://localhost:8080}",
        path = "/events")
public interface PublicEventFeignClient {

    @GetMapping
    List<EventShortDto> get(@RequestParam(value = "text", required = false) String text,
                            @RequestParam(value = "categories", required = false) List<Long> categories,
                            @RequestParam(value = "paid", required = false) Boolean paid,
                            @RequestParam(value = "rangeStart", required = false) String rangeStart,
                            @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                            @RequestParam(value = "sort", required = false) String sort,
                            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                            @RequestParam(value = "size", defaultValue = "10") @Positive int size,
                            @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp,
                            @RequestHeader(value = "X-Request-URI", required = false) String requestUri);

    @GetMapping("/{id}")
    EventFullDto getById(@PathVariable("id") Long id);
}
