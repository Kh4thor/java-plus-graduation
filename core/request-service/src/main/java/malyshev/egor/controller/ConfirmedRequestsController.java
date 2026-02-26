package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.service.privates.PrivateRequestService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для получения количества подтверждённых заявок на участие в событии.
 * Предоставляет эндпоинт, доступный для внутреннего использования или для проверки статистики.
 */
@Validated
@RestController
@RequiredArgsConstructor
public class ConfirmedRequestsController {

    private final PrivateRequestService privateRequestService;

    /**
     * Возвращает количество подтверждённых заявок на участие в указанном событии.
     *
     * @param eventId идентификатор события
     * @return количество подтверждённых заявок
     */
    @GetMapping("/events/{eventId}/requests/confirmed")
    public Long countConfirmedRequests(@PathVariable(name = "eventId") Long eventId) {
        return privateRequestService.countConfirmedRequests(eventId);
    }
}