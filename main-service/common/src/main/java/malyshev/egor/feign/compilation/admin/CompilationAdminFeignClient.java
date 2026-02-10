package malyshev.egor.feign.compilation.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import malyshev.egor.dto.compilation.CompilationDto;
import malyshev.egor.dto.compilation.NewCompilationDto;
import malyshev.egor.dto.compilation.UpdateCompilationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "compilation-service",
        contextId = "compilationAdminClient",
        path = "/admin/compilations")
@Validated
public interface CompilationAdminFeignClient {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto dto);

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long compId);

    @PatchMapping("/{compId}")
    public CompilationDto update(
            @PathVariable @Positive Long compId,
            @Valid @RequestBody(required = false) UpdateCompilationRequest dto);
}
