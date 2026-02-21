package malyshev.egor.event.controller.privates;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import malyshev.egor.event.dto.EventFullDto;
import malyshev.egor.event.dto.EventShortDto;
import malyshev.egor.event.dto.NewEventDto;
import malyshev.egor.event.dto.UpdateEventUserRequest;
import malyshev.egor.event.service.EventService;
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

    private final EventService service;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(value = "from", defaultValue = "0") int from,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        return service.getUserEvents(
                userId,
                PageRequest.of(from / size, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody NewEventDto dto) {
        return service.addEvent(
                userId,
                dto
        );
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        return service.getUserEvent(
                userId,
                eventId
        );
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventUser(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest dto) {
        return service.updateEventUser(
                userId,
                eventId,
                dto
        );
    }
}
