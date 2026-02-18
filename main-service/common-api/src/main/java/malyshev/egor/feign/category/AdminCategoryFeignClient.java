package malyshev.egor.feign.category;

import jakarta.validation.Valid;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.category.NewCategoryDto;
import malyshev.egor.dto.category.UpdateCategoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "category-service",
        contextId = "admin-category-service",
        url = "${gateway.url:http://localhost:8080}",
        path = "/admin/categories")
public interface AdminCategoryFeignClient {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto add(@Valid @RequestBody NewCategoryDto dto);

    @PatchMapping("/{catId}")
    CategoryDto update(@PathVariable long catId,
                       @Valid @RequestBody UpdateCategoryRequest dto);

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable long catId);
}
