package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.request.EventRequestStatusUpdateRequest;
import malyshev.egor.dto.request.EventRequestStatusUpdateResult;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.service.privates.PrivateRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления заявками на участие в событии от имени его инициатора (приватный API).
 * Предоставляет методы для получения всех заявок на событие и обновления их статуса (подтверждение/отклонение).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class PrivateRequestController {

    private final PrivateRequestService privateRequestService;

    /**
     * Возвращает список всех заявок на участие в указанном событии.
     * Доступно только инициатору события.
     *
     * @param userId  идентификатор пользователя (должен совпадать с инициатором события)
     * @param eventId идентификатор события
     * @return список заявок на участие
     * @throws malyshev.egor.exception.NotFoundException если событие не найдено
     * @throws IllegalStateException если пользователь не является инициатором события
     */
    @GetMapping
    public List<ParticipationRequestDto> list(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        return privateRequestService.getEventRequests(userId, eventId);
    }

    /**
     * Обновляет статус одной или нескольких заявок на участие (подтверждение/отклонение).
     * Доступно только инициатору события.
     *
     * @param userId  идентификатор пользователя (должен совпадать с инициатором события)
     * @param eventId идентификатор события
     * @param body    объект с идентификаторами заявок и новым статусом (CONFIRMED или REJECTED)
     * @return результат обновления с разделением на подтверждённые и отклонённые заявки
     * @throws malyshev.egor.exception.NotFoundException если событие не найдено
     * @throws IllegalStateException если пользователь не является инициатором события,
     *         если превышен лимит участников, или если заявка не в статусе PENDING
     */
    @PatchMapping
    public EventRequestStatusUpdateResult update(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @RequestBody EventRequestStatusUpdateRequest body) {
        return privateRequestService.updateEventRequests(userId, eventId, body);
    }
}