package malyshev.egor.service.publics;

import jakarta.transaction.Transactional;
import malyshev.egor.dto.comment.CommentShortDto;

import java.util.List;

public interface PublicCommentService {

    // PUBLIC
    @Transactional
    List<CommentShortDto> getAllCommentsByEventPublic(Long eventId);
}
