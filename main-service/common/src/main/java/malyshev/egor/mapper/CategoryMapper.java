package malyshev.egor.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.model.Category;

@UtilityClass
public final class CategoryMapper {
    public static CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }
}
