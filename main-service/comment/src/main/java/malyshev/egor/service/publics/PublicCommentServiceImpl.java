package malyshev.egor.service.publics;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import malyshev.egor.dto.comment.CommentFullDto;
import malyshev.egor.dto.comment.CommentShortDto;
import malyshev.egor.dto.comment.NewCommentDto;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.CommentMapper;
import malyshev.egor.model.Comment;
import malyshev.egor.model.event.Event;
import malyshev.egor.model.event.EventState;
import malyshev.egor.model.request.ParticipationRequest;
import malyshev.egor.model.request.RequestStatus;
import malyshev.egor.model.user.User;
import malyshev.egor.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
