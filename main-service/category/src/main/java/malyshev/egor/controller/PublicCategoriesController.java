package malyshev.egor.controller;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.service.admins.AdminCategoryService;
import malyshev.egor.service.publics.PublicCategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoriesController {

    private final PublicCategoryService publicCategoryService;

    @GetMapping
    public List<CategoryDto> list(@RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        return publicCategoryService.list(PageRequest.of(from / size, size));
    }

    // PUBLIC
    @GetMapping("/{catId}")
    public CategoryDto get(@PathVariable long catId) {
        return publicCategoryService.get(catId);
    }
}
