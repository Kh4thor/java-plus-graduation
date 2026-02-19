package malyshev.egor.category.service;

import lombok.RequiredArgsConstructor;
import malyshev.egor.category.dto.CategoryDto;
import malyshev.egor.category.dto.NewCategoryDto;
import malyshev.egor.category.dto.UpdateCategoryRequest;
import malyshev.egor.category.mapper.CategoryMapper;
import malyshev.egor.category.model.Category;
import malyshev.egor.category.repository.CategoryRepository;
import malyshev.egor.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repo;

    @Override
    @Transactional
    public CategoryDto add(NewCategoryDto dto) {
        return CategoryMapper.toDto(
                repo.save(
                        Category.builder()
                                .name(dto.getName())
                                .build()
                )
        );
    }

    @Override
    @Transactional
    public CategoryDto update(long id, UpdateCategoryRequest dto) {
        var c = repo.findById(id).orElseThrow(
                () -> new NotFoundException("Category with id=" + id + " was not found"));
        c.setName(dto.getName());
        return CategoryMapper.toDto(c);
    }

    @Override
    @Transactional
    public void delete(long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
        repo.deleteById(id);
    }

    @Override
    public List<CategoryDto> list(Pageable p) {
        return repo.findAll(p)
                .map(CategoryMapper::toDto)
                .getContent();
    }

    @Override
    public CategoryDto get(long id) {
        return CategoryMapper.toDto(
                repo.findById(id).orElseThrow(
                        () -> new NotFoundException("Category with id=" + id + " was not found"))
        );
    }
}
