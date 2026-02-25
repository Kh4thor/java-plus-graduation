package malyshev.egor.feign.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign-клиент для взаимодействия с сервисом заявок (request-service)
 * с целью получения статистики по подтверждённым заявкам для события.
 */
@FeignClient(name = "request-service",
        contextId = "request-stats",
        url = "${gateway.url:http://localhost:8080}")
public interface ConfirmedRequestsFeignClient {

    /**
     * Возвращает количество подтверждённых заявок на участие в указанном событии.
     *
     * @param eventId идентификатор события
     * @return количество подтверждённых заявок
     * @throws feign.FeignException если запрос завершился ошибкой (например, сервис недоступен, событие не найдено и т.п.)
     */
    @GetMapping("/events/{eventId}/requests/confirmed")
    Long countConfirmedRequests(@PathVariable("eventId") Long eventId);
}