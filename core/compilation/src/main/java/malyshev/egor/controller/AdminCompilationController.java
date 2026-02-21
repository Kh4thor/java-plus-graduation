package malyshev.egor.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import malyshev.egor.dto.compilation.CompilationDto;
import malyshev.egor.dto.compilation.NewCompilationDto;
import malyshev.egor.dto.compilation.UpdateCompilationRequest;
import malyshev.egor.service.admins.AdminCompilationService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

    private final AdminCompilationService adminCompilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto dto) {
        log.info("АДМИН API: создание подборки title={}", dto.getTitle());
        return adminCompilationService.create(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long compId) {
        log.info("АДМИН API: удаление подборки id={}", compId);
        adminCompilationService.delete(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(
            @PathVariable @Positive Long compId,
            @Valid @RequestBody(required = false) UpdateCompilationRequest dto) {
        // Пропускаем PATCH без тела
        if (dto == null) {
            log.info("АДМИН API: PATCH /admin/compilations/{} получено пустое тело — применяем пустой DTO", compId);
            dto = UpdateCompilationRequest.builder().build();
        }

        log.info("АДМИН API: PATCH /admin/compilations/{} тело: events={}, pinned={}, title={}",
                compId,
                dto.getEvents(),
                dto.getPinned(),
                dto.getTitle());

        CompilationDto resp = adminCompilationService.update(compId, dto);

        log.info("АДМИН API: результат PATCH: id={}, количество событий={}, pinned={}, title={}",
                resp.getId(),
                resp.getEvents() == null ? 0 : resp.getEvents().size(),
                resp.getPinned(),
                resp.getTitle());

        return resp;
    }
}
