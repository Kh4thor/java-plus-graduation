package malyshev.egor.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.model.category.Category;

@UtilityClass
public final class CategoryMapper {
    public static CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }

    public static Category toCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }

        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }
}
