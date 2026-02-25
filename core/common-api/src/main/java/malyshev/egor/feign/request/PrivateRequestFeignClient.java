package malyshev.egor.feign.request;

import malyshev.egor.dto.request.ParticipationRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign-клиент для взаимодействия с приватным API сервиса заявок (request-service).
 * Предоставляет метод для получения списка заявок на участие в конкретном событии от имени его инициатора.
 */
@Validated
@FeignClient(
        name = "request-service",
        contextId = "private-request-service-list",
        url = "${gateway.url:http://localhost:8080}",
        path = "/users/{userId}/events/{eventId}/requests"
)
public interface PrivateRequestFeignClient {

    /**
     * Возвращает список заявок на участие в указанном событии, созданных пользователем.
     *
     * @param userId  идентификатор пользователя-инициатора события
     * @param eventId идентификатор события
     * @return список заявок на участие (может быть пустым)
     * @throws feign.FeignException.NotFound если событие не найдено или пользователь не является его инициатором
     * @throws feign.FeignException           при других ошибках взаимодействия (сервис недоступен, таймаут и т.п.)
     */
    @GetMapping
    List<ParticipationRequestDto> list(
            @PathVariable long userId,
            @PathVariable long eventId
    );
}