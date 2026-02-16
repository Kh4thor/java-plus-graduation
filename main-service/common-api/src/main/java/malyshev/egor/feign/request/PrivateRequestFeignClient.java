package malyshev.egor.feign.request;

import malyshev.egor.dto.request.EventRequestStatusUpdateRequest;
import malyshev.egor.dto.request.EventRequestStatusUpdateResult;
import malyshev.egor.dto.request.ParticipationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Validated
@FeignClient(
        name = "request-service",
        contextId = "private-request-client",
        url = "${gateway.url:http://localhost:8080}",
        path = "/users/{userId}/events/{eventId}/requests"
)
public interface PrivateRequestFeignClient {

    @GetMapping
    List<ParticipationRequestDto> list(
            @PathVariable long userId,
            @PathVariable long eventId
    );

    @PatchMapping
    EventRequestStatusUpdateResult update(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody EventRequestStatusUpdateRequest body
    );
}