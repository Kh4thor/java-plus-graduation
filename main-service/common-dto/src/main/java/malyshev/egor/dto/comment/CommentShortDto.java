package malyshev.egor.dto.comment;

import lombok.Builder;
import lombok.Data;
import malyshev.egor.model.user.User;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentShortDto {
    private Long id;
    private String text;
    private User commentator;
    private LocalDateTime publishedOn;
}
