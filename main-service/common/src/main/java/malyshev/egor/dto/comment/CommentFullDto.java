package malyshev.egor.dto.comment;

import lombok.Builder;
import lombok.Data;
import malyshev.egor.dto.event.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentFullDto {
    private Long id;
    private String text;
    private UserShortDto commentator;
    private LocalDateTime publishedOn;
    private boolean deleted;
}
