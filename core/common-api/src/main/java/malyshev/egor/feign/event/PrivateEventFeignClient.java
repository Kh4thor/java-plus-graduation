package malyshev.egor.feign.event;

import malyshev.egor.dto.event.EventFullDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign-клиент для взаимодействия с приватным API сервиса событий.
 * Используется для получения полной информации о событии, принадлежащем конкретному пользователю.
 */
@Validated
@FeignClient(name = "event-service",
        contextId = "private-event-service")
public interface PrivateEventFeignClient {

    /**
     * Возвращает подробную информацию о событии, созданном указанным пользователем.
     *
     * @param userId  идентификатор пользователя-инициатора события
     * @param eventId идентификатор события
     * @return полное DTO события
     * @throws feign.FeignException.NotFound если событие не найдено или пользователь не является его инициатором
     * @throws feign.FeignException          при других ошибках взаимодействия (сервис недоступен и т.п.)
     */
    @GetMapping("/users/{userId}/events/{eventId}")
    EventFullDto getUserEvent(@PathVariable("userId") Long userId,
                              @PathVariable("eventId") Long eventId);
}