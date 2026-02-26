package malyshev.egor.service.privates;

import jakarta.validation.Valid;
import malyshev.egor.dto.comment.CommentShortDto;
import malyshev.egor.dto.comment.NewCommentDto;

import java.util.List;

public interface PrivateCommentService {
    // PRIVATE
    CommentShortDto createComment(NewCommentDto dto, Long userId, Long eventId);

    // PRIVATE
    CommentShortDto patchComment(@Valid NewCommentDto dto, Long userId, Long eventId, Long commentId);

    // PRIVATE
    CommentShortDto deleteCommentByPrivate(Long userId, Long eventId, Long commentId);

    // PRIVATE
    List<CommentShortDto> getAllCommentsByEventPrivate(Long userId, Long eventId);
}
