package malyshev.egor.feign.request;

import feign.FeignException;
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
    List<ParticipationRequestDto> list(@PathVariable long userId,
                                       @PathVariable long eventId) throws FeignException;

    @PatchMapping
    EventRequestStatusUpdateResult update(@PathVariable long userId,
                                          @PathVariable long eventId,
                                          @RequestBody EventRequestStatusUpdateRequest body) throws FeignException;
}