package malyshev.egor.feign.request;

import feign.FeignException;
import malyshev.egor.dto.request.ParticipationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@FeignClient(name = "request-service",
        contextId = "publicRequestEventApiClient",
        url = "${gateway.url:http://localhost:8080}",
        path = "/users/{userId}/requests")
public interface PublicRequestEventFeignClient {

    @GetMapping
    List<ParticipationRequestDto> get(@PathVariable long userId);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto create(@PathVariable long userId, @RequestParam long eventId) throws FeignException;

    @PatchMapping("{requestId}/cancel")
    ParticipationRequestDto cancel(@PathVariable long userId, @PathVariable long requestId) throws FeignException;
}
