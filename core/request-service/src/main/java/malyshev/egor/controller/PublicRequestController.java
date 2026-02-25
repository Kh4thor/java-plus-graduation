package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.service.publics.PublicRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Публичный контроллер для управления заявками на участие пользователя в событиях.
 * Предоставляет методы для получения списка заявок текущего пользователя,
 * создания новой заявки и отмены существующей заявки.
 */
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class PublicRequestController {
    private final PublicRequestService publicRequestService;

    /**
     * Возвращает список всех заявок на участие, созданных указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список заявок на участие
     * @throws malyshev.egor.exception.NotFoundException если пользователь не найден
     */
    @GetMapping
    public List<ParticipationRequestDto> get(@PathVariable long userId) {
        return publicRequestService.getUserRequests(userId);
    }

    /**
     * Создаёт новую заявку на участие в указанном событии от имени пользователя.
     *
     * @param userId  идентификатор пользователя, подающего заявку
     * @param eventId идентификатор события
     * @return созданная заявка на участие
     * @throws malyshev.egor.exception.NotFoundException если пользователь или событие не найдены
     * @throws IllegalStateException если:
     *         - событие не опубликовано;
     *         - пользователь является инициатором события;
     *         - заявка уже существует;
     *         - достигнут лимит участников
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable long userId, @RequestParam long eventId) {
        return publicRequestService.createRequest(userId, eventId);
    }

    /**
     * Отменяет заявку на участие по её идентификатору.
     * Только автор заявки может её отменить.
     *
     * @param userId    идентификатор пользователя (должен совпадать с автором заявки)
     * @param requestId идентификатор отменяемой заявки
     * @return отменённая заявка
     * @throws malyshev.egor.exception.NotFoundException если заявка или пользователь не найдены
     * @throws IllegalStateException если пользователь не является автором заявки
     */
    @PatchMapping("{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable long userId, @PathVariable long requestId) {
        return publicRequestService.cancelRequest(userId, requestId);
    }
}