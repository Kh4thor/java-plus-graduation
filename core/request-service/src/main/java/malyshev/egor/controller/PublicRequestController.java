package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.service.publics.PublicRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class PublicRequestController {
    private final PublicRequestService publicRequestService;

    @GetMapping
    public List<ParticipationRequestDto> get(@PathVariable long userId) {
        return publicRequestService.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable long userId, @RequestParam long eventId) {
        return publicRequestService.createRequest(userId, eventId);
    }

    @PatchMapping("{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable long userId, @PathVariable long requestId) {
        return publicRequestService.cancelRequest(userId, requestId);
    }
}
