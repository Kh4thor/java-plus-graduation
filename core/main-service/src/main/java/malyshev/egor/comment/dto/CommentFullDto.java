package malyshev.egor.comment.dto;

import lombok.Builder;
import lombok.Data;
import malyshev.egor.event.dto.UserShortDto;

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
