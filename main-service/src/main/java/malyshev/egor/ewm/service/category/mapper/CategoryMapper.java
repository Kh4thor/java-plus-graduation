package malyshev.egor.ewm.service.category.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.ewm.service.category.dto.CategoryDto;
import malyshev.egor.ewm.service.category.model.Category;

@UtilityClass
public final class CategoryMapper {
    public static CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }
}
