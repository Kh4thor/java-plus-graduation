package malyshev.egor.feign.category;

import malyshev.egor.dto.category.CategoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "category-service",
        contextId = "categoryPublicApiClient",
        path = "/categories")
public interface CategoryPublicFeignClient {

    @GetMapping
    public List<CategoryDto> list(@RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size);

    @GetMapping("/{catId}")
    public CategoryDto get(@PathVariable long catId);
}
