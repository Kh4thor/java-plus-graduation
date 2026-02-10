package malyshev.egor.feign.request;

import malyshev.egor.dto.request.EventRequestStatusUpdateRequest;
import malyshev.egor.dto.request.EventRequestStatusUpdateResult;
import malyshev.egor.dto.request.ParticipationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@FeignClient(name = "request-service",
        contextId = "eventRequestPrivateApiClient")
public interface EventRequestPrivateFeignClient {

    @GetMapping
    public List<ParticipationRequestDto> list(@PathVariable long userId,
                                              @PathVariable long eventId);

    @PatchMapping
    public EventRequestStatusUpdateResult update(@PathVariable long userId,
                                                 @PathVariable long eventId,
                                                 @RequestBody EventRequestStatusUpdateRequest body);
}