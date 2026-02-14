package malyshev.egor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.category.NewCategoryDto;
import malyshev.egor.dto.category.UpdateCategoryRequest;
import malyshev.egor.service.admins.AdminCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoriesController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Valid @RequestBody NewCategoryDto dto) {
        return adminCategoryService.add(dto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable long catId,
                              @Valid @RequestBody UpdateCategoryRequest dto) {
        return adminCategoryService.update(catId, dto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long catId) {
        adminCategoryService.delete(catId);
    }
}
