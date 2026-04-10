package malyshev.egor.feign.event;

import malyshev.egor.dto.event.EventShortDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign-клиент для взаимодействия с административным API сервиса событий.
 * Предоставляет методы для получения событий по их идентификаторам и проверки наличия событий по категории.
 */
@Validated
@FeignClient(name = "event-service",
        contextId = "admin-event-service",
        path = "/admin/events")
public interface AdminEventFeignClient {

    /**
     * Возвращает список событий в сокращённом представлении по их идентификаторам.
     *
     * @param ids список идентификаторов событий
     * @return список событий (EventShortDto)
     * @throws feign.FeignException если запрос завершился ошибкой (например, сервис недоступен)
     */
    @GetMapping("/by-ids")
    List<EventShortDto> getEventsByIds(@RequestParam("ids") List<Long> ids);

    /**
     * Проверяет, существуют ли события, принадлежащие указанной категории.
     *
     * @param categoryId идентификатор категории
     * @return true, если существует хотя бы одно событие с данной категорией; false в противном случае
     * @throws feign.FeignException если запрос завершился ошибкой (например, сервис недоступен)
     */
    @GetMapping("/exists-by-categoryId/{categoryId}")
    boolean existsEventsByCategoryId(@PathVariable Long categoryId);
}