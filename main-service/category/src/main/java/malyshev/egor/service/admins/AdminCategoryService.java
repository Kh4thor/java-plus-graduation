package malyshev.egor.service.admins;

import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.category.NewCategoryDto;
import malyshev.egor.dto.category.UpdateCategoryRequest;

public interface AdminCategoryService {

    // ADMIN
    CategoryDto add(NewCategoryDto dto);

    // ADMIN
    CategoryDto update(long id, UpdateCategoryRequest dto);

    // ADMIN
    void delete(long id);
}
