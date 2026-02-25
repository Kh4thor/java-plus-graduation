package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.service.privates.PrivateRequestService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class ConfirmedRequestsController {

    private final PrivateRequestService privateRequestService;

    @GetMapping("/events/{eventId}/requests/confirmed")
    public Long countConfirmedRequests(@PathVariable(name = "eventId") Long eventId) {
        return privateRequestService.countConfirmedRequests(eventId);
    }
}