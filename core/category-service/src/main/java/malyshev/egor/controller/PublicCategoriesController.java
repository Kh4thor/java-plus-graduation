package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.service.publics.PublicCategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для публичного доступа к категориям.
 * Предоставляет эндпоинты для получения списка категорий и получения категории по идентификатору.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoriesController {

    private final PublicCategoryService publicCategoryService;

    /**
     * Возвращает список категорий с пагинацией.
     *
     * @param from индекс первого элемента (смещение), по умолчанию 0
     * @param size количество элементов на странице, по умолчанию 10
     * @return список категорий
     */
    @GetMapping
    public List<CategoryDto> list(@RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        return publicCategoryService.list(PageRequest.of(from / size, size));
    }

    /**
     * Возвращает категорию по её идентификатору.
     *
     * @param catId идентификатор категории
     * @return категория
     */
    @GetMapping("/{catId}")
    public CategoryDto get(@PathVariable long catId) {
        return publicCategoryService.get(catId);
    }
}