package malyshev.egor.controller.publics;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.service.privates.RequestPrivateService;
import malyshev.egor.service.publics.RequestPublicService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestPublicController {
    private final RequestPublicService requestService;

    @GetMapping
    public List<ParticipationRequestDto> get(@PathVariable long userId) {
        return requestService.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable long userId, @RequestParam long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable long userId, @PathVariable long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
