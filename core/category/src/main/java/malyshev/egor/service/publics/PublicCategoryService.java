package malyshev.egor.service.publics;

import malyshev.egor.dto.category.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PublicCategoryService {

    // PUBLIC
    List<CategoryDto> list(Pageable pageable);

    // PUBLIC
    CategoryDto get(long id);
}
