package malyshev.egor.feign.event;

import malyshev.egor.dto.event.EventFullDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign-клиент для взаимодействия с публичным API сервиса событий.
 * Предоставляет метод для получения полной информации о событии по его идентификатору.
 */
@Validated
@FeignClient(name = "event-service",
        contextId = "public-event-service",
        url = "${gateway.url:http://localhost:8080}",
        path = "/events")
public interface PublicEventFeignClient {

    /**
     * Возвращает подробную информацию о событии по его идентификатору.
     *
     * @param id идентификатор события
     * @return полное DTO события
     * @throws feign.FeignException.NotFound если событие с указанным идентификатором не найдено (статус 404)
     * @throws feign.FeignException           при других ошибках взаимодействия (сервис недоступен, таймаут и т.п.)
     */
    @GetMapping("/{id}")
    EventFullDto getById(@PathVariable("id") Long id);
}