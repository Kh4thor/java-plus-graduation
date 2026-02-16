package malyshev.egor.feign.request;

import malyshev.egor.dto.request.EventRequestStatusUpdateRequest;
import malyshev.egor.dto.request.EventRequestStatusUpdateResult;
import malyshev.egor.dto.request.ParticipationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@FeignClient(
        name = "request-service",
        contextId = "private-request-service",
        url = "${gateway.url:http://localhost:8080}",
        path = "/users/{userId}/events/{eventId}/requests"
)
public interface PrivateRequestFeignClient {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<ParticipationRequestDto> list(
            @PathVariable long userId,
            @PathVariable long eventId
    );

    @PatchMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    EventRequestStatusUpdateResult update(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody EventRequestStatusUpdateRequest body
    );
}