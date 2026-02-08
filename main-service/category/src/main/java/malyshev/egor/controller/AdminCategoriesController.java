package malyshev.egor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import malyshev.egor.ewm.service.category.dto.CategoryDto;
import malyshev.egor.ewm.service.category.dto.NewCategoryDto;
import malyshev.egor.ewm.service.category.dto.UpdateCategoryRequest;
import malyshev.egor.ewm.service.category.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoriesController {

    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Valid @RequestBody NewCategoryDto dto) {
        return service.add(dto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable long catId,
                              @Valid @RequestBody UpdateCategoryRequest dto) {
        return service.update(catId, dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long catId) {
        service.delete(catId);
    }
}
