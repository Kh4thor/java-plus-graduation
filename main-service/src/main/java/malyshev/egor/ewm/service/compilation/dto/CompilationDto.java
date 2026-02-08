package malyshev.egor.ewm.service.compilation.dto;

import lombok.Builder;
import lombok.Data;
import malyshev.egor.ewm.service.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;
}