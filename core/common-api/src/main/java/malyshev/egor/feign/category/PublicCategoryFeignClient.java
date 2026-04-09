package malyshev.egor.feign.category;

import malyshev.egor.dto.category.CategoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign-клиент для взаимодействия с публичным API сервиса категорий.
 * Предоставляет методы для получения списка категорий и получения категории по идентификатору.
 */
@Validated
@FeignClient(name = "category-service",
        contextId = "public-category-service",
        path = "/categories")
public interface PublicCategoryFeignClient {

    /**
     * Возвращает список категорий с пагинацией.
     *
     * @param from количество элементов, которое нужно пропустить (для пагинации), по умолчанию 0
     * @param size количество элементов на странице, по умолчанию 10
     * @return список категорий
     * @throws feign.FeignException если запрос завершился ошибкой (например, сервис недоступен)
     */
    @GetMapping
    List<CategoryDto> list(@RequestParam(defaultValue = "0") int from,
                           @RequestParam(defaultValue = "10") int size);

    /**
     * Возвращает категорию по её идентификатору.
     *
     * @param catId идентификатор категории
     * @return категория
     * @throws feign.FeignException если запрос завершился ошибкой (например, категория не найдена – статус 404)
     */
    @GetMapping("/{catId}")
    CategoryDto get(@PathVariable long catId);
}