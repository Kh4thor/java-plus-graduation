package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.request.EventRequestStatusUpdateRequest;
import malyshev.egor.dto.request.EventRequestStatusUpdateResult;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.service.privates.PrivateRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class PrivateRequestController {

    private final PrivateRequestService privateRequestService;

    @GetMapping
    public List<ParticipationRequestDto> list(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        return privateRequestService.getEventRequests(userId, eventId);
    }

    @PatchMapping
    public EventRequestStatusUpdateResult update(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @RequestBody EventRequestStatusUpdateRequest body) {
        return privateRequestService.updateEventRequests(userId, eventId, body);
    }
}