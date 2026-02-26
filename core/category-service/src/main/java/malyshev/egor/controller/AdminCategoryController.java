package malyshev.egor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.category.NewCategoryDto;
import malyshev.egor.dto.category.UpdateCategoryRequest;
import malyshev.egor.service.admins.AdminCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления категориями от имени администратора.
 * Предоставляет эндпоинты для создания, обновления и удаления категорий.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    /**
     * Создаёт новую категорию.
     *
     * @param dto данные новой категории (название)
     * @return созданная категория с присвоенным идентификатором
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Valid @RequestBody NewCategoryDto dto) {
        return adminCategoryService.add(dto);
    }

    /**
     * Обновляет существующую категорию.
     *
     * @param catId идентификатор обновляемой категории
     * @param dto   новые данные категории (название)
     * @return обновлённая категория
     */
    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable long catId,
                              @Valid @RequestBody UpdateCategoryRequest dto) {
        return adminCategoryService.update(catId, dto);
    }

    /**
     * Удаляет категорию по идентификатору.
     *
     * @param catId идентификатор удаляемой категории
     */
    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long catId) {
        adminCategoryService.delete(catId);
    }
}