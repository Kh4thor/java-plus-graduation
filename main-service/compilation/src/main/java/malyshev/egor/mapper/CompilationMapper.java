package malyshev.egor.mapper;

import lombok.experimental.UtilityClass;
import malyshev.egor.ewm.service.compilation.dto.CompilationDto;
import malyshev.egor.ewm.service.compilation.dto.NewCompilationDto;
import malyshev.egor.ewm.service.compilation.dto.UpdateCompilationRequest;
import malyshev.egor.ewm.service.compilation.model.Compilation;
import malyshev.egor.ewm.service.event.dto.EventShortDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class CompilationMapper {

    /**
     * Создаёт новую подборку по DTO.
     * Важно: коллекции должны быть ИЗМЕНЯЕМЫМИ (HashSet),
     * иначе Hibernate может не выполнять dirty-checking.
     */
    public Compilation toEntity(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);

        Set<Long> events = dto.getEvents() != null
                ? new HashSet<>(dto.getEvents())
                : new HashSet<>();

        compilation.setEvents(events);

        return compilation;
    }

    /**
     * Частичное обновление сущности подборки.
     */
    public void updateEntity(UpdateCompilationRequest dto, Compilation compilation) {
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            compilation.setEvents(new HashSet<>(dto.getEvents()));
        }
    }

    /**
     * Преобразует подборку в DTO.
     */
    public CompilationDto toDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(events != null ? events : List.of())
                .build();
    }
}
