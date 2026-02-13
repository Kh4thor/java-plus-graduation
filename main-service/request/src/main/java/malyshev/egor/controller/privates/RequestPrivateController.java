package malyshev.egor.controller.privates;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.request.EventRequestStatusUpdateRequest;
import malyshev.egor.dto.request.EventRequestStatusUpdateResult;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.service.privates.RequestPrivateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class RequestPrivateController {

    private final RequestPrivateService requestService;

    @GetMapping
    public List<ParticipationRequestDto> list(@PathVariable long userId,
                                              @PathVariable long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping
    public EventRequestStatusUpdateResult update(@PathVariable long userId,
                                                 @PathVariable long eventId,
                                                 @RequestBody EventRequestStatusUpdateRequest body) {
        return requestService.updateEventRequests(userId, eventId, body);
    }
}