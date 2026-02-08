package malyshev.egor.service;

import malyshev.egor.ewm.service.compilation.dto.CompilationDto;

import java.util.List;

/**
 * Публичный сервис подборок.
 */
public interface CompilationPublicService {

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getById(Long compId);
}