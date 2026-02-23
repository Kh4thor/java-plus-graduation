package malyshev.egor.service.privates;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import malyshev.egor.InteractionApiManager;
import malyshev.egor.dto.comment.CommentShortDto;
import malyshev.egor.dto.comment.NewCommentDto;
import malyshev.egor.dto.event.EventFullDto;
import malyshev.egor.dto.event.EventState;
import malyshev.egor.dto.request.ParticipationRequestDto;
import malyshev.egor.dto.request.RequestStatus;
import malyshev.egor.exception.NotFoundException;
import malyshev.egor.mapper.CommentMapper;
import malyshev.egor.model.Comment;
import malyshev.egor.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final CommentRepository commentRepository;
    private final InteractionApiManager interactionApiManager;
    private final CommentMapper commentMapper;

    // PRIVATE
    @Transactional
    @Override
    public CommentShortDto createComment(NewCommentDto dto, Long userId, Long eventId) {

        EventFullDto event = interactionApiManager.getEventOfUserByPrivate(eventId, userId);
        ParticipationRequestDto request = interactionApiManager.getRequestOfUserByPrivate(userId, eventId);

        // у пользователя не одобрена заявка на событие
        if (request.getStatus() != RequestStatus.CONFIRMED) {
            throw new IllegalArgumentException(
                    "Unable to create comment. Request status must be CONFIRMED. Current value:" + request.getStatus());
        }

        //событие не опубликовано
        if (event.getState() != EventState.PUBLISHED) {
            throw new IllegalArgumentException("Unable to create comment. Event id=" + eventId + "not published");
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .commentator(userId)
                .publishedOn(LocalDateTime.now())
                .event(eventId)
                .build();

        Comment cretedComment = commentRepository.save(comment);

        return commentMapper.toShortDto(cretedComment);
    }

    // PRIVATE
    @Transactional
    @Override
    public CommentShortDto patchComment(@Valid NewCommentDto dto, Long userId, Long eventId, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException("Unable to patch comment. Comment id=" + commentId + "not found")
        );
        EventFullDto event = interactionApiManager.getEventOfUserByPrivate(eventId, userId);

        //событие не опубликовано
        if (event.getState() != EventState.PUBLISHED) {
            throw new IllegalArgumentException("Unable to create comment. Event id=" + eventId + "not published");
        }

        // комментарий не относится к событию eventId
        if (comment.getEvent() != null) {
            if (!Objects.equals(comment.getEvent(), eventId)) {
                throw new IllegalArgumentException("Comment does not belong to event with id=" + eventId);
            }
        }

        // пользователь не является автором комментария
        if (!Objects.equals(comment.getCommentator(), userId))
            throw new IllegalArgumentException(
                    "Unable to patch comment. User with id=" + userId + " is not creator of comment id=" + commentId
            );

        String text = dto.getText() == null ? comment.getText() : dto.getText();
        comment.setText(text);
        comment = commentRepository.save(comment);
        return commentMapper.toShortDto(comment);
    }

    // PRIVATE
    @Transactional
    @Override
    public CommentShortDto deleteCommentByPrivate(Long userId, Long eventId, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException("Unable to delete comment. Comment id=" + commentId + "not found")
        );
        EventFullDto event = interactionApiManager.getEventOfUserByPrivate(eventId, userId);

        // пользователь не является автором комментария
        if (!Objects.equals(comment.getCommentator(), userId))
            throw new IllegalArgumentException(
                    "Unable to delete comment. User with id=" + userId + " is not creator of comment id=" + commentId
            );

        //событие не опубликовано
        if (event.getState() != EventState.PUBLISHED) {
            throw new IllegalArgumentException("Unable to create comment. Event id=" + eventId + "not published");
        }

        // комментарий не относится к событию eventId
        if (comment.getEvent() != null) {
            if (!Objects.equals(comment.getEvent(), eventId)) {
                throw new IllegalArgumentException("Comment does not belong to event with id=" + eventId);
            }
        }

        comment.setDeleted(true);
        comment = commentRepository.save(comment);
        return commentMapper.toShortDto(comment);
    }

    // PRIVATE
    @Transactional
    @Override
    public List<CommentShortDto> getAllCommentsByEventPrivate(Long userId, Long eventId) {
        List<Comment> commentList = commentRepository.findByEventAndDeleted(eventId, false);

        return commentList.stream()
                .map(commentMapper::toShortDto)
                .filter(c -> c.getCommentator().getId().equals(userId))
                .toList();
    }
}