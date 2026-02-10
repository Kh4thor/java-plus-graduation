package malyshev.egor.service;


import malyshev.egor.dto.CompilationDto;
import malyshev.egor.dto.NewCompilationDto;
import malyshev.egor.dto.UpdateCompilationRequest;

/**
 * Админский сервис подборок.
 */
public interface CompilationAdminService {

    CompilationDto create(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest dto);
}