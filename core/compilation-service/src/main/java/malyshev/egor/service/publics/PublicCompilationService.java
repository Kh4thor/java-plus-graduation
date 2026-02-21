package malyshev.egor.service.publics;

import malyshev.egor.dto.compilation.CompilationDto;

import java.util.List;

/**
 * Публичный сервис подборок.
 */
public interface PublicCompilationService {

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getById(Long compId);
}