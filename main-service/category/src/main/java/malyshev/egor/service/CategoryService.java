package malyshev.egor.service;

import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.category.NewCategoryDto;
import malyshev.egor.dto.category.UpdateCategoryRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto dto);

    CategoryDto update(long id, UpdateCategoryRequest dto);

    void delete(long id);

    List<CategoryDto> list(Pageable pageable);

    CategoryDto get(long id);
}
