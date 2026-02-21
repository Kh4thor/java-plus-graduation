package malyshev.egor.category.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.category.dto.CategoryDto;
import malyshev.egor.category.model.Category;

@UtilityClass
public final class CategoryMapper {
    public static CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }
}
