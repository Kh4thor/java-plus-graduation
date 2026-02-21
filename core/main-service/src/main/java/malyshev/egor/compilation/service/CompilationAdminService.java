package malyshev.egor.compilation.service;


import malyshev.egor.compilation.dto.CompilationDto;
import malyshev.egor.compilation.dto.NewCompilationDto;
import malyshev.egor.compilation.dto.UpdateCompilationRequest;

/**
 * Админский сервис подборок.
 */
public interface CompilationAdminService {

    CompilationDto create(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest dto);
}