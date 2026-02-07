package ru.practicum.ewm.service.compilation.service;

import ru.practicum.ewm.service.compilation.dto.CompilationDto;

import java.util.List;

/**
 * Публичный сервис подборок.
 */
public interface CompilationPublicService {

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getById(Long compId);
}