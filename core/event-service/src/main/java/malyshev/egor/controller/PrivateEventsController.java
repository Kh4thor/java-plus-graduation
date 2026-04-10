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

/**
 * Контроллер для управления событиями от имени текущего пользователя (приватный API).
 * Предоставляет методы для получения списка событий пользователя, создания события,
 * получения конкретного события и его обновления.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventsController {

    private final PrivateEventService privateEventService;

    /**
     * Возвращает список событий, созданных указанным пользователем, с пагинацией.
     *
     * @param userId идентификатор пользователя
     * @param from   количество элементов, которое нужно пропустить (для пагинации), по умолчанию 0
     * @param size   количество элементов на странице, по умолчанию 10
     * @return список событий в сокращённом представлении
     * @throws malyshev.egor.exception.NotFoundException если пользователь не найден
     */
    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(value = "from", defaultValue = "0") int from,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        return privateEventService.getUserEvents(
                userId,
                PageRequest.of(from / size, size));
    }

    /**
     * Создаёт новое событие от имени указанного пользователя.
     *
     * @param userId идентификатор пользователя-инициатора
     * @param dto    данные нового события (аннотация, категория, описание, дата, локация и т.д.)
     * @return созданное событие в расширенном представлении
     * @throws malyshev.egor.exception.NotFoundException если пользователь или категория не найдены
     * @throws IllegalArgumentException                  если дата события раньше чем через 2 часа от текущего момента,
     *                                                   или participantLimit отрицательный
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody NewEventDto dto) {
        return privateEventService.addEvent(
                userId,
                dto
        );
    }

    /**
     * Возвращает полную информацию о конкретном событии, принадлежащем пользователю.
     *
     * @param userId  идентификатор пользователя (должен совпадать с инициатором события)
     * @param eventId идентификатор события
     * @return событие в расширенном представлении
     * @throws malyshev.egor.exception.NotFoundException если событие не найдено или пользователь не является его инициатором
     */
    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        return privateEventService.getUserEvent(
                userId,
                eventId
        );
    }

    /**
     * Обновляет событие, созданное пользователем (только если оно ещё не опубликовано).
     *
     * @param userId  идентификатор пользователя (должен совпадать с инициатором события)
     * @param eventId идентификатор события
     * @param dto     данные для обновления (аннотация, категория, описание, дата, локация и т.д.)
     * @return обновлённое событие в расширенном представлении
     * @throws malyshev.egor.exception.NotFoundException если событие или пользователь не найдены
     * @throws IllegalStateException                     если событие уже опубликовано
     * @throws IllegalArgumentException                  если новая дата события раньше чем через 2 часа от текущего момента,
     *                                                   или participantLimit отрицательный
     */
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