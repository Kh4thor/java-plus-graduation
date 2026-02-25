package malyshev.egor.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.dto.compilation.CompilationDto;
import malyshev.egor.service.publics.PublicCompilationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Публичный контроллер для работы с подборками событий.
 * Предоставляет эндпоинты для получения списка подборок и получения подборки по идентификатору.
 */
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicCompilationController {

    private final PublicCompilationService publicCompilationService;

    /**
     * Возвращает список подборок событий с возможностью фильтрации по признаку закрепления и пагинацией.
     *
     * @param pinned если указан, возвращаются только закреплённые (true) или только обычные (false) подборки;
     *               если не указан, возвращаются все подборки
     * @param from   индекс первого элемента (смещение), по умолчанию 0
     * @param size   количество элементов на странице, по умолчанию 10
     * @return список подборок, соответствующих заданным параметрам
     */
    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return publicCompilationService.getCompilations(pinned, from, size);
    }

    /**
     * Возвращает подборку событий по её идентификатору.
     *
     * @param compId идентификатор подборки (должен быть положительным)
     * @return подборка событий
     * @throws malyshev.egor.exception.CompilationNotFoundException если подборка с указанным идентификатором не найдена
     */
    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable @Positive Long compId) {
        return publicCompilationService.getById(compId);
    }
}