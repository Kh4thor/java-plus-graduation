package malyshev.egor.service.privates;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import malyshev.egor.InteractionApiManager;
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
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final CommentRepository commentRepository;
    private final InteractionApiManager  interactionApiManager;

    // PRIVATE
    @Transactional
    @Override
    public CommentShortDto createComment(NewCommentDto dto, Long userId, Long eventId) {
        User commentator = interactionApiManager.adminGetUserById(userId);
        Event event = interactionApiManager.adminGetEventByUserIdAndEventId(userId, eventId);
        ParticipationRequest participationRequest = interactionApiManager.findByRequesterIdAndEventId(userId, eventId);

        ParticipationRequest request = requestRepository.findByRequesterIdAndEventId(userId, eventId).orElseThrow(
                () -> new IllegalArgumentException(
                        "Unable to create comment. Request with userId=" + userId + " and eventId=" + eventId +
                                " was not found")
        );

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
                .commentator(commentator)
                .publishedOn(LocalDateTime.now())
                .event(event)
                .build();

        Comment cretedComment = commentRepository.save(comment);

        return CommentMapper.toShortDto(cretedComment);
    }

    // PRIVATE
    @Transactional
    @Override
    public CommentShortDto patchComment(@Valid NewCommentDto dto, Long userId, Long eventId, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException("Unable to patch comment. Comment id=" + commentId + "not found")
        );

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(
                        "Unable to patch comment. Event with id=" + eventId + " was not found")
        );

        //событие не опубликовано
        if (event.getState() != EventState.PUBLISHED) {
            throw new IllegalArgumentException("Unable to create comment. Event id=" + eventId + "not published");
        }

        // комментарий не относится к событию eventId
        if (comment.getEvent() != null) {
            if (!Objects.equals(comment.getEvent().getId(), eventId)) {
                throw new IllegalArgumentException("Comment does not belong to event with id=" + eventId);
            }
        }

        // пользователь не является автором комментария
        User commentator = comment.getCommentator();
        if (!Objects.equals(commentator.getId(), userId))
            throw new IllegalArgumentException(
                    "Unable to patch comment. User with id=" + userId + " is not creator of comment id=" + commentId
            );

        String text = dto.getText() == null ? comment.getText() : dto.getText();
        comment.setText(text);
        comment = commentRepository.save(comment);
        return CommentMapper.toShortDto(comment);
    }

    // PRIVATE
    @Transactional
    @Override
    public CommentShortDto deleteCommentByPrivate(Long userId, Long eventId, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException("Unable to delete comment. Comment id=" + commentId + "not found")
        );

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(
                        "Unable to patch comment. Event with id=" + eventId + " was not found")
        );

        // пользователь не является автором комментария
        User commentator = comment.getCommentator();
        if (!Objects.equals(commentator.getId(), userId))
            throw new IllegalArgumentException(
                    "Unable to delete comment. User with id=" + userId + " is not creator of comment id=" + commentId
            );

        //событие не опубликовано
        if (event.getState() != EventState.PUBLISHED) {
            throw new IllegalArgumentException("Unable to create comment. Event id=" + eventId + "not published");
        }

        // комментарий не относится к событию eventId
        if (comment.getEvent() != null) {
            if (!Objects.equals(comment.getEvent().getId(), eventId)) {
                throw new IllegalArgumentException("Comment does not belong to event with id=" + eventId);
            }
        }

        comment.setDeleted(true);
        comment = commentRepository.save(comment);
        return CommentMapper.toShortDto(comment);
    }


    // PRIVATE
    @Transactional
    @Override
    public List<CommentShortDto> getAllCommentsByEventPrivate(Long userId, Long eventId) {
        List<Comment> commentList = commentRepository.findByEventIdAndDeleted(eventId, false);

        return commentList.stream()
                .map(CommentMapper::toShortDto)
                .filter(c -> c.getCommentator().getId().equals(userId))
                .toList();
    }
}
