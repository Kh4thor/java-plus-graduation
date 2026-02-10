package malyshev.egor.controller.publics;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import malyshev.egor.service.EventService;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventsController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventService service;

    @GetMapping
    public List<EventShortDto> get(@RequestParam(value = "text", required = false) String text,
                                   @RequestParam(value = "categories", required = false) List<Long> categories,
                                   @RequestParam(value = "paid", required = false) Boolean paid,
                                   @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                   @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                   @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                   @RequestParam(value = "sort", required = false) String sort,
                                   @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                   @RequestParam(value = "size", defaultValue = "10") @Positive int size,
                                   HttpServletRequest request) {

        LocalDateTime start = null;
        LocalDateTime end = null;

        try {
            if (rangeStart != null && !rangeStart.isBlank()) {
                start = LocalDateTime.parse(rangeStart, FMT);
            }
            if (rangeEnd != null && !rangeEnd.isBlank()) {
                end = LocalDateTime.parse(rangeEnd, FMT);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Incorrect date format, expected 'yyyy-MM-dd HH:mm:ss'");
        }

        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("rangeStart must be before or equal to rangeEnd");
        }

        return service.publicSearch(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                PageRequest.of(from / size, size),
                request.getRequestURI(),
                request.getRemoteAddr()
        );
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable("id") Long id,
                                HttpServletRequest request) {
        return service.publicGet(
                id,
                request.getRequestURI(),
                request.getRemoteAddr()
        );
    }
}
