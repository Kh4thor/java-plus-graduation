package malyshev.egor.service.publics;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.category.NewCategoryDto;
import malyshev.egor.dto.category.UpdateCategoryRequest;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.CategoryMapper;
import malyshev.egor.model.category.Category;
import malyshev.egor.repository.CategoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCategoryServiceImpl implements PublicCategoryService {
    private final CategoryRepository categoryRepository;

    // PUBLIC
    @Override
    public List<CategoryDto> list(Pageable p) {
        return categoryRepository.findAll(p)
                .map(CategoryMapper::toDto)
                .getContent();
    }

    @Override
    public CategoryDto get(long id) {
        return CategoryMapper.toDto(
                categoryRepository.findById(id).orElseThrow(
                        () -> new NotFoundException("Category with id=" + id + " was not found"))
        );
    }
}
