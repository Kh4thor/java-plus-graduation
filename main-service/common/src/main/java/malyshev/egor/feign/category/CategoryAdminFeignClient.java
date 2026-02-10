package malyshev.egor.feign.category;

import feign.FeignException;
import jakarta.validation.Valid;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.category.NewCategoryDto;
import malyshev.egor.dto.category.UpdateCategoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "category-service",
        contextId = "categoryAdminApiClient",
        path = "/admin/categories")
public interface CategoryAdminFeignClient {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Valid @RequestBody NewCategoryDto dto) throws FeignException;

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable long catId,
                              @Valid @RequestBody UpdateCategoryRequest dto) throws FeignException;

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long catId) throws FeignException;
}
