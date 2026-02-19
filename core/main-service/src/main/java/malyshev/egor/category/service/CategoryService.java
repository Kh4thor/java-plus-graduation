package malyshev.egor.category.service;

import malyshev.egor.category.dto.CategoryDto;
import malyshev.egor.category.dto.NewCategoryDto;
import malyshev.egor.category.dto.UpdateCategoryRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto dto);

    CategoryDto update(long id, UpdateCategoryRequest dto);

    void delete(long id);

    List<CategoryDto> list(Pageable pageable);

    CategoryDto get(long id);
}
