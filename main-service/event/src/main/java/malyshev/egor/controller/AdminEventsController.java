package malyshev.egor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import malyshev.egor.service.EventService;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.UpdateEventAdminRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventsController {

    private final EventService service;

    @GetMapping
    public List<EventFullDto> search(@RequestParam(value = "users", required = false) List<Long> users,
                                     @RequestParam(value = "states", required = false) List<String> states,
                                     @RequestParam(value = "categories", required = false) List<Long> categories,
                                     @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                     @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                     @RequestParam(value = "from", defaultValue = "0") int from,
                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        return service.adminSearch(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                PageRequest.of(from / size, size)
        );
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventAdminRequest dto) {
        return service.adminUpdate(
                eventId,
                dto
        );
    }
}
