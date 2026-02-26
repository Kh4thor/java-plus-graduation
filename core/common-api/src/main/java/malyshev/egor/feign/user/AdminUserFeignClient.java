package malyshev.egor.feign.user;

import malyshev.egor.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign-клиент для взаимодействия с административным API сервиса пользователей.
 * Предоставляет метод для получения списка пользователей с возможностью фильтрации по идентификаторам и пагинацией.
 */
@FeignClient(name = "user-service",
        contextId = "admin-request-service",
        url = "${gateway.url:http://localhost:8080}",
        path = "/admin/users")
public interface AdminUserFeignClient {

    /**
     * Возвращает список пользователей.
     *
     * @param ids  список идентификаторов пользователей для фильтрации (необязательный).
     *             Если не указан или пуст, возвращаются все пользователи.
     * @param from количество элементов, которое нужно пропустить (для пагинации), по умолчанию 0.
     * @param size количество элементов на странице, по умолчанию 10.
     * @return список пользователей (UserDto)
     * @throws feign.FeignException если запрос завершился ошибкой (например, сервис недоступен, внутренняя ошибка сервера и т.п.)
     */
    @GetMapping
    List<UserDto> list(@RequestParam(value = "ids", required = false) List<Long> ids,
                       @RequestParam(defaultValue = "0") int from,
                       @RequestParam(defaultValue = "10") int size);
}