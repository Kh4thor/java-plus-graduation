package malyshev.egor.comment.dto;

import lombok.Builder;
import lombok.Data;
import malyshev.egor.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentShortDto {
    private Long id;
    private String text;
    private User commentator;
    private LocalDateTime publishedOn;
}
