package malyshev.egor.feign.request;

import malyshev.egor.dto.request.ParticipationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@FeignClient(name = "request-service",
        contextId = "public-request-service",
        url = "${gateway.url:http://localhost:8080}", path = "/users")
public interface PublicRequestFeignClient {

    @GetMapping("/{userId}/requests")
    List<ParticipationRequestDto> get(@PathVariable long userId);

    @PostMapping("/{userId}/requests")
    ParticipationRequestDto create(@PathVariable long userId, @RequestParam long eventId);

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto cancel(@PathVariable long userId, @PathVariable long requestId);
}
