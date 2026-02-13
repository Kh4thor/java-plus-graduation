package malyshev.egor.service.privates;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import malyshev.egor.dto.comment.CommentShortDto;
import malyshev.egor.dto.comment.NewCommentDto;

import java.util.List;

public interface PrivateCommentService {
    // PRIVATE
    @Transactional
    CommentShortDto createComment(NewCommentDto dto, Long userId, Long eventId);

    // PRIVATE
    @Transactional
    CommentShortDto patchComment(@Valid NewCommentDto dto, Long userId, Long eventId, Long commentId);

    // PRIVATE
    @Transactional
    CommentShortDto deleteCommentByPrivate(Long userId, Long eventId, Long commentId);

    // PRIVATE
    @Transactional
    List<CommentShortDto> getAllCommentsByEventPrivate(Long userId, Long eventId);
}
