package malyshev.egor.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.service.publics.PublicEventService;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Публичный контроллер для работы с событиями.
 * Предоставляет эндпоинты для получения списка событий с фильтрацией и пагинацией,
 * а также для получения подробной информации о конкретном событии.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PublicEventService service;

    /**
     * Возвращает список событий с возможностью фильтрации и пагинации.
     * Также фиксирует факт просмотра для статистики (через HttpServletRequest).
     *
     * @param text          ключевое слово для поиска в аннотации и описании (необязательно)
     * @param categories    список идентификаторов категорий для фильтрации (необязательно)
     * @param paid          признак платности события (необязательно)
     * @param rangeStart    начало временного диапазона даты события (строка в формате "yyyy-MM-dd HH:mm:ss")
     * @param rangeEnd      конец временного диапазона даты события
     * @param onlyAvailable если true, возвращаются только события, на которые ещё есть места (необязательно)
     * @param sort          тип сортировки: "EVENT_DATE" или "VIEWS" (необязательно)
     * @param from          количество элементов для пропуска (пагинация), по умолчанию 0
     * @param size          количество элементов на странице, по умолчанию 10
     * @param request       объект HTTP-запроса для получения IP и URI (для статистики)
     * @return список событий в сокращённом представлении
     * @throws IllegalArgumentException если передан некорректный формат даты или rangeStart позже rangeEnd
     */
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

        String clientIp = request.getRemoteAddr();
        String requestURI = request.getRequestURI();

        if (clientIp == null) clientIp = "unknown";
        if (requestURI == null) requestURI = "unknown";

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
                clientIp,
                requestURI
        );
    }

    /**
     * Возвращает подробную информацию о событии по его идентификатору.
     * Фиксирует факт просмотра для статистики.
     *
     * @param id      идентификатор события
     * @param request объект HTTP-запроса для получения IP и URI (для статистики)
     * @return событие в расширенном представлении
     * @throws malyshev.egor.exception.NotFoundException если событие не найдено или не опубликовано
     */
    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable("id") Long id,
                                HttpServletRequest request
    ) {
        String clientIp = request.getRemoteAddr();
        String requestURI = request.getRequestURI();

        if (clientIp == null) clientIp = "unknown";
        if (requestURI == null) requestURI = "unknown";
        return service.publicGet(
                id,
                requestURI,
                clientIp
        );
    }
}