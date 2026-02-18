package malyshev.egor.feign.category;

import malyshev.egor.dto.category.CategoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Validated
@FeignClient(name = "category-service",
        contextId = "public-category-service",
        url = "${gateway.url:http://localhost:8080}",
        path = "/categories")
public interface PublicCategoryFeignClient {

    @GetMapping
    List<CategoryDto> list(@RequestParam(defaultValue = "0") int from,
                           @RequestParam(defaultValue = "10") int size);

    @GetMapping("/{catId}")
    CategoryDto get(@PathVariable long catId);
}
