package malyshev.egor.feign.compilation;

import feign.FeignException;
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
        contextId = "adminCompilationApiClient",
        url = "${gateway.url:http://localhost:8080}",
        path = "/admin/compilations")
@Validated
public interface AdminCompilationFeignClient {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CompilationDto create(@Valid @RequestBody NewCompilationDto dto) throws FeignException;

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable @Positive Long compId) throws FeignException;

    @PatchMapping("/{compId}")
    CompilationDto update(
            @PathVariable @Positive Long compId,
            @Valid @RequestBody(required = false) UpdateCompilationRequest dto) throws FeignException;
}
