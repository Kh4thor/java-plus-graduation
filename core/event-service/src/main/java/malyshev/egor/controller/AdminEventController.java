package malyshev.egor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventShortDto;
import malyshev.egor.dto.event.UpdateEventAdminRequest;
import malyshev.egor.mapper.EventMapper;
import malyshev.egor.service.admins.AdminEventService;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления событиями от имени администратора.
 * Предоставляет методы для поиска, обновления и получения событий.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final AdminEventService adminEventService;
    private final EventMapper eventMapper;

    /**
     * Выполняет поиск событий по заданным фильтрам с пагинацией.
     *
     * @param users       список идентификаторов инициаторов событий (необязательный)
     * @param states      список состояний событий (например, PUBLISHED, PENDING) (необязательный)
     * @param categories  список идентификаторов категорий (необязательный)
     * @param rangeStart  начало временного диапазона события (строка в формате "yyyy-MM-dd HH:mm:ss" или "yyyy-MM-dd'T'HH:mm:ss")
     * @param rangeEnd    конец временного диапазона события
     * @param from        количество элементов, которое нужно пропустить (для пагинации, по умолчанию 0)
     * @param size        количество элементов на странице (по умолчанию 10)
     * @return список событий в расширенном представлении (EventFullDto)
     * @throws IllegalArgumentException если передан некорректный формат даты или rangeStart позже rangeEnd
     */
    @GetMapping
    public List<EventFullDto> search(@RequestParam(value = "users", required = false) List<Long> users,
                                     @RequestParam(value = "states", required = false) List<String> states,
                                     @RequestParam(value = "categories", required = false) List<Long> categories,
                                     @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                     @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                     @RequestParam(value = "from", defaultValue = "0") int from,
                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        return adminEventService.adminSearch(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                PageRequest.of(from / size, size)
        );
    }

    /**
     * Обновляет событие администратором.
     *
     * @param eventId идентификатор события
     * @param dto     данные для обновления события
     * @return обновлённое событие в расширенном представлении (EventFullDto)
     * @throws malyshev.egor.exception.NotFoundException если событие не найдено
     * @throws IllegalStateException если действие (публикация/отмена) невозможно в текущем состоянии события
     * @throws IllegalArgumentException если передан некорректный лимит участников или дата события в прошлом
     */
    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventAdminRequest dto) {
        return adminEventService.adminUpdate(
                eventId,
                dto
        );
    }

    /**
     * Возвращает список событий в сокращённом представлении по их идентификаторам.
     *
     * @param ids список идентификаторов событий
     * @return список событий (EventShortDto)
     */
    @GetMapping("/by-ids")
    List<EventShortDto> getEventsByIds(@RequestParam("ids") List<Long> ids) {
        return adminEventService.getEventsByIds(ids).stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    /**
     * Проверяет, существуют ли события, принадлежащие указанной категории.
     *
     * @param categoryId идентификатор категории
     * @return true, если существует хотя бы одно событие с данной категорией; false в противном случае
     */
    @GetMapping("/exists-by-categoryId/{categoryId}")
    boolean deleteEventsByCategoryId(@PathVariable Long categoryId) {
        return adminEventService.existsByCategoryId(categoryId);
    }
}