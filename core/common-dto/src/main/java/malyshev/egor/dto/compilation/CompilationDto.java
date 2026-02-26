package malyshev.egor.dto.compilation;

import lombok.Builder;
import lombok.Data;
import malyshev.egor.dto.event.EventShortDto;

import java.util.List;

@Data
@Builder
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;
}