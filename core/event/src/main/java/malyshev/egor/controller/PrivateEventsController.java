package malyshev.egor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.dto.event.NewEventDto;
import malyshev.egor.dto.event.UpdateEventUserRequest;
import malyshev.egor.service.privates.PrivateEventService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventsController {

    private final PrivateEventService privateEventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(value = "from", defaultValue = "0") int from,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        return privateEventService.getUserEvents(
                userId,
                PageRequest.of(from / size, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody NewEventDto dto) {
        return privateEventService.addEvent(
                userId,
                dto
        );
    }

    // PRIVATE
    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        return privateEventService.getUserEvent(
                userId,
                eventId
        );
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventUser(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest dto) {
        return privateEventService.updateEventUser(
                userId,
                eventId,
                dto
        );
    }
}
