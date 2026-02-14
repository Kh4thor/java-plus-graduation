package malyshev.egor.feign.category;

import feign.FeignException;
import malyshev.egor.dto.category.CategoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@FeignClient(name = "category-service",
        contextId = "publicCategoryApiClient",
        url = "${gateway.url:http://localhost:8080}",
        path = "/categories")
public interface PublicCategoryFeignClient {

    @GetMapping
    List<CategoryDto> list(@RequestParam(defaultValue = "0") int from,
                           @RequestParam(defaultValue = "10") int size) throws FeignException;

    @GetMapping("/{catId}")
    CategoryDto get(@PathVariable long catId) throws FeignException;
}
