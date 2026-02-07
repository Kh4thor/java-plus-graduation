package ru.practicum.ewm.service.request.controller.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.service.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class PrivateEventRequestsController {

    private final RequestService requestService;

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