package malyshev.egor.service.admins;

import jakarta.transaction.Transactional;
import malyshev.egor.dto.comment.CommentFullDto;

import java.util.List;

public interface AdminCommentService {

    // ADMIN
    @Transactional
    CommentFullDto deleteCommentByAdmin(Long eventId, Long commentId);

    // ADMIN
    @Transactional
    List<CommentFullDto> getAllCommentsByEventAdmin(Long eventId);
}
