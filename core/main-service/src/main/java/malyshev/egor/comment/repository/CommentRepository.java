package malyshev.egor.comment.repository;

import malyshev.egor.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndDeleted(Long commentId, boolean deleted);

    List<Comment> findByEventIdAndDeleted(Long commentId, boolean deleted);

    List<Comment> findByEventId(Long eventId);
}
