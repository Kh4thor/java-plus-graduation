package malyshev.egor.ewm.service.compilation.service;

import malyshev.egor.ewm.service.compilation.dto.CompilationDto;
import malyshev.egor.ewm.service.compilation.dto.NewCompilationDto;
import malyshev.egor.ewm.service.compilation.dto.UpdateCompilationRequest;

/**
 * Админский сервис подборок.
 */
public interface CompilationAdminService {

    CompilationDto create(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest dto);
}