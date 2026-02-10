package malyshev.egor.service;

import malyshev.egor.dto.compilation.CompilationDto;
import malyshev.egor.dto.compilation.NewCompilationDto;
import malyshev.egor.dto.compilation.UpdateCompilationRequest;

/**
 * Админский сервис подборок.
 */
public interface CompilationAdminService {

    CompilationDto create(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest dto);
}