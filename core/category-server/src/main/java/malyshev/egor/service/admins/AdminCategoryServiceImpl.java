package malyshev.egor.service.admins;

import lombok.RequiredArgsConstructor;
import malyshev.egor.dto.category.CategoryDto;
import malyshev.egor.dto.category.NewCategoryDto;
import malyshev.egor.dto.category.UpdateCategoryRequest;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.CategoryMapper;
import malyshev.egor.model.category.Category;
import malyshev.egor.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository repo;

    // ADMIN
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

    // ADMIN
    @Override
    @Transactional
    public CategoryDto update(long id, UpdateCategoryRequest dto) {
        var c = repo.findById(id).orElseThrow(
                () -> new NotFoundException("Category with id=" + id + " was not found"));
        c.setName(dto.getName());
        return CategoryMapper.toDto(c);
    }

    // ADMIN
    @Override
    @Transactional
    public void delete(long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
        repo.deleteById(id);
    }
}
