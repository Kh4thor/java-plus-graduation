package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.request.RequestStatus;
import malyshev.egor.service.privates.PrivateRequestService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/requests/count")
public class CountRequestController {

    private final PrivateRequestService privateRequestService;

    @GetMapping
    public Long countByEventAndStatus(
            @PathVariable Long eventId,
            @RequestBody RequestStatus status) {
        return privateRequestService.countByEventAndStatus(eventId, status);
    }
}