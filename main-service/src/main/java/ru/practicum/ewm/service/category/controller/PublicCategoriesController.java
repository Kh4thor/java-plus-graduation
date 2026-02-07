package ru.practicum.ewm.service.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.service.CategoryService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoriesController {

    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> list(@RequestParam(defaultValue="0") int from,
                                  @RequestParam(defaultValue="10") int size) {
        return service.list(PageRequest.of(from/size, size));
    }

    @GetMapping("/{catId}")
    public CategoryDto get(@PathVariable long catId) {
        return service.get(catId);
    }
}
