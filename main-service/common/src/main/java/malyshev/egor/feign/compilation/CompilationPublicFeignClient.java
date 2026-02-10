package malyshev.egor.feign.compilation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import malyshev.egor.dto.compilation.CompilationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "compilation-service",
        contextId = "compilationPublicApiClient",
        path = "/compilations")
@Validated
public interface CompilationPublicFeignClient {

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size);

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable @Positive Long compId);
}