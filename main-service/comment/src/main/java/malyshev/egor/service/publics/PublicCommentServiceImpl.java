package malyshev.egor.service.publics;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import malyshev.egor.dto.comment.CommentShortDto;
import malyshev.egor.mapper.CommentMapper;
import malyshev.egor.model.Comment;
import malyshev.egor.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService {

    private final CommentRepository commentRepository;

    // PUBLIC
    @Transactional
    @Override
    public List<CommentShortDto> getAllCommentsByEventPublic(Long eventId) {
        List<Comment> commentList = commentRepository.findByEventIdAndDeleted(eventId, false);

        return commentList.stream()
                .map(CommentMapper::toShortDto)
                .toList();
    }
}
