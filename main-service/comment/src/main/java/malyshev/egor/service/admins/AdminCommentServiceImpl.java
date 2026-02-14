package malyshev.egor.service.admins;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.dto.comment.CommentFullDto;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.CommentMapper;
import malyshev.egor.model.Comment;
import malyshev.egor.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository commentRepository;

    // ADMIN
    @Transactional
    @Override
    public CommentFullDto deleteCommentByAdmin(Long eventId, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException("Unable to delete comment. Comment id=" + commentId + "not found")
        );
        comment.setDeleted(true);
        comment = commentRepository.save(comment);
        return CommentMapper.toFullDto(comment);
    }

    // ADMIN
    @Transactional
    @Override
    public List<CommentFullDto> getAllCommentsByEventAdmin(Long eventId) {
        return commentRepository.findByEventId(eventId).stream()
                .map(CommentMapper::toFullDto)
                .toList();
    }
}
