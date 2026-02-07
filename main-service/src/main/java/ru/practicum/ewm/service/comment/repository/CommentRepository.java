package ru.practicum.ewm.service.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndDeleted(Long commentId, boolean deleted);

    List<Comment> findByEventIdAndDeleted(Long commentId, boolean deleted);

    List<Comment> findByEventId(Long eventId);
}
